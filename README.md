# 🌾 Kisanbajar (किसानबाजार)
**Direct Farmer-to-Consumer (D2C) Agri-Marketplace** | *Built by KRUTI Labs*

**Kisanbajar** is a multilingual (**Marathi, Hindi, English**) Android app that connects farmers directly with consumers, removing middlemen so farmers get 100% profit and buyers get fresh produce at fair prices.

---

## ✨ Key Features
- 👨‍🌾 **Farmer Portal:** Upload crop photos, set price (`₹/kg`) & stock (`kg`), manage buyer orders, and call buyers directly.
- 🛒 **Consumer Portal:** Filter crops by village (`गाव निवडा`), enter custom kg quantity with auto price calculation, and place direct orders.
- 📊 **Live APMC Mandi Rates:** View daily market prices (`₹/Quintal`) across major mandis (Pune, Nashik, Lasalgaon, Nagpur, etc.).
- 🌐 **3 Languages:** Instant switch between **मराठी**, **हिंदी**, and **English**.
- 🔐 **OTP Login & Cloud Sync:** 6-digit Mobile OTP login with offline **Room SQLite** storage + live **Supabase (PostgreSQL)** cloud sync.

---

## 🛠️ Tech Stack
- **Frontend (UI):** Jetpack Compose (Material Design 3), Coil
- **Backend Logic:** Java & Kotlin (MVVM Architecture)
- **Database:** Android Room (Local SQLite) + Supabase REST API (Cloud PostgreSQL)
- **Authentication:** 6-Digit Mobile OTP (`SmsManager` / Fast2SMS)

---

## 🚀 How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/kisanbajar.git
   ```
2. *(Optional)* Add your keys in `.env` (see `.env.example`) and run `SUPABASE_SETUP.sql` in Supabase SQL Editor for live cloud sync.
3. Open in **Android Studio** and click **Run ▶**.

---
**Developed by KRUTI Labs**
