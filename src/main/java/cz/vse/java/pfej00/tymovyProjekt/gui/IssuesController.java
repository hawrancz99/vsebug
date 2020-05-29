package cz.vse.java.pfej00.tymovyProjekt.gui;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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

public class IssuesController {
    @FXML
    private TextField searchIssues = new TextField();

    @FXML
    private Button createIssue = new Button();

    @FXML
    private Button removeIssue = new Button();

    @FXML
    private Button editIssue = new Button();

    @FXML
    private Button users_list_button = new Button();

    //all set from Project cuz of disable
    //////////////////////////////////////////////////////////////////////////////


    private Button createProject;

    private Button editProject;

    private Button log_out;


    private ObservableList<Button> buttons = FXCollections.observableArrayList();

    private static final Logger logger = LogManager.getLogger(IssuesController.class);


    /////////////
    private ChoiceBox<String> assignTo;

    public void setButtons(ObservableList<Button> buttons) {
        this.buttons = buttons;
    }

    //tohle je vlastně stejný jako na projektech, takže je to fajn - nemusim je vůbec volat, stačí zobrazit stejnou tabulku na kliknutí
    // asi nějak takhle??
    private List<UserDto> listOfUsers ;

    public void openListOfUsers(ActionEvent actionEvent) throws IOException {
        disableAllButtons();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/list_of_users.fxml"));
        Parent root = fxmlLoader.load();
        UsersListController usersListController = fxmlLoader.getController();
        usersListController.setListOfUsers(listOfUsers);
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event1 -> enableAllButtons());
    }

    private void enableAllButtons() {
        users_list_button.setDisable(false);
        createProject.setDisable(false);
        editProject.setDisable(false);
        log_out.setDisable(false);
        createIssue.setDisable(false);
        removeIssue.setDisable(false);
        editIssue.setDisable(false);
        for(Button b : buttons){
            b.setDisable(false);
        }
    }



    public void setAssignTo(ChoiceBox<String> assignTo) {
        this.assignTo = assignTo;
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

    public void setEditProject(Button editProject) {
        this.editProject = editProject;
    }


    public void setListOfUsers(List<UserDto> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }

    //dostanu nasetovaný z projektů
    private List<IssueDto> listOfIssues;


    public void setListOfIssues(List<IssueDto> listOfIssues) {
        this.listOfIssues = listOfIssues;
    }

    @FXML
    public void initialize() {
        //   listOfUsers.getColumns().add(username);
        //   listOfUsers.getColumns().add(role);
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
        createIssueController.setListOfUsers(listOfUsers);
        createIssueController.setIssuesController(this);
        createIssueController.setAssignTo(assignTo);
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
        users_list_button.setDisable(false);
    }


    public void loadIssues(){
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
                  //namapovat issues
                    //volat jen když vytvořim novej projekt, jinak mi to vrátí loadProjects
                    logger.info("All issues loaded successfully");
                } else logger.error("Error while loading issues");
            } catch (InterruptedException | ExecutionException e) {
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
        users_list_button.setDisable(true);
    }


}




