package org.project.smartHome.Service;
import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;
import picocli.CommandLine;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import static org.project.smartHome.Utils.Utils.getValidRoomNameFromUser;

@CommandLine.Command(name = "device",
        mixinStandardHelpOptions = true,
        description = "Do operations on house",
        subcommands = {DeviceAdd.class})
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
