# 🏪 Shop Manager – Android App

## Features
- **Multi-Shop Support** – Create/select shops at launch
- **Create Bill** – Add customer name, search & add products with qty control, auto-total, generate PDF, share via WhatsApp or any app
- **Draft Bills** – Save in-progress bills as drafts, convert to final later
- **Products/Parts** – Add with name, type, buy price, sell price. Filter by type chips, search bar
- **Progress** – Weekly / Monthly / Yearly bar charts for Sales & Profit. Summary cards for current month
- **Bills** – Tabs: This Month / Saved / Drafts / All. Filter by date range or month. Download any bill as PDF

## Tech Stack
- **Language**: Kotlin
- **Architecture**: MVVM + Repository
- **Database**: Room (SQLite) – 100% offline, stored locally on device
- **PDF**: Android PdfDocument API (no library needed)
- **Charts**: MPAndroidChart
- **UI**: Material Design 3, Navigation Drawer, ViewBinding

## Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK API 26+

### Steps
1. **Open in Android Studio**: File → Open → select `ShopManager` folder
2. Let Gradle sync complete (it downloads dependencies automatically)
3. **Build APK**: Build → Build Bundle(s)/APK(s) → Build APK(s)
4. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`
5. Transfer APK to your Android device and install

### OR Build via Command Line
```bash
cd ShopManager
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

## Database Location
Room database is stored at: `/data/data/com.shopmanager/databases/shop_manager_db`
PDF bills are saved to: `/Android/data/com.shopmanager/files/`

## App Flow
```
Launch → ShopSelectionActivity
            ├── Create new shop
            └── Select existing shop
                    └── MainActivity (Drawer Navigation)
                            ├── Create Bill
                            ├── Progress (Charts)
                            ├── Products / Parts
                            └── Bills
```
