package cz.vse.java.pfej00.tymovyProjekt.gui;
import cz.vse.java.pfej00.tymovyProjekt.Model.*;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditIssueController {
    @FXML
    private TextField nameEdit;

    @FXML
    private TextArea descEdit;

    @FXML
    private ChoiceBox<String> choiceAssign;

    @FXML
    private ChoiceBox<String> choiceState;

    @FXML
    private Button save = new Button();

    @FXML
    private Button delete = new Button();

    private int issueId;

    private IssuesController issuesController;

    private List<UserDto> listOfUsers = new ArrayList<>();

    private List<IssueDto> listOfIssues = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(IssuesController.class);



    @FXML
    public void initialize(){
        save.setOnAction(this::saveEditedIssue);
    }

    @FXML
    public void deleteIssue(ActionEvent event) {

    }


    public void saveEditedIssue(Event event) {
        if (nameEdit.getText().isEmpty() || descEdit.getText().isEmpty() /* tamto nejde zvolit prázdný*/) {
            PopupBuilder.loadPopup("/allFieldsValid.html");
        }else if (!issueAlreadyExists(nameEdit.getText())){
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("issueId", issueId);
            post.put("name", nameEdit.getText());
            post.put("description", descEdit.getText());
            post.put("project", CurrentOpenedProject.getPROJECT().getProjetId());
            StatesEnum stateInt = StatesEnum.valueOf(choiceState.getSelectionModel().getSelectedItem());
            post.put("state", stateInt.getNumVal());
            post.put("assignee", getAssigneeIdByUsername(choiceAssign.getValue()));
            ClientCallerTask task = new ClientCallerTask("sendUpdateIssue", post.toString());
            task.setOnRunning((successEvent) -> {
                stg.show();
            });

            task.setOnSucceeded((succeededEvent) -> {
                stg.hide();
                try {
                    Response response = task.get();
                    if (response.isSuccessful()) {
                        Stage editStage = (Stage) nameEdit.getScene().getWindow();
                        editStage.close();
                        issuesController.fillIssuesTable();
                        logger.info("Issue edited successfully {}", "");
                    } else logger.error("Error while editing issue, caused by {}", response);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while editing issue, caused by {}", e.getMessage());
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
            PopupBuilder.loadPopup("/issueNotUnique.html");
        }
    }

    public void acceptDataFromIssue(int issueId,String nameText, IssuesController issuesController, String stateText, String description, String assignTo, List<UserDto> acceptedUsers, List<IssueDto> acceptedIssues){
        this.issueId = issueId;
        this.listOfUsers.addAll(acceptedUsers);
        this.listOfIssues.addAll(acceptedIssues);
        this.choiceState.getItems().addAll("Open", "Closed", "New", "Fixed");
        this.choiceState.setValue(stateText);
        this.nameEdit.setText(nameText);
        this.issuesController = issuesController;
        this.descEdit.setText(description);
        fillChoiceAssign();
        this.choiceAssign.setValue(assignTo.toLowerCase());
    }

    private void fillChoiceAssign(){
        for(UserDto u : listOfUsers) {
            this.choiceAssign.getItems().add(u.getUsername().toLowerCase());
        }
    }

    private boolean issueAlreadyExists(String issueName){
        boolean exists = false;
        for (IssueDto i : listOfIssues) {
            if (issueName.equals(i.getName())) {
                exists = true;
                break;
            }
        }
        return exists;
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

}
