package cz.vse.java.pfej00.tymovyProjekt.Model;

public class UserDto {
    private String username;
    private String password;
    private int roleID;
    private RoleDto roleName;

    public UserDto(String username, String password, int roleID, RoleDto roleName) {
        this.username = username;
        this.password = password;
        this.roleID = roleID;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public RoleDto getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleDto roleName) {
        this.roleName = roleName;
    }
}