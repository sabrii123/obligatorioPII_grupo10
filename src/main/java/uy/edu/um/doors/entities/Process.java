package uy.edu.um.doors.entities;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.doors.entities.Event;

public class Process implements Comparable<Process> {

    private int pid;
    private String name;
    private User user;
    private int priority;
    private String state;
    private MyList<Event> events;

    public Process() {
    }

    public Process(int pid, String name, User user, MyList<Event> events) {
        this.pid = pid;
        this.name = name;
        this.user = user;
        this.events = events;
        this.priority = 0;
        this.state = "new";
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public MyList<Event> getEvents() {
        return events;
    }

    public void setEvents(MyList<Event> events) {
        this.events = events;
    }

    @Override
    public int compareTo(Process o) {
        return this.priority - o.priority;
    }

    public int calculatePriority() {
        int cpuEvents = 0;
        int ramEvents = 0;
        int diskEvents = 0;

        int i = 0;

        while (i < this.getEvents().size()) {
            Event event = this.getEvents().get(i);

            if (event.getType().equals("CPU") ) {
                cpuEvents++;
            } else if (event.getType().equals("RAM")) {
                ramEvents++;
            } else if (event.getType().equals("DISK")) {
                diskEvents++;
            }

            i++;
        }

        int totalEvents = cpuEvents + ramEvents + diskEvents;

        if (totalEvents == 0) {
            return 0;
        }

        int userWeight;

        if (this.getUser().getType().equals("admin")) {
            userWeight = 32;
        } else {
            userWeight = 16;
        }

        int eventPriority = (8 * cpuEvents + 2 * ramEvents + 2 * diskEvents) / totalEvents;

        return eventPriority + userWeight * totalEvents;
    }

}
