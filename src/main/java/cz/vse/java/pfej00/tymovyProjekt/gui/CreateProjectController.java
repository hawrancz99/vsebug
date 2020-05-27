package cz.vse.java.pfej00.tymovyProjekt.gui;

import cz.vse.java.pfej00.tymovyProjekt.Model.ProjectDto;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Builder;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateProjectController {

    @FXML
    private TextField inputOfProjects = new TextField();

    @FXML
    private Button createNewProject;

    private final String CREATE_PROJECT = "sendCreateProject";

    private static final Logger logger = LogManager.getLogger(CreateProjectController.class);

    ////setování ne editable

    private Button users_list_button;

    private Button createProject;

    private Button editProject;

    private Button log_out;

    private ObservableList<Button> buttons = FXCollections.observableArrayList();

    private List<ProjectDto> projects;

    private ProjectsController projectsController;

    public void setProjectsController(ProjectsController projectsController) {
        this.projectsController = projectsController;
    }

    public void setProjects(List<ProjectDto> projects) {
        this.projects = projects;
    }

    public void setButtons(ObservableList<Button> buttons) {
        this.buttons = buttons;
    }

    public void setLog_out(Button log_out) {
        this.log_out = log_out;
    }

    public void setInputOfProjects(TextField inputOfProjects) {
        this.inputOfProjects = inputOfProjects;
    }


    public void setUsers_list_button(Button users_list_button) {
        this.users_list_button = users_list_button;
    }

    public void setCreateProject(Button createProject) {
        this.createProject = createProject;
    }


    public void setEditProject(Button editProject) {
        this.editProject = editProject;
    }

    @FXML
    public void initialize() {
        createNewProject.setOnAction(this::handle);
    }

    private void handle(Event event) {
        if (inputOfProjects.getText().isEmpty()) {
            PopupBuilder.loadPopup("/allFieldsValid.html");
        } else if(!projectAlreadyExists(inputOfProjects.getText())){
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("name", inputOfProjects.getText());
            //zatim takhle než zjistíme formát
            post.put("created", "2020-05-22T21:03:41Z");
            ClientCallerTask task = new ClientCallerTask(CREATE_PROJECT, post.toString());
            task.setOnRunning((successEvent) -> {
                createNewProject.setDisable(true);
                stg.show();
            });

            task.setOnSucceeded((succeededEvent) -> {
                createNewProject.setDisable(false);
                stg.hide();
                try {
                    Response response = task.get();
                    if (response.isSuccessful()) {
                        Stage stage = (Stage) createNewProject.getScene().getWindow();
                        enableAllButtons();
                        stage.close();
                        projectsController.initialize();
                        logger.info("Project created successfully");
                    } else logger.error("Error while creating project");
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while creating project, caused by {}", e.getMessage());
                }
            });

            //všude asi udělat task.setOnFailure()
            ProgressBar progressBar = new ProgressBar();
            progressBar.progressProperty().bind(task.progressProperty());
            stg.setScene(new Scene(progressBar));
            stg.initStyle(StageStyle.UNDECORATED);
            stg.setAlwaysOnTop(true);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(task);
            executorService.shutdown();
        } else {
            //zatim takhle
            inputOfProjects.clear();
            PopupBuilder.loadPopup("/projectNotUnique.html");
        }
    }


    private void enableAllButtons() {
        users_list_button.setDisable(false);
        createProject.setDisable(false);
        editProject.setDisable(false);
        log_out.setDisable(false);
        for(Button b : buttons){
            b.setDisable(false);
        }
    }

    private boolean projectAlreadyExists(String projectName){
        boolean exists = false;
        for (ProjectDto project : projects) {
            if (projectName.equals(project.getName())) {
                exists = true;
                break;
            }
        }
        return exists;
    }
}
