# 📑 Bid.ai - Technical Documentation & Architecture

This document provides a concise and comprehensive technical overview of the **Bid.ai** Mobile App (Android Kotlin) and Backend (Node.js + PostgreSQL).

---

## 🛠️ 1. Tech Stack Used

### 📱 Android Mobile Client
- **Language:** Kotlin 2.0+
- **UI Toolkit:** Jetpack Compose + Material 3 Design System
- **Architecture Pattern:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Reactive State Management:** Kotlin Coroutines + `StateFlow` / `SharedFlow`
- **Networking:** Retrofit 2 + OkHttp3 with JWT Authorization Interceptor
- **JSON Serialization:** Google Gson / Kotlinx Serialization
- **Image Pipeline:** Coil (Coroutine Image Loader) with memory & disk caching
- **Local Persistence:** EncryptedSharedPreferences / Jetpack DataStore

### 💻 Backend API Engine
- **Runtime:** Node.js v24+
- **Web Framework:** Express.js 4.x
- **Database:** PostgreSQL (Cloud Neon.tech or Local PG) with resilient In-Memory Driver
- **Authentication:** JWT (JSON Web Tokens) with 7-day expiration + Bcrypt password hashing
- **Security & Headers:** CORS, parameter sanitization, and structured error boundaries

---

## 🗄️ 2. Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    USERS ||--o{ PRODUCTS : "lists / sells"
    USERS ||--o{ BIDS : "places"
    CATEGORIES ||--o{ PRODUCTS : "contains"
    PRODUCTS ||--o{ BIDS : "receives"

    USERS {
        uuid id PK "Unique User ID"
        string full_name "User's display name"
        string email UK "Unique Login Email"
        string phone "Phone number with country code"
        string password_hash "Bcrypt hashed password"
        integer bid_credits "Reward credits for bidding"
        integer eco_score "Reputation score from reselling"
        timestamp created_at "Account creation timestamp"
    }

    CATEGORIES {
        integer id PK "Category ID (1..N)"
        string name "Category name (Mobiles, Laptops, etc.)"
        string icon_name "Material icon identifier"
    }

    PRODUCTS {
        uuid id PK "Unique Product ID"
        uuid seller_id FK "References USERS(id)"
        integer category_id FK "References CATEGORIES(id)"
        string title "Product Title"
        text description "Detailed description & condition"
        decimal starting_price "Initial starting bid in INR"
        decimal current_bid "Highest active bid in INR"
        string condition "Like New | Good | Fair"
        string image_url "High-resolution product image"
        integer estimated_eco_score "Calculated environmental benefit"
        string status "ACTIVE | COMPLETED | CANCELLED"
        timestamp ends_at "Auction expiration timestamp"
        timestamp created_at "Listing timestamp"
    }

    BIDS {
        uuid id PK "Unique Bid ID"
        uuid product_id FK "References PRODUCTS(id)"
        uuid bidder_id FK "References USERS(id)"
        decimal amount "Bid offer amount in INR"
        timestamp created_at "Bid timestamp"
    }
```

---

## 🔄 3. Flow Diagram (User Journey & Architecture Flow)

```mermaid
flowchart TD
    subgraph "Authentication Flow"
        A[Launch Bid.ai App] --> B{JWT Token Stored?}
        B -- No --> C[Login / Register Screen]
        C --> D[Submit Form with Validation]
        D --> E[POST /api/auth/login or register]
        E --> F[Receive & Save JWT Token]
        F --> G[Home Screen]
        B -- Yes --> G
    end

    subgraph "Auction Discovery Flow"
        G --> H[GET /api/products]
        H --> I[Render Auction Cards with Live Countdown]
        G --> J[Filter by Category: Mobiles, Laptops, etc.]
        G --> K[Live Search Query]
        I --> L[Click Item -> View Details & Bid History]
        L --> M[POST /api/products/:id/bid]
        M --> N[Real-time Current Bid Update]
    end

    subgraph "One-Click Sell AI Flow"
        G --> O[Click '+ One-Click Sell' Floating Button]
        O --> P[One-Click Sell Screen]
        P --> Q[Pick Image from Gallery or Camera]
        Q --> R[Click '✨ Magic Auto-Fill with AI']
        R --> S[POST /api/products/ai-suggest]
        S --> T[Auto-populate: Title, Category, Starting Price, Eco-Score]
        T --> U[User Reviews / Edits Details]
        U --> V[Click 'Launch Auction Now']
        V --> W[POST /api/products]
        W --> X[Success Dialog & Confetti + Seller Eco-Score +25]
        X --> G
    end
```

---

## 📡 4. REST API Endpoints Table

| Method | Endpoint | Access | Description | Sample Payload / Response |
|---|---|---|---|---|
| `GET` | `/api/health` | Public | Server uptime & status | `{"status":"healthy","uptime":...}` |
| `POST` | `/api/auth/register` | Public | Create new user account | `{"fullName":"..","email":"..","password":".."}` |
| `POST` | `/api/auth/login` | Public | Authenticate user & get JWT | `{"email":"..","password":".."}` |
| `GET` | `/api/auth/profile` | Bearer Token | Fetch user's credits & eco score | `{"data":{"fullName":"..","bidCredits":100}}` |
| `GET` | `/api/products/categories` | Public | Fetch all available categories | `[{"id":2,"name":"Mobiles"}]` |
| `GET` | `/api/products` | Public | Live auctions with search/filters | `?category=Mobiles&search=iPhone` |
| `GET` | `/api/products/:id` | Public | Full product details + bids | `{"data":{ "title":"..", "bids":[..] }}` |
| `POST` | `/api/products` | Bearer Token | List product ("One-Click Sell") | `{"title":"..","startingPrice":45000}` |
| `POST` | `/api/products/:id/bid` | Bearer Token | Submit higher bid for auction | `{"amount": 48000}` |
| `POST` | `/api/products/ai-suggest` | Public | Auto-fill smart price & details | `{"query":"iPhone 14"}` |

---

## 🧪 5. Testing with Postman

1. Open Postman.
2. Click **Import** and select `docs/BidAI_Postman_Collection.json`.
3. Set the `baseUrl` variable to `http://localhost:5000` (or `http://10.0.2.2:5000` if testing from Android Emulator).
4. Run the requests in order. The **Auth - Login** request automatically stores the returned JWT token into the collection's `authToken` variable!
