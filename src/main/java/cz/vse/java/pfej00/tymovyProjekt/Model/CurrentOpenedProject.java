package cz.vse.java.pfej00.tymovyProjekt.Model;

public class CurrentOpenedProject {
    private static final CurrentOpenedProject PROJECT = new CurrentOpenedProject();
    private int projetId;


    /**
     * Konstruktor pro singleton představující id projektu,
     * které se zrovna edituje
     */

    private CurrentOpenedProject() {
    }

    public static CurrentOpenedProject getPROJECT() {
        return PROJECT;
    }

    public int getProjetId() {
        return projetId;
    }

    public void setProjetId(int projetId) {
        this.projetId = projetId;
    }
}
