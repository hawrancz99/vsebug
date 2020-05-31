package cz.vse.java.pfej00.tymovyProjekt.Model;

import java.util.Date;

public class IssueDto {
    private int id;
    private String name;
    private String description;
    private Date created;
    private int project;
    private String state;
    private UserDto assignee;


    /**
     * Konstruktor třídy IssueDto
     * super() dědí atributy ze svého předka, nutné
     * při využívání třídy ObjectMapper
     */
    public IssueDto() {
        super();
    }


    /**
     * Konstruktor třídy IssueDto
     * Objekt slouží k přemapování databázového objektu do čitelné podoby
     *
     * @param id
     * @param name
     * @param description
     * @param created
     * @param project
     * @param state
     * @param assignee
     */
    public IssueDto(int id, String name, String description, Date created, int project, String state, UserDto assignee) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.created = created;
        this.project = project;
        this.state = state;
        this.assignee = assignee;
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

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UserDto getAssignee() {
        return assignee;
    }

    public void setAssignee(UserDto assignee) {
        this.assignee = assignee;
    }
}
