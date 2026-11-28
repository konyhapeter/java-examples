# Airport Management System - Frontend Documentation Index

Welcome! This folder contains a complete Angular 18 frontend application for managing airport operations.

## 📚 Documentation Guide

### For First-Time Users
Start here if you're new to this project:

1. **[GETTING_STARTED.md](./GETTING_STARTED.md)** ⭐ **START HERE**
   - Installation instructions
   - System requirements
   - Running the application
   - Troubleshooting common issues
   - Typical workflow examples

2. **[README.md](./README.md)**
   - Project overview
   - Feature list
   - Technology stack
   - Project structure
   - API endpoints reference

### For Developers
These documents help understand the codebase:

3. **[PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md)**
   - Complete directory layout
   - File organization
   - Component descriptions
   - Architecture overview
   - Configuration details

4. **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)**
   - Quick lookup guide
   - Common tasks & commands
   - API endpoints table
   - Status values & colors
   - Code examples

### Project Summary
5. **[COMPLETION_SUMMARY.md](./COMPLETION_SUMMARY.md)**
   - What's included
   - Project highlights
   - Feature checklist
   - Next steps

---

## 🚀 Quick Start (3 Steps)

```bash
# 1. Install dependencies
npm install

# 2. Start development server
npm start

# 3. Open in browser
# http://localhost:4200
```

---

## 📂 Project Structure

```
airport-app/
├── src/
│   ├── app/
│   │   ├── core/            ← Services & Models
│   │   ├── shared/          ← Reusable components
│   │   ├── features/        ← Feature pages (Dashboard, Flights, etc.)
│   │   └── app.*            ← Root configuration
│   └── styles.scss          ← Global styles
├── package.json             ← Dependencies
├── angular.json             ← Build config
├── proxy.conf.json          ← Backend connection
└── Documentation files...
```

---

## 🎯 Available Features

| Feature | Access | Purpose |
|---------|--------|---------|
| Dashboard | `/` | Overview & statistics |
| Flights | `/flights` | Manage flights |
| Aircraft | `/aircraft` | Manage aircraft |
| Passengers | `/passengers` | Manage passengers |
| Bookings | `/bookings` | Manage bookings |

---

## 🔧 Available Commands

```bash
npm start              # Start dev server (http://localhost:4200)
npm run build          # Build for production
npm run watch          # Build and watch for changes
npm test               # Run unit tests
```

---

## 📋 Checklist for Getting Started

- [ ] Read GETTING_STARTED.md
- [ ] Run `npm install`
- [ ] Verify backend is running on localhost:8080
- [ ] Run `npm start`
- [ ] Navigate to http://localhost:4200
- [ ] Create test data (aircraft, flights, passengers, bookings)
- [ ] readme.Test all CRUD operations
- [ ] Review PROJECT_STRUCTURE.md for code organization

---

## 💡 Common Questions

**Q: Where do I start?**
A: Read GETTING_STARTED.md and run `npm install` && `npm start`

**Q: How do I connect to the backend?**
A: Backend must run on localhost:8080. Update proxy.conf.json if different.

**Q: Where are the API calls?**
A: See `src/app/core/services/api.service.ts`

**Q: How do I add a new feature?**
A: Create a new folder in `src/app/features/`, create a component, add route.

**Q: Where is the styling?**
A: Global styles in `src/styles.scss`, component styles in component files.

**Q: What if something doesn't work?**
A: Check GETTING_STARTED.md troubleshooting section or browser console (F12).

---

## 🔗 Key Files

| File | Purpose |
|------|---------|
| `package.json` | Dependencies and scripts |
| `angular.json` | Angular CLI configuration |
| `proxy.conf.json` | Backend API proxy settings |
| `src/main.ts` | Application entry point |
| `src/app/app.component.ts` | Root component |
| `src/app/app.routes.ts` | Route definitions |
| `src/app/core/services/api.service.ts` | API client |
| `src/app/core/models/index.ts` | Data models |

---

## 🌐 Backend Integration

The app connects to: **http://localhost:8080**

Configured endpoints:
- `/api/flights` - Flight CRUD
- `/api/aircraft` - Aircraft CRUD  
- `/api/passengers` - Passenger CRUD
- `/api/bookings` - Booking CRUD

---

## 📝 Development Workflow

1. **Understand**: Read documentation (start with GETTING_STARTED.md)
2. **Setup**: Run `npm install` && `npm start`
3. **Create Data**: Add test aircraft, flights, passengers
4. **readme.Test Features**: Create bookings, test CRUD operations
5. **Develop**: Modify components in `src/app/`
6. **Build**: Run `npm run build` for production

---

## 🎓 Learning Resources

- **Angular Documentation**: https://angular.io
- **TypeScript Documentation**: https://www.typescriptlang.org
- **RxJS Documentation**: https://rxjs.dev
- **SCSS Documentation**: https://sass-lang.com

---

## 📞 Support

If you get stuck:
1. Check the relevant documentation file
2. Review GETTING_STARTED.md troubleshooting section
3. Open browser console (F12) for error messages
4. Check if backend API is running

---

## ✅ Project Status

✅ **Complete and Ready to Use**

- Angular 18 application with standalone components
- Full CRUD functionality for all entities
- Backend integration configured
- Comprehensive documentation included
- Professional UI with responsive design
- Production-ready code quality

---

## 📄 Document Overview

| Document | Length | Best For |
|----------|--------|----------|
| GETTING_STARTED.md | 5-10 min read | First-time users, setup |
| README.md | 5 min read | Overview, features, tech stack |
| PROJECT_STRUCTURE.md | 10 min read | Developers, code organization |
| QUICK_REFERENCE.md | 3 min read | Quick lookup, commands |
| COMPLETION_SUMMARY.md | 5 min read | Project summary, highlights |
| INDEX.md (this file) | 2 min read | Navigation guide |

---

## 🎉 You're All Set!

The application is complete and ready to use. Start with **GETTING_STARTED.md** and you'll be up and running in minutes!

---

**Created**: 2026-11-24  
**Technology**: Angular 18 + TypeScript 5.4  
**Backend**: Spring Boot on localhost:8080  

✈️ **Happy Flying!**
