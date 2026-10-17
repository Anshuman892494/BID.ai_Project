# 🚀 Bid.ai - AI-Powered Smart Selling & Live Auction Platform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-Jetpack%20Compose-green.svg?style=for-the-badge&logo=android)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-blue.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Node.js](https://img.shields.io/badge/Node.js-Express%20REST%20API-brightgreen.svg?style=for-the-badge&logo=nodedotjs)](https://nodejs.org)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg?style=for-the-badge&logo=postgresql)](https://www.postgresql.org)
[![Material 3](https://img.shields.io/badge/Design-Material%203%20%2B%20Figma-orange.svg?style=for-the-badge)](https://m3.material.io)

> **Bid.ai** is a full-stack mobile auction and re-commerce ecosystem engineered for ultra-fast, intelligent selling. Featuring pixel-perfect Figma screens, an MVVM Android architecture with Jetpack Compose, an Express + PostgreSQL backend, and an AI-driven **One-Click Sell** workflow.

---

## 📑 Table of Contents
1. [System Architecture](#-system-architecture)
2. [Android MVVM Architecture](#-android-mvvm-architecture)
3. [Database Schema & ER Diagram](#-database-schema--er-diagram)
4. [End-to-End Application Workflows](#-end-to-end-application-workflows)
   - [Workflow 1: Authentication & JWT Session](#1-authentication--session-lifecycle-workflow)
   - [Workflow 2: Live Auction Discovery & Bidding](#2-live-auction-discovery--bidding-workflow)
   - [Workflow 3: One-Click Sell & AI Auto-Fill](#3-one-click-sell--ai-auto-fill-workflow)
5. [Key Features & Highlights](#-key-features--highlights)
6. [Repository Structure](#-repository-structure)
7. [REST API Documentation](#-rest-api-documentation)
8. [Getting Started & Local Setup](#-getting-started--local-setup)
   - [Backend API Setup](#1-backend-api-setup)
   - [Android App Setup](#2-android-app-setup)
9. [Credentials & Testing Guide](#-credentials--testing-guide)

---

## 🏛️ System Architecture

Bid.ai is structured with a decoupled client-server architecture:

```mermaid
graph TD
    subgraph "Android Mobile Client (Kotlin)"
        UI[Jetpack Compose UI Screens]
        VM[MVVM ViewModels & StateFlow]
        REPO[Repository Layer]
        LOCAL[EncryptedSharedPreferences / TokenManager]
        NET[Retrofit 2 + OkHttp Interceptor]
        UI --> VM
        VM --> REPO
        REPO --> LOCAL
        REPO --> NET
    end

    subgraph "Network Boundary"
        NET -- "HTTPS / REST (JSON) + Bearer JWT" --> API[Express.js REST API]
    end

    subgraph "Backend Engine (Node.js)"
        API --> ROUTER[Express Routers]
        ROUTER --> MW[JWT Auth & CORS Middleware]
        MW --> CTRL[Business Controllers]
        CTRL --> AISERVICE[AI Recommendation Engine]
        CTRL --> DBDRIVER[Resilient Database Abstraction]
    end

    subgraph "Data Storage"
        DBDRIVER -- "Primary Connection" --> PG[(PostgreSQL Database)]
        DBDRIVER -- "Automatic Fallback" --> MEM[(In-Memory High-Speed Store)]
    end
```

---

## 📱 Android MVVM Architecture

The mobile application strictly adheres to the official Android Architecture Guidelines with uni-directional data flow (UDF):

```mermaid
graph LR
    subgraph "UI Layer"
        Screen[Composable Screens\nHomeScreen / OneClickSellScreen / LoginScreen]
        State[UI State StateFlow]
        Event[User Events / Intents]
    end

    subgraph "ViewModel Layer"
        VM[ViewModel\nHomeViewModel / SellViewModel / AuthViewModel]
        Scope[viewModelScope / Coroutines]
    end

    subgraph "Data Layer"
        Repo[Repository Layer\nProductRepository / AuthRepository]
        Remote[Retrofit ApiService]
        Local[TokenManager / Prefs]
    end

    Event -->|User clicks / inputs| VM
    VM -->|Launches Coroutines| Scope
    Scope -->|Invokes API / cache| Repo
    Repo -->|Network Calls| Remote
    Repo -->|Local Persistence| Local
    Remote -->|Result| Repo
    Repo -->|Domain Models| VM
    VM -->|Emits State| State
    State -->|Triggers Recomposition| Screen
```

- **Declarative UI:** Jetpack Compose with Material 3 tokens, custom royal blue curved headers (`#0B3B95`), dynamic category filters, and smooth state animations.
- **State Management:** Reactive `StateFlow` and `SharedFlow` for lifecycle-aware state consumption (`collectAsStateWithLifecycle`).
- **Network Resilience:** OkHttp client configured with automatic JWT authorization header injection and custom logging.
- **Image Pipeline:** Async image decoding, caching, and placeholder fallback with Coil.

---

## 🗄️ Database Schema & ER Diagram

```mermaid
erDiagram
    USERS ||--o{ PRODUCTS : "lists / sells"
    USERS ||--o{ BIDS : "places"
    CATEGORIES ||--o{ PRODUCTS : "classifies"
    PRODUCTS ||--o{ BIDS : "receives"

    USERS {
        uuid id PK "Unique User UUID"
        string full_name "Full Display Name"
        string email UK "Unique Login Email"
        string phone "Contact Mobile Number"
        string password_hash "Bcrypt Encrypted Hash"
        integer bid_credits "Bidding Reward Credits"
        integer eco_score "Green Resale Reputation Score"
        timestamp created_at "Registration Timestamp"
    }

    CATEGORIES {
        integer id PK "Category ID (1..15)"
        string name "Category Name (Mobiles, Bikes, Cars, etc.)"
        string icon_name "Android Drawable Resource Identifier"
    }

    PRODUCTS {
        uuid id PK "Unique Product UUID"
        uuid seller_id FK "References USERS(id)"
        integer category_id FK "References CATEGORIES(id)"
        string title "Product Name & Model"
        text description "Detailed Specs & Condition"
        decimal starting_price "Base / Starting Bid in INR"
        decimal current_bid "Highest Placed Bid in INR"
        string condition "Like New | Good | Fair"
        string image_url "Product Image URI / URL"
        integer estimated_eco_score "CO2 Savings / Eco Benefit"
        string status "ACTIVE | COMPLETED | CANCELLED"
        timestamp ends_at "Auction Expiration Timestamp"
        timestamp created_at "Creation Timestamp"
    }

    BIDS {
        uuid id PK "Unique Bid UUID"
        uuid product_id FK "References PRODUCTS(id)"
        uuid bidder_id FK "References USERS(id)"
        decimal amount "Offered Bid Amount (INR)"
        timestamp created_at "Bid Placement Timestamp"
    }
```

---

## 🔄 End-to-End Application Workflows

### 1. Authentication & Session Lifecycle Workflow

```mermaid
sequenceDiagram
    autonumber
    actor User as Mobile User
    participant App as Android Client
    participant TM as TokenManager
    participant API as Node.js Auth Controller
    participant DB as Database

    User->>App: Opens App
    App->>TM: Check for saved JWT Token
    alt Token Exists & Valid
        TM-->>App: Return Token
        App->>User: Directly display Home Feed
    else No Token Found
        App->>User: Display Login / Signup Screen
        User->>App: Submits Email / Phone + Password
        App->>API: POST /api/auth/login
        API->>DB: Query user & verify Bcrypt password
        DB-->>API: User Record
        API-->>App: 200 OK + JWT Token + Profile Data
        App->>TM: Persist Token securely
        App->>User: Navigate to Home Screen
    end
```

---

### 2. Live Auction Discovery & Bidding Workflow

```mermaid
sequenceDiagram
    autonumber
    actor User as Buyer / Bidder
    participant UI as HomeScreen UI
    participant VM as HomeViewModel
    participant API as Products API
    participant DB as Database

    User->>UI: Selects Category (e.g. "Mobiles") or types search query
    UI->>VM: Trigger filter / query update
    VM->>API: GET /api/products?category=Mobiles&search=iPhone
    API->>DB: Query active auctions matching filters
    DB-->>API: Filtered active products list
    API-->>VM: 200 OK (Products JSON)
    VM-->>UI: Update StateFlow (Recompose Auction Grid)
    UI->>User: Render Auction Cards with countdowns & Eco-Score
    User->>UI: Clicks Product -> Submits Higher Bid (₹58,000)
    UI->>API: POST /api/products/:id/bid (Bearer JWT)
    API->>DB: Validate bid > current_bid & insert into BIDS
    DB-->>API: Bid Accepted
    API-->>UI: 200 OK (Updated current_bid & bids_count)
    UI->>User: Success confirmation & instant card price update
```

---

### 3. One-Click Sell & AI Auto-Fill Workflow

```mermaid
sequenceDiagram
    autonumber
    actor Seller as Seller
    participant SellUI as OneClickSellScreen
    participant VM as SellViewModel
    participant API as Backend AI Engine
    participant DB as Database

    Seller->>SellUI: Clicks "+ One-Click Sell"
    Seller->>SellUI: Selects up to 5 photos from Gallery
    SellUI->>SellUI: Render multi-photo previews with delete badge
    Seller->>SellUI: Types quick prompt (e.g. "iPhone 14 Pro 128GB")
    Seller->>SellUI: Clicks "✨ Magic Auto-Fill with AI"
    SellUI->>API: POST /api/products/ai-suggest
    Note over API: AI Engine evaluates device specs,<br/>calculates market starting price (₹56,000),<br/>generates specs & sets Eco-Score (+75)
    API-->>VM: Suggested Product Data JSON
    VM-->>SellUI: Auto-populate Title, Category, Price, Condition & Specs
    Seller->>SellUI: Reviews / tweaks parameters -> Clicks "Upload"
    SellUI->>API: POST /api/products (Multipart / JSON + Bearer JWT)
    API->>DB: Insert new ACTIVE product into catalog
    DB-->>API: Product Created
    API-->>SellUI: 201 Created
    SellUI->>Seller: Display Success Dialog & Confetti Animation
    SellUI->>SellUI: Navigate back & refresh Home Feed
```

---

## ✨ Key Features & Highlights

- **15 Native Category Filters:** Integrated with high-resolution vector assets:
  - *Mobiles, Electronics, Cars, Bikes, Clothing, Home & Garden, Books, Beauty, Art & Craft, Real Estate, Services, Jobs, Agriculture, General Science, and Yodha Special.*
- **Multi-Image Upload & Preview:** Pick multiple images from device storage with live carousel preview and instant removal capability.
- **AI-Powered Product Estimation:** Dynamic market valuation and automated description generation for quick listings.
- **Eco-Score & Circular Economy:** Every pre-owned transaction computes environmental score points, promoting sustainable commerce.
- **Resilient Offline / Memory Mode:** The backend automatically falls back to an internal data store if cloud PostgreSQL is unreachable, guaranteeing 100% demo uptime.

---

## 📂 Repository Structure

```
BID.ai/
├── android/                               # Native Android Kotlin Project
│   ├── app/
│   │   ├── src/main/java/com/bidai/app/
│   │   │   ├── data/
│   │   │   │   ├── api/                  # Retrofit ApiService & OkHttp Client
│   │   │   │   ├── model/                # Data Classes (User, Product, Bid, Category)
│   │   │   │   └── repository/           # AuthRepository & ProductRepository
│   │   │   ├── ui/
│   │   │   │   ├── components/           # Reusable Compose Widgets (AuctionCard, EcoBadge)
│   │   │   │   ├── navigation/           # Compose Navigation Routes & NavHost
│   │   │   │   ├── screens/              # Core Screen Implementations
│   │   │   │   │   ├── auth/             # Login & Signup Screens
│   │   │   │   │   ├── home/             # Home Auction Feed & Search
│   │   │   │   │   └── sell/             # One-Click Sell & AI Auto-Fill Screen
│   │   │   │   └── theme/                # Royal Blue Theme, Typography & Colors
│   │   │   └── utils/                    # TokenManager & Form Validators
│   │   └── src/main/res/
│   │       └── drawable/                 # 15 Category Icons & Graphic Assets
│   ├── build.gradle.kts
│   └── settings.gradle.kts
│
├── backend/                               # Node.js + Express REST API Server
│   ├── src/
│   │   ├── controllers/                  # authController.js & productsController.js
│   │   ├── db/                           # postgres.js, schema.sql & in-memory fallback
│   │   ├── middleware/                   # JWT verification & Error handlers
│   │   ├── routes/                       # auth.js & products.js
│   │   └── server.js                     # Express App Initialization & Port Binding
│   ├── package.json
│   └── .env
│
├── .gitignore                             # Git ignore rules for Android & Node
└── README.md                              # Complete System Documentation
```

---

## 📡 REST API Documentation

Base URL: `http://localhost:5000/api` (Android Emulator: `http://10.0.2.2:5000/api`)

| Method | Endpoint | Auth | Description | Sample Request / Query |
|---|---|:---:|---|---|
| `GET` | `/health` | No | Health check & server status | `GET /api/health` |
| `POST` | `/auth/register` | No | Register new user account | `{"fullName":"Anshuman","email":"anshu@bid.ai","password":"password123","phone":"+919876543210"}` |
| `POST` | `/auth/login` | No | Authenticate user & get JWT | `{"email":"demo@bid.ai","password":"password123"}` |
| `GET` | `/auth/profile` | **Yes** | Retrieve user profile & credits | Header: `Authorization: Bearer <token>` |
| `GET` | `/products/categories` | No | Fetch all 15 auction categories | `GET /api/products/categories` |
| `GET` | `/products` | No | Live auction feed with filters | `?category=Mobiles&search=iPhone&sort=endingSoon` |
| `GET` | `/products/:id` | No | Get single product + bid history | `GET /api/products/3f4e5a...` |
| `POST` | `/products/ai-suggest` | No | Smart price & spec suggestion | `{"query":"Samsung Galaxy S23 Ultra 256GB"}` |
| `POST` | `/products` | **Yes** | Create new auction listing | `{"title":"iPhone 14 Pro","startingPrice":56000,"categoryId":2,"condition":"Like New"}` |
| `POST` | `/products/:id/bid` | **Yes** | Place a higher bid | `{"amount": 58000}` |

---

## 🚀 Getting Started & Local Setup

### 1. Backend API Setup

**Prerequisites:** Node.js v18+ installed.

```bash
# 1. Navigate to the backend directory
cd backend

# 2. Install dependencies
npm install

# 3. Configure environment variables (optional: default settings work out-of-the-box)
# .env is pre-configured with resilient database options

# 4. Start the server
npm start
```

The server will launch at `http://localhost:5000` with the following output:
```text
🚀 Bid.ai API Server running on port 5000
👉 Local Health Check: http://localhost:5000/api/health
```

---

### 2. Android App Setup

**Prerequisites:** Android Studio Ladybug / Meerkat or newer with Android SDK 34+.

1. Open **Android Studio**.
2. Select **File -> Open...** and select the `BID.ai/android` directory.
3. Wait for Gradle Sync to complete.
4. Run on an **Android Virtual Device (AVD)** (Emulator) or a physical Android device with USB Debugging enabled.
   > **Note on Emulator Networking:** The Android app automatically routes requests to `http://10.0.2.2:5000` when running in the emulator to communicate seamlessly with your local Node.js backend.

To build the debug APK via CLI:
```bash
cd android
./gradlew assembleDebug
```
The compiled APK will be located at:
`android/app/build/outputs/apk/debug/app-debug.apk`

---

## 🔑 Credentials & Testing Guide

For quick evaluation and testing, the application comes with built-in test accounts:

| Role | Email / Identifier | Password | Access Level |
|---|---|---|---|
| **Demo User** | `demo@bid.ai` | `password123` | Full Buyer / Seller Access |
| **Test Admin** | `admin@bid.ai` | `admin123` | Full Access + Elevated Eco Score |

### Quick Smoke Test Checklist:
- [x] **Login:** Login with `demo@bid.ai` / `password123` or register a new account.
- [x] **Feed Discovery:** Browse items, switch between categories, and search keywords like `iPhone` or `MacBook`.
- [x] **Bidding:** Open any active auction card and place a bid higher than the current price.
- [x] **AI Auto-Fill:** Navigate to the **One-Click Sell** tab, select photos, click **Magic Auto-Fill with AI**, and upload your product to watch it appear immediately on the Home screen.

---

## 📄 License & Assessment Note
This codebase is developed as part of the technical evaluation for **Bid.ai**. All rights reserved.
