# Library Management System

## Overview
The Library Management System is a Java-based application designed to manage library operations efficiently. It consists of two main modules:

1. **Client App**: Allows users to log in, sign up, borrow books, and return books. The app updates the database and UI accordingly.
2. **Admin App**: Enables administrators to manage users and books, search for specific records, and synchronize UI updates.

## Features

### Client App
- **Authentication**: Login and signup functionality with error handling.
- **Book Management**: Borrow and return books with real-time updates to the database and UI.
- **User Interface**: Modern and responsive design with sidebar buttons styled for consistency.

### Admin App
- **User Management**: View and manage user details.
- **Book Management**: Search and manage book records.
- **UI Enhancements**: Consistent styling and synchronized updates.

## Project Structure
The project is organized into the following packages:

- **bookNest.client**: Contains the client-side application logic.
- **bookNest.admin**: Contains the admin-side application logic.
- **bookNest.models**: Defines the data models for `User` and `Book`.
- **bookNest.database**: Handles database operations and sample data.

## Setup Instructions

1. **Prerequisites**:
   - Java Development Kit (JDK) installed.
   - MySQL database setup.
   - MySQL Connector JAR file included in the `lib` folder.

2. **Database Configuration**:
   - Import the `sampleData.sql` file into your MySQL database.
   - Update database connection details in `DatabaseUtil.java`.

3. **Run the Application**:
   - Compile the project using your preferred IDE or command line.
   - Run the `App.java` files in the `bookNest.client` and `bookNest.admin` packages.

## Running the Project Locally

To run the Library Management System locally, follow these steps:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/tse-coder/bookNest.git
   cd bookNest
   ```

2. **Set Up the Database**:
   - Ensure MySQL is installed and running.
   - Import the `sampleData.sql` file into your MySQL database.
     ```bash
     mysql -u <username> -p <database_name> < src/bookNest/database/sampleData.sql
     ```
   - Update the database connection details in `src/bookNest/database/DatabaseUtil.java`.

3. **Compile the Project**:
   - Use your preferred IDE or the command line to compile the project.
     ```bash
     javac -cp lib/mysql-connector-java-9.3.0.jar src/bookNest/**/*.java
     ```

4. **Run the Applications**:
   - Run the Client App:
     ```bash
     java -cp lib/mysql-connector-java-9.3.0.jar:src bookNest.client.App
     ```
   - Run the Admin App:
     ```bash
     java -cp lib/mysql-connector-java-9.3.0.jar:src bookNest.admin.App
     ```

## Technologies Used
- Java
- MySQL
- JDBC

## Authors
Developed by:

| Name               | Role         |
|--------------------|--------------|
| Adonias Abiyot     | UGR/0796/15  |
| Ezra Ayalew        | UGR/9077/16  |
| Natan Tewodros     | UGR/6982/16  |
| Sawel Yohannes     | UGR/2969/16  |
| Tsegaye Shewamare  | UGR/2048/16  |

