package com.zeejfps.sr.events;

public class Event {

    private boolean consumed;

    public void consume() {
        this.consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }

}
