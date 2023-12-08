package org.project.smartHome.Crons;

import org.project.smartHome.UserSession.UserSession;
import org.project.smartHome.db.DataSource;

import java.sql.*;

public class NotificationCron implements Runnable{

    public void run() {
        try (Connection conn = DataSource.getConnection()) {
            String SQL = "CALL GetUserNotifications(?)";

            while(UserSession.getLoggedInUser()!=null && UserSession.getHouseId()!=0) {
                Thread.sleep(5000);

                try (CallableStatement pstmt = conn.prepareCall(SQL)) {
                    pstmt.setString(1, UserSession.getLoggedInUser());
                    pstmt.execute();
                    ResultSet rs = pstmt.getResultSet();

                    if (rs.next()) {
                        System.out.println();
                        System.out.println();
                        System.out.println("NOTIFICATION ALERT!!!");
                        System.out.println();
                        do {
                            String routineName = rs.getString("routine_name");
                            Timestamp notificationTime = rs.getTimestamp("notification_time");
                            System.out.println(routineName + " has executed @ Time: " + notificationTime);
                            System.out.println();
                            // process the result set
                        } while (rs.next());
                        System.out.println("PRESS ENTER!! \n");

                    }

                }
            }

        } catch (SQLException | InterruptedException e) {
            System.out.println(String.format("Exception: %s", e.getMessage()));
            System.out.println();
        }
    }
}
