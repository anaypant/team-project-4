hase 2 - CS18000 Team 5
How to Run and Compile the Project

Compile and Run the Server

Compile the server code:

bash
Copy code
javac -d bin ./src/Server.java
Compiles Server.java and places the class files in the bin directory.
Run the server:

bash
Copy code
java -cp bin Server
Runs the Server class from the bin directory.
Compile and Run the Client Application (SocialMedia.java)

Compile the client code:

bash
Copy code
javac -d bin ./src/*.java ./GUI/*.java
Compiles all Java files in the src and GUI directories and outputs the class files to the bin directory.
Run the client application:

bash
Copy code
java -cp bin GUI.SocialMedia
Runs the SocialMedia class from the GUI package.
Note: Ensure that the SQLite JDBC driver is included in your classpath if required.

Roles

Anay Pant - Submitted the code on Vocareum.

Contributions:

Implemented the User, Comment, and Post classes.

Wrote unit tests for User and Post classes.

Developed UserDBDatabase and PostDBDatabase for database operations.

Worked on server-side code (Server.java).

[Student 2] - Submitted the report on Brightspace.

Contributions:

Implemented the GUI classes (SignInPage, MainPage, PostPage, and SocialMedia).

Developed the Connection class for client-server communication.

Wrote unit tests for GUI components.

Assisted in integrating GUI with backend.

Class Descriptions

Below is a detailed description of each class, including functionality, methods, testing done, and relationships with other classes.

3.1. Constants
Package: src

Functionality:

Provides constant variables used throughout the application.

Defines file paths for user and post databases.

Specifies server host name and port number.

Defines delimiters used for data parsing.

Key Constants:

PORT_NUMBER: Port number for server communication (4343).

SERVER_HOST_NAME: Server host name ("localhost").

DELIMITER: Delimiter for parsing data (":::").

USER_DB: Path to the user database ("jdbc:sqlite:users.sqlite").

POST_DB: Path to the post database ("jdbc:sqlite:posts.sqlite").

Testing:

Verified that all constants are accessible and correctly referenced in other classes.
Relationships:

Used by both server and client classes to ensure consistency in configurations and data parsing.
3.2. User
Package: src

Functionality:

Represents a user in the social media application.

Manages user attributes such as username, password, profile image path, friends list, and blocked list.

Key Methods:

Constructors:

User(String username, String password): Initializes a new user with the given username and password.

User(String username, String password, String imagePath, ArrayList<String> friends, ArrayList<String> blocked): Initializes a user with all attributes.

Getters and Setters:

getUsername(), setUsername(String username)

getPassword(), setPassword(String password)

getImagePath(), setImagePath(String imagePath)

getFriendsList(): Returns the friends list.

getBlockedList(): Returns the blocked users list.

Friend Management:

addFriend(String friend): Adds a user to the friends list if not already present and removes them from the blocked list.

removeFriend(String friend): Removes a user from the friends list.

Blocking Users:

blockUser(String user): Adds a user to the blocked list and removes them from the friends list.
Serialization Methods:

toString(): Serializes the user object into a string using the defined delimiter.

static parseUser(String s): Parses a serialized user string to create a User object.

Testing:

Created unit tests in UserTest to verify:

Proper initialization of user objects.

Correct functioning of friend and block management methods.

Accurate serialization and deserialization.

Ensured that blocked users cannot be added to the friends list.

Relationships:

Used by UserDBDatabase for database operations related to users.

Interacts with Post and Comment classes indirectly through user actions.

3.3. Comment
Package: src

Functionality:

Represents a comment on a post.

Manages attributes such as comment text, creator username, upvotes, and downvotes.

Key Methods:

Constructors:

Comment(String comment, String creator): Initializes a new comment with default vote counts.

Comment(int upvotes, int downvotes, String comment, String creator): Initializes a comment with specified vote counts.

Getters and Setters:

getComment(), setComment(String comment)

getCreator(), setCreator(String creator)

getUpvotes(), setUpvotes(int upvotes)

getDownvotes(), setDownvotes(int downvotes)

Serialization Methods:

encode(): Serializes the comment into a string.

static parseCommentFromString(String c): Parses a serialized comment string to create a Comment object.

Display Methods:

display(): Returns a formatted string representation of the comment for display.
Overridden Methods:

equals(Object obj): Compares comments based on content and votes.

toString(): Returns a simple string combining creator and comment text.

Testing:

Unit tests in PostTest verified:

Creation and manipulation of comments.

Voting functionality.

Serialization and deserialization accuracy.

Relationships:

Used within the Post class to manage comments on posts.

Interacts with users through actions like commenting and voting.

3.4. Post
Package: src

Functionality:

Represents a social media post.

Manages attributes such as unique ID, creator, caption, image URL, date created, votes, comments, and visibility settings.

Key Methods:

Constructors:

Post(String creator, String caption, String url, String dateCreated): Initializes a new post without specifying an ID.

Post(String id, String creator, String caption, String url, String dateCreated, int upVotes, int downVotes, ArrayList<Comment> comments, boolean commentsEnabled, boolean hidden): Initializes a post with all attributes.

Getters and Setters:

getId()

getCreator(), setCreator(String creator)

getCaption(), setCaption(String caption)

getUrl(), setUrl(String url)

getDateCreated(), setDateCreated(String dateCreated)

getUpVotes(), setUpVotes(int upVotes)

getDownVotes(), setDownVotes(int downVotes)

getComments(), setComments(ArrayList<Comment> comments)

isCommentsEnabled(), setCommentsEnabled(boolean commentsEnabled)

isHidden(), setHidden(boolean hidden)

Serialization Methods:

encode(): Serializes the post into a string.

static parsePost(String s): Parses a serialized post string to create a Post object.

Display Methods:

display(): Returns a formatted string representation of the post for display, including comments.
Overridden Methods:

equals(Object obj): Compares posts based on their unique IDs.

toString(): Serializes the post for storage or transmission.

Testing:

Unit tests in PostTest verified:

Correct initialization and manipulation of posts.

Accurate handling of votes and comments.

Serialization and deserialization correctness.

Proper functioning of display methods.

Relationships:

Interacts with User through the creator attribute.

Contains Comment objects in its comments list.

Managed by PostDBDatabase for database operations.

3.5. UserDBDatabase
Package: src

Functionality:

Manages database operations related to users.

Handles creation, authentication, updating, and deletion of user data.

Key Methods:

static synchronized boolean createUser(User u): Inserts a new user into the database.

static synchronized boolean createUser(String username, String password): Overloaded method to create a user with credentials.

static synchronized User loginUser(String loginUsername, String loginPassword): Authenticates a user and returns a User object.

static synchronized boolean addFriend(String username, String targetUsername): Adds a friend to a user's friends list.

static synchronized boolean removeFriend(String username, String targetUsername): Removes a friend from a user's friends list.

static synchronized boolean addBlocked(String username, String targetUsername): Blocks a user.

static synchronized boolean deleteUser(String username): Deletes a user and their associated data from the database.

static synchronized boolean isFriend(String base, String target): Checks if two users are friends.

static synchronized User getAndDeleteUser(String username): Retrieves and deletes a user from the database.

Testing:

Unit tests in UserDBDatabaseTest verified:

Successful creation and deletion of users.

Proper authentication with valid credentials.

Correct handling of friends and blocked users.

Data consistency after database operations.

Relationships:

Uses User objects for data manipulation.

Interacts with PostDBDatabase when deleting users to also delete their posts.

3.6. PostDBDatabase
Package: src

Functionality:

Manages database operations related to posts.

Handles creation, retrieval, updating, and deletion of post data.

Key Methods:

Post Creation:

static synchronized boolean createPost(String username, String content, String image): Creates a new post with optional image.

static synchronized boolean createPost(Post p): Creates a post using an existing Post object.

static synchronized boolean createPost(String username, String content): Creates a post without an image.

Post Retrieval:

static synchronized Post selectPost(String username, int index, String activeUser): Retrieves a specific post by index.

static synchronized ArrayList<Post> getPostsByUsername(String username, String activeUser): Retrieves all posts by a user.

Post Deletion:

static synchronized boolean deletePost(String postId): Deletes a post by its ID.

static synchronized boolean deletePostsByUsername(String username): Deletes all posts by a user.

Comment Management:

static synchronized boolean addComment(String postId, String username, String comment): Adds a comment to a post.

static synchronized boolean deleteComment(String postId, String commenter, String comment): Deletes a comment from a post.

Voting:

static synchronized boolean upvotePost(String postId): Increments the upvote count of a post.

static synchronized boolean downvotePost(String postId): Increments the downvote count of a post.

static synchronized boolean upvoteComment(String postId, Comment targetComment): Upvotes a comment.

static synchronized boolean downvoteComment(String postId, Comment targetComment): Downvotes a comment.

Visibility and Comments Settings:

static synchronized boolean enableComments(String postId): Enables comments on a post.

static synchronized boolean disableComments(String postId): Disables comments on a post.

static synchronized boolean hidePost(String postId): Hides a post.

static synchronized boolean unhidePost(String postId): Unhides a post.

Utility Methods:

static synchronized boolean getCommentsEnabled(String postId): Checks if comments are enabled on a post.

static synchronized ArrayList<Comment> getCommentsFromPost(String postId): Retrieves comments from a post.

static synchronized String getCreatorOfPost(String postId): Gets the creator of a post.

Testing:

Unit tests in PostDBDatabaseTest verified:

Correct creation and deletion of posts.

Accurate retrieval of posts and comments.

Proper functioning of voting mechanisms.

Data integrity after enabling/disabling comments and hiding/unhiding posts.

Relationships:

Interacts with Post and Comment classes for data manipulation.

Works with UserDBDatabase when deleting users to also delete their posts.

3.7. Utils
Package: src

Functionality:

Provides utility methods for data conversion and manipulation.
Key Methods:

static String arrListToString(ArrayList<String> list): Converts an ArrayList<String> to a delimited string.

static ArrayList<String> arrayFromString(String s): Converts a delimited string back to an ArrayList<String>.

static String arrListCommentToString(ArrayList<Comment> comments): Serializes a list of Comment objects into a string.

static ArrayList<Comment> arrayCommentFromString(String s): Deserializes a string into a list of Comment objects.

Testing:

Tested with various lists and strings to ensure accurate conversions.

Verified that special characters in strings do not break parsing.

Relationships:

Used by User, Post, UserDBDatabase, and PostDBDatabase for data serialization and deserialization.
3.8. Server
Package: src

Functionality:

Manages network connections and handles client requests.

Processes commands from clients and interacts with the database accordingly.

Key Methods:

public static void main(String[] args): Entry point to start the server.

private void handleClient(Socket clientSocket): Handles client communication in a separate thread.

Command Processing:

Parses incoming messages to identify commands such as create user, login user, create post, add friend, etc.

Calls appropriate methods in UserDBDatabase and PostDBDatabase.

Response Handling:

Sends success or failure messages back to the client.

Ensures EOM (End Of Message) is sent to signal the end of a response.

Testing:

Simulated client connections to test command handling.

Verified that concurrent client connections are managed properly.

Ensured server remains stable under multiple requests.

Relationships:

Interacts with UserDBDatabase and PostDBDatabase for database operations.

Communicates with client applications through sockets.

3.9. Connection
Package: GUI

Functionality:

Manages the network connection from the client side.

Handles sending and receiving messages to/from the server.

Key Methods:

public Connection(): Establishes a connection to the server.

public void println(String message): Sends a message to the server.

public boolean ready(): Checks if there is data to read from the server.

public String readLine(): Reads a line of text from the server.

Testing:

Verified connection establishment.

Tested message sending and receiving.

Ensured proper handling of server disconnections.

Relationships:

Used by SocialMedia class for communication with the server.
3.10. SocialMedia
Package: GUI

Functionality:

Acts as the main controller for the GUI application.

Manages GUI pages and handles interactions between the client and server.

Key Methods:

public SocialMedia(): Initializes the application, sets up connections, and starts polling the server.

public void sendMessage(String message): Sends a message to the server via Connection.

public void pollServer(): Periodically checks for server responses and processes them.

public void processServerResponse(String response): Parses server responses and updates the GUI accordingly.

public void displayImg(String imagePath, boolean postPage): Displays images in the GUI.

GUI Navigation Methods:

public void reset(boolean delete): Resets the application to the sign-in page.

public void returnMainPage(): Navigates back to the main page.

User Session Management:

public String getActiveUser(): Returns the currently logged-in user.

public String getSelectedPost(): Returns the currently selected post.

Testing:

Tested navigation between GUI pages.

Simulated server responses to ensure correct GUI updates.

Verified session management and handling of user actions.

Relationships:

Uses Connection for server communication.

Manages instances of SignInPage, MainPage, and PostPage.

3.11. SignInPage
Package: GUI

Functionality:

Provides the login and account creation interface.
Key Methods:

public SignInPage(SocialMedia sm): Constructor that initializes the sign-in page.

public void init(): Sets up GUI components.

public void login(): Handles user login.

public void createAccount(): Handles new account creation.

Testing:

Tested with valid and invalid credentials.

Ensured proper error messages are displayed.

Verified that navigation to MainPage occurs upon successful login.

Relationships:

Interacts with SocialMedia to send login and account creation requests.
3.12. MainPage
Package: GUI

Functionality:

Represents the main menu page after user login.

Allows users to create posts, select posts, manage friends, and navigate through the application.

Key Methods:

public MainPage(SocialMedia sm): Constructor that initializes the main page.

public void init(): Sets up GUI components.

public void createPost(): Opens a dialog to create a new post.

public void selectPost(): Allows users to select a post to view.

public void addFriend(), removeFriend(), block(): Manages friends and blocked users.

public void logout(): Logs out the user.

Testing:

Ensured all buttons and actions function correctly.

Verified that posts are created and sent to the server.

Tested friend management functionalities.

Relationships:

Interacts with SocialMedia to handle user actions and server communication.

Navigates to PostPage when a post is selected.

3.13. PostPage
Package: GUI

Functionality:

Displays a selected post in detail.

Allows users to interact with the post (e.g., upvote, downvote, comment).

Key Methods:

public PostPage(SocialMedia sm): Constructor that initializes the post page.

public void init(): Sets up GUI components.

public void upvote(), downvote(): Handles voting actions.

public void comment(): Allows users to add a comment.

public void selectComment(): Allows users to select a comment for further interaction.

Testing:

Verified that post details are displayed correctly.

Tested voting and commenting functionalities.

Ensured navigation back to MainPage works.

Relationships:

Interacts with SocialMedia for server communication.

Receives data to display from SocialMedia.

3.14. SignInPageInterface, MainPageInterface, PostPageInterface, SocialMediaInterface, ConnectionInterface
Functionality:

These interfaces define the methods that must be implemented in their respective classes.
Purpose:

Ensure consistency and enforce contracts for method implementations.
Testing:

Verified that all methods declared in interfaces are implemented.
Relationships:

Implemented by their respective GUI classes.
3.15. UserTest
Package: test

Functionality:

Contains unit tests for the User class.
Key Tests:

Creation of users with different constructors.

Testing friend and block management methods.

Verifying getters and setters.

Testing serialization and deserialization.

Testing:

Ensured all tests pass, confirming correct behavior of the User class.
Relationships:

Uses User class for testing.
3.16. PostTest
Package: test

Functionality:

Contains unit tests for the Post class.
Key Tests:

Creation of posts with different constructors.

Testing voting and commenting methods.

Verifying getters and setters.

Testing serialization and deserialization.

Testing:

Ensured all tests pass, confirming correct behavior of the Post class.
Relationships:

Uses Post and Comment classes for testing.
3.17. UserDBDatabaseTest
Package: test

Functionality:

Contains unit tests for the UserDBDatabase class.
Key Tests:

Testing user creation and login.

Testing friend and block management in the database.

Testing user deletion.

Testing:

Ensured database operations work correctly.

Verified data integrity after each operation.

Relationships:

Uses UserDBDatabase and User classes for testing.
3.18. PostDBDatabaseTest
Package: test

Functionality:

Contains unit tests for the PostDBDatabase class.
Key Tests:

Testing post creation, retrieval, and deletion.

Testing comment addition and deletion.

Testing voting mechanisms.

Testing:

Ensured database operations work correctly.

Verified data integrity after each operation.

Relationships:

Uses PostDBDatabase, Post, and Comment classes for testing.
