package sample;

public class UserData {
    //name, signedIn totalTime meetings
    private final String name;
    private final int signedIn;
    private final long totalTime;
    private final int meetings;

    public UserData(String name, int signedIn, long totalTime, int meetings){
        this.name = name;
        this.signedIn = signedIn;
        this.totalTime = totalTime;
        this.meetings = meetings;
    }

    public boolean getIsSignedIn(){
        return signedIn == 1;
    }

    public String getName(){
        return name;
    }

    public long getTotalTime(){
        return totalTime;
    }
    public int getMeetings(){
        return meetings;
    }
}
