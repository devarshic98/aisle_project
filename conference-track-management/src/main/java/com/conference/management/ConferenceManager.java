package main.java.com.conference.management;

import main.java.com.conference.management.bo.*;
import main.java.com.conference.management.enums.DataOutputEnum;
import main.java.com.conference.management.enums.DataSourceEnum;
import main.java.com.conference.management.exceptions.UnsupportedDestinationException;
import main.java.com.conference.management.exceptions.UnsupportedSourceException;
import main.java.com.conference.management.io.ConferenceFileSourceManager;
import main.java.com.conference.management.io.ConsoleOutputManager;
import main.java.com.conference.management.util.ConferenceUtils;

import java.io.FileNotFoundException;
import java.util.*;

public class ConferenceManager {

    public List<Talk> fetchTalksListFromSource(DataSourceEnum dataSourceEnum) throws UnsupportedSourceException{

        // Not exactly a factory. This will create an instance of the required SourceManager
        if (dataSourceEnum.equals(DataSourceEnum.FILE)) {
            ConferenceFileSourceManager sourceManager = new ConferenceFileSourceManager();
            try {
                return sourceManager.fetchTalks();
            } catch (FileNotFoundException e) {

                return null;
            }
        } else {
            throw new UnsupportedSourceException("Source type not valid");
        }
    }

    public void outputConferenceSchedule(Conference conference, DataOutputEnum dataOutputEnum) throws UnsupportedDestinationException {

        // Not exactly a factory. This will create an instance of the required OutputManager
        if (dataOutputEnum.equals(DataOutputEnum.CONSOLE.CONSOLE)) {
            ConsoleOutputManager outputManager = new ConsoleOutputManager();
            outputManager.printSchedule(conference);
        } else {
            throw new UnsupportedDestinationException("Destination not valid.");
        }
    }


    public Conference processAndScheduleTalks(List<Talk> talkList){
        Conference conference = new Conference();

        
        Collections.sort(talkList, new TalksCompare());
        int trackCount = 0;

      
        while (0 != talkList.size()) {

            
            Slot morningSlot = new Slot(ConferenceManagementConfig.MORNING_SLOT_DURATION_MINUTES, ConferenceManagementConfig.TRACK_START_TIME);
            fillSlot(morningSlot, talkList);

           
            Slot lunchSlot = new Slot(ConferenceManagementConfig.LUNCH_DURATION_MINUTES, ConferenceManagementConfig.LUNCH_START_TIME);
            lunchSlot.addEvent(new Lunch());

           
            Slot afternoonSlot = new Slot(ConferenceManagementConfig.AFTERNOON_SLOT_DURATION_MINUTES,
                    ConferenceManagementConfig.POST_LUNCH_SLOT_START_TIME);
            fillSlot(afternoonSlot, talkList);

           
            Slot networkingSlot = new Slot(ConferenceManagementConfig.NETWORKING_DURATION_MINUTES,
                    ConferenceManagementConfig.NETWORKING_START_TIME);
            networkingSlot.addEvent(new Networking());

            
            Track track = new Track(++trackCount);
            track.addSlot(morningSlot);
            track.addSlot(lunchSlot);
            track.addSlot(afternoonSlot);
            track.addSlot(networkingSlot);
            conference.addTrack(track);
        }

        return conference;
    }

    private void fillSlot(Slot slot, List<Talk> talks) {
       
        Calendar currentStartTime = slot.getStartTime();
        for (Iterator<Talk> talksIterator = talks.iterator(); talksIterator.hasNext();) {
            Talk talk = talksIterator.next();
            if (slot.hasRoomFor(talk)) {
                
                slot.addEvent(new Event(currentStartTime, talk.getTitle(), talk.getDurationInMinutes()));
                
                currentStartTime = ConferenceUtils.getNextStartTime(currentStartTime, talk);
                
                talksIterator.remove();
            }
        }
    }

}
