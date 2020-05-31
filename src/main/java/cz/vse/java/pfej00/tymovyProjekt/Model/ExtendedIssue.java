package cz.vse.java.pfej00.tymovyProjekt.Model;

import cz.vse.java.pfej00.tymovyProjekt.gui.EditIssueController;
import cz.vse.java.pfej00.tymovyProjekt.gui.IssuesController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;


/**
 * Třída rozšiřuje IssueDto o atributy
 * potřebné pro práci s EditIssueControllerem.
 */
public class ExtendedIssue extends IssueDto {
    private Button button = new Button("edit");

    private IssuesController issuesController;

    private List<UserDto> usersList;


    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public void setIssuesController(IssuesController issuesController) {
        this.issuesController = issuesController;
    }

    public void setUsersList(List<UserDto> usersList) {
        this.usersList = usersList;
    }


    /**
     * Konstruktor třídy ExtendedIssue, který
     * dědí atributy z IssueDto,
     * Nastavuje funkci tlačítka BUTTON, kde
     * na "akci" otevírá editaci issue
     */

    public ExtendedIssue() {
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/edit_issue.fxml"));
                Parent root = null;
                try {
                    root = fxmlLoader.load();
                    EditIssueController editIssueController = fxmlLoader.getController();
                    editIssueController.acceptDataFromIssue(getId(), getName(), issuesController, getState(), getDescription(), getAssignee().getUsername(), usersList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Stage primaryStage = new Stage();
                primaryStage.initStyle(StageStyle.UTILITY);
                primaryStage.setTitle("");
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            }
        });
    }


}
