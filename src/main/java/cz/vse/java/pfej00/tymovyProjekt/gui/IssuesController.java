package cz.vse.java.pfej00.tymovyProjekt.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.*;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IssuesController implements Initializable {
    @FXML
    private TableView<ExtendedIssue> issueDtoTableView;

    @FXML
    private TextField searchIssues = new TextField();

    @FXML
    private TableColumn<ExtendedIssue, String> issueNameColumn = new TableColumn<>();

    @FXML
    private TableColumn<ExtendedIssue, String> descriptionColumn = new TableColumn<>();

    @FXML
    private TableColumn<ExtendedIssue, String> stateColumn = new TableColumn<>();

    @FXML
    private TableColumn<ExtendedIssue, UserDto> assignedToColumn = new TableColumn<>();

    @FXML
    private TableColumn<ExtendedIssue, Date> createdColumn = new TableColumn<>();

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


    private List<IssueDto> localLoadedIssues = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(IssuesController.class);

    /**
     * Metoda otev??r?? obrazovku s u??ivateli
     * Na zav??en?? uvo??uje v??echny tla????tka na t??to obrazovce
     */
    public void openListOfUsers(ActionEvent actionEvent) throws IOException {
        disableAllButtons();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/list_of_users.fxml"));
        Parent root = fxmlLoader.load();
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event1 -> enableAllCreateIssueuttons());
    }


    /**
     * Metoda p??episuje metodu z interfacu Initializable
     * Setuje sloupce tabulky
     * Na????t?? v??echny u??ivatele
     * Na????t?? v??echny issues
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        issueNameColumn.setCellValueFactory(new PropertyValueFactory<ExtendedIssue, String>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<ExtendedIssue, String>("description"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<ExtendedIssue, Date>("created"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<ExtendedIssue, String>("state"));
        assignedToColumn.setCellValueFactory(new PropertyValueFactory<ExtendedIssue, UserDto>("assignee"));
        buttonColumn.setCellValueFactory(new PropertyValueFactory<ExtendedIssue, Button>("button"));


        fillIssuesTable();
    }

    /**
     * Akce tla????tka CREATE_NEW_ISSUE
     * Otev??r?? novou obrazovku pro vytvo??en?? issue
     */
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
        //d??lam to takhle, proto??e kv??li initialize nem????u volat LOAD na screen??
        createIssueController.setProjectId(CurrentOpenedProject.getPROJECT().getProjetId());
        //kv??li unik??tnosti jm??na
        createIssueController.setIssues(localLoadedIssues);
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //tohle je p??es k??????ek
        primaryStage.setOnCloseRequest(event ->
        {
            enableAllCreateIssueuttons();
        });
    }


    /**
     * Metoda p??emapov??v?? u??ivatele z datab??ze
     * do listu u??ivatel??
     */
    private List<UserDto> fillAssignToValues(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.reader().forType(new TypeReference<List<UserDto>>() {
        }).readValue(Objects.requireNonNull(response.body().string()));
    }


    /**
     * V p????pad?? zav??en?? obrazovky s u??ivateli uvol??uje tla????tka
     * na obrazovce
     */
    private void enableAllCreateIssueuttons() {
        searchIssues.setDisable(false);
        createIssue.setDisable(false);
        editIssue.setDisable(false);
        removeIssue.setDisable(false);
        userListButtonOnIssuesScreen.setDisable(false);
    }


    /**
     * Metoda vol??na z inicializace controlleru
     * Vol?? BE a response pln?? tabulku s issues
     */
    public void fillIssuesTable() {
        Stage stg = new Stage();
        String post = "" + CurrentOpenedProject.getPROJECT().getProjetId() + "";
        ClientCallerTask task = new ClientCallerTask("sendGetIssuesForProject", post);
        task.setOnRunning((successEvent) -> {
            stg.show();
        });

        task.setOnSucceeded((succeededEvent) -> {
            stg.hide();
            try {
                Response response = task.get();
                if (response.isSuccessful()) {
                    List<IssueDto> issuesForProject = fillListOfIssues(response);
                    localLoadedIssues.addAll(issuesForProject);
                    fillFilteredIssues(issuesForProject);

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

    /**
     * V p????pad?? otev??r??n?? jin?? obrazovky
     * zablokuje tla????tka
     */
    private void disableAllButtons() {
        searchIssues.setDisable(true);
        createIssue.setDisable(true);
        editIssue.setDisable(true);
        removeIssue.setDisable(true);
        userListButtonOnIssuesScreen.setDisable(true);
    }

    /**
     * Metoda p??emapuje RESPONSE ve form?? STRING (json)
     * do listu issues
     */
    private List<IssueDto> fillListOfIssues(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.reader().forType(new TypeReference<List<IssueDto>>() {
        }).readValue(Objects.requireNonNull(response.body().string()));
    }

    /**
     * Metoda vytvo???? z listu issues FilteredList
     * a napln?? n??m tabulku
     * Nastavuje akci pro TextField SEARCH_ISSUES, kde
     * texfield funguje jako fulltextov?? vyhled??va?? nad tabulkou
     */
    private void fillFilteredIssues(List<IssueDto> issues) {
        choiceBox.getItems().clear();
        ObservableList localList = getIssues(issues);
        FilteredList<ExtendedIssue> filteredList = new FilteredList(localList, p -> true); //Pass the data to a filtered list
        issueDtoTableView.setItems(filteredList);//Set the table's items using the filtered list
        choiceBox.getItems().addAll("name", "state", "description", "assignee", "created");
        choiceBox.setValue("name");
        searchIssues.setPromptText("Search here!");
        searchIssues.setOnKeyReleased(keyEvent ->
        {
            switch (choiceBox.getValue())//Switch on choiceBox value
            {
                case "name":
                    filteredList.setPredicate(p -> p.getName().toLowerCase().contains(searchIssues.getText().toLowerCase().trim()));//filter table by issueName
                    break;
                case "state":
                    filteredList.setPredicate(p -> p.getState().toLowerCase().contains(searchIssues.getText().toLowerCase().trim()));//filter table by state
                    break;
                case "description":
                    filteredList.setPredicate(p -> p.getDescription().toLowerCase().contains(searchIssues.getText().toLowerCase().trim()));//filter table by description
                    break;
                case "assignee":
                    filteredList.setPredicate(p -> p.getAssignee().getUsername().toLowerCase().contains(searchIssues.getText().toLowerCase().trim()));//filter table by assignee
                    break;
                case "created":
                    filteredList.setPredicate(p -> p.getCreated().toString().toLowerCase().contains(searchIssues.getText().toLowerCase().trim()));//filter table by date
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

    /**
     * Metoda vrac?? ObserverList, kter?? obsahuje
     * ExtendedIssue
     *
     * @param loadedIssues slou???? k napln??n?? ObserverListu
     */
    private ObservableList<ExtendedIssue> getIssues(List<IssueDto> loadedIssues) {
        ObservableList<ExtendedIssue> extendedIssues = FXCollections.observableArrayList();
        for (IssueDto i : loadedIssues) {
            ExtendedIssue extendedIssue = new ExtendedIssue();
            extendedIssue.setAssignee(i.getAssignee());
            extendedIssue.setCreated(i.getCreated());
            extendedIssue.setDescription(i.getDescription());
            extendedIssue.setId(i.getId());
            extendedIssue.setName(i.getName());
            extendedIssue.setState(i.getState());
            extendedIssue.setProject(i.getProject());
            //abychom po zav??en?? mohli volat ten refresh, pot??ebujeme i controller
            extendedIssue.setIssuesController(this);
            extendedIssues.add(extendedIssue);
        }
        return extendedIssues;
    }
}




