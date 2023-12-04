package org.project.smartHome.Service;
import java.sql.Connection;
import java.sql.SQLException;

import org.project.smartHome.db.DataSource;
import picocli.CommandLine;

@CommandLine.Command(name = "house", description = "Do operations on house")
public class HouseService implements Runnable {

    @CommandLine.Option(names = {"-n", "--name"}, required = true, description = "The name of the person to say goodbye to.")
    private String name;

    @Override
    public void run() {
        System.out.printf("Goodbye, %s!\n", name);

        try (Connection conn = DataSource.getConnection()) {
            System.out.println("This is user service");
            System.out.println("Connected to the database successfully.");
            System.out.printf("Hello, %s!\n", name);



        } catch (SQLException e) {
            System.out.println(String.format("Exception: %s", e.getMessage()));
        }
    }
}
