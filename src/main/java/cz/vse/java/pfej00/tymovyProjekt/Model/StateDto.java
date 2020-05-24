package cz.vse.java.pfej00.tymovyProjekt.Model;

public class StateDto {
    private String state;

    public StateDto(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public StateDto() {
        super();
    }
}
