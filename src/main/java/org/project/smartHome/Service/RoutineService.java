package org.project.smartHome.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.tuple.Pair;
import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;
import picocli.CommandLine;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

import static org.project.smartHome.Utils.Utils.*;

@CommandLine.Command(name = "routine",
        mixinStandardHelpOptions = true,
        description = "Do operations on routine",
        subcommands = {RoutineCreate.class, RoutinesList.class, RoutineDeactivate.class, RoutineActivate.class})
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

            List<Pair<String, String>> deviceList = getDevicesList();
            prettyPrintDeviceNames(deviceList);
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

@CommandLine.Command(name = "list",
        mixinStandardHelpOptions = true,
        description = "List the routines")
class RoutinesList implements Runnable {

    @Override
    public void run() {
        listRoutines();
    }
}

@CommandLine.Command(name = "deactivate",
        mixinStandardHelpOptions = true,
        description = "Deactivate the routine")
class RoutineDeactivate implements Runnable {

    @Override
    public void run() {
        updateRoutineState(false);
    }
}

@CommandLine.Command(name = "activate",
        mixinStandardHelpOptions = true,
        description = "Activate the routine")
class RoutineActivate implements Runnable {
    @Override
    public void run() {
        updateRoutineState(true);
    }
}