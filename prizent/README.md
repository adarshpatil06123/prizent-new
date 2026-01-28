# Prizent - Brand Management System

A full-stack application for managing brands with a React TypeScript frontend and Spring Boot microservices backend.

## Project Structure

```
prizent/
├── backend/                    # Spring Boot Microservices
│   ├── admin-service/         # Brand management service (Port 8082)
│   ├── identity-service/      # Authentication service (Port 8081)
│   ├── api-gateway/          # API Gateway (Port 8080)
│   ├── product-service/      # Product management service
│   └── pricing-service/      # Pricing management service
└── prizent/                   # React TypeScript Frontend
    ├── src/
    │   ├── components/        # Reusable components
    │   ├── contexts/         # React contexts (Auth, etc.)
    │   ├── hooks/           # Custom hooks
    │   ├── pages/           # Page components
    │   ├── services/        # API service layer
    │   └── superadmin/      # Super admin functionality
    ├── public/              # Static assets
    └── package.json
```

## Technology Stack

### Frontend
- **React 18** with TypeScript
- **React Router DOM** for navigation
- **Custom hooks** for state management
- **CSS Modules** for styling
- **JWT Authentication** with context API

### Backend
- **Spring Boot 3.2.2** (Java 17)
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **MySQL Database** with HikariCP connection pooling
- **Maven** for dependency management
- **Microservices Architecture**

## Features

### Brand Management
- ✅ View all brands with pagination
- ✅ Add new brands
- ✅ Delete brands with confirmation
- ✅ Tenant-isolated data access
- ✅ Role-based access control

### Authentication & Authorization
- ✅ JWT-based authentication
- ✅ User login/logout
- ✅ Protected routes
- ✅ Role-based authorization (ADMIN, SUPER_ADMIN)
- ✅ Client-tenant isolation

### User Management
- ✅ Super admin user management
- ✅ Add/Edit users
- ✅ User role assignment

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Backend Setup

1. **Start MySQL** and create database:
   ```sql
   CREATE DATABASE prizent_db;
   ```

2. **Configure application.yml** in each service with your database credentials.

3. **Start Identity Service** (Port 8081):
   ```bash
   cd backend/identity-service
   mvn spring-boot:run
   ```

4. **Start Admin Service** (Port 8082):
   ```bash
   cd backend/admin-service
   mvn spring-boot:run
   ```

5. **Start API Gateway** (Port 8080):
   ```bash
   cd backend/api-gateway
   mvn spring-boot:run
   ```

### Frontend Setup

1. **Install dependencies**:
   ```bash
   cd prizent
   npm install
   ```

2. **Start development server**:
   ```bash
   npm start
   ```

3. **Access application**:
   - Frontend: http://localhost:3000
   - API Gateway: http://localhost:8080

## API Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout

### Brand Management
- `GET /api/brands` - Get all brands
- `POST /api/brands` - Create new brand
- `DELETE /api/brands/{id}` - Delete brand

### User Management
- `GET /api/users` - Get all users (Super Admin)
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user

## Database Schema

### Key Tables
- `p_brands` - Brand information with client isolation
- `users` - User accounts with roles
- `clients` - Tenant/client information

## Security

- **JWT Tokens** for stateless authentication
- **Role-based access control** (RBAC)
- **Tenant isolation** via client_id
- **CORS** configured for frontend integration
- **Password hashing** with BCrypt

## Development

### Code Organization
- **Service Layer** - Business logic
- **Controller Layer** - REST API endpoints
- **Repository Layer** - Data access with JPA
- **Security Layer** - JWT authentication and authorization
- **DTO Pattern** - Data transfer objects for API

### Key Components
- `UserPrincipal` - Custom authentication principal
- `JwtAuthenticationFilter` - Request authentication
- `useBrands` - React hook for brand operations
- `useAuth` - React hook for authentication
- `AuthContext` - Authentication state management

## Troubleshooting

### Common Issues
1. **Port conflicts** - Ensure ports 8080, 8081, 8082 are available
2. **Database connection** - Check MySQL credentials and database exists
3. **CORS errors** - Verify frontend URL in backend CORS configuration
4. **JWT errors** - Check token expiration and secret key consistency

### Logs
- Backend logs available in console output
- Frontend console for debugging

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## License

This project is proprietary and confidential.