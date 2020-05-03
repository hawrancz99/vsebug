package cz.vse.java.pfej00.tymovyProjekt.Model;

public class IssueDto {
    private String name;
    private String description;
    private long project;

    public IssueDto(){
        super();
    }

    public IssueDto( String name, String description, long project) {
        this.name = name;
        this.description = description;
        this.project = project;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getProject() {
        return project;
    }

    public void setProject(long project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return project + " " + name + " " + description;
    }
}
