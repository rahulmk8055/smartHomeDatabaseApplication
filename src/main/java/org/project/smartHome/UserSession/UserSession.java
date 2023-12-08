package org.project.smartHome.UserSession;

public class UserSession {
    private static String loggedInUser;
    private static int houseId;


    public static void setLoggedInUser(String username) {
        loggedInUser = username;
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static int getHouseId() {
        return houseId;
    }

    public static void setHouseId(int houseId) {
        UserSession.houseId = houseId;
    }
}

