package com.example.instaclone

import android.net.Uri
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
//            _uiState.value = UiState.Loading
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
                postLikes = listOf<String>(),
                postComments = listOf<String>()
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
}