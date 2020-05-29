package cz.vse.java.pfej00.tymovyProjekt.Model;

public enum StatesEnum {
    New(1),
    Open(2),
    Fixed(3);
    //cojávim

    private int numVal;

    StatesEnum(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
