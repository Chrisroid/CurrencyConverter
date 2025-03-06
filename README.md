# Currency Converter Application

## 📌 Overview
This is a **Currency Converter Application** built using **Jetpack Compose** in Kotlin. The application allows users to convert currencies using real-time exchange rates fetched from **Fixer.io**. It follows **MVVM architecture** with **Hilt** for dependency injection and integrates **Retrofit, Room, and Coroutines** for networking and database operations.

## THIS IS A COWRYWISE ANDROID DEVELOPER ASSESSMENT PROJECT

## 🏗️ Features
- **Jetpack Compose UI** following **Material 3** design principles
- **Real-time currency conversion** using **Fixer.io API**
- **Offline support** using **Room database**
- **MVVM architecture** for separation of concerns
- **Hilt for Dependency Injection**
- **Error handling** for API failures and network issues
- **Unit and UI tests** using **Mockk, JUnit, and Espresso**

## 📸 Design Reference
**Source:** [Dribbble - Currency Converter](https://dribbble.com/shots/6647815-Calculator)

![Design](![image](https://github.com/user-attachments/assets/94546ccc-cf09-442f-af29-e9883cc69d93)
)


https://github.com/user-attachments/assets/fb110876-45ab-4edd-91c2-48c07cc728f1


---

## 🚀 Tech Stack
- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Networking**: Retrofit
- **Database**: Room
- **Coroutines**: For asynchronous operations
- **Dependency Injection**: Hilt
- **Testing**: JUnit, Mockk, Espresso

---

### Build and Run the App
- Open the project in **Android Studio** (Giraffe or later recommended)
- Sync Gradle files
- Run the app on an emulator or physical device


---


---

## ✅ Unit & UI Testing
This project includes:
- **Unit Tests** using JUnit & Mockk (`CurrencyRepositoryTest.kt`)
- **UI Tests** using Espresso & Compose UI Testing (`CurrencyConverterScreenTest.kt`)

### Running Tests
Run the following command in the terminal:
```sh
gradlew testDebugUnitTest  # Run Unit Tests
gradlew connectedAndroidTest  # Run UI Tests
```


---
### IMPROVISATIONS
- Due to using the free tier API, I could only use EUR as base currency, so you can only use EUR as base currency
- For the graph, i can't get historical data from the free tier also, so I use dummy data to fill up the chart

**🚀 Happy Coding!** 🎉
