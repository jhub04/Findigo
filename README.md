# E-commerce marketplace: Findigo
Findigo is a full-stack application developed with Vue.js and Spring Boot. 
The project was developed as the final assesment in the course IDATT2105 Fullstack-Applikasjonsutvikling
for the spring semester of 2025 at NTNU.

## The team
- Aryan Malekian
- Scott Langum du Plessis
- Jonathan Skomsøy Hübertz
- Mikael Stray Frøyshov

## Table of contents
...

## Overview
The project is a full-stack webapplication aiming to provide a user-friendly
e-commerce platform for users to buy, sell and look at items.

The project utilizes the following technologies:
- Frontend: Vue 3 with Node.js
- Backend: Springboot V3 with Maven and Java 21
- Database: MySQL V8 for runtime and H2 for tests

You can find the swagger API documentation [here] (add link to swagger docs)

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


## Running the Application for Development 
#### Prerequisites 
- JDK 21
- Maven 
- MySQL

1. Clone the repository
```bash
git clone https://github.com/jhub04/Findigo.git
```
2. Navigate to the project root folder
```bash
cd Findigo
```
3. Run the application with development configuration
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Deployment
Findigo is deployed at https://idatt2105-09.idi.ntnu.no, hosted on a dedicated virtual machine within the NTNU network. The application will remain online until the administrator at NTNU shuts it down.

#### <u>Hosting Environment</u>
The project is deployed on an Ubuntu-based virtual machine using the following stack:
- Backend: Spring Boot (Java 21, Maven)
- Frontend: Vue 3 (Vite)
- Database: MySQL 8
- Web Server / Reverse Proxy: Nginx
- Deployment Automation: GitHub Actions (CI/CD)

#### <u>Setup Process</u>
1. Connected to the Virtual Machine via ssh
2. Installed Required Dependencies
    - OpenJDK 21
    - Maven
    - Node.js (v20+)
    - MySQL Server
    - Nginx
3. Cloned the repositories using a GitHub personal access token [(PAT)](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens)

#### <u>Backend Deployment</u>
1. Built the application into a self-contained JAR
2. Ran the JAR manually
3. Set up a systemd service for automatic startup and easier management

#### <u>Frontend Deployment</u>
1. Installed required dependencies
2. Built the project
3. Deployed the static build files to Nginx's root directory

#### <u>Nginx Configuration</u>
Nginx was configured to:
- Serve the built static files from `/var/www/html`
- Reverse proxy `/api/` requests to the Spring Boot backend running on `https://localhost:8443`
- Support HTTPS (via the backend's self-signed SSL keystore)

#### <u>Spring Profiles</u>
The backend uses Spring profiles to separate dev and prod configurations:

- `application-dev.properties` is used during local development (e.g., using `mvn spring-boot:run -Dspring-boot.run.profiles=dev`)
- `application-prod.properties` is used on the VM server
- Common config is stored in `application.properties`

