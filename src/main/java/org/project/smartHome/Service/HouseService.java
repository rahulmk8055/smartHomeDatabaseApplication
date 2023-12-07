package org.project.smartHome.Service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.project.smartHome.UserSession.AuthenticationException;
import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;
import picocli.CommandLine;

@CommandLine.Command(name = "house",
        mixinStandardHelpOptions = true,
        description = "Do operations on house",
        subcommands = {HouseCreate.class, HouseSet.class})
public class HouseService {

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new HouseService());
        cmd.execute(args);
    }
}

@CommandLine.Command(name = "add",
        description = "add a new house",
        mixinStandardHelpOptions = true)
class HouseCreate implements Runnable {
    @Override
    public void run() {
        try (Connection conn = DataSource.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter house name: ");
            String houseName = scanner.nextLine();

            String SQL = "INSERT INTO house(house_name, owner) VALUES(?,?)";

            try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

                pstmt.setString(1, houseName);
                pstmt.setString(2, UserSession.getLoggedInUser());


                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("House " + houseName + " added");
                }

            }

        }

        catch (SQLException e) {
                System.out.println(String.format("Exception: %s", e.getMessage()));
                System.out.println();
        }
        catch (AuthenticationException e) {
            System.out.println("No user has logged in");
        }

    }
}


@CommandLine.Command(name = "set",
        description = "set the current house",
        mixinStandardHelpOptions = true)
class HouseSet implements Runnable {

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter house name to set: ");
        String houseName = scanner.nextLine();

        try (Connection conn = DataSource.getConnection()) {
            String query = "SELECT houseId FROM house WHERE house_name = ? AND owner = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, houseName);
                stmt.setString(2, UserSession.getLoggedInUser());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int houseId = rs.getInt("houseId");
                        UserSession.setHouseId(houseId);
                    } else {
                        System.out.println("Try Again");
                    }
                }
            }
        } catch (SQLException | AuthenticationException e) {
            e.printStackTrace();
        }
    }
}