package cz.vse.java.pfej00.tymovyProjekt.gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditIssueController {
    @FXML
    private TextField nameEdit;

    @FXML
    private TextArea descEdit;

    @FXML
    private ChoiceBox<?> choiceAssign;

    @FXML
    private ChoiceBox<?> choiceState;

    @FXML
    private Button save;

    @FXML
    private Button delete;

    @FXML
    void deleteIssue(ActionEvent event) {

    }
    @FXML
    void saveEditedIssue(ActionEvent event) {

    }

}
