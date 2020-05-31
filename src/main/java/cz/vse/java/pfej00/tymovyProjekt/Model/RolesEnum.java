package cz.vse.java.pfej00.tymovyProjekt.Model;

public enum RolesEnum {
    Developer(1),
    Tester(2),
    Analyst(3);

    private int numVal;

    RolesEnum(int numVal) {
        this.numVal = numVal;
    }

    /**
     * Vrací INTEGER hodnotu na základě STRING možností definovaných v enumu
     */
    public int getNumVal() {
        return numVal;
    }
}
