package cz.vse.java.pfej00.tymovyProjekt.Model;

public enum StatesEnum {
    Open(1),
    Closed(2),
    New(3),
    Fixed(4);

    private int numVal;

    StatesEnum(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
