package moe_nya.findcheers;

import java.util.ArrayList;
import java.util.List;

import moe_nya.findcheers.artifacts.Events;


public class DataService {
    /**
     * Fake all the event data for now. We will refine this and connect
     * to our backend later.
     */
    public static List<Events> getEventData() {
        List<Events> eventData = new ArrayList<Events>();
        for (int i = 0; i < 10; ++i) {
            eventData.add(
                    new Events("Event", "1184 W valley Blvd, CA 90101",
                            "This is a huge event"));
        }
        return eventData;
    }

}
