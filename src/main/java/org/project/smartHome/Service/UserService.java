package org.project.smartHome.Service;

import org.project.smartHome.Crons.NotificationCron;
import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;
import picocli.CommandLine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CommandLine.Command(name = "user",
        mixinStandardHelpOptions = true,
        description = "Do operations on user",
        subcommands = {UserCreate.class, UserLogin.class})
public class UserService {
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new UserService());
        cmd.execute(args);
    }
}

@CommandLine.Command(name = "add",
        description = "create a new user",
        mixinStandardHelpOptions = true)
class UserCreate implements Runnable {


    @Override
    public void run() {
        // Do whatever create is supposed to do
        try (Connection conn = DataSource.getConnection()) {

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();

            String SQL = "INSERT INTO user VALUES(?,?,?,?)";

            try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, firstName);
                pstmt.setString(4, lastName);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("User " + username + " added");
                }

            }

        } catch (SQLException e) {
            System.out.println(String.format("Exception: %s", e.getMessage()));
            System.out.println();
        }
    }
}

@CommandLine.Command(name = "login",
        description = "login a user",
        mixinStandardHelpOptions = true)
class UserLogin implements Runnable {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Connection conn = DataSource.getConnection()) {
            String query = "SELECT password FROM user WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        // Compare the stored password with the provided one
                        if (storedPassword.equals(password)) {
                            UserSession.setLoggedInUser(username);
                            System.out.println("User Logged in successfully");
                            NotificationCron notificationCron = new NotificationCron();
                            executorService.execute(notificationCron);
                            executorService.shutdown();
                        } else {
                            System.out.println("Try Again");
                        }
                    } else {
                        System.out.println("Try Again");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

