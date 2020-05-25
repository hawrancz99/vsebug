package cz.vse.java.pfej00.tymovyProjekt.Model;

public class UserDto {
    private String username;
    private RoleDto role;

    public UserDto(String username, RoleDto role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }
}