package cz.vse.java.pfej00.tymovyProjekt.gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ProjectsController {
    @FXML
    private Button users_list;

    @FXML
    private Button createProject;

    @FXML
    private Button log_out;

    @FXML
    private Button deleteProject;

    @FXML
    private Button editProject;



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

    @FXML
    public void goToUsersList(ActionEvent event) throws IOException {
        Parent part = FXMLLoader.load(getClass().getResource("/resources/list_of_users.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(part);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Stage stage = (Stage) log_out.getScene().getWindow();
        // do what you have to do
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = fxmlLoader.load();
        Stage primaryStage = new Stage();
        primaryStage.setTitle("VŠEBUG");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    /*public editButtonText{
        btn.setText("Hello World");
    }*/

}
