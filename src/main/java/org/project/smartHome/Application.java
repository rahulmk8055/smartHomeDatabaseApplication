package org.project.smartHome;

import org.project.smartHome.Service.*;
import org.project.smartHome.UserSession.UserSession;
import picocli.CommandLine;

import java.util.Map;
import java.util.Scanner;

@CommandLine.Command(name = "app", mixinStandardHelpOptions = true,
        description = "A simple CLI application using Picocli.",
        subcommands = {UserService.class, HouseService.class})
public class Application {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CommandLine cmd = new CommandLine(new Application());

        while (true) {
            System.out.println();

            if (UserSession.getLoggedInUser() == null && UserSession.getHouseId() == 0 ) {

                System.out.println("Login or Create a New User");

            } else if (UserSession.getLoggedInUser() != null && UserSession.getHouseId() == 0 ) {

                System.out.println("Set the House Name");

                // If user is logged in and house is selected, expose the other commands
            } else if(UserSession.getLoggedInUser() != null && UserSession.getHouseId() != 0) {

                Map<String, CommandLine> subcommands = cmd.getSubcommands();

                if (!subcommands.containsKey("room")) {
                    cmd.addSubcommand(RoomService.class);
                }

                if (!subcommands.containsKey("device")) {
                    cmd.addSubcommand(DeviceService.class);
                }

                if (!subcommands.containsKey("routine")) {
                    cmd.addSubcommand(RoutineService.class);
                }
            }
            System.out.print("Enter command: ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }
            cmd.execute(input.split(" "));
        }
    }
}
