package cz.vse.java.pfej00.tymovyProjekt.gui;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.vse.java.pfej00.tymovyProjekt.Model.RoleDto;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.ProjectDto;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectsController {
    @FXML
    private Button users_list = new Button();

    @FXML
    private Button createProject = new Button();

    @FXML
    private Button log_out = new Button();

    @FXML
    private Button deleteProject = new Button();

    @FXML
    private Button editProject = new Button();

    private TableView listOfUsers = new TableView<>();

    private TableColumn username = new TableColumn();
    private TableColumn role = new TableColumn();


    private static final Logger logger = LogManager.getLogger(MainController.class);


    @FXML
    public Button button = new Button();
    @FXML
    public TextField inputOfProjects = new TextField();

    public ProjectsController() {
    }

    @FXML
    public void initialize(){
        users_list.setOnAction(this::handleUsers);
        listOfUsers.getColumns().add(username);
        listOfUsers.getColumns().add(role);
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


    private void handleUsers(Event event) {

            Stage stg = new Stage();
            ClientCallerTask task = new ClientCallerTask("sendGetUsers", null);
            task.setOnRunning((successEvent) -> {
                stg.show();
            });

            task.setOnSucceeded((succeededEvent) -> {
                stg.hide();
                try {
                    Response response = task.get();
                    if(response.isSuccessful()){
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/list_of_users.fxml"));
                        Parent root = fxmlLoader.load();
                        UsersListController usersListController = fxmlLoader.getController();
                        fillTable(response);
                        usersListController.setListOfUsers(listOfUsers);
                        Stage primaryStage = new Stage();
                        primaryStage.initStyle(StageStyle.UTILITY);
                        primaryStage.setTitle("");
                        primaryStage.setScene(new Scene(root));
                        primaryStage.show();
                        logger.info("All users loaded successfully");
                    } else logger.error("Error while loading all users");
                } catch (InterruptedException | ExecutionException | IOException e) {
                    logger.error("Error while loading all users, caused by {}", e.getMessage());
                }
            });

            ProgressBar progressBar = new ProgressBar();
            progressBar.progressProperty().bind(task.progressProperty());
            stg.setScene(new Scene(progressBar));
            stg.initStyle(StageStyle.UNDECORATED);
            stg.setAlwaysOnTop(true);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(task);
            executorService.shutdown();

    }

    private void fillTable(Response response) throws IOException {
        final ObjectNode node = new ObjectMapper().readValue(Objects.requireNonNull(response.body()).string(), ObjectNode.class);
        JsonNode role = node.get("role");
        JsonNode username = node.get("username");
    }


}
