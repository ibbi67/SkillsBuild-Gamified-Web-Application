# User Manual

## Table of Contents

- [User Manual](#user-manual)
  - [Table of Contents](#table-of-contents)
  - [System Design Overview](#system-design-overview)
    - [System Design for Frontend](#system-design-for-frontend)
      - [`useApi` Hook](#useapi-hook)
    - [System Design for Backend](#system-design-for-backend)
      - [`ApiResponse` Class](#apiresponse-class)
    - [Overriding Default Responses in Spring](#overriding-default-responses-in-spring)
  - [Authentication System](#authentication-system)
    - [Authentication System for Backend](#authentication-system-for-backend)
    - [Authentication System for Frontend](#authentication-system-for-frontend)
    - [Flow of Authentication](#flow-of-authentication)
    - [Testing](#testing)
    - [Security Considerations](#security-considerations)
  - [Streak System](#streak-system)
    - [Overview](#overview)
    - [How Streaks Work](#how-streaks-work)
      - [Getting Started](#getting-started)
      - [Maintaining Your Streak](#maintaining-your-streak)
      - [Breaking a Streak](#breaking-a-streak)
    - [Viewing Your Streak](#viewing-your-streak)
      - [Dashboard Display](#dashboard-display)
      - [API Access](#api-access)
    - [Technical Details](#technical-details)
      - [Streak Rules](#streak-rules)
      - [Time Zones](#time-zones)
    - [Implementation](#implementation)
    - [Save to Favourite Courses](#save-to-favourite-courses)  
  - [Save to Favourite Courses for Backend](#save-to-favourite-courses-for-backend)  
    - [FavouriteCourse Service](#favouritecourse-service)  
    - [FavouriteCourse Controller](#favouritecourse-controller)  
    - [FavouriteCourse Repository](#favouritecourse-repository)  
    - [Authentication Integration](#authentication-integration)  
  - [Save to Favourite Courses for Frontend](#save-to-favourite-courses-for-frontend)  
    - [Favourite Courses Dashboard](#favourite-courses-dashboard)  
    - [User Interaction](#user-interaction)  
  - [Flow of Save to Favourite Courses](#flow-of-save-to-favourite-courses)  
    - [Saving a Course](#saving-a-course)  
    - [Removing a Course](#removing-a-course)  
  - [Testing](#testing)  
    - [Unit Tests](#unit-tests)  
    - [Integration Tests](#integration-tests) 
  - [Comments System](#comments-system)
    - [Overview](#overview-2)
    - [Comments System for Backend](#comments-system-for-backend)
    - [Comments System for Frontend](#comments-system-for-frontend)
    - [Flow of Comments Feature](#flow-of-comments-feature)
    - [Testing](#testing-3)

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


## Streak System
**Author: Zain Altaf**

### Overview

The Streaks feature rewards users for consistent daily engagement with
the platform by tracking consecutive daily logins.

### How Streaks Work

#### Getting Started

- A streak counter is automatically created when you register a new
  account

- Your streak is visible on your dashboard

- Initial streak starts at 1 on your first login

#### Maintaining Your Streak

- Log in at least once each day to maintain your streak -- note that
  staying logged in will also allow you to maintain/increase your streak

- Streak increases by 1 for each consecutive day you log in

- Multiple logins in the same day count as one streak day

#### Breaking a Streak

- Missing a day of login will reset your streak

### Viewing Your Streak

#### Dashboard Display

- Located on the main dashboard

#### API Access

To check your streak programmatically:

GET /api/streak/{userId}

(Remember that this data is accessed from the backend so it will show on
backend port -- localhost:8080)

### Technical Details

#### Streak Rules

- Only one streak update per calendar day

- Streak calculations occur automatically during:

  - Token refresh

  - Authentication events -- Login or Signup

#### Time Zones

- Streaks are calculated based on your local calendar date

- Days are determined using system default time zone

### Implementation

**Streak.java**

- Each user has an associated Streak object that contains the following
  data:

  - Id -- primary key

  - Streak -- the counter value

  - previousLogin -- the date when the streak was last incremented

- The user object and streak object are stored in separate tables in the
  database with user where streak contains a foreign key that points to
  the user id it is associated with.

- The related streak object is linked with the user object upon
  authentication

**AuthService.java**

- The related streak object is linked with the user object upon
  authentication

- Within the authserivce file is a method named checkAndUpdateStreak
  which takes the user and increments their streak if they haven't
  already done so today

- This method is called within each of the authentication service
  functions -- signup, login, refresh

**StreaksService.Java**

- In this class I have included methods to increment a streak by the
  user that it is associated with and updating the values within the db

- I have also created a linked function to just save the streak that is
  passed through to the db


**StreakRepository.Java**

- findByUserId method allows you to find the streak associated with a
specific user

## Course Recommendation System
**Author: Daniel Dineen**

The course recommendation system analyzes student profiles and course data to suggest relevant courses based on academic history, preferences, and similarity to other students' choices, while continuously improving recommendations through user feedback.

### Course Recommendation System for Backend

1. **Course Service**
    - Implements hybrid recommendation algorithm (collaborative and content-based filtering)
    - Analyzes student's favourite courses for personalized recommendations
    - Manages recommendation by difficulty level

2. **Course Controller**
    - Handles course recommendation API endpoints and searches
    - Processes course ratings and filtering requests
    - Routes authenticated requests to services

3. **Course Repository**
    - Manages course data and similarity matrices in database
    - Handles course metadata and prerequisites
    - Implements efficient data retrieval and caching

4. **Student Profile Repository**
    - Manages student profiles and course history
    - Updates recommendation parameters based on interactions
    - Maintains student similarity metrics

5. Authentication Integration
    - Ensures secure access to recommendations
    - Manages role-based feature access
    - Integrates with JWT authentication

### Course Recommendation System for Frontend

1. **Recommendation Dashboard**
    - Displays personalized course recommendations with relevance scores and details
    - Implements dynamic filtering and sorting capabilities (difficulty, schedule, ratings)
    - Shows course compatibility with current schedule and academic progress

2. **Course Search & Interaction**
    - Provides advanced search with real-time filtering based on student preferences
    - Enables course enrollment, wishlist management, and review submissions
    - Updates recommendations automatically based on user interactions

3. **Student Profile Interface**
    - Displays academic progress, course history, and recommended course path
    - Allows preference updates for recommendation refinement
    - Shows personalized course suggestions based on career goals and interests

### Flow of Course Recommendation

1. **Initial Recommendation Generation**
    - System analyzes student profile, academic history, and preferences upon login
    - Generates personalized course list using hybrid filtering algorithm
    - Ranks recommendations based on relevance scores and prerequisites

2. **User Interaction & Refinement**
    - User can filter recommendations by multiple criteria (difficulty, schedule, ratings)
    - System updates recommendations in real-time based on user interactions
    - Course selections and ratings automatically refine future recommendations

3. **Course Selection Process**
    - User reviews detailed course information and compatibility
    - System verifies prerequisites and schedule conflicts
    - Updates student profile and recommendations upon enrollment

### Testing

1. **Unit Tests**
    - Validates recommendation algorithm and filtering logic in CourseService
    - Tests CourseController endpoints and response handling

2. **Integration Tests**
    - Verifies complete recommendation flow and API functionality
    - Tests interactions between service, repository, and controller layers

    # Save to Favourite Courses  
**Author:** Sonny Powell

## Save to Favourite Courses for Backend  

### FavouriteCourse Service  
- Manages adding, removing, and retrieving favourite courses  
- Stores user-course relationships in the database  
- Ensures duplicate courses aren’t added  

### FavouriteCourse Controller  
- Handles API requests for saving and retrieving favourites  
- Routes authenticated requests to the service  
- Validates user permissions for modifying favourites  

### FavouriteCourse Repository  
- Manages database interactions for favourite courses  
- Efficiently retrieves a user’s saved courses  
- Implements caching for faster access  

### Authentication Integration  
- Ensures only logged-in users can save favourites  
- Uses JWT authentication for secure access  
- Restricts access to user-specific favourite lists  

---

## Save to Favourite Courses for Frontend  

### Favourite Courses Dashboard  
- Displays user’s saved courses in an easy-to-access list  
- Allows quick navigation to favourite courses  
- Supports sorting and filtering  

### User Interaction  
- Users can add/remove courses from favourites with one click  
- Updates the backend in real time  
- Provides visual feedback on saved courses  

---

## Flow of Save to Favourite Courses  

### Saving a Course  
1. User clicks the "Save to Favourites" button  
2. API adds the course to the user’s favourites list  
3. The UI updates to reflect the change  

### Removing a Course  
1. User selects a course to remove from favourites  
2. API deletes the course from the favourites list  
3. UI updates to show the course is no longer saved  

---

## Testing  

### Unit Tests  
- Validate favourite course addition/removal logic  
- Ensure database queries return correct results  

### Integration Tests  
- Verify API endpoints handle requests correctly  
- Test authentication and access control 

## Comments System
**Author: Eren Arslan**

### Overview

The Comments feature allows users to engage in discussions about courses, share insights, and ask questions. This interactive element enhances the learning experience by creating a community around each course.

### Comments System for Backend

1. **Comment Service**
    - Manages creating, retrieving, and handling comments
    - Associates comments with specific courses and users
    - Implements error handling for various scenarios

2. **Comment Controller**
    - Provides REST API endpoints for comment operations
    - Handles authentication for comment creation
    - Routes requests to appropriate service methods

3. **Comment Repository**
    - Manages comment data storage and retrieval
    - Efficiently fetches comments by course ID
    - Implements data access patterns for comment operations

4. **Authentication Integration**
    - Ensures only authenticated users can post comments
    - Stores user information with each comment
    - Uses JWT authentication for secure access

### Comments System for Frontend

1. **Comments Section Component**
    - Displays all comments for a specific course
    - Shows username and timestamp for each comment
    - Provides real-time feedback for comment submission

2. **Comment Creation Interface**
    - Offers a text area for writing new comments
    - Validates input before submission
    - Shows loading and error states during submission

3. **Comments Display**
    - Organizes comments chronologically
    - Displays "No comments" message when appropriate
    - Formats dates in a user-friendly manner

### Flow of Comments Feature

1. **Viewing Comments**
    - Navigate to a course detail page
    - Scroll to the bottom to see the comments section
    - Comments load automatically with the course details

2. **Creating a Comment**
    - Type your comment in the text area
    - Click "Post Comment" to submit
    - New comment appears immediately in the list
    - Error handling is provided if submission fails

3. **Comment Validation**
    - Empty comments cannot be submitted
    - Button is disabled until comment is entered
    - Feedback is displayed during submission process

### Testing

1. **Unit Tests**
    - Test comment service and controller methods
    - Validate error handling and edge cases
    - Ensure proper validation of input data

2. **Integration Tests**
    - Verify API endpoints function correctly
    - Test authentication integration
    - Ensure proper data relationships between users, courses, and comments

3. **Manual Tests**
    - Comprehensive test cases for different user scenarios
    - Validation of UI states (loading, error, empty)
    - Cross-browser compatibility testing