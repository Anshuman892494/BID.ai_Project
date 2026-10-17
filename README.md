# 🚀 Bid.ai - AI-Powered Smart Selling & Auction Mobile App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-Jetpack%20Compose-green.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-blue.svg)](https://developer.android.com/topic/architecture)
[![Node.js](https://img.shields.io/badge/Node.js-Express%20REST%20API-brightgreen.svg)](https://nodejs.org)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)](https://www.postgresql.org)
[![License](https://img.shields.io/badge/License-Confidential-red.svg)]()

> Technical Assignment submission for **Bid.ai** Mobile App Developer role.
> Features pixel-perfect Figma screens, MVVM architecture, Node.js + PostgreSQL backend, and an intelligent **One-Click Sell** workflow.

---

## 📱 Features & Highlights

1. **Pixel-Perfect Figma UI**: Built using Jetpack Compose and Material 3 design tokens directly matching the official Bid.ai design system.
2. **Robust Authentication**: Email/Password and Mobile auth with client-side form validation, error states, and secure JWT persistence.
3. **Live Auction Feed**: Dynamic home screen with categorized filters, real-time search, countdown timers, and Eco-Score badges.
4. **⭐ One-Click AI Sell**:
   - Image picker with preview.
   - **"✨ Magic Auto-Fill with AI"**: Automatically detects product category, recommends fair starting bid prices, and writes technical specifications.
   - Real-time submission directly updating the public auction feed.
5. **MVVM & Reactive StateFlow**: Clean separation of UI, ViewModel, and Repository layers with Kotlin Coroutines.

---

## 🏗️ Project Architecture

```
BID.ai/
├── android/                      # Android Native Application (Kotlin MVVM)
│   ├── app/
│   │   ├── src/main/java/com/bidai/app/
│   │   │   ├── data/            # Retrofit ApiService, Data Models, Repositories
│   │   │   ├── ui/
│   │   │   │   ├── theme/       # Figma Design Tokens (Color, Type, Theme)
│   │   │   │   ├── components/  # Reusable UI widgets (AuctionCard, EcoBadge)
│   │   │   │   ├── screens/     # Login, Home, and One-Click Sell screens
│   │   │   │   └── navigation/  # Compose Navigation Routes
│   │   │   └── utils/           # TokenManager, ValidationUtils
│   └── build.gradle.kts
│
├── backend/                      # Node.js + Express + PostgreSQL REST APIs
│   ├── src/
│   │   ├── controllers/         # Auth, Products, and AI Suggestion logic
│   │   ├── routes/              # Express API Routes
│   │   ├── middleware/          # JWT Verification & CORS
│   │   └── db/                  # PostgreSQL Schema, DDL & In-Memory Fallback
│   ├── package.json
│   └── .env
│
└── docs/                         # Technical Documentation & Deliverables
    ├── TECHNICAL_DOCUMENTATION.md # ER Diagram, Flow Diagram, Tech Stack
    ├── BidAI_Postman_Collection.json # Importable Postman Collection
    └── BID_AI_IMPLEMENTATION_PLAN.md
```

---

## 🚀 Quick Setup & Run Instructions

### 1. Start the Backend API Server
```bash
cd backend
npm install
npm start
```
- Server will run on: `http://localhost:5000`
- Android Emulator alias: `http://10.0.2.2:5000`
- Demo user credentials:
  - **Email:** `demo@bid.ai`
  - **Password:** `password123`

### 2. Run the Android App
1. Open the `/android` folder in **Android Studio**.
2. Sync Gradle dependencies.
3. Run on an Android Emulator or connected physical device.
4. Or build the APK via command line:
   ```bash
   cd android
   ./gradlew assembleDebug
   ```
   The APK will be generated at: `android/app/build/outputs/apk/debug/app-debug.apk`.

---

## 📬 Postman API Testing
Import `docs/BidAI_Postman_Collection.json` into Postman to test all endpoints (Auth, Feed, AI Auto-Fill, and Bidding).

---

## 📄 License & Confidentiality
Confidential property of Bid.ai assessment evaluation.
