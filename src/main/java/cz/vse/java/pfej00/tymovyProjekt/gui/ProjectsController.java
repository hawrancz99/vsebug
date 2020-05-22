package cz.vse.java.pfej00.tymovyProjekt.gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;

public class ProjectsController {

    @FXML
    public Button button = new Button();

    public ProjectsController() {
    }

    @FXML
    public void initialize(){
             
    }

    public void justTryingButton(ActionEvent actionEvent) throws IOException {
        //přijde mi sem, že se to zavřelo, tak otevřu tohle?
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = fxmlLoader.load();
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
