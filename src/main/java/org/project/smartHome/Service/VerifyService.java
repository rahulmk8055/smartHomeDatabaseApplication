//package org.project.smartHome.Service;
//
//
//import picocli.CommandLine;
//
//import java.util.Scanner;
//
//@CommandLine.Command(name = "app", mixinStandardHelpOptions = true,
//        description = "A simple CLI application using Picocli.",
//        subcommands = {HouseService.class})
//public class VerifyService {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        CommandLine cmd = new CommandLine(new VerifyService());
//
//        while (true) {
//            System.out.print("Enter command: ");
//            String input = scanner.nextLine();
//            if ("exit".equalsIgnoreCase(input)) {
//                break;
//            }
//            cmd.execute(input.split(" "));
//        }
//    }
//
//}
