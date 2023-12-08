package org.project.smartHome.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.project.smartHome.UserSession.AuthenticationException;
import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;
import picocli.CommandLine;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static org.project.smartHome.Utils.Utils.*;

@CommandLine.Command(name = "routine",
        mixinStandardHelpOptions = true,
        description = "Do operations on routine",
        subcommands = {RoutineCreate.class})
public class RoutineService {
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new RoutineService());
        cmd.execute(args);
    }
}
@CommandLine.Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a routine")
class RoutineCreate implements Runnable {

    @Override
    public void run() {
        try (Connection conn = DataSource.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Routine name: ");
            String routineName = scanner.nextLine();
            System.out.println();


            System.out.print("Enter Start Time: ");
            String startTime = scanner.nextLine();
            System.out.println();

            System.out.println("\"daily\", \"weekly\", \"monthly\"");
            System.out.print("Enter Frequency: ");
            String frequency = scanner.nextLine();
            System.out.println();

            List<String> deviceList = getDevicesList();
            prettyPrintWithSerialNumbers(deviceList);
            System.out.println("Configure the device actions in the format DeviceId:ON/OFF");
            String actionsInput = scanner.nextLine();
            System.out.println();

            String actionsJson = getActionsDataJson(actionsInput, deviceList);

            String SQL = "CALL CreateRoutineAndActions(?, ?, ?, ?, ?)";


            try (CallableStatement pstmt = conn.prepareCall(SQL)) {
                pstmt.setString(1, UserSession.getLoggedInUser());
                pstmt.setString(2, routineName);
                pstmt.setString(3, startTime);
                pstmt.setString(4, frequency);
                pstmt.setString(5, actionsJson);
                pstmt.execute();
                System.out.println("Routine added successfully.");
                System.out.println();
            }

        } catch (SQLException | JsonProcessingException  e) {
            System.out.println(String.format("Exception: %s", e.getMessage()));
            System.out.println();
        }
    }
}