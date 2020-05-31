package cz.vse.java.pfej00.tymovyProjekt.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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


    private static final Logger logger = LogManager.getLogger(IssuesController.class);


    /**
     * Metoda při inicializaci controlleru
     * nastavuje akce tlačítek
     */
    @FXML
    public void initialize() {
        save.setOnAction(this::saveEditedIssue);
        delete.setOnAction(this::deleteIssue);
        loadUsers();
    }


    private void loadUsers() {
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
                    List<UserDto> users = fillTableWithUsers(response);
                    listOfUsers.addAll(users);
                    fillChoiceAssign();

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
    }


    /**
     * Akce tlačítka DELETE
     * Metoda zavolá BE a smaže issue
     */
    private void deleteIssue(ActionEvent event) {
        Stage stg = new Stage();
        String post = "" + issueId + "";
        ClientCallerTask task = new ClientCallerTask("sendDeleteIssue", post);
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
                    logger.info("Issue {} was deleted", issueId);
                } else logger.error("Error while deleting issue {}, caused by {}", issueId, response);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error while deleting issue {}, caused by {}", issueId, e.getMessage());
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


    /**
     * Akce tlačítka SAVE
     * Volá BE a updatuje údaje o issue
     */
    private void saveEditedIssue(Event event) {
        if (nameEdit.getText().isEmpty() || descEdit.getText().isEmpty() /* tamto nejde zvolit prázdný*/) {
            PopupBuilder.loadPopup("/allFieldsValid.html");
        } else {
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
        }
    }

    /**
     * Metoda přemapuje RESPONSE ve formě
     * STRING (json) do listu uživatelů
     *
     * @param response
     */
    private List<UserDto> fillTableWithUsers(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.reader().forType(new TypeReference<List<UserDto>>() {
        }).readValue(Objects.requireNonNull(response.body().string()));
    }

    /**
     * Metoda získává potřebná data o issue
     */
    public void acceptDataFromIssue(int issueId, String nameText, IssuesController issuesController, String stateText, String description, String assignTo) {
        this.issueId = issueId;
        this.choiceState.getItems().addAll("Open", "Closed", "New", "Fixed");
        this.choiceState.setValue(stateText);
        this.nameEdit.setText(nameText);
        this.issuesController = issuesController;
        this.descEdit.setText(description);
        this.choiceAssign.setValue(assignTo.toLowerCase());
    }

    /**
     * Metoda plní choicebox uživatelů
     */
    private void fillChoiceAssign() {
        for (UserDto u : listOfUsers) {
            this.choiceAssign.getItems().add(u.getUsername().toLowerCase());
        }
    }


    /**
     * Metoda vrací ID uživatele podle
     * jeho username
     */
    private int getAssigneeIdByUsername(String username) {
        for (UserDto user : listOfUsers) {
            if (user.getUsername().equals(username)) {
                return user.getId();
            }
        }
        return 0;
    }

}
