package cz.vse.java.pfej00.tymovyProjekt.Model;

public class UserDto {
    private String username;
    private int role;
    private RoleDto roleName;

    public UserDto(String username, int role, RoleDto roleName) {
        this.username = username;
        this.role = role;
        this.roleName = roleName;
    }

    public UserDto() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public int getRoleID() {
        return role;
    }

    public void setRoleID(int roleID) {
        this.role = roleID;
    }

    public RoleDto getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleDto roleName) {
        this.roleName = roleName;
    }
}