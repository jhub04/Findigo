# E-commerce marketplace: Findigo
Findigo is a full-stack application developed with Vue.js and Spring Boot. 
The project was developed as the final assesment in the course IDATT2105 Fullstack-Applikasjonsutvikling
for the spring semester of 2025 at NTNU.

Link to the hosted Findigo: https://idatt2105-09.idi.ntnu.no 

**NB!** This repository contains the backend source code. You can find the frontend source code [here](https://github.com/jhub04/Findigo-Frontend.git)

## The team
- Aryan Malekian
- Scott Langum du Plessis
- Jonathan Skomsøy Hübertz
- Mikael Stray Frøyshov

## Table of contents
1. [Overview](#Overview)
2. [Features](#Features)
3. [System Architecture](#System-Architecture)
4. [ER Diagram](#ER-Diagram)
5. [Setup for Development and Test Environments](#Setup-for-Development-and-Test-Environments)
      1. [Prerequisites](#Prerequisites)
      2. [Spring Profiles](#Spring-Profiles)
      3. [Setup](#Setup)
      4. [Admin user credentials (use on login)](#Admin-user-credentials-(use-on-login))
6. [Continuous Deployment](#Continuous-Deployment)
      1. [Hosting Environment](#Hosting-Environment)
      2. [Self-hosted runners](#Self-hosted-runners)

## Overview
The project is a full-stack webapplication aiming to provide a user-friendly
e-commerce platform for users to buy, sell and look at items.

The project utilizes the following technologies:
- Frontend: Vue 3 with Node.js
- Backend: Springboot V3 with Maven and Java 21
- Database: MySQL V8 for production and development, and H2 for tests

## Features
- **Secure login:** Users can securely log in, register, and update their account details
- **Listing Creation:** Users can create listings and publish them to the marketplace
- **Archiving:** A user can archive his own listings, so that they're not available on the public marketplace
- **Favorite Selection:** Users can favorite listings they like or find interesting, and view all of them from the profile page 
- **Recommendation algorithm:** Listings on the homepage are filtered based on an advanced recommendation algorithm, and can be further filtered based on category.
- **Advanced search:** Users can search for listings and filter based on *category*, *price*, *location*, and the listing creation *date*
- **Map View:** Users get to see listings marked on a real map and filter them with advanced search
- **Messaging:** Users can send messages to each other to negotiate or inquire on listed items
- **Admin functionality:** Admin users can do administrate actions such as adding, modifying, and deleting categories and listings
- **Internationalization:** The application has support for multiple languages
- A user can reserve an item?

More ...

## System Architecture
<img width="766" alt="Screenshot 2025-04-06 at 19 17 28" src="https://github.com/user-attachments/assets/65784c97-7c08-44ab-b9ed-95d9f4732622" />

## ER Diagram
![image](https://github.com/user-attachments/assets/929c96d5-c4ca-493f-8daf-72255e64a709)


## Setup for Development and Test Environments
#### Prerequisites 
- JDK 21
- Maven 
- MySQL

#### Spring Profiles
The backend uses Spring profiles to separate dev, test and prod configurations:

- `application-dev.properties` is used during local development
- `application-prod.properties` is used for deployment on the VM server
- `application-test.properties` is used for testing the development environment
- Common config is stored in `application.properties`

#### Setup
1. Clone the repository
```bash
git clone https://github.com/jhub04/Findigo.git
```
2. Navigate to the project root folder
```bash
cd Findigo
```
3. Run the application:
- Running the application for development
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
- Running the application for testing:
   ```bash
  mvn spring-boot:run -Dspring-boot.run.profiles=test
  ```
  
   or configure your run configuration to use the spring dev profile in your chosen IDE.

#### Admin user credentials (use on login)
- Username: admin
- Password: admin123

## Continuous Deployment
Findigo is deployed at https://idatt2105-09.idi.ntnu.no, hosted on a dedicated virtual machine within the NTNU network. The application will remain online until the administrator at NTNU shuts it down.

#### <u>Hosting Environment</u>
The project is deployed on an Ubuntu-based virtual machine using the following stack:
- Backend: Spring Boot (Java 21, Maven)
- Frontend: Vue 3 (Vite)
- Database: MySQL 8
- Web Server / Reverse Proxy: Nginx
- Deployment Automation: GitHub Actions (CI/CD)



#### <u>Self-hosted runners</u>
We created self-hosted runner for both of the repos, so that it was possible to communicate with the NTNU virtual machine from Github.
The continuous deployment workflows were configured to:
- Backend: Build the backend application into a self-contained JAR and set up a systemd service for automatic startup and easier management.
- Frontend: Deployed the static build files to Nginx's root directory.  





