package cz.vse.java.pfej00.tymovyProjekt.gui;

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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.List;
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


    ///////////////////kvůli ENABLE/DISABLE

    private TextField searchIssues;

    private Button createIssue;

    private Button userListButtonOnIssuesScreen;

    private Button removeIssue;

    private Button editIssue;

    private List<UserDto> listOfUsers;

    private IssuesController issuesController;

    public void setUserListButtonOnIssuesScreen(Button userListButtonOnIssuesScreen) {
        this.userListButtonOnIssuesScreen = userListButtonOnIssuesScreen;
    }

    public void setIssuesController(IssuesController issuesController) {
        this.issuesController = issuesController;
    }

    private static final Logger logger = LogManager.getLogger(CreateIssueController.class);

    public void setListOfUsers(List<UserDto> listOfUsers) {
        this.listOfUsers = listOfUsers;
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
    }

    public void handle(Event event) {
            enableAllButtons();
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("name", issueName.getText());
            post.put("description", description.getText());
            post.put("project", "");
            //zatim takhle než zjistíme formát
            post.put("created", "2020-05-22T21:03:41Z");
            post.put("assignee", assignToNew.getSelectionModel().getSelectedItem());
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
                        issuesController.loadIssues();
                        logger.info("Issue created successfully");
                    } else logger.error("Error while creating issue");
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

    public void fillChoice(MouseEvent mouseEvent){
        for (UserDto i : listOfUsers) {
            assignToNew.getItems().addAll(i.getUsername());
        }
        assignToNew.show();
    }



    private void enableAllButtons() {
        searchIssues.setDisable(false);
        createIssue.setDisable(false);
        editIssue.setDisable(false);
        removeIssue.setDisable(false);
        userListButtonOnIssuesScreen.setDisable(false);
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
