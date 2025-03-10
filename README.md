# Library Management System

## Overview
This is a **Spring Boot-based Library Management System** that allows users to manage books, authors, categories, loans, reservations, and reviews.

## Features
- **User Management**: Registration, authentication, and role-based authorization (Admin, User, Moderator).
- **Book Management**: CRUD operations for books and their availability tracking.
- **Author Management**: Manage author details and associate books with authors.
- **Category Management**: Categorize books.
- **Loan & Reservation System**: Borrow and reserve books with status tracking.
- **Review System**: Users can review books and give ratings.
- **Spring Security with JWT**: Secure authentication and authorization.
- **Global Error Handling**: Handles exceptions consistently.
- **Database Migration**: Liquibase for schema and data versioning.
- **RESTful APIs with Swagger Documentation**.

## Technologies Used
- **Spring Boot**
- **Spring Security (JWT)**
- **Spring Data JPA (Hibernate)**
- **Liquibase**
- **PostgreSQL**
- **Lombok**
- **Jakarta Validation API**

## Architecture

### Layers
- **Controller Layer**: Handles HTTP requests.
- **Service Layer**: Business logic implementation.
- **Repository Layer**: Database operations using JPA.
- **Entity Layer**: Defines database tables as Java classes.
- **DTO & Mapper Layer**: Data transformation and mapping.

