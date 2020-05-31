package cz.vse.java.pfej00.tymovyProjekt.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
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
import java.util.*;
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


    ///////////////////////////////tohle je projekt, abych věděl, ke kterýmu creatuju issue

    private int projectId;

    ///////////////////////////issues, abych mohl ověřit, že takový neexistuje

    private List<IssueDto> issues;


    ///////////////////kvůli ENABLE/DISABLE

    private TextField searchIssues;

    private Button createIssue;

    private Button userListButtonOnIssuesScreen;

    private Button removeIssue;

    private Button editIssue;

    private List<UserDto> listOfUsers = new ArrayList<>();

    private IssuesController issuesController;

    private static final Logger logger = LogManager.getLogger(CreateIssueController.class);

    public void setIssues(List<IssueDto> issues) {
        this.issues = issues;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    /**
     * Metoda načítá uživatele,
     * kterými naplní choicebox
     */
    @FXML
    public void initialize() {
        fillAssignTo();
    }

    /**
     * Metoda vyvolaná stisknutím SAVE buttonu
     * slouží k založení nového issue
     */
    public void handle(Event event) {
        if (assignToNew.getSelectionModel().isEmpty() || description.getText().isEmpty() || issueName.getText().isEmpty()) {
            clear();
            PopupBuilder.loadPopup("/allFieldsValid.html");
        } else if (!issueAlreadyExists(issueName.getText())) {
            enableAllButtons();
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("name", issueName.getText());
            //nový budou vždycky new = 3
            post.put("state", 3);
            post.put("description", description.getText());
            post.put("project", projectId);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String strDate = sdf.format(date);
            post.put("created", strDate);
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

            ProgressBar progressBar = new ProgressBar();
            progressBar.progressProperty().bind(task.progressProperty());
            stg.setScene(new Scene(progressBar));
            stg.initStyle(StageStyle.UNDECORATED);
            stg.setAlwaysOnTop(true);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(task);
            executorService.shutdown();
        } else {
            clear();
            PopupBuilder.loadPopup("/issueNotUnique.html");
        }

    }

    /**
     * Výsledné naplnění choiceboxu
     */
    private void fillChoice(List<UserDto> users) {
        for (UserDto i : users) {
            assignToNew.getItems().addAll(i.getUsername());
        }
    }

    /**
     * Metoda slouží k načtení všech uživatelů
     */
    private void fillAssignTo() {
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
    }

    /**
     * Metoda přemapovává STRING odpověď ze severu
     * do listu uživatelů
     *
     * @param response
     */
    private List<UserDto> fillAssignToValues(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.reader().forType(new TypeReference<List<UserDto>>() {
        }).readValue(Objects.requireNonNull(response.body().string()));
    }


    /**
     * Metoda slouží k odblokování tlačítek
     * na obrazoce "níž" v případě zavření
     * stávající screeny
     */
    private void enableAllButtons() {
        searchIssues.setDisable(false);
        createIssue.setDisable(false);
        editIssue.setDisable(false);
        removeIssue.setDisable(false);
        userListButtonOnIssuesScreen.setDisable(false);
    }

    /**
     * Metoda podle username vrací
     * ID konkrétního užviatele
     *
     * @param username
     */
    private int getAssigneeIdByUsername(String username) {
        for (UserDto user : listOfUsers) {
            if (user.getUsername().equals(username)) {
                return user.getId();
            }
        }
        return 0;
    }

    /**
     * Metoda slouží k validace
     * unikátnosti názvu projektu
     *
     * @param issueName
     */
    private boolean issueAlreadyExists(String issueName) {
        boolean exists = false;
        for (IssueDto issueDto : issues) {
            if (issueName.equals(issueDto.getName())) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    /**
     * Pokud uživatel neprojde přes validaci
     * resetuje se formulář
     */
    private void clear() {
        description.clear();
        issueName.clear();
        assignToNew.getSelectionModel().clearSelection();
    }
}
