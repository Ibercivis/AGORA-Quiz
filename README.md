# AGORA-Quiz

AGORA-Quiz is a quiz-based mobile application developed as part of the European project AGORA to combat disinformation on climate change. The app enables users to enhance their knowledge and assess their understanding through gamified questions.

## 📂 Repository Structure
This repository contains three main components:
- `/backend` → The API developed using Django Rest Framework.
- `/android` → The Android application developed in Android Studio.
- `/ios` → The iOS application developed in Xcode.

## 🚀 Installation and Usage

### Backend
The backend of AGORA-Quiz is built using Django Rest Framework and handles user management, question importation for generating the question bank, and endpoint management for communication with the mobile app.

#### 1️⃣ Setting up the environment
It is highly recommended to create a virtual environment before installing dependencies to keep the project isolated:
```bash
cd backend
python -m venv venv
source venv/bin/activate  # On Windows use: venv\Scripts\activate
```

#### 2️⃣ Installing dependencies and configuring the environment
Once the virtual environment is activated, install the required dependencies:
```bash
pip install -r requirements.txt
```

Next, review the `agora/settings.py` file to check for necessary environment variables. Some critical settings are configured via `local.env` for security reasons, such as:
- **Database configuration** (PostgreSQL)
- **Domain URL settings**
- **Other sensitive credentials**

Ensure you create a `.env` file and fill in the required values before running the application.

#### 3️⃣ Applying migrations and running the server
After setting up the environment variables, apply the migrations:
```bash
python manage.py migrate
```

Now, you can start the backend server:
```bash
python manage.py runserver
```
The server will be available at `http://127.0.0.1:8000/`.

### Android
The Android application is developed using Android Studio. The setup process is straightforward, but you must configure the correct API endpoints before running the app.

#### 1️⃣ Setting up the project
1. Open the `/android` directory in **Android Studio**.
2. Allow the IDE to sync dependencies and build the project automatically.

#### 2️⃣ Configuring API endpoints
Before running the app, ensure that the backend API URL is correctly set in:
```
android/app/src/main/res/values/strings.xml
```
Look for the following key and update it accordingly:
```xml
<string name="base_url">http://your-backend-url/api/</string>
```
Replace `"http://your-backend-url/api/"` with the actual backend URL.

#### 3️⃣ Running the application
Once the API configuration is correct:
1. Select a connected **physical device** or an **Android emulator**.
2. Click **Run** ▶️ in Android Studio or use the following command:
   ```bash
   ./gradlew installDebug
   ```
The application should now be installed and ready for use.

### iOS
The iOS application is developed using Xcode. The setup is straightforward, but before running the app, you must ensure that the backend API URL is correctly configured.

#### 1️⃣ Setting up the project
1. Open the `/ios` directory in **Xcode**.
2. If dependencies are required, install them using **CocoaPods**:
   ```bash
   cd ios
   pod install
   ```
3. Open `AgoraQuiz.xcworkspace` in Xcode.

#### 2️⃣ Configuring API endpoints
Before running the app, update the backend API URL in:
```
ios/agoraquiz/Services/URLs.swift
```
Look for the following constant and update it accordingly:
```swift
let BASE_URL = "http://your-backend-url/api/"
```
Replace `"http://your-backend-url/api/"` with the actual backend URL.

#### 3️⃣ Running the application
Once the API configuration is correct:
1. Select a connected **physical device** or an **iOS simulator**.
2. Click **Run** ▶️ in Xcode or use the following command:
   ```bash
   xcodebuild -scheme AgoraQuiz -destination 'platform=iOS Simulator,name=iPhone 14,OS=latest' build
   ```
The application should now be installed and ready for use.

## 🎯 Project Objectives
AGORA-Quiz aims to engage citizens and stakeholders in combating misinformation on climate change while fostering education and awareness. The app connects identified knowledge gaps with reliable resources and interactive challenges created by the AGORA community, ensuring a dynamic and participatory learning experience.

## 🛠 Contributing
AGORA-Quiz is an open-code project, and contributions are welcome! If you would like to contribute, feel free to fork the repository and submit a pull request.

If you encounter any issues or have suggestions, please open a GitHub issue.

Thank you for your interest in improving AGORA-Quiz!

## 📜 Licence
This project is licensed under the **European Union Public License 1.2 (EUPL 1.2)**. 

You can find the full text of the license [here](https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12).

## ✉️ Contact
For further information or collaboration inquiries, please contact development[a]ibercivis.es

Thank you for your interest in AGORA-Quiz! Together, we can counter disinformation and promote climate literacy.
