package cz.vse.java.pfej00.tymovyProjekt.gui;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.List;

public class IssuesController {
    @FXML
    private TextField search;

    @FXML
    private Button usersList;

    @FXML
    private Button createIssue;

    @FXML
    private Button removeIssue;

    @FXML
    private Button back;

    @FXML
    private Button editIssue;


    //tady bude nějaký listview

    //dostanu nasetovaný z projektů
    private List<IssueDto> listOfIssues;

    public IssuesController(List<IssueDto> listOfIssues) {
        this.listOfIssues = listOfIssues;
    }

    public void setListOfIssues(List<IssueDto> listOfIssues) {
        this.listOfIssues = listOfIssues;
    }

    @FXML
    public void initialize(){
        //tady vemu to listview a namrdam do toho ty issues - možná bych si nemusel dělat ani privátní ale rovnou poslat FXML - spíš ne
    }
}




