package org.project.smartHome.Entity;

public class DeviceActions {
    String device;
    String action;

    public DeviceActions(String device, String action) {
        this.device = device;
        this.action = action;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
