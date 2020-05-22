package cz.vse.java.pfej00.tymovyProjekt.gui;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import cz.vse.java.pfej00.tymovyProjekt.Model.ProjectDto;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ProjectsController {

    @FXML
    public Button button = new Button();
    @FXML
    public TextField inputOfProjects = new TextField();

    public ProjectsController() {
    }

    @FXML
    public void initialize(){
             
    }

    public void clickOnParticularProject(ActionEvent actionEvent) throws Exception {
        ClientCallerTask clientCallerTask = new ClientCallerTask("sendGetProject", null);
        Response response = clientCallerTask.call();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        List<ProjectDto> projectDtos = objectMapper.reader().forType(new TypeReference<List<ProjectDto>>() {}).readValue(Objects.requireNonNull(response.body()).string());
        loadIssues(projectDtos.get(0));
        //přes kliknutí na projekt getnu přes ID k němu přiřazený issues

    }

    private void loadIssues(ProjectDto projectDto) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NO SCREEN SO FAR"));
        Parent root = fxmlLoader.load();
        IssuesController issuesController = fxmlLoader.getController();
        issuesController.setListOfIssues(projectDto.getIssues());
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
