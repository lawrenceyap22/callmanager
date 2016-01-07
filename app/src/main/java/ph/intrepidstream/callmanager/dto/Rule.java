package ph.intrepidstream.callmanager.dto;

public class Rule {
    private String name;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return name + ": " + state;
    }
}
