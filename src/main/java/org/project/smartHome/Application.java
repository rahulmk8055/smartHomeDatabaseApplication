package org.project.smartHome;

import org.project.smartHome.Service.HouseService;
import org.project.smartHome.Service.VerifyService;
import picocli.CommandLine;

import java.util.Scanner;


@CommandLine.Command(name = "app", mixinStandardHelpOptions = true, version = "1.0",
        description = "A simple CLI application using Picocli.",
        subcommands = {HouseService.class})
public class Application {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CommandLine cmd = new CommandLine(new VerifyService());

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }
            cmd.execute(input.split(" "));
        }
    }
}
