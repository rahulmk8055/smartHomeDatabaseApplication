package org.project.smartHome.Service;


import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;
import picocli.CommandLine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static org.project.smartHome.Utils.Utils.getRoomsList;

@CommandLine.Command(name = "room",
        mixinStandardHelpOptions = true,
        description = "Do operations on house",
        subcommands = {RoomCreate.class, RoomList.class})
public class RoomService {

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new RoomService());
        cmd.execute(args);
    }

}

@CommandLine.Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a new room")
class RoomCreate implements Runnable {
    @Override
    public void run() {
        try (Connection conn = DataSource.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter room name: ");
            String roomName = scanner.nextLine();

            String SQL = "INSERT INTO room(roomName, houseId) VALUES(?,?)";


            try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

                pstmt.setString(1, roomName);
                pstmt.setInt(2, UserSession.getHouseId());


                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Room " + roomName + " added");
                }

            }

        }  catch (SQLException e) {
            System.out.println(String.format("Exception: %s", e.getMessage()));
            System.out.println();
        }
    }
}

@CommandLine.Command(name = "list",
        mixinStandardHelpOptions = true,
        description = "Lists Rooms")
class RoomList implements Runnable {

    @Override
    public void run() {
        List<String> roomNames = getRoomsList();
        for (String room : roomNames) {
            System.out.println(room);
        }
    }
}

