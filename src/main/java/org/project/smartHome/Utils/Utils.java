package org.project.smartHome.Utils;

import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class Utils {
    public static List<String> getRoomsList() {
        List<String> roomNames = new ArrayList<>();
        try (Connection conn = DataSource.getConnection()) {
            String query = "SELECT roomName FROM room WHERE houseId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getHouseId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()) {
                        roomNames.add(rs.getString("roomName"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());

        }
        return roomNames;
    }

    public static String getValidRoomNameFromUser() {
        Scanner scanner = new Scanner(System.in);

        List<String> roomNames = getRoomsList();
        System.out.println("Valid room names");
        for (String room : roomNames) {
            System.out.println(room);
        }

        System.out.print("Enter room name: ");
        String roomName = scanner.nextLine();


        if (roomNames.contains(roomName)) {
            return roomName;
        }

        return null;

    }


    public static List<String> getDevicesList() {
        List<String> roomNames = new ArrayList<>();
        try (Connection conn = DataSource.getConnection()) {
            String query = "SELECT roomName FROM room WHERE houseId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getHouseId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()) {
                        roomNames.add(rs.getString("roomName"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());

        }
        return roomNames;
    }


}
