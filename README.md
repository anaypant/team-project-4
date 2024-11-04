# Phase 1 - CS18000 Team 5
1. How to run and compile the project
   1. Compile and run Server.java
      2. "javac Server.java"
      3. "java Server"
   2. Compile and run SocialMedia.java
      2. "javac SocialMedia.java"
      3. "java SocialMedia"

2. Roles
   1. Anay Pant submitted the code on Vocareum

3. Class descriptions
   1. Constants is a class that contains constant variables used to manipulate the database. These include file path names, including user.txt and post.txt. Additionally, there is a delimiter included that is used to parse the files for the Users and Posts.
   2. Post is a class that defines a social media post object. Parses a string to get the creator, caption, and url. Also defines functions to display the post with a nice format, set upvotes and downvotes, as well as getters and setters for all the class variables.
   3. PostDBDatabase is a class that defines the behavior of methods related to the Post class and the database. Defines how to parse strings and call specific Post methods to create, delete, add comments, upvote, and downvote.
   4. PostInterface is an interface that outlines the methods of the Post class.
   5. PostDBInterface is an interface that outlines the methods of PostDBDatabase.
   6. Server establishes a network connection. Defines fail and success states for each command (creating posts, creating users, deleting posts, etc.) and returns messages based on if the command went through or not. It also handles any thread-based errors.
   7. User defines a social media user and any methods that go along with it. This includes blocking users and adding friends, as well as getters and setters for changing passwords, usernames, imagePath, etcetera. Parses strings for class variables including username, password, imagePath, friends (list of friends of the account), and blocked (list of accounts the user has blocked).
   8. UserDBDatabase is a class that defines the behavior of methods related to the User class and the database. Defines how to parse strings and call specific User methods to create, delete, add friends and block users.
   9. UserInterface is an interface that outlines the methods defined by the User class.
   10. UserDBInterface is an interface that outlines the methods defined by UserDBDatabase.
   11. Utils contains helper methods that are used throughout the program by various classes. These include arrListToString() which converts an ArrayList to a String as well as deletePost() and deleteUser().
   12. UserTest creates a User object. It creates a friends and blocked ArrayList. It will add friends and blocked users to the ArrayList. It will verify that blocked users can't be added to the friends list. It will also verify that the friends are being added successfully. Checks setters and getters to make sure they are returning appropriate values. Checks that the removeFriend() method actually removes the friend from the list. Checks that the toString() is of the appropriate format. Tests that a new User can be created from the parsed data.
   13. PostTest creates a Post object and gives it an initial number of upvotes and downvotes, as well as an ArrayList of comments that it passes into the Post. Checks the setters and getters to ensure the correct values are returned. Checks that the overridden equals() method is accurately comparing Posts (based on id). Tests the overridden toString() method and display() method to make sure the output is properly formatted. Checks that the parsed string creates a Post without error. Tests the comments format so that when a User comments, the comment will actually be added to the ArrayList of comments.
   14. UserDBDatabaseTest changes file path to ensure that the test file is being appended and not the actual database file. Creates a User and then tries to log in with the user. Checks the credentials of the created User. Tries to log in with a user that isn't in the database to make sure that no invalid users are allowed in. Checks addFriend() to make sure that the added friend gets written to the database correctly. Checks that blockUser() actually writes the blocked user to the database. Checks that the same friend can't be added twice. Tests that the same User can't be blocked twice.
   15. PostDBDatabaseTest changes file path to ensure that the test file is being appended and not the actual database file. Creates two Users. Checks that when a user creates a post, the post gets added to the post ArrayList. Checks that when a specific post is selected, that is the post that is displayed. Checks that deletePost() works properly and removes the post from the ArrayList. Verifies that addComment() and deleteComment() add and delete comments, respectively, and that the database is properly updated in either case. Checks if the database is updated properly following an upvote or downvote on a specific post. Checks the getPostsByUsername(String username) method and ensures the method returns an ArrayList of all posts made by a specific User with a specific username.