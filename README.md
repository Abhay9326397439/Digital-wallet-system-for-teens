# Digital-wallet-system-for-teens

> 📋 Project Overview
TeenWallet Manager is a comprehensive Java Swing-based desktop application designed to help parents manage their teenagers' finances through a virtual wallet system. It provides a safe environment for teens to learn financial responsibility while giving parents complete control over spending limits and monitoring.

🎯 Purpose
Teach teenagers financial literacy

Provide parents with tools to monitor and control teen spending

Create a safe environment for learning money management

Encourage saving through goal-based incentives

👥 Target Users
Parents: Want to teach financial responsibility to their children

Teenagers (13-19 years): Learning to manage money

Financial Educators: Teaching tools for financial literacy programs

![License](https://img.shields.io/badge/license-MIT-green) ![Version](https://img.shields.io/badge/version-1.0.0-blue) ![Language](https://img.shields.io/badge/language-Java-yellow) ![GitHub](https://img.shields.io/badge/GitHub-Abhay9326397439/Digital-wallet-system-for-teens-black?logo=github) ![Build Status](https://img.shields.io/github/actions/workflow/status/Abhay9326397439/Digital-wallet-system-for-teens/ci.yml?branch=main)

## 📋 Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Tech Stack](#tech-stack)
- [File Structure](#custom-1773638722144)
- [Database Schema](#custom-1773638787742)

## ℹ️ Project Information

- **👤 Author:** Abhay9326397439
- **📦 Version:** 1.0.0
- **📄 License:** MIT
- **📂 Repository:** [https://github.com/Abhay9326397439/Digital-wallet-system-for-teens](https://github.com/Abhay9326397439/Digital-wallet-system-for-teens)
- **🏷️ Keywords:** 📝 Keywords General Keywords text teen wallet, parental control, allowance management, financial literacy, money management for teens, virtual wallet, parent teen banking, savings goals, spending limits, transaction tracking, financial education Technical Keywords text Java Swing, MySQL database, Maven project, desktop application, JFreeChart, CRUD operations, MVC architecture, DAO pattern, authentication system, role-based access, data persistence Feature Keywords text virtual debit card, spending categories, daily limits, weekly limits, goal tracking, progress bars, transaction history, monthly reports, CSV export, card lock/unlock, parent bonus, multi-teen management Business Keywords text financial literacy for teens, allowance management system, parental control software, teen banking simulation, educational finance app, money management for children

## Features

Key Features
1. User Management System
Dual Role Authentication: Parent and Teen separate logins

Multi-Teen Support: One parent can manage multiple teen accounts

User Registration: Self-registration with role selection

Profile Management: Update passwords and account settings

2. Virtual Wallet System
Real-time Balance: Instant balance updates after each transaction

Virtual Debit Card: Realistic card interface with tap-to-pay simulation

Transaction History: Complete record of all financial activities

Balance Tracking: Running balance after each transaction

3. Parental Controls
Spending Limits:

Daily spending limits (default ₹300)

Weekly spending limits (default ₹1500)

Category-wise limits (Food, Entertainment, etc.)

Card Lock/Unlock: Instantly freeze teen's card if needed

Money Addition: Add funds to any teen's wallet

Limit Management: Adjust limits per teen individually

4. Payment System
Category-based Spending: 6 predefined categories

Food

Entertainment

Education

Shopping

Transport

Others

Multi-level Validation:

Card status check

Balance sufficiency

Daily limit verification

Weekly limit verification

Category limit verification

Real-time Feedback: Clear success/error messages with remaining limits

5. Savings Goals
Goal Creation: Teens can create multiple savings goals

Progress Tracking: Visual progress bars with percentage

Deadline Management: Days remaining counter

Parent Bonuses: Parents can add bonus money to any goal

Automatic Alerts: Notifications at 80% and 100% completion

Color-coded Progress:

🔴 Red: Below 50%

🟠 Orange: 50-79%

🔵 Blue: 80-99%

🟢 Green: 100% (Completed)

6. Reporting & Analytics
Monthly Reports: Comprehensive spending analysis

Category-wise Charts: Bar charts using JFreeChart

Transaction Tables: Detailed transaction lists

Spending Breakdown: Percentage distribution by category

CSV Export: Download reports for external analysis

Limit Alerts: Warnings when approaching limits

7. Transaction History
Complete History: All transactions ever made

Search Functionality: Find transactions by keyword

Color Coding:

🟢 Green: Credits (money in)

🔴 Red: Debits (money out)

🟠 Orange: Savings transfers

Summary Statistics: Total credits, debits, and current balance

8. UI/UX Features
Modern Interface: Clean, professional design

Responsive Layout: Adapts to different screen sizes

Navigation System: Home and Back buttons on all screens

Visual Feedback: Color-coded status indicators

Tap Simulation: Interactive card with tap animation

Gradient Backgrounds: Modern visual appeal

9. Data Persistence
MySQL Database: Robust data storage

Transaction Records: All financial activities saved

User Settings: Preferences stored per user

Goal Tracking: Savings goals persist across sessions

Data Integrity: Foreign key constraints and validation

10. Security Features
Password Protection: Secure authentication

Role-based Access: Different features for parents/teens

Input Validation: All user inputs validated

Transaction Validation: Multiple checks before processing

Error Handling: Comprehensive error messages



## Installation

Requirement	Minimum	Recommended
Java	JDK 11	JDK 17
RAM	2 GB	4 GB
Disk Space	100 MB	500 MB
Database	MySQL 5.7	MySQL 8.0
Screen	1024x768	1920x1080

## Usage

Key Functionalities
Parent Operations
java
- Login to parent dashboard
- View all teens under management
- Add money to any teen's wallet
- Set/modify spending limits
- Lock/unlock teen cards
- View teen transaction history
- Give bonuses to savings goals
- Generate monthly reports
- Export data to CSV
- Manage teen accounts (add/remove)
Teen Operations
java
- Login to personal dashboard
- View virtual card and balance
- Make payments with category selection
- Create and manage savings goals
- Transfer money to goals
- View transaction history
- Check spending limits
- Generate personal reports
- Receive parent bonuses
- Track goal progress

## Tech Stack

Technology Stack
Component	Technology
Language	Java 11+
GUI Framework	Swing
Build Tool	Maven
Database	MySQL 8.0+
Charts	JFreeChart 1.5.3
IDE Support	IntelliJ IDEA, Eclipse, NetBeans

## File Structure

TeenWalletManager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── teenwallet/
│   │   │           ├── Main.java
│   │   │           │
│   │   │           ├── model/
│   │   │           │   ├── User.java
│   │   │           │   ├── Transaction.java
│   │   │           │   ├── SavingsGoal.java
│   │   │           │   └── UserSettings.java
│   │   │           │
│   │   │           ├── dao/
│   │   │           │   ├── UserDAO.java
│   │   │           │   ├── TransactionDAO.java
│   │   │           │   ├── SavingsGoalDAO.java
│   │   │           │   └── SettingsDAO.java
│   │   │           │
│   │   │           ├── service/
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── WalletService.java
│   │   │           │   └── ReportService.java
│   │   │           │
│   │   │           ├── ui/
│   │   │           │   ├── BaseFrame.java
│   │   │           │   ├── LoginFrame.java
│   │   │           │   ├── RegistrationFrame.java
│   │   │           │   ├── ParentDashboardFrame.java
│   │   │           │   ├── TeenDashboardFrame.java
│   │   │           │   ├── AddMoneyFrame.java
│   │   │           │   ├── PaymentFrame.java
│   │   │           │   ├── ReportsFrame.java
│   │   │           │   ├── GoalsFrame.java
│   │   │           │   ├── SettingsFrame.java
│   │   │           │   ├── TransactionHistoryFrame.java
│   │   │           │   │
│   │   │           │   └── components/
│   │   │           │       ├── VirtualCardPanel.java
│   │   │           │       ├── SideNavigationPanel.java
│   │   │           │       └── ButtonStyler.java
│   │   │           │
│   │   │           └── utils/
│   │   │               ├── DBConnection.java
│   │   │               ├── ExportUtils.java
│   │   │               └── DateUtils.java
│   │   │
│   │   └── resources/
│   │       ├── config.properties
│   │       └── data/
│   │
│   └── test/
│       └── java/
│
├── database/
│   └── schema.sql
│
├── pom.xml
├── README.md
└── .gitignore

## Database Schema

users: Parent and teen accounts

transactions: All wallet transactions

savings_goals: Teen savings goals

user_settings_per_teen: Individual teen settings

category_limits_per_teen: Category limits per teen

default_categories: Predefined spending categories

