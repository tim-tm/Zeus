package me.tim.features.event;

import me.tim.features.event.api.EventCancelable;

public class EventRenderItem extends EventCancelable {
    private float f, f1;

    public EventRenderItem(float f, float f1) {
        this.f = f;
        this.f1 = f1;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public float getF1() {
        return f1;
    }

    public void setF1(float f1) {
        this.f1 = f1;
    }
}
