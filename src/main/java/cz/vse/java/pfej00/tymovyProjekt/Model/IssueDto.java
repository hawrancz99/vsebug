package cz.vse.java.pfej00.tymovyProjekt.Model;

import java.util.Date;

public class IssueDto {
    private String name;
    private String description;
    private int projectId;
    private int stateId;
    private int userId;
    private Date created;
    private UserDto assignee;
    private StateDto stateName;

    public IssueDto(String name, String description, int projectId, int stateId, int userId, Date created, UserDto assignee, StateDto stateName) {
        this.name = name;
        this.description = description;
        this.projectId = projectId;
        this.stateId = stateId;
        this.userId = userId;
        this.created = created;
        this.assignee = assignee;
        this.stateName = stateName;
    }

    public IssueDto() {
        super();
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

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserDto getAssignee() {
        return assignee;
    }

    public void setAssignee(UserDto assignee) {
        this.assignee = assignee;
    }

    public StateDto getStateName() {
        return stateName;
    }

    public void setStateName(StateDto stateName) {
        this.stateName = stateName;
    }

    @Override
    public String toString() {
        return name + " " + description;
    }
}
