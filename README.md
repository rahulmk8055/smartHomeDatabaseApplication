## Smart Home Automation System


1. Install Java JDK 17 if not installed. \
   Refer: https://www.oracle.com/java/technologies/downloads/#java17

2. Navigate into jars directory into project directory
```bash
cd smartHome
```
3. Run using gradle wrapper(Only verified in macos)
   This should produce a jar named smartHome-1.0-SNAPSHOT.jar at the /jars directory
```bash
 ./gradlew clean jar
```

5. Navigate into jars directory
```bash
 cd jars
```

6. Run the jar
```bash
 java -jar smartHome-1.0-SNAPSHOT.jar
```

7. In case the above step does not work try this alternative (this does not look good in a CLI) \
   Navigate to project root directory
```bash
 ./gradlew clean run
```


Example Output

```bash
Login or Create a New User
Enter command: --help
Usage: app [-hV] [COMMAND]
A simple CLI application using Picocli.
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  user   Do operations on user
  house  Do operations on house

Login or Create a New User
Enter command: user --help
Usage: app user [-hV] [COMMAND]
Do operations on user
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  add    create a new user
  login  login a user

Login or Create a New User
Enter command: user login
Enter username: rmusalay
Enter password: password
User Logged in successfully

Set the House Name
Enter command:
```
