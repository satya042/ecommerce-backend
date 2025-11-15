# User Service

A comprehensive user management microservice for the e-commerce application with JWT authentication, built using Spring Boot.

## Features

- **User Registration & Authentication**: Complete user registration and login with JWT tokens
- **JWT Security**: Stateless authentication using JSON Web Tokens
- **Password Management**: Secure password hashing with BCrypt and password reset functionality
- **User Profile Management**: Update user profiles and change passwords
- **Admin Operations**: User management for administrators
- **Data Validation**: Comprehensive input validation using Bean Validation
- **Exception Handling**: Global exception handling with proper HTTP status codes
- **CORS Support**: Cross-origin resource sharing configuration

## API Endpoints

### Authentication Endpoints

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "fullname": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Password123!",
  "gender": "MALE",
  "avatar": "https://example.com/avatar.jpg",
  "phone": "+1234567890"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "Password123!"
}
```

#### Logout
```
POST /api/auth/logout
Authorization: Bearer <token>
```

### User Profile Endpoints

#### Get Current User Profile
```
GET /api/users/profile
Authorization: Bearer <token>
```

#### Update Current User Profile
```
PUT /api/users/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullname": "John Smith",
  "email": "johnsmith@example.com",
  "gender": "MALE",
  "avatar": "https://example.com/new-avatar.jpg",
  "phone": "+1234567891"
}
```

#### Change Password
```
PUT /api/users/change-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword123!",
  "confirmPassword": "NewPassword123!"
}
```

#### Delete Current User
```
DELETE /api/users/profile
Authorization: Bearer <token>
```

### Admin Endpoints

#### Get All Users
```
GET /api/users
Authorization: Bearer <admin-token>
```

#### Get User by ID
```
GET /api/users/{userId}
Authorization: Bearer <admin-token>
```

#### Update User by ID
```
PUT /api/users/{userId}
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "fullname": "Updated Name",
  "email": "updated@example.com"
}
```

#### Delete User by ID
```
DELETE /api/users/{userId}
Authorization: Bearer <admin-token>
```

#### Enable User
```
PUT /api/users/{userId}/enable
Authorization: Bearer <admin-token>
```

#### Disable User
```
PUT /api/users/{userId}/disable
Authorization: Bearer <admin-token>
```

### Password Reset Endpoints

#### Forgot Password
```
POST /api/password/forgot
Content-Type: application/json

{
  "email": "user@example.com"
}
```

#### Reset Password
```
POST /api/password/reset
Content-Type: application/json

{
  "token": "reset-token",
  "newPassword": "NewPassword123!",
  "confirmPassword": "NewPassword123!"
}
```

## Database Schema

The service uses the following main entity:

### User Entity
- `id`: Primary key (auto-generated)
- `fullname`: User's full name
- `username`: Unique username
- `email`: Unique email address
- `password`: Encrypted password
- `gender`: User's gender (MALE, FEMALE, OTHER)
- `avatar`: Profile picture URL
- `phone`: Phone number
- `role`: User role (USER, ADMIN, MODERATOR)
- `isEnabled`: Account status
- `isAccountNonLocked`: Account lock status
- `isCredentialsNonExpired`: Credentials expiration status
- `isAccountNonExpired`: Account expiration status
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp

## Configuration

### Application Properties
```properties
# Server Configuration
server.port=8081
spring.application.name=user-service

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_user_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password

# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890123456789012345678901234567890
jwt.expiration=86400
```

## Security Features

1. **JWT Authentication**: Stateless authentication using JSON Web Tokens
2. **Password Encryption**: BCrypt password hashing
3. **Role-based Access Control**: Different access levels for users and admins
4. **CORS Configuration**: Cross-origin resource sharing support
5. **Input Validation**: Comprehensive validation using Bean Validation annotations
6. **Exception Handling**: Global exception handling with proper HTTP status codes

## Dependencies

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL Connector
- JWT (jjwt)
- Lombok
- Validation API
- Spring Mail

## Running the Application

1. Ensure MySQL is running on your system
2. Create a database named `ecommerce_user_db`
3. Update the database credentials in `application.properties`
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The service will start on port 8081.

## Testing

You can test the API endpoints using tools like Postman or curl. Make sure to:

1. Register a new user first
2. Use the JWT token from the registration/login response in the Authorization header
3. For admin operations, ensure the user has ADMIN role

## Error Handling

The service provides comprehensive error handling with appropriate HTTP status codes:

- `400 Bad Request`: Validation errors, invalid input
- `401 Unauthorized`: Authentication failures
- `403 Forbidden`: Access denied
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server errors

All error responses follow the standard API response format with success flag, message, and optional data.
