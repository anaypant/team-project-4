# **CS18000 - Social Media Application**  
**Team 5**

---

## **Overview**

This project is a comprehensive social media application implemented in Java. It features both a **Server** and a **Client GUI application**, enabling users to interact in a social environment through posts, comments, and user relationships. The project utilizes **SQLite** databases for data storage and supports robust client-server communication.

---

## **Table of Contents**

1. [How to Compile and Run](#how-to-compile-and-run)
2. [Roles and Contributions](#roles-and-contributions)
3. [Class Descriptions](#class-descriptions)  
   - [Constants](#constants)  
   - [User](#user)  
   - [Comment](#comment)  
   - [Post](#post)  
   - [UserDBDatabase](#userdbdatabase)  
   - [PostDBDatabase](#postdbdatabase)  
   - [Utils](#utils)  
   - [Server](#server)  
   - [Connection](#connection)  
   - [SocialMedia](#socialmedia)  
   - [SignInPage](#signinpage)  
   - [MainPage](#mainpage)  
   - [ProfilePage](#profilepage)  
   - [Interfaces](#interfaces)  
4. [Testing Details](#testing-details)

---

## **How to Compile and Run**

### **Compile and Run the Server**

1. **Compile the server code:**
   ```bash
   javac -d bin ./src/Server.java
Compiles Server.java and places the class files in the bin directory.

2. **Run the server:**
    ```bash
    java -cp bin Server
Runs the Server class from the bin directory.

## Compile and Run the Client Application (SocialMedia.java)
1. **Compile the client code:**
    ```bash
    javac -d bin ./src/*.java ./GUI/*.java
Compiles all Java files in the src and GUI directories and outputs the class files to the bin directory.

2. **Run the client application:**
    ```bash
    java -cp bin GUI.SocialMedia
Runs the SocialMedia class from the GUI package.

Note: Ensure that the SQLite JDBC driver is included in your classpath if required.

## Roles and Contributions
###  1. Anay Pant
* Role: Group Leader
* Contributions:
    * Implemented the GUI classes.
    * Developed interfaces and methods for GUI Frames, Application, and Utilities.
    * Created Server/Client base connection.
    * Documentation.

###  2. Utsav Arora
* Role: Databases
* Contributions:
    * Connected Application to SQLite Databases.
    * Developed methods and storage for User Information.
    * Developed methods and storage for Post Information.
    * Documentation.

###  3. Vishwa Surabhi
* Role: Methods and Application Control Flow
* Contributions:
    * Implemented the control flow for GUI and command-line application.
    * Implemented Server-side functionality.
    * Documentation.

###  4. Jordyn Rhule
* Role: Thread Safety and Test Cases
* Contributions:
    * Implemented all JTest Cases.
    * Developed synchronized methods to monitor thread safety.
    * Configured Application to run with multiple instances.


###  5. Sharvali Ladekar
* Role: Login Details and User Account Management
* Contributions:
    * Implemented the User, Comment, and Post classes.
    * Developed utility functions and helper functions for base classes.
    * Serialized and deserialized objects from Server-client connection.

### Class Descriptions
## **Constants**
- **Package:** `src`
- **Purpose:** Provides global constants used throughout the application.
- **Key Constants:**
  - `PORT_NUMBER`: `4343`
  - `SERVER_HOST_NAME`: `"localhost"`
  - `DELIMITER`: `":::"`
  - `COMMENT_DELIMITER`: `"~~~"` - Separates parsing comments from posts.
  - `USER_DB`: `"jdbc:sqlite:users.sqlite"`
  - `POST_DB`: `"jdbc:sqlite:posts.sqlite"`
- **Testing:** Verified all constants are correctly referenced.
- **Relationships:** Used by server and client classes.

---

## **User**
- **Package:** `src`
- **Purpose:** Represents a user with attributes like username, password, profile image, friends list, and blocked list.
- **Key Methods:**
  - `addFriend(String friend)`, `blockUser(String user)`
  - `toString()` and `static parseUser(String s)` for serialization.
- **Testing:** Unit tests verify initialization, friend/block management, and serialization.
- **Relationships:** Interacts with `UserDBDatabase`, `Post`, and `Comment`.

---

## **Comment**
- **Package:** `src`
- **Purpose:** Represents comments on a post.
- **Key Methods:**
  - `encode()` and `static parseCommentFromString(String c)` for serialization.
  - `display()`: Provides a formatted representation of the comment.
- **Testing:** Verified creation, serialization, and voting functionality.
- **Relationships:** Used within the `Post` class.

---

## **Post**
- **Package:** `src`
- **Purpose:** Represents a social media post with features like votes, comments, and visibility settings.
- **Key Methods:**
  - `display()`: Provides formatted post details.
  - `encode()` and `static parsePost(String s)` for serialization.
- **Testing:** Verified creation, voting, commenting, and serialization.
- **Relationships:** Contains `Comment` objects and interacts with `User`.

---

## **UserDBDatabase**
- **Package:** `src`
- **Purpose:** Manages user-related database operations.
- **Key Methods:**
  - `createUser()`, `loginUser()`
  - `addFriend()`, `addBlocked()`
- **Testing:** Verified creation, login, and friend/block management.
- **Relationships:** Uses `User` and interacts with `PostDBDatabase`.

---

## **PostDBDatabase**
- **Package:** `src`
- **Purpose:** Manages post-related database operations.
- **Key Methods:**
  - `createPost()`, `addComment()`
  - `upvotePost()`, `enableComments()`
- **Testing:** Verified post creation, deletion, and comment management.
- **Relationships:** Interacts with `Post` and `Comment`.

---

## **Utils**
- **Package:** `src`
- **Purpose:** Provides utility methods for data conversion.
- **Testing:** Ensured accurate string-to-ArrayList conversions.
- **Relationships:** Used by `User`, `Post`, and database classes.

---

## **Server**
- **Package:** `src`
- **Purpose:** Handles client connections and processes commands.
- **Testing:** Simulated client interactions to verify command processing.
- **Relationships:** Communicates with database classes and clients.

---

## **Connection**
- **Package:** `GUI`
- **Purpose:** Manages client-side network connections.
- **Testing:** Verified message sending and receiving.
- **Relationships:** Used by `SocialMedia`.

---

## **SocialMedia**
- **Package:** `GUI`
- **Purpose:** Main controller for the GUI application.
- **Testing:** Verified GUI updates and session management.
- **Relationships:** Manages GUI pages and server communication.

---



### **SignInPage**
- **Package:** `GUI`
- **Purpose:** Provides the login and account creation interface for the application.
- **Key Methods:**
  - `init()`: Initializes the GUI components for the sign-in page.
  - `login()`: Handles user login by verifying credentials with the server.
  - `createAccount()`: Handles account creation by sending user details to the server.
- **Testing:** Verified:
  - Proper error handling for invalid credentials.
  - Navigation to `MainPage` upon successful login.
  - Correct server communication for account creation.
- **Relationships:** Interacts with the `SocialMedia` class to send and receive data from the server.

---

### **MainPage**
- **Package:** `GUI`
- **Purpose:** Represents the main dashboard of the application after login, allowing users to navigate through core features.
- **Key Methods:**
  - `init()`: Initializes the main page GUI with buttons for creating posts, managing friends, and viewing posts.
  - `createPost()`: Opens a dialog for users to create new posts.
  - `selectPost()`: Enables users to select and view a post in detail.
  - `addFriend()`, `removeFriend()`, `block()`: Manages user relationships.
  - `logout()`: Logs the user out and navigates back to the `SignInPage`.
- **Testing:** Verified:
  - Navigation between pages.
  - Correct post creation and friend management functionalities.
- **Relationships:** Works with `SocialMedia` to handle user actions and communicate with the server. Navigates to `PostPage` for detailed post interactions.

---

### **ProfilePage**
- **Package:** `GUI`
- **Purpose:** Displays the profile of a user, including their posts and relationship management options.
- **Key Methods:**
  - `init()`: Sets up the GUI for the profile page, displaying the user’s posts and providing options to manage relationships.
  - `addFriend()`: Adds a friend by sending a request to the server.
  - `removeFriend()`: Removes a friend from the user’s list.
  - `block()`: Blocks another user and removes them from the friend list.
  - `removeSelf()`: Deletes the current user’s account.
  - `ret()`: Returns to the `MainPage`.
- **Testing:** Verified:
  - Correct display of user posts and interactions with the server for relationship management.
  - Accurate functionality of buttons, including account deletion.
- **Relationships:** Interacts with `SocialMedia` for server communication and with `PostDBDatabase` to fetch user posts.
  - Contains `PostGUI` components for rendering individual posts in the profile.

---

## **Testing Details**

- **Unit Tests:** Extensive unit testing for all major classes (`User`, `Post`, `Database`).
- **Simulated Interactions:** Validated client-server communication and GUI functionality.
- **Database Integrity:** Ensured data consistency after operations.

---

## **Contributors**
- **Anay Pant**
- **Utsav Arora**
- **Vishwa Surabhi**
- **Jordyn Rhule**
- **Sharvali Ladekar**

---

**Thank you for exploring our project!**