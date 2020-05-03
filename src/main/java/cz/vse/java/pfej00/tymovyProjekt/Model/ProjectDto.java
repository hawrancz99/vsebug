package cz.vse.java.pfej00.tymovyProjekt.Model;

import java.util.Date;
import java.util.List;

public class ProjectDto {
    private long Id;
    private String name;
    private String description;
    private Date created;
    private List<IssueDto> issues;

    public ProjectDto() {
        super();
    }

    public ProjectDto(long id, String name, String description, Date created, List<IssueDto> issues) {
        Id = id;
        this.name = name;
        this.description = description;
        this.created = created;
        this.issues = issues;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
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
}
