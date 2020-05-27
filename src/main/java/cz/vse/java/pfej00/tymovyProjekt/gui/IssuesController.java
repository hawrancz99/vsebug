package cz.vse.java.pfej00.tymovyProjekt.gui;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.List;

public class IssuesController {
    @FXML
    private TextField searchIssues;

    @FXML
    private Button createIssue;

    @FXML
    private Button removeIssue;

    @FXML
    private Button editIssue;

    //all set from Project cuz of disable
    //////////////////////////////////////////////////////////////////////////////

    private Button users_list_button;

    private Button createProject;

    private Button deleteProject;

    private Button editProject;

    private Button log_out;

    private ObservableList<Button> buttons = FXCollections.observableArrayList();

    public void setButtons(ObservableList<Button> buttons) {
        this.buttons = buttons;
    }

    //tohle je vlastně stejný jako na projektech, takže je to fajn - nemusim je vůbec volat, stačí zobrazit stejnou tabulku na kliknutí
    private TableView listOfUsers;

    public void setLog_out(Button log_out) {
        this.log_out = log_out;
    }

    public void setUsers_list_button(Button users_list_button) {
        this.users_list_button = users_list_button;
    }

    public void setCreateProject(Button createProject) {
        this.createProject = createProject;
    }

    public void setDeleteProject(Button deleteProject) {
        this.deleteProject = deleteProject;
    }

    public void setEditProject(Button editProject) {
        this.editProject = editProject;
    }


    public void setListOfUsers(TableView listOfUsers) {
        this.listOfUsers = listOfUsers;
    }

    //dostanu nasetovaný z projektů
    private List<IssueDto> listOfIssues;


    public void setListOfIssues(List<IssueDto> listOfIssues) {
        this.listOfIssues = listOfIssues;
    }



    private void enableAllButtons() {
        users_list_button.setDisable(false);
        createProject.setDisable(false);
        deleteProject.setDisable(false);
        editProject.setDisable(false);
        listOfUsers.setDisable(false);
        log_out.setDisable(true);
        for(Button b : buttons){
            b.setDisable(false);
        }
    }
}




