package me.tim.util.render.animation;

import me.tim.util.Timer;

public class Animation {
    private final Timer timer;
    private final boolean autoState;

    private long delay;
    private AnimationType type;
    private AnimationState state;

    public Animation(long delay, AnimationType type, AnimationState initialState) {
        this.timer = new Timer();
        this.delay = delay;
        this.type = type;
        this.state = initialState;
        this.autoState = false;
    }

    public Animation(long delay, AnimationType type, AnimationState initialState, boolean autoState) {
        this.timer = new Timer();
        this.delay = delay;
        this.type = type;
        this.state = initialState;
        this.autoState = autoState;
    }

    /**
     * Handle the animation given the easing function.
     * @return Floating-point value ranging between 0-1
     */
    public float animate() {
        float val = (float) this.timer.getElapsedTime() / this.delay;
        if (this.timer.elapsed(this.delay)) {
            if (autoState) {
                switch (this.state) {
                    case IN:
                        this.state = AnimationState.OUT;
                        break;
                    case OUT:
                        this.state = AnimationState.IN;
                        break;
                }
                this.timer.reset();
            } else {
                val = 1;
            }
        }

        return this.getEquation(this.type, this.state, val).floatValue();
    }

    public void reset() {
        this.timer.reset();
    }

    /**
     * @see <a href="https://easings.net">All easing functions from here.</a>
     *
     * @param type Type of animation.
     * @param state State of animation, IN or OUT.
     * @param current Value to animate on, value between 0 and 1.
     * @return Animation value between 0 and 1.
     */
    private Double getEquation(AnimationType type, AnimationState state, float current) {
        switch (state) {
            case IN: {
                switch (type) {
                    case SINE:
                        return 1 - Math.cos((current * Math.PI) / 2);
                    case CUBIC:
                        return Math.pow(current, 3);
                    case QUINT:
                        return Math.pow(current, 5);
                    case CIRC:
                        return 1 - Math.sqrt(1 - Math.pow(current, 2));
                    case ELASTIC:
                        double temp = (Math.PI * 2) / 3;
                        return current == 0 ? 0 : current == 1 ? 1 : -Math.pow(2, 10 * current - 10) * Math.sin((current * 10 - 10.75) * temp);
                    case QUAD:
                        return Math.pow(current, 2);
                    case QUART:
                        return Math.pow(current, 4);
                    case EXPO:
                        return current == 0 ? 0 : Math.pow(2, 10 * current - 10);
                    case BACK:
                        double cnst = 1.70158;
                        double temp1 = cnst + 1;
                        return temp1 * current * current * current - cnst * current * current;
                    case BOUNCE:
                        return 1 - getEquation(AnimationType.BOUNCE, AnimationState.OUT, 1 - current);
                }
            }
            case OUT: {
                switch (type) {
                    case SINE:
                        return Math.sin((current * Math.PI) / 2);
                    case CUBIC:
                        return 1 - Math.pow(1 - current, 3);
                    case QUINT:
                        return 1 - Math.pow(1 - current, 5);
                    case CIRC:
                        return Math.sqrt(1 - Math.pow(current - 1, 2));
                    case ELASTIC:
                        double temp = (Math.PI * 2) / 3;
                        return current == 0 ? 0 : current == 1 ? 1 : Math.pow(2, -10 * current) * Math.sin((current * 10 - 0.75) * temp) + 1;
                    case QUAD:
                        return 1 - Math.pow(1 - current, 2);
                    case QUART:
                        return 1 - Math.pow(1 - current, 4);
                    case EXPO:
                        return current == 1 ? 1 : 1 - Math.pow(2, -10 * current);
                    case BACK:
                        double cnst = 1.70158;
                        double temp1 = cnst + 1;
                        return 1 + temp1 * Math.pow(current - 1, 3) + cnst * Math.pow(current - 1, 2);
                    case BOUNCE:
                        double d = 7.5625;
                        double d1 = 2.75;

                        if (current < 1 / d1) {
                            return d * current * current;
                        } else if (current < 2 / d1) {
                            return d * (current -= 1.5 / d1) * current + 0.75;
                        } else if (current < 2.5 / d1) {
                            return d * (current -= 2.25 / d1) * current + 0.9375;
                        } else {
                            return d * (current -= 2.625 / d1) * current + 0.984375;
                        }
                }
            }
        }
        return 1d;
    }

    public Timer getTimer() {
        return timer;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public AnimationType getType() {
        return type;
    }

    public void setType(AnimationType type) {
        this.type = type;
    }

    public AnimationState getState() {
        return state;
    }

    public void setState(AnimationState state) {
        this.state = state;
    }

    public boolean isAutoState() {
        return autoState;
    }

    public enum AnimationState { IN, OUT }
}
