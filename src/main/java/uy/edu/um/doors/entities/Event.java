package uy.edu.um.doors.entities;

import uy.edu.um.tad.list.MyList;

public class Event {

    private String type;
    private MyList<String> instructions;

    public Event(String type, MyList<String> instructions) {
        this.type = type;
        this.instructions = instructions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MyList<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(MyList<String> instructions) {
        this.instructions = instructions;
    }
}