# NetKeep
Android client app for notes-api with user authetication
### Usage
1. Signing in if user exists or creating new user.
2. The server generates a unique token for user signed in and stores it in shared preferences for making network calls.
3. If user exists the app will retrieve notes from the server if there exists any using a Retrival Worker.
4. Another Periodic Sync Worker works in backend to sync the notes to server if network is connected.
5. The user can manually sync also.
6. When signed out the app clears the all notes from database as well as token in shared preferences.
### Demo


https://github.com/DarkNDev/NetKeep/assets/49820671/9ad7c456-bb1f-416d-8eea-8d2351693333


### Libraries Used
Material 3, Navigation Component, Kotlin coroutines, Jetpack Datastore, Lifecycle (ViewModel + LiveData), Dagger Hilt, Work Managers, Broadcast Receivers, Splash Screen Api, Ktor Client with okhttp, Content Negotiation, Kotlin serilization and Client Logging.
### Links
Authentication usage example -
1. Server for usage [com.darkndev.ktor-auth](https://github.com/DarkNDev/com.darkndev.ktor-auth)
2. Client Android app for usage [KtorAuth](https://github.com/DarkNDev/KtorAuth)

Server for usage [com.darkndev.notes-api-alt](https://github.com/DarkNDev/com.darkndev.notes-api-alt)
