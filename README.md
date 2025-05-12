# Complaint Manager

A Spring Boot application for managing product complaints.

## Overview

Complaint Manager is a RESTful API service that allows users to submit, retrieve, and update complaints about products.

The system tracks the origin country of complaints using IP geolocation and prevents duplicate complaints from the same user for the same product by incrementing a counter instead.

## Features

- Create complaints with automatic IP-based geolocation
- Retrieve individual complaints by ID
- List all complaints
- Update existing complaint content
- Prevent duplicate complaints (same user/product combination)
- OpenAPI documentation

## Technologies

- Java 24
- Spring Boot 3.4.5
- Spring Data JPA
- H2 Database (file-based)
- Liquibase for database migrations
- MapStruct for object mapping
- SpringDoc OpenAPI for API documentation
- NV-i18n for country code handling

## Setup and Running

### Prerequisites

- Java 24 JDK
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:
   ```
   mvn spring-boot:run
   ```
4. The application will be available at http://localhost:8080
5. API documentation is available at http://localhost:8080/swagger-ui.html

## Database

The application uses an H2 database with file-based storage. The database file is created in the project root directory as `complaint-manager-h2db`.

### Database Migrations

The project uses Liquibase for database version control and schema migrations.
Migrations are stored in the `src/main/resources/db/changelog/` directory.