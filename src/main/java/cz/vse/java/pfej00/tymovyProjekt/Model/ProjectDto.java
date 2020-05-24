package cz.vse.java.pfej00.tymovyProjekt.Model;

import java.util.Date;
import java.util.List;

public class ProjectDto {
    private int id;
    private String name;
    private String description;
    private Date created;
    private List<IssueDto> issues;

    public ProjectDto() {
        super();
    }

    public ProjectDto(int id, String name, String description, Date created, List<IssueDto> issues) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.created = created;
        this.issues = issues;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<IssueDto> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueDto> issues) {
        this.issues = issues;
    }

    @Override
    public String toString() {
        return "ProjectDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", created=" + created +
                ", issues=" + issues +
                '}';
    }
}
