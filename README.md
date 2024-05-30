# Instagram Clone

This project is an Instagram clone built using Android Jetpack Compose, Firebase, and the MVVM (Model-View-ViewModel) architecture. The application allows users to perform various Instagram-like functionalities such as user authentication, posting photos, liking and commenting on posts, and viewing user profiles.

## Features

- **User Authentication:** Users can sign up and log in using their email and password.
- **Profile Management:** Users can edit their profile information, including their display name and bio.
- **Post Photos:** Users can upload photos with captions.
- **Feed:** Users can view a feed of posts from all users.
- **Likes and Comments:** Users can like and comment on posts.
- **Follow System:** Users can follow and unfollow other users to see their posts in the feed.
- **Search:** Users can search for other users by their username.

## Technologies Used

- **Jetpack Compose:** A modern toolkit for building native Android UI.
- **Firebase Authentication:** For user authentication.
- **Firebase Firestore:** A NoSQL database for storing user data and posts.
- **Firebase Storage:** For storing user-uploaded images.
- **MVVM Architecture:** To separate business logic from UI and to make the app more modular and testable.

## Project Structure

```
Instagram-Clone/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/instagramclone/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/          # Data models
│   │   │   │   │   ├── repository/     # Data repositories
│   │   │   │   │   └── source/         # Data sources (Firebase)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── theme/          # UI theming and styling
│   │   │   │   │   ├── view/           # Composables for different screens
│   │   │   │   │   ├── viewmodel/      # ViewModels for handling UI-related data
│   │   │   │   │   └── navigation/     # Navigation setup
│   │   │   │   └── util/               # Utility classes
│   │   └── androidTest/
│   │   └── test/
│   └── build.gradle
├── build.gradle
└── README.md
```

## Getting Started

### Prerequisites

- Android Studio
- Firebase account

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/unon4all/Instagram-Clone.git
   cd Instagram-Clone
   ```

2. **Open the project in Android Studio.**

3. **Set up Firebase:**
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project.
   - Add an Android app to your Firebase project.
   - Follow the instructions to download the `google-services.json` file.
   - Place the `google-services.json` file in the `app` directory of your project.

4. **Add Firebase dependencies:**
   Make sure your `app/build.gradle` file includes the necessary Firebase dependencies:
   ```gradle
   implementation platform('com.google.firebase:firebase-bom:28.4.1')
   implementation 'com.google.firebase:firebase-auth-ktx'
   implementation 'com.google.firebase:firebase-firestore-ktx'
   implementation 'com.google.firebase:firebase-storage-ktx'
   ```

5. **Build and run the project:**
   - Sync your project with Gradle files.
   - Run the app on an emulator or a physical device.

## Contributing

Contributions are welcome! If you find any issues or have any suggestions for improvements, please create an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Contact

For any questions or inquiries, please contact [unon4all](https://github.com/unon4all).

---

This README provides a high-level overview of the project structure, setup, and key features. It aims to help developers get started with the project quickly and understand its core components.
