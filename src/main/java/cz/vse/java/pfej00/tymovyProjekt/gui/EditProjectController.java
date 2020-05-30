package cz.vse.java.pfej00.tymovyProjekt.gui;
import cz.vse.java.pfej00.tymovyProjekt.Model.ProjectDto;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProjectController {

    @FXML
    private Button save = new Button();

    @FXML
    private TextField textChange;

    //nutný pro change

    private Button buttonWithProjectName;

    private ProjectDto editedProject;

    private ProjectsController projectsController;

    private List<ProjectDto> projects;

    ///////////////nutný pro enable :)

    private Button users_list_button;

    private Button createProject;

    private Button log_out;

    private ObservableList<Button> buttons = FXCollections.observableArrayList();


    private static final Logger logger = LogManager.getLogger(EditProjectController.class);

    public void setProjects(List<ProjectDto> projects) {
        this.projects = projects;
    }

    public void setButtonWithProjectName(Button buttonWithProjectName) {
        this.buttonWithProjectName = buttonWithProjectName;
    }

    public void setEditedProject(ProjectDto editedProject) {
        this.editedProject = editedProject;
    }

    public void setProjectsController(ProjectsController projectsController) {
        this.projectsController = projectsController;
    }

    public void setUsers_list_button(Button users_list_button) {
        this.users_list_button = users_list_button;
    }

    public void setCreateProject(Button createProject) {
        this.createProject = createProject;
    }

    public void setLog_out(Button log_out) {
        this.log_out = log_out;
    }

    public void setButtons(ObservableList<Button> buttons) {
        this.buttons = buttons;
    }

    @FXML
    public void saveName(ActionEvent event) {
        if (textChange.getText().isEmpty()) {
            PopupBuilder.loadPopup("/allFieldsValid.html");
        }else if(!projectAlreadyExists(textChange.getText())){
            String firstTimeName = buttonWithProjectName.getText();
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("name", textChange.getText());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String strDate = sdf.format(date);
            post.put("created", strDate);
            int id = editedProject.getId();
            post.put("id", id);
            ClientCallerTask task = new ClientCallerTask("sendUpdateProject", post.toString());
            task.setOnRunning((successEvent) -> {
                stg.show();
            });

            task.setOnSucceeded((succeededEvent) -> {
                stg.hide();
                try {
                    Response response2 = task.get();
                    if (response2.isSuccessful()) {
                        Stage stage = (Stage) save.getScene().getWindow();
                        stage.close();
                        projectsController.loadProjects();
                        buttonWithProjectName.setText(textChange.getText());
                        textChange.clear();
                        enableAllButtons();
                        logger.info("Project was updated");
                    } else {
                        //nepovedlo se, tak stará hodnota
                        buttonWithProjectName.setText(firstTimeName);
                        logger.error("Error occurred while updating project, caused by {}", response2);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while updating project, caused by {}", e.getMessage());
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
        } else {
            textChange.clear();
            PopupBuilder.loadPopup("/projectNotUnique.html");
        }
    }

    private void enableAllButtons() {
        users_list_button.setDisable(false);
        createProject.setDisable(false);
        log_out.setDisable(false);
        for (Button b : buttons) {
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
