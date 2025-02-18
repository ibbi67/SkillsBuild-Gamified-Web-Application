# User Manual

## Table of Contents

1.  [System Design Overview](#system-design-overview)
    *   [System Design for Frontend](#system-design-for-frontend)
    *   [System Design for Backend](#system-design-for-backend)
    *   [Overriding Default Responses in Spring](#overriding-default-responses-in-spring)
2.  [Authentication System](#authentication-system)
    *   [Authentication System for Backend](#authentication-system-for-backend)
    *   [Authentication System for Frontend](#authentication-system-for-frontend)
    *   [Flow of Authentication](#flow-of-authentication)
    *   [Testing](#testing)
    *   [Security Considerations](#security-considerations)

---

## System Design Overview
**Author: Justin Fung**

The system is designed to provide a seamless user experience and a robust developer experience. It leverages TypeScript for type safety and security, and employs various DAOs (Data Access Objects) and DTOs (Data Transfer Objects) to ensure data integrity.

### System Design for Frontend
The frontend is built using React and Next.js, with a focus on modularity and reusability. The `useApi` hook is a central piece of the frontend, handling API requests and managing their states.

#### `useApi` Hook
The `useApi` hook simplifies API interactions by managing loading, error, and response states. It uses Axios for HTTP requests and includes automatic token refresh functionality.

1. **Generics for Type Safety**:
    - Ensures type safety by using TypeScript generics for request and response data.

2. **Axios for HTTP Requests**:
    - Configured to include credentials and handle cross-origin requests.

3. **State Management**:
    - Manages states like `isLoading`, `isError`, `message`, `status`, and `data`.

4. **Automatic Token Refresh**:
    - Includes an Axios interceptor to handle token refresh automatically if the `access_token` expires.

5. **Error Handling**:
    - Provides comprehensive error handling for network, server, and client errors.

### System Design for Backend
The backend is built using Spring Boot, with a focus on security and scalability. The `ApiResponse` class standardizes API responses, ensuring consistency across endpoints.

#### `ApiResponse` Class
The `ApiResponse` class encapsulates the response structure for API endpoints, including fields for status code, message, and data.

1. **Generic Type Parameter**:
    - Allows the class to be used with any type of data, providing flexibility and type safety.

2. **Status Code**:
    - Stores the HTTP status code of the response.

3. **Message**:
    - Stores a descriptive message about the response.

4. **Data**:
    - Stores the actual data being returned in the response.

5. **Static Factory Methods**:
    - Simplify the creation of `ApiResponse` objects and ensure consistency in the response structure.

### Overriding Default Responses in Spring
To provide more consistent and informative responses, default Spring Boot error responses are overridden.

1. **ValidationExceptionHandler**:
    - Handles validation exceptions and provides custom error responses.

2. **AccessDeniedHandler**:
    - Handles access denied exceptions and provides custom error responses.

3. **AuthenticationEntryPoint**:
    - Handles authentication exceptions and provides custom error responses.

## Authentication System
**Author: Justin Fung**

The authentication system uses JWT for secure authentication and authorization, ensuring that only authorized users can access certain resources.

### Authentication System for Backend

1. **JWT Authentication Filter**:
    - Intercepts incoming HTTP requests and checks for JWT tokens in cookies.
    - Bypasses the filter for login, signup, refresh, and public endpoints.
    - Verifies tokens and sets the authentication context if valid.

2. **JWT Service**:
    - Handles the generation and verification of JWT tokens.
    - Provides methods to generate `access_token` and `refresh_token` cookies.

3. **Authentication Controller**:
    - Provides endpoints for user signup, login, token refresh, and logout.

4. **User and Role Management**:
    - Manages user and role entities in the database.
    - Handles user-related operations like finding a user by username and saving a new user.

5. **Security Configuration**:
    - Configures Spring Security to use JWT for stateless authentication.
    - Sets up CORS, CSRF, and session management policies.

### Authentication System for Frontend

1. **Authentication Pages**:
    - `LoginPage` and `SignUpPage` components provide forms for user login and signup.
    - Use the `useApi` hook to make API requests for authentication.

2. **API Hook**:
    - Handles API requests and responses, including automatic token refresh.

3. **Authentication Provider**:
    - Provides authentication context to the entire application.
    - Checks authentication status by making a request to the `/auth/me` endpoint.

4. **Protected Routes**:
    - `Navbar` component displays different links based on the authentication state.
    - Provides links to login, signup, profile, and logout.

### Flow of Authentication

1. **Signup**:
    - User submits the signup form.
    - Frontend sends a POST request to `/auth/signup`.
    - Backend creates a new user, generates JWT tokens, and returns them in cookies.
    - Frontend sets the authentication state and redirects the user to the home page.

2. **Login**:
    - User submits the login form.
    - Frontend sends a POST request to `/auth/login`.
    - Backend authenticates the user, generates JWT tokens, and returns them in cookies.
    - Frontend sets the authentication state and redirects the user to the home page.

3. **Token Refresh**:
    - When an API request fails with a 401 status, the frontend automatically sends a POST request to `/auth/refresh`.
    - Backend verifies the `refresh_token`, generates a new `access_token`, and returns it in a cookie.
    - Frontend retries the original API request with the new `access_token`.

4. **Logout**:
    - User clicks the logout button.
    - Frontend sends a POST request to `/auth/logout`.
    - Backend invalidates the JWT tokens by setting their cookies with a max age of zero.
    - Frontend updates the authentication state and redirects the user to the home page.

### Testing

1. **Unit Tests**:
    - Comprehensive unit tests for the service and controller layers.
    - Tests for signup, login, token refresh, and logout methods.

2. **Integration Tests**:
    - Tests the authentication endpoints using the `MockMvc` framework.
    - Verifies the behavior of the signup, login, me, refresh, and logout endpoints.

### Security Considerations

1. **Password Encryption**:
    - Uses `BCryptPasswordEncoder` to encrypt user passwords before storing them in the database.

2. **Token Expiration**:
    - `access_token` has a short expiration time (15 minutes).
    - `refresh_token` has a longer expiration time (365 days).

3. **HttpOnly and Secure Cookies**:
    - JWT tokens are stored in HttpOnly and Secure cookies to prevent client-side scripts from accessing them.

4. **CORS Configuration**:
    - Allows cross-origin requests from trusted origins (e.g., `http://localhost:3000`).
    - Specifies allowed methods, headers, and credentials to ensure secure cross-origin communication.
