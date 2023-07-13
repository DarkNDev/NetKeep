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
1. Material 3
2. Navigation Component
3. Kotlin coroutines
4. Jetpack Datastore
5. Lifecycle (ViewModel + LiveData)
6. Dagger Hilt
7. Work Managers
8. Broadcast Receivers
9. Splash Screen Api
10. Ktor Client with okhttp
11. Content Negotiation
12. Kotlin serilization
13. Client Logging.
### Links
Authentication usage example -
1. Server for usage [com.darkndev.ktor-auth](https://github.com/DarkNDev/com.darkndev.ktor-auth)
2. Client Android app for usage [KtorAuth](https://github.com/DarkNDev/KtorAuth)

Server for usage [com.darkndev.notes-api-alt](https://github.com/DarkNDev/com.darkndev.notes-api-alt)
