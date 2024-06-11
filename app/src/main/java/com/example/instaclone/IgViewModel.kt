package com.example.instaclone

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.instaclone.data.Event
import com.example.instaclone.data.PostData
import com.example.instaclone.data.UiState
import com.example.instaclone.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class IgViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> get() = _userData.asStateFlow()

    private val _popupNotification = MutableStateFlow<Event<String>?>(null)
    val popupNotification: StateFlow<Event<String>?> get() = _popupNotification.asStateFlow()

    private val _refreshPostsProgress = MutableStateFlow(false)
    val refreshPostsProgress: StateFlow<Boolean> get() = _refreshPostsProgress.asStateFlow()

    private val _posts = MutableStateFlow<List<PostData>>(emptyList())
    val posts: StateFlow<List<PostData>> get() = _posts.asStateFlow()

    private val _searchPosts = MutableStateFlow<List<PostData>>(emptyList())
    val searchPosts: StateFlow<List<PostData>> get() = _searchPosts.asStateFlow()

    private val _postFeed = MutableStateFlow<List<PostData>>(emptyList())
    val postFeed: StateFlow<List<PostData>> get() = _postFeed.asStateFlow()


    init {
        auth.currentUser?.uid?.let { uid ->
            getUserData(uid)
        }
    }

    fun onSignUp(username: String, email: String, password: String) {

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all fields")
            return
        }

        _uiState.value = UiState.Loading

        db.collection("users").whereEqualTo("username", username).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    handleException(customMessage = "Username already exists")
                    _uiState.value = UiState.Idle
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                createOrUpdateUserProfile(username = username)
                            } else {
                                handleException(task.exception, "Error creating account")
                            }
                        }
                }
            }.addOnFailureListener {
                handleException(it)
            }
    }


    fun onLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all fields")
            return
        }

        _uiState.value = UiState.Loading

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser?.uid?.let { uid ->
                    getUserData(uid)
                    _uiState.value = UiState.Success(message = "Logged in successfully")
                }
            } else {
                handleException(task.exception, "Error logging in")
            }
        }.addOnFailureListener {
            handleException(it)
        }
    }

    private fun createOrUpdateUserProfile(
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        profileImage: String? = null
    ) {
        val uid = auth.currentUser?.uid

        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            userName = username ?: userData.value?.userName,
            bio = bio ?: userData.value?.bio,
            imgUrl = profileImage ?: userData.value?.imgUrl,
            following = userData.value?.following,
        )


        uid?.let { userId ->
            db.collection("users").document(userId).get().addOnSuccessListener {
                if (it.exists()) {
                    it.reference.update(userData.toMap()).addOnSuccessListener {
                        this._userData.value = userData
                        _uiState.value = UiState.Success("Profile updated successfully")
                    }.addOnFailureListener { exception ->
                        handleException(exception)
                    }
                } else {
                    db.collection("users").document(userId).set(userData).addOnSuccessListener {
                        this._userData.value = userData
                        _uiState.value = UiState.Success("Profile created successfully")
                    }.addOnFailureListener { exception ->
                        handleException(exception)
                    }
                    getUserData(userId)
                }
            }.addOnFailureListener {
                handleException(it)
            }
        }
    }

    private fun getUserData(uid: String) {
        _uiState.value = UiState.Loading
        db.collection("users").document(uid).get().addOnSuccessListener {
            val userData = it.toObject(UserData::class.java)
            this._userData.value = userData
            _uiState.value = UiState.Success("User data fetched successfully")
            refreshPosts()
            getPersonalizedFeed()
        }.addOnFailureListener {
            handleException(it)
        }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        exception?.printStackTrace()
        val error = exception?.localizedMessage ?: "Something went wrong"
        val message = if (customMessage.isNotEmpty()) "$customMessage: $error" else error
        _popupNotification.value = Event(message)
        _uiState.value = UiState.Error(message)
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
        _userData.value = null
        _uiState.value = UiState.Success("Logged out successfully")
        _posts.value = emptyList()
        _searchPosts.value = emptyList()
        _postFeed.value = emptyList()
    }

    fun updateUserData(name: String?, username: String?, bio: String?) {
        createOrUpdateUserProfile(
            name = name, username = username, bio = bio, profileImage = null
        )
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {

            val result = it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener { uri ->
                onSuccess(uri)
            }

        }.addOnFailureListener { exception ->
            handleException(exception)
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateUserProfile(profileImage = it.toString())
            updatePostUserImage(it.toString())
        }
    }

    private fun updatePostUserImage(imgUrl: String) {
        val uid = auth.currentUser?.uid
        db.collection("posts").whereEqualTo("userId", uid).get().addOnSuccessListener {
            val posts = MutableStateFlow<List<PostData>>(arrayListOf())
            convertPosts(it, posts)
            val refs = arrayOf<DocumentReference>()
            for (post in posts.value) {
                post.postId?.let { id ->
                    refs.plus(db.collection("posts").document(id))
                }
            }

            if (refs.isNotEmpty()) {
                db.runBatch { batch ->
                    for (ref in refs) {
                        batch.update(ref, "userImage", imgUrl)
                    }
                }.addOnSuccessListener {
                    refreshPosts()
                }
            }
        }
    }


    fun onNewPost(uri: Uri, description: String, onPostSuccess: () -> Unit) {
        uploadImage(uri) {
            onCreatePost(it, description, onPostSuccess)
        }
    }

    private fun onCreatePost(it: Uri, description: String, onPostSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid

        val fillerWords = listOf(
            "the",
            "be",
            "to",
            "of",
            "and",
            "a",
            "in",
        )
        val searchTerms = description.split(
            " ",
            ".",
            "?",
            "!",
            "/",
            ",",
            "'",
            ":",
            "-",
            "#",
            "@",
            "_",
            "&",
            "*",
        ).map { it.lowercase() }.filter { it.isNotEmpty() && !fillerWords.contains(it.lowercase()) }

        if (uid != null) {
            val postId = UUID.randomUUID().toString()
            val post = PostData(
                postId = postId,
                userId = uid,
                userName = userData.value?.userName,
                userImage = userData.value?.imgUrl,
                postImage = it.toString(),
                postDescription = description,
                postTime = System.currentTimeMillis(),
                postLikes = listOf(),
                postComments = listOf(),
                searchTerms = searchTerms,
                following = listOf()
            )
            db.collection("posts").document(postId).set(post).addOnSuccessListener {
                _uiState.value = UiState.Success("Post created successfully")
                onPostSuccess()
                refreshPosts()
            }.addOnFailureListener {
                handleException(it, "Error creating post")
            }
        } else {
            handleException(customMessage = "User not found.Create your Account")
            logout()
        }
    }

    private fun refreshPosts() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            _refreshPostsProgress.value = true
            db.collection("posts").whereEqualTo("userId", currentUserId).get()
                .addOnCompleteListener { documents ->
                    convertPosts(
                        documents.result, _posts
                    )
                    _refreshPostsProgress.value = false
                }.addOnFailureListener {
                    handleException(it, "Error fetching posts")
                    _refreshPostsProgress.value = false
                }
        } else {
            handleException(customMessage = "User not found.Create your Account")
            logout()
        }
    }

    private fun convertPosts(documents: QuerySnapshot, outState: MutableStateFlow<List<PostData>>) {
        val newPosts = mutableListOf<PostData>()
        documents.forEach { document ->
            val post = document.toObject(PostData::class.java)
            newPosts.add(post)
        }

        val sortedPost = newPosts.sortedByDescending { it.postTime }
        outState.value = sortedPost
    }


    fun searchPosts(searchTerm: String) {
        if (searchTerm.isNotEmpty()) {
            _uiState.value = UiState.Loading
            db.collection("posts").whereArrayContains("searchTerms", searchTerm.trim().lowercase())
                .get().addOnFailureListener {
                    handleException(it, "Error searching posts")
                }.addOnSuccessListener { querySnapshot ->
                    convertPosts(querySnapshot, _searchPosts)
                    _uiState.value = UiState.Success("Posts fetched successfully")
                }
        }
    }

    fun onFollowClick(userId: String) {
        auth.currentUser?.uid?.let { currentUser ->
            val following = arrayListOf<String>()
            _userData.value?.following?.let {
                following.addAll(it)
            }
            if (following.contains(userId)) {
                following.remove(userId)
            } else {
                following.add(userId)
            }
            db.collection("users").document(currentUser).update("following", following)
                .addOnSuccessListener {
                    getUserData(currentUser)
                    _uiState.value = UiState.Success("User followed successfully")
                }
        }
    }

    private fun getPersonalizedFeed() {
        val following = _userData.value?.following ?: emptyList()

        if (following.isNotEmpty()) {
            _uiState.value = UiState.Loading
            db.collection("posts")
                .whereIn("userId", following)
                .get()
                .addOnSuccessListener {
                    convertPosts(it, _postFeed)
                    if (_postFeed.value.isEmpty()) {
                        getGeneralFeed()
                    } else {
                        _uiState.value = UiState.Success("Posts fetched successfully")
                    }
                }.addOnFailureListener {
                    handleException(it, "Error fetching posts")
                }
        } else {
            getGeneralFeed()
        }
    }


    private fun getGeneralFeed() {
        _uiState.value = UiState.Loading

        val currentTime = System.currentTimeMillis()
        val lastDay = currentTime - (24 * 60 * 60 * 1000)

        db.collection("posts")
            .whereGreaterThan("postTime", lastDay)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    Log.d("getGeneralFeed", "Post data: ${document.data}")
                }
                convertPosts(querySnapshot, _postFeed)
                _uiState.value = UiState.Success("Posts fetched successfully")
            }
            .addOnFailureListener {
                handleException(it, "Error fetching posts")
            }
    }
}