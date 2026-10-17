# 🚀 Bid.ai (Android + Node.js + PostgreSQL) - 48-Hour Technical Implementation Plan

---

## 📌 1. Project Overview & Objective

- **Target Assignment:** Bid.ai Technical Evaluation (Android Kotlin App + Backend API).
- **Core Screens:** 
  1. **Login / Signup** (Auth with Validation & JWT Token persistence)
  2. **Home Screen** (Dynamic Product Auction Feed, Categories, Eco-Score, Search)
  3. **One-Click Sell** (Image Upload, AI-assisted auto-fill for title/price/category, form validation & real-time listing)
- **Backend:** Node.js (Express) + PostgreSQL (Database) + REST APIs.
- **Delivery Timeline:** 48 Hours.
- **Deliverables:**
  - 📱 Working APK (`bidai-app-debug.apk`)
  - 💻 Clean GitHub Repository (Modular Android + Backend)
  - 📄 Technical Documentation (ER Diagram, Flow Diagram, Tech Stack)
  - 📬 Postman Collection JSON with environment variables
  - 🎤 Google Meet Live Demo Presentation Deck/Guide

---

## ⏱️ 2. 48-Hour Milestone Execution Schedule

| Phase | Timeframe | Key Focus Areas | Deliverables |
|---|---|---|---|
| **Phase 1** | **Hours 0 – 6** | Architecture, Database setup, Backend Core | Express server, Cloud PostgreSQL DB, DB schema migrations |
| **Phase 2** | **Hours 6 – 14** | REST APIs, Auth, Product CRUD, AI Engine | Working APIs, Postman Collection, JWT Auth, AI pricing endpoint |
| **Phase 3** | **Hours 14 – 22** | Android Foundation & Auth Screen | Project scaffold (MVVM), Retrofit/OkHttp layer, Login/Register UI & Logic |
| **Phase 4** | **Hours 22 – 32** | Home Feed Screen & Auction Engine | Product listing feed, Category chips, Search bar, Bid card components |
| **Phase 5** | **Hours 32 – 40** | One-Click Sell Screen & Image Pipeline | Image selector, AI auto-fill simulation, form validation, POST flow |
| **Phase 6** | **Hours 40 – 48** | APK Build, Documentation & Demo Prep | Debug/Release APK, GitHub README, ER/Flow Diagrams, Live Demo practice |

---

## 🏗️ 3. System Architecture & Tech Stack

### Tech Stack Details

| Layer | Technology | Justification |
|---|---|---|
| **Mobile OS & Language** | **Android / Kotlin 2.0+** | Recommended standard, 100% Kotlin coroutines support. |
| **Android UI** | **Jetpack Compose + Material 3** | Modern declarative UI, makes Figma pixel-perfect UI 3x faster, eliminates XML boilerplate, native dark/light support. |
| **Architecture** | **MVVM (Model-View-ViewModel)** | Clean separation of concerns, testable, recommended by Google. |
| **Async & State** | **Kotlin Coroutines + StateFlow** | Reactive state handling without memory leaks. |
| **Networking** | **Retrofit 2 + Gson + OkHttp Logging** | Industry standard REST client with interceptors for JWT. |
| **Image Loading** | **Coil (Coroutine Image Loader)** | Lightweight, Compose-native image cache & loader. |
| **Backend** | **Node.js (Express.js)** | Ultra-fast to build, lightweight, easy to deploy or run locally. |
| **Database** | **PostgreSQL (Neon / Supabase or Local)** | Relational DB ensuring auction consistency, foreign keys, and remote testability on real devices. |
| **Authentication** | **JWT (JSON Web Tokens) + Bcrypt** | Secure stateless auth with Bearer tokens. |

---

## 🗄️ 4. Database Schema (PostgreSQL) & ER Structure

- **`users`**: `id (UUID)`, `full_name`, `email (UNIQUE)`, `phone`, `password_hash`, `bid_credits (default 100)`, `eco_score (default 0)`, `created_at`
- **`categories`**: `id (SERIAL)`, `name`, `icon_name`
- **`products`**: `id (UUID)`, `seller_id (FK users.id)`, `category_id (FK categories.id)`, `title`, `description`, `starting_price`, `current_bid`, `condition`, `image_url`, `estimated_eco_score`, `status`, `ends_at`, `created_at`
- **`bids`**: `id (UUID)`, `product_id (FK products.id)`, `bidder_id (FK users.id)`, `amount`, `created_at`

---

## 🌐 5. REST API Specifications

Base URL: `http://<HOST_IP>:5000/api`

### 1. Authentication Endpoints
- `POST /api/auth/register` (Full Name, Email, Password, Phone) -> JWT Token + User Object
- `POST /api/auth/login` (Email, Password) -> JWT Token + User Object
- `GET /api/auth/profile` (Headers: `Authorization: Bearer <token>`) -> User info, Credits, Eco Score

### 2. Products & Auctions Endpoints
- `GET /api/products` (Params: `category`, `search`, `sort`) -> List of Active Products
- `GET /api/products/:id` -> Single Product Details & Bid history
- `POST /api/products` (Headers: `Authorization: Bearer <token>`) -> Create New Listing
- `POST /api/products/:id/bid` (Headers: `Authorization: Bearer <token>`) -> Place a Bid

### 3. One-Click AI Sell Endpoint
- `POST /api/products/ai-suggest` (Simulates AI estimation: smart title, category, starting price, and eco score calculation)

---

## 📱 6. Android App Architecture & Screen Details

```
com.bidai.app/
│
├── data/
│   ├── api/             # Retrofit ApiService, AuthInterceptor
│   ├── model/           # Data models (User, Product, Bid, AuthResponse)
│   └── repository/      # AuthRepository, ProductRepository
│
├── di/                  # ServiceLocator / Dependency Provider
│
├── ui/
│   ├── theme/           # Color, Typography, Shape (Matching Figma)
│   ├── components/      # Common UI: BidCard, TopBar, CustomTextField, EcoBadge
│   ├── screens/
│   │   ├── auth/        # LoginScreen, RegisterScreen, AuthViewModel
│   │   ├── home/        # HomeScreen, HomeViewModel, CategoryFilterBar
│   │   └── sell/        # OneClickSellScreen, SellViewModel, AIModal
│   └── navigation/      # NavGraph, Screen routes
│
└── utils/               # TokenManager, ValidationUtils, DateFormatters
```

### Screen 1: Login & Register
- Form validation (Email regex, Password >= 6 characters).
- Token storage in `EncryptedSharedPreferences` / `DataStore`.
- Toggle between Login & Signup.
- Loading indicator & error banner.

### Screen 2: Home Screen
- Header: User greeting, Eco-score badge, Bid credits pill.
- Horizontal Category Chips (`All`, `Mobiles`, `Laptops`, `Electronics`, `Fashion`).
- Search Bar with instant filtering.
- Auction cards with product image, live badge, countdown timer, current bid, and "Bid" button.
- Floating / Bottom bar Action to "+ One-Click Sell".

### Screen 3: One-Click Sell Screen (Key Differentiator)
- Image picker with preview.
- "✨ Magic AI Auto-Fill" button that automatically estimates:
  - Product Title
  - Category
  - Recommended Starting Bid
  - Eco-Score points (+25 pts)
  - Auto-generated condition & specs description
- User editable inputs for adjustments.
- Instant submission to backend.
- Success animation/dialog redirecting to the Home feed.

---

## 🎯 7. Evaluator Presentation Strategy (15-Minute Google Meet Pitch)
1. **Architecture & Tech Choices (3 mins):** Why Kotlin Compose + MVVM, Node.js + PostgreSQL.
2. **Live Android Demo (6 mins):** Login flow -> Browse & search auctions -> **One-Click Sell flow with AI auto-fill** -> Verify listed item appears in Home feed.
3. **API & Database Inspection (3 mins):** Postman collection execution (all 200 OKs) + PostgreSQL schema.
4. **Code Quality & Q&A (3 mins):** Git commit log, clean project structure, coroutines error handling.
