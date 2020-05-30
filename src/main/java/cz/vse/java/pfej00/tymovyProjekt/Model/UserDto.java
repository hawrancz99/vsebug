package cz.vse.java.pfej00.tymovyProjekt.Model;

public class UserDto {
    private String username;
    private String role;
    private int id;

    /**
     * Konstruktor pro třídu User
     * Slouží k přemapování databázového objektu
     *
     * @param username
     * @param role
     * @param id
     */
    public UserDto(String username, String role, int id) {
        this.username = username;
        this.role = role;
        this.id = id;
    }


    /**
     * Konstruktor třídy UsrDto
     * super() dědí atributy ze svého předka, nutné
     * při využívání třídy ObjectMapper
     */
    public UserDto() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Zobrazování userDto v tabulce jako username
     */
    @Override
    public String toString() {
        return username;
    }
}