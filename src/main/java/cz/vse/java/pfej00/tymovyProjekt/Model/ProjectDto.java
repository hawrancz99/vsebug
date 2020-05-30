package cz.vse.java.pfej00.tymovyProjekt.Model;

import java.util.Date;

public class ProjectDto {
    private int id;
    private String name;
    private String description;
    private Date created;

    /**
     * Konstruktor třídy ProjectDto
     * super() dědí atributy ze svého předka, nutné
     * při využívání třídy ObjectMapper
     */
    public ProjectDto() {
        super();
    }

    /**
     * Konstruktor třídy ProjectDto
     * Objekt slouží k přemapování databázového objektu do čitelné podoby
     *
     * @param id
     * @param name
     * @param description
     * @param created
     */
    public ProjectDto(int id, String name, String description, Date created) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.created = created;
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
}
