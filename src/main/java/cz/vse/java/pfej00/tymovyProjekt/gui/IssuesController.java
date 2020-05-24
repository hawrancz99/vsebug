package cz.vse.java.pfej00.tymovyProjekt.gui;

import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.List;

public class IssuesController {

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
