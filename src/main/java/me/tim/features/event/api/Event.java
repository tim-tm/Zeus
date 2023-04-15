package me.tim.features.event.api;

import java.util.ArrayList;

public class Event {
    public Event call() {
        final ArrayList<EventData> data = EventManager.get(this.getClass());

        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                EventData datum = data.get(i);
                try {
                    datum.target.invoke(datum.source, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }
}
