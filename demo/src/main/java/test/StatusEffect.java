package test;

public class StatusEffect {
    private String name;
    private String type; // e.g. "buff", "debuff", "dot", etc.
    private int duration; // remaining duration in rounds
    private double magnitude; // effect strength

    public StatusEffect(String name, String type, int duration, double magnitude) {
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.magnitude = magnitude;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }
    
    public void addMagnitude(double magnitude) {
        this.magnitude += magnitude;
    } // just so I can reuse this class for absorption

    public void tick() {
        duration--;
    }

    public boolean isExpired() {
        return duration < 0 || magnitude < 0.0001; //decremented immediately on round application
    } // magnitude check is for absorption

    // Getters
    public String getName() { return name; }
    public double getMagnitude() { return magnitude; }
    public String getType() { return type; }
    public int getDuration() { return duration; }
}
