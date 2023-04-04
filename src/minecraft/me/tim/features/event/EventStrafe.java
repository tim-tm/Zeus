package me.tim.features.event;

import me.tim.features.event.api.EventCancelable;

public class EventStrafe extends EventCancelable {
    private float yaw;
    private float forward, strafe, friction;

    public EventStrafe(float yaw, float forward, float strafe, float friction) {
        this.yaw = yaw;
        this.forward = forward;
        this.strafe = strafe;
        this.friction = friction;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }
}
