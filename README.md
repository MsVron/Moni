# Moni - Financial Management App

## Current Stable Version Features

### Authentication System
- User registration with email and password
- User login functionality
- Session management (stay logged in)
- Logout capability

### Database Implementation
- SQLite Room Database setup
- User entity with fields:
  - ID (auto-generated)
  - Email
  - Password
  - Name

### Key Components

#### Activities
1. `LoginActivity`: Handles user authentication
   - Email and password input
   - Login validation
   - Session state checking
   - Navigation to registration

2. `RegisterActivity`: Manages new user registration
   - User information collection
   - Email duplication check
   - Database entry creation

3. `MainActivity`: Main landing page
   - Welcome message with user's name
   - Logout functionality
   - Session state verification

#### Data Management
- `AppDatabase`: Room database configuration
- `User`: Entity class for user data
- `UserDao`: Database access methods
- `SessionManager`: Handles login state persistence using SharedPreferences

### Technical Implementation
- Built with Java
- Uses AndroidX libraries
- Implements Room persistence library
- Material Design components for UI
- Custom color scheme (#FF1A3C as primary color)

### Project Structure
```
app/
├── java/
│   └── com.example.moni/
│       ├── LoginActivity.java
│       ├── RegisterActivity.java
│       ├── MainActivity.java
│       ├── SessionManager.java
│       ├── User.java
│       ├── UserDao.java
│       └── AppDatabase.java
└── res/
    ├── layout/
    │   ├── activity_login.xml
    │   ├── activity_register.xml
    │   └── activity_main.xml
    └── values/
        ├── colors.xml
        └── strings.xml


### Dependencies
```gradle
dependencies {
    implementation 'androidx.room:room-runtime:2.6.1'
    annotationProcessor 'androidx.room:room-compiler:2.6.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

## Setup Instructions
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run the app

## Future Development Plans
- Budget tracking
- Savings goals
- Investment recommendations
- Receipt scanning
- Financial literacy content
- Partnerships and offers
```
