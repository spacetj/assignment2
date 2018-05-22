# Social Network GUI
A JavaFX project which utilises the MVC model to build a social media GUI.

# Constraints of the project
- There are three type of person: Adult (over 16), Child (3 - 16 year old) and YoungChild (2 year old or younger).
- Each person has a “profile” in the social network that keeps track of persons’ name, age, gender, profile picture, status and list of connections on his/her profile.
- A child can be a friend ONLY to another child who is also younger than 16 and from a different family with an age difference of 3 or less.
- Other than friend and parent relations, there are other types of connections such as sibling, colleague and classmater. 
- Colleague relation only applies on Adults. 
- Classmate relation can apply on Adults and Children, but not young children.
 
# Functionality of the project
- If a people.txt file is present, the SQLite tables will be emptied and repopulated with the entries in the file.
- If a file and database if not available, then the application will not show any data or add any data.

# Getting Started
To get started:
 Clone the project -> Open the directory in mac or unix terminal -> Type 'make' and press enter.
To clean class file:
 Type 'make clean' and press enter.

# Prerequisites

No prerequisites needed to run the project, although a UNIX or Mac terminal is required in order to run the Makefile

Built With
- Java 8
- JavaFX
- SQLite Database
- JUnit 5 Unit Tests
- Makefile

Enhancements

The project will be converted to a Maven project in the future.

Author

Tejas Cherukara

