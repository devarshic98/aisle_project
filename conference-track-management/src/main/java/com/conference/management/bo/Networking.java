package main.java.com.conference.management.bo;

import main.java.com.conference.management.ConferenceManagementConfig;

import java.text.SimpleDateFormat;

public class Networking extends Event {

    public Networking () {
        super(ConferenceManagementConfig.NETWORKING_START_TIME, "Networking Event", 0);
    }
}
