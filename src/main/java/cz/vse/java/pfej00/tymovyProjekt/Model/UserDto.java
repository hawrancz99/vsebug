package cz.vse.java.pfej00.tymovyProjekt.Model;

public class UserDto {
    private String username;
    private RoleDto roleName;

    public UserDto(String username, RoleDto roleName) {
        this.username = username;
        this.roleName = roleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RoleDto getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleDto roleName) {
        this.roleName = roleName;
    }

    public UserDto() {
        super();
    }

}