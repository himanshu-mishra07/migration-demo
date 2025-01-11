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
After the project has started, you can access the application at `http://localhost:8080/kitchensink/index`

## REST Endpoints
* POST /rest/members: Add a new member. (Authorized endpoint for Admin role only)
* GET /rest/members: Get all members.
* GET /rest/members/{id}: Get a member by ID.

## JWT Authentication

This application uses JWT for authentication to create members. To obtain a JWT token, use the following endpoint:
* POST /auth/token: Authenticate and receive a JWT token.

Example Request

```curl -X POST http://localhost:8080/auth/token -H "Content-Type: application/json" -d '{"username":"your_username", "password":"your_password"}'```

Example Response

```json
{
  "token": "your_jwt_token"
}
```