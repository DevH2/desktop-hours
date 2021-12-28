package sample;

public class User {
    private final int id;
    private final String name;
    private final int signedIn;
    private final long timeIn;
    private final long totalTime;
    private final int meetings;

    public User(int id, String name, int signedIn, long timeIn, long totalTime, int meetings){
        this.id = id;
        this.name = name;
        this.signedIn = signedIn;
        this.timeIn = timeIn;
        this.totalTime = totalTime;
        this.meetings = meetings;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public boolean getIsSignedIn(){
        return signedIn == 0;
    }
    public long getTimeIn(){
        return timeIn;
    }
    public long getTotalTime(){
        return totalTime;
    }
    public int getMeetings(){
        return meetings;
    }


}

