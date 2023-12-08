package org.project.smartHome.Service;
import org.apache.commons.lang3.tuple.Pair;
import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;
import picocli.CommandLine;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static org.project.smartHome.Utils.Utils.*;

@CommandLine.Command(name = "device",
        mixinStandardHelpOptions = true,
        description = "Do operations on house",
        subcommands = {DeviceAdd.class, DeviceList.class, DeviceUpdate.class})
public class DeviceService {
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new DeviceService());
        cmd.execute(args);
    }

}
@CommandLine.Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a new device")
class DeviceAdd implements Runnable {

    @Override
    public void run() {
        try (Connection conn = DataSource.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter device name: ");
            String deviceName = scanner.nextLine();

            String roomName = null;
            while(roomName == null) {
                roomName = getValidRoomNameFromUser();
            }

            String SQL = "CALL AddDevice(?, ?, ?)";


            try (CallableStatement pstmt = conn.prepareCall(SQL)) {
                pstmt.setString(1, deviceName);
                pstmt.setString(2, roomName);
                pstmt.setInt(3, UserSession.getHouseId());
                pstmt.execute();
                System.out.println("Device added successfully.");

            }

        } catch (SQLException e) {
            System.out.println(String.format("Exception: %s", e.getMessage()));
            System.out.println();
        }
    }
}


@CommandLine.Command(name = "list",
        mixinStandardHelpOptions = true,
        description = "list devices")
class DeviceList implements Runnable {

    @Override
    public void run() {
        List<Pair<String, String>> devicesList = getDevicesList();

        prettyPrintDeviceNameAndState(devicesList);
    }
}

@CommandLine.Command(name = "update",
        mixinStandardHelpOptions = true,
        description = "update device state")
class DeviceUpdate implements Runnable {

    @Override
    public void run() {
        List<Pair<String, String>> devicesList = getDevicesList();

        prettyPrintDeviceNames(devicesList);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter device name: ");
        String deviceName = scanner.nextLine();

        System.out.print("Enter device state ON/OFF: ");
        String state = scanner.nextLine();


        try (Connection conn = DataSource.getConnection()) {

            String SQL = "CALL UpdateDeviceState(?, ?, ?)";

            try (CallableStatement stmt = conn.prepareCall(SQL)) {
                stmt.setInt(1, UserSession.getHouseId());
                stmt.setString(2, deviceName);
                stmt.setString(3, state);
                stmt.execute();
                System.out.println("Device updated successfully.");

            }

        } catch (SQLException e) {
            System.out.println(String.format("Exception: %s", e.getMessage()));
            System.out.println();
        }
    }
}