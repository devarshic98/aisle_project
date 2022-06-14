package main.java.com.conference.management;

import main.java.com.conference.management.bo.Conference;
import main.java.com.conference.management.bo.Talk;
import main.java.com.conference.management.enums.DataOutputEnum;
import main.java.com.conference.management.enums.DataSourceEnum;
import main.java.com.conference.management.exceptions.UnsupportedDestinationException;
import main.java.com.conference.management.exceptions.UnsupportedSourceException;
import main.java.com.conference.management.util.ConferenceUtils;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        ConferenceManager conferenceManager = new ConferenceManager();
        List<Talk> talkList = null;
       
        try {
            talkList = conferenceManager.fetchTalksListFromSource(DataSourceEnum.FILE);
        } catch (UnsupportedSourceException e){
            System.err.println(e.getMessage());
        }

        if(talkList == null || talkList.size() == 0)
            return;

       
        ConferenceUtils.printTalks(talkList);

        
        Conference conference = conferenceManager.processAndScheduleTalks(talkList);

       
        try {
            conferenceManager.outputConferenceSchedule(conference, DataOutputEnum.CONSOLE);
        } catch (UnsupportedDestinationException e){
            System.err.println(e.getMessage());
        }

    }
}
