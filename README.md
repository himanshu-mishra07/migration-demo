# Kitechensink Spring Boot Migration

This project is a migration of the Kitchensink example from JBoss Forge to Spring Boot.

## System Requirements
* Java 21
* Maven

## Build and Run
To build and run the project, execute the following:

1. Open a terminal and navigate to the project root directory.
2. Run the following commands:

```shell script
  mvn clean package
  mvn spring-boot:run
```

## Access the Application
The UI code of this application is react which is her https://github.com/himanshu-mishra07/kitchensink-ui/tree/main

**Note:** The web pages are authenticated and require a valid username and password to access. Ensure you have the correct credentials to log in.

### Authentication

This application uses simple username and password authentication to secure the web pages. To log in, use the following credentials:

* **Username:** your_username
* **Password:** your_password

**Note:** The DB has two types of users admin and user with different level of access.


## REST Endpoints
* POST /api/kitchensink/v1/members: Add a new member. (Authorized endpoint for Admin role only)
* GET /api/kitchensink/v1/members: Get all members. (Authorized endpoint for Admin role only)
* PUT /api/kitchensink/v1/members/{id}: Update a member by ID. (Authorized endpoint for Admin role only)
* DELETE /api/kitchensink/v1/members/{id}: Delete a member by ID. (Authorized endpoint for Admin role only)
* GET /api/kitchensink/v1/members/email/{email}: Get a member by email.
* POST /api/kitchensink/v1/members/register: Register a new member without roles.
* POST /auth/logout: Logout and blacklist the JWT token.

### JWT Authentication

This application uses JWT for authentication to create members via rest. To obtain a JWT token, use the following endpoint:
* POST /auth/token: Authenticate and receive a JWT token.

Example Request

```shell
curl -X POST http://localhost:8080/api/kitchensink/v1/auth/token -H "Content-Type: application/json" -d '{"username":"your_username", "password":"your_password"}'
```

Example Response

```json
{
  "token": "your_jwt_token"
}
```