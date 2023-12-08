package org.project.smartHome.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
        prettyPrintWithSerialNumbers(roomNames);
        System.out.println();

        System.out.print("Enter room name: ");
        String roomName = scanner.nextLine();

        if (roomNames.contains(roomName)) {
            return roomName;
        }

        return null;
    }
    public static  List<Pair<String, String>> getDevicesList() {
        List<Pair<String, String>> deviceNames = new ArrayList<>();
        try (Connection conn = DataSource.getConnection()) {
            String query = "CALL GetUserDevices(?)";
            try (CallableStatement stmt = conn.prepareCall(query)) {
                stmt.setInt(1, UserSession.getHouseId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()) {
                        deviceNames.add(new ImmutablePair<>(rs.getString("device_name"), rs.getString("state")));
//                        deviceNames.add(rs.getString("device_name"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());

        }
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

    public static void prettyPrintDeviceNameAndState(List<Pair<String, String>> devices) {
        if (devices == null || devices.isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }

        int serialNumber = 1;
        for (Pair<String, String> device : devices) {
            System.out.printf("%d. %s-%s\n", serialNumber++, device.getKey(), device.getValue());
        }
    }

    public static void prettyPrintDeviceNames(List<Pair<String, String>> devices) {
        if (devices == null || devices.isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }

        int serialNumber = 1;
        for (Pair<String, String> device : devices) {
            System.out.printf("%d. %s%n", serialNumber++, device.getKey());
        }
    }

    public static String getActionsDataJson(String input, List<Pair<String, String>> deviceList) throws JsonProcessingException {
        Map<Integer, String> actionsData = extractActionsData(input);
        List<DeviceActions> actionsJson = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : actionsData.entrySet()) {
            int index = entry.getKey() - 1;
            actionsJson.add(new DeviceActions(deviceList.get(index).getKey(), entry.getValue()));
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

    public static void listRoutines() {
        try (Connection conn = DataSource.getConnection()) {

            String SQL = "CALL GetUserRoutines(?)";
            try (CallableStatement pstmt = conn.prepareCall(SQL)) {
                pstmt.setString(1, UserSession.getLoggedInUser());
                pstmt.execute();

                ResultSet rs = pstmt.getResultSet();
                if (rs.next()) {
                    System.out.println(String.format("%-20s%-30s%-20s%-20s", "routineName", "notificationTime", "frequency", "isActive", "deviceActions"));
                    System.out.println();
                    do {
                        String routineName = rs.getString("routineName");
                        Timestamp notificationTime = rs.getTimestamp("startTime");
                        String frequency = rs.getString("frequency");
                        String deviceActions = rs.getString("deviceActions");
                        String isActive = rs.getString("isActive");
                        System.out.println(String.format("%-20s%-30s%-20s%-20s", routineName, notificationTime, frequency, isActive, deviceActions));
                        System.out.println();
                        // process the result set
                    } while (rs.next());
                }

            }


        } catch (SQLException  e) {
            System.out.println(String.format("Exception: %s", e.getMessage()));
            System.out.println();
        }
    }

    public static void updateRoutineState(Boolean isActive) {
        try (Connection conn = DataSource.getConnection()) {

            listRoutines();

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Routine name: ");
            String routineName = scanner.nextLine();

            String SQL = "UPDATE routine set isActive = ? where routineName = ? AND userId = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
                pstmt.setBoolean(1, isActive);
                pstmt.setString(2, routineName);
                pstmt.setString(3, UserSession.getLoggedInUser());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Routine " + routineName + " updated");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
