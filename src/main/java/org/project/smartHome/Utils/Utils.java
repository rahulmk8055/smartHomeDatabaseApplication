package org.project.smartHome.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.smartHome.Entity.DeviceActions;
import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;

import java.sql.*;
import java.util.*;

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

    public static List<String> getHouseList() {
        List<String> roomNames = new ArrayList<>();
        try (Connection conn = DataSource.getConnection()) {
            String query = "SELECT house_name FROM house WHERE owner = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, UserSession.getLoggedInUser());
                try (ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()) {
                        roomNames.add(rs.getString("house_name"));
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
        List<String> deviceNames = new ArrayList<>();
        try (Connection conn = DataSource.getConnection()) {
            String query = "CALL GetUserDevices(?)";
            try (CallableStatement stmt = conn.prepareCall(query)) {
                stmt.setString(1, UserSession.getLoggedInUser());
                try (ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()) {
                        deviceNames.add(rs.getString("device_name"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());

        }
//        catch (AuthenticationException e) {
//            System.out.println("User Not logged In");
//        }
        return deviceNames;
    }

    public static void prettyPrintWithSerialNumbers(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }

        int serialNumber = 1;
        for (String string : strings) {
            System.out.printf("%d. %s%n", serialNumber++, string);
        }
    }

    public static String getActionsDataJson(String input, List<String> deviceList) throws JsonProcessingException {
        Map<Integer, String> actionsData = extractActionsData(input);
        List<DeviceActions> actionsJson = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : actionsData.entrySet()) {
            int index = entry.getKey() - 1;
            actionsJson.add(new DeviceActions(deviceList.get(index), entry.getValue()));
        }
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(actionsJson);

    }

    public static Map<Integer, String> extractActionsData(String input) {
        Map<Integer, String> dataMap = new HashMap<>();
        String[] pairs = input.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.trim().split(":");
            Integer key = Integer.parseInt(keyValue[0]);
            String value = keyValue[1];
            dataMap.put(key, value);
        }
        return dataMap;
    }

}
