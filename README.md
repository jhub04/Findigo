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

## Running the application (Uncomplete)
NB! See [Findigo-Frontend](https://github.com/jhub04/Findigo-Frontend) for how to run the frontend
#### Prerequisites 
- JDK 21
- Maven 
- Docker

1. Clone the repository
```bash
git clone https://github.com/jhub04/Findigo.git
```
2. Navigate to the project root folder
```bash
cd Findigo
```
3. Run the springboot executable
```bash
mvn spring-boot:run 
```

