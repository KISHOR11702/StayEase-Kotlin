# StayEase: A Smarter Way to Manage Hostel Dining & Leave

StayEase is a smart, mobile-based hostel management system that streamlines the daily operations of hostel dining and student leave management. It is designed to benefit both students and administrators through digitized preorder meals, leave tracking, QR-based validation, and push notifications.

---

## 📱 Features

### 👩‍🎓 Student Side (Android App - Kotlin)
- 🔐 **Login/Signup**
  - Secure Firebase Authentication
  - Profile includes Name, Email, Hostel Block, Room No.

- 🍽️ **Food Preorder Module**
  - Weekly meal menu displayed as image cards (uploaded by admin)
  - Students can select quantity (up to 5) and preorder meals
  - QR code generated after successful preorder
  - View current preorder + order history

- 📅 **Leave Application**
  - Students can apply for leave specifying dates and reasons
  - Admin can approve/reject leave requests

- 🔔 **Push Notifications**
  - Notifications for preorder deadlines
  - Sent every 6 hours before deadline (1 day prior)

- 📊 **Dashboard**
  - Overview of upcoming meals and leave status
  - Displays daily menu preview

---

### 🧑‍💼 Admin Panel (React.js + Firebase)
- 🔐 **Admin Login**
  - Secured with Firebase Authentication

- 📤 **Upload Preorder Menu**
  - Add weekly food items with images via Cloudinary
  - Set dates and meal types (breakfast/lunch/dinner)

- 📈 **View Preorder Summary**
  - See total quantity of each item ordered by students
  - View detailed list of students who preordered

- 📝 **Manage Leave Requests**
  - Review, approve, or reject leave requests

---

## 💾 Tech Stack

| Layer        | Technology                        |

|--------------|-----------------------------------|
| Frontend     | Kotlin (Android),HTML (Admin)|
| Backend      | Firebase (Firestore, Auth, FCM)   |
| Database     | Firebase Firestore                |
| Storage      | Cloudinary (for food images)      |
| Notifications| Firebase Cloud Messaging (FCM)    |

---

## 🚀 Getting Started

### For Android App
1. Open `/mobile-app` in Android Studio
2. Add your `google-services.json` from Firebase
3. Sync Gradle and run on emulator/device

