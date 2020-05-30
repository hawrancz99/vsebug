package cz.vse.java.pfej00.tymovyProjekt.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import cz.vse.java.pfej00.tymovyProjekt.Model.ProjectDto;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateIssueController {

    @FXML
    private ChoiceBox<String> assignToNew;

    @FXML
    private TextField issueName = new TextField();

    @FXML
    private TextArea description = new TextArea();

    @FXML
    private Button save;


    ///////////////////////////////tohle je projekt, abych věděl, ke kterýmu creatuju išu

    private ProjectDto projectDto;


    ///////////////////kvůli ENABLE/DISABLE

    private TextField searchIssues;

    private Button createIssue;

    private Button userListButtonOnIssuesScreen;

    private Button removeIssue;

    private Button editIssue;

    private List<UserDto> listOfUsers = new ArrayList<>();

    private IssuesController issuesController;

    private static final Logger logger = LogManager.getLogger(CreateIssueController.class);

    public void setProjectDto(ProjectDto projectDto) {
        this.projectDto = projectDto;
    }

    public void setUserListButtonOnIssuesScreen(Button userListButtonOnIssuesScreen) {
        this.userListButtonOnIssuesScreen = userListButtonOnIssuesScreen;
    }

    public void setIssuesController(IssuesController issuesController) {
        this.issuesController = issuesController;
    }


    public void setSearchIssues(TextField searchIssues) {
        this.searchIssues = searchIssues;
    }

    public void setCreateIssue(Button createIssue) {
        this.createIssue = createIssue;
    }

    public void setRemoveIssue(Button removeIssue) {
        this.removeIssue = removeIssue;
    }

    public void setEditIssue(Button editIssue) {
        this.editIssue = editIssue;
    }

    @FXML
    public void initialize() {
        fillAssignTo();
    }

    public void handle(Event event) {
            enableAllButtons();
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("name", issueName.getText());
            //asi může zůstat, nový budou vždycky new
            post.put("state", 1);
            post.put("description", description.getText());
            post.put("project", projectDto.getId());
            //zatim takhle než zjistíme formát
            post.put("created", "2020-05-22T21:03:41Z");
            int assignee = getAssigneeIdByUsername(assignToNew.getSelectionModel().getSelectedItem());
            post.put("assignee", assignee);
            ClientCallerTask task = new ClientCallerTask("sendCreateIssue", post.toString());
            task.setOnRunning((successEvent) -> {
                save.setDisable(true);
                stg.show();
            });

            task.setOnSucceeded((succeededEvent) -> {
                save.setDisable(false);
                stg.hide();
                try {
                    Response response = task.get();
                    if (response.isSuccessful()) {
                        Stage stage = (Stage) save.getScene().getWindow();
                        stage.close();
                        issuesController.fillIssuesTable();
                        logger.info("Issue created successfully {}", response);
                    } else logger.error("Error while creating issue, caused by {}", response);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while creating issue, caused by {}", e.getMessage());
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

    }

    private void fillChoice(List<UserDto> users){
        for (UserDto i : users) {
            assignToNew.getItems().addAll(i.getUsername());
        }
    }

    private void fillAssignTo(){
        Stage stg = new Stage();
        ClientCallerTask task = new ClientCallerTask("sendGetUsers", null);
        task.setOnRunning((successEvent) -> {
            stg.show();
        });

        task.setOnSucceeded((succeededEvent) -> {
            stg.hide();
            try {
                Response response = task.get();
                if (response.isSuccessful()) {
                    List<UserDto> users = fillAssignToValues(response);
                    fillChoice(users);
                    listOfUsers.addAll(users);
                    logger.info("Users loaded successfully");
                } else logger.error("Error while loading issues, caused by {}", response);
            } catch (InterruptedException | ExecutionException | IOException e) {
                logger.error("Error while loading issues, caused by {}", e.getMessage());
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
    };

    private List<UserDto> fillAssignToValues(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.reader().forType(new TypeReference<List<UserDto>>() {
        }).readValue(Objects.requireNonNull(response.body().string()));
    }



    private void enableAllButtons() {
        searchIssues.setDisable(false);
        createIssue.setDisable(false);
        editIssue.setDisable(false);
        removeIssue.setDisable(false);
        userListButtonOnIssuesScreen.setDisable(false);
    }

    private int getAssigneeIdByUsername(String username){
        for(UserDto user : listOfUsers){
            if(user.getUsername().equals(username)){
                return user.getId();
            }
        }
        //chudák user 0 haha
        return 0;
    }

    /*
    private boolean issueAlreadyExists(String projectName){
        boolean exists = false;
        for (ProjectDto project : issues) {
            if (projectName.equals(project.getName())) {
                exists = true;
                break;
            }
        }
        return exists;
    } */
}
