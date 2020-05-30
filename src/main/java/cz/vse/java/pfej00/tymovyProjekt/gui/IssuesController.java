package cz.vse.java.pfej00.tymovyProjekt.gui;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.ProjectDto;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IssuesController implements Initializable {
    @FXML
    private TableView<IssueDto> issueDtoTableView;

    @FXML
    private TextField searchIssues = new TextField();

    @FXML
    private TableColumn<IssueDto, String> issueNameColumn = new TableColumn<>();

    @FXML
    private TableColumn<IssueDto, String> descriptionColumn = new TableColumn<>();

    @FXML
    private TableColumn<IssueDto, String> stateColumn = new TableColumn<>();

    @FXML
    private TableColumn<IssueDto, UserDto> assignedToColumn = new TableColumn<>();

    @FXML
    private TableColumn<IssueDto, Date> createdColumn = new TableColumn<>();

    @FXML
    private TableColumn buttonColumn = new TableColumn<>();

    @FXML
    private Button createIssue = new Button();

    @FXML
    private Button removeIssue = new Button();

    @FXML
    private Button editIssue = new Button();

    @FXML
    private Button userListButtonOnIssuesScreen = new Button();

    @FXML
    private ChoiceBox<String> choiceBox = new ChoiceBox<>();


    //all set from Project cuz of disable
    //////////////////////////////////////////////////////////////////////////////

    private Button users_list_button;

    private Button createProject;

    private Button log_out;

    private ProjectDto projectDto;

    private ObservableList<Button> buttons = FXCollections.observableArrayList();

    private static final Logger logger = LogManager.getLogger(IssuesController.class);


    /////////////

    public void setButtons(ObservableList<Button> buttons) {
        this.buttons = buttons;
    }

    public void setProjectDto(ProjectDto projectDto) {
        this.projectDto = projectDto;
    }


    public void openListOfUsers(ActionEvent actionEvent) throws IOException {
        disableAllButtons();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/list_of_users.fxml"));
        Parent root = fxmlLoader.load();
        UsersListController usersListController = fxmlLoader.getController();
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event1 -> enableAllCreateIssueuttons());
    }

    //evidentně to funguje i bez toho, netušim proč :DD
    private void enableAllButtons() {
        users_list_button.setDisable(false);
        createProject.setDisable(false);
        log_out.setDisable(false);
        createIssue.setDisable(false);
        removeIssue.setDisable(false);
        editIssue.setDisable(false);
        userListButtonOnIssuesScreen.setDisable(false);
        for(Button b : buttons){
            b.setDisable(false);
        }
    }



    public void setLog_out(Button log_out) {
        this.log_out = log_out;
    }

    public void setUsers_list_button(Button users_list_button) {
        this.users_list_button = users_list_button;
    }

    public void setCreateProject(Button createProject) {
        this.createProject = createProject;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        issueNameColumn.setCellValueFactory(new PropertyValueFactory<IssueDto, String>("name"));
        fillIssuesTable();
    }

    public void createNewIssue(ActionEvent actionEvent) throws IOException {
        disableAllButtons();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/create_issue.fxml"));
        Parent root = fxmlLoader.load();
        CreateIssueController createIssueController = fxmlLoader.getController();
        createIssueController.setSearchIssues(searchIssues);
        createIssueController.setCreateIssue(createIssue);
        createIssueController.setEditIssue(editIssue);
        createIssueController.setRemoveIssue(removeIssue);
        createIssueController.setIssuesController(this);
        createIssueController.setUserListButtonOnIssuesScreen(userListButtonOnIssuesScreen);
        createIssueController.setProjectDto(projectDto);
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //tohle je přes křížek
        primaryStage.setOnCloseRequest(event ->
        {
            enableAllCreateIssueuttons();
        });
    }


    private void enableAllCreateIssueuttons() {
        searchIssues.setDisable(false);
        createIssue.setDisable(false);
        editIssue.setDisable(false);
        removeIssue.setDisable(false);
        userListButtonOnIssuesScreen.setDisable(false);
    }


    public void fillIssuesTable(){
        issueDtoTableView.getItems().clear();
        Stage stg = new Stage();
        ClientCallerTask task = new ClientCallerTask("sendGetIssues", null);
        task.setOnRunning((successEvent) -> {
            stg.show();
        });

        task.setOnSucceeded((succeededEvent) -> {
            stg.hide();
            try {
                Response response = task.get();
                if (response.isSuccessful()) {
          //          listOfIssues.clear();
                    List<IssueDto> issuesForProject = fillListOfIssues(response);
                    fillFilteredIssues(issuesForProject);
       //             listOfIssues.addAll(issuesForProject);

                    logger.info("All issues loaded successfully");
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

    private void disableAllButtons() {
        searchIssues.setDisable(true);
        createIssue.setDisable(true);
        editIssue.setDisable(true);
        removeIssue.setDisable(true);
        userListButtonOnIssuesScreen.setDisable(true);
    }

    private List<IssueDto> fillListOfIssues(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.reader().forType(new TypeReference<List<IssueDto>>() {
        }).readValue(Objects.requireNonNull(response.body().string()));
    }

    private void fillFilteredIssues(List<IssueDto> issues) {
        ObservableList localList = getUsers(issues);
        FilteredList<IssueDto> filteredList = new FilteredList(localList, p -> true);;//Pass the data to a filtered list
        issueDtoTableView.setItems(filteredList);//Set the table's items using the filtered list
        choiceBox.getItems().addAll("name", "state");
        choiceBox.setValue("name");
        searchIssues.setPromptText("Search here!");
        searchIssues.setOnKeyReleased(keyEvent ->
        {
            //zatím choicebox nemáme, asi bych nechal jen na username
            switch (choiceBox.getValue())//Switch on choiceBox value
            {
                case "name":
                    filteredList.setPredicate(p -> p.getAssignee().getUsername().toLowerCase().contains(searchIssues.getText().toLowerCase().trim()));//filter table by username
                    break;
                case "state":
                    filteredList.setPredicate(p -> p.getState().toLowerCase().contains(searchIssues.getText().toLowerCase().trim()));//filter table by role
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {//reset table and textfield when new choice is selected
            if (newVal != null) {
                searchIssues.setText("");
                filteredList.setPredicate(null);
            }
        });


    }

    private ObservableList<IssueDto> getUsers(List<IssueDto> loadedIssues) {
        ObservableList<IssueDto> issues = FXCollections.observableArrayList();
        issues.addAll(loadedIssues);
        return issues;
    }
}




