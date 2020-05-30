package cz.vse.java.pfej00.tymovyProjekt.gui;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsersListController implements Initializable {

    @FXML
    private TableView<UserDto> usersTableView;

    @FXML
    private TextField search;

    @FXML
    private ChoiceBox<String> choiceBox = new ChoiceBox<>();


    @FXML
    private TableColumn<UserDto, String> usernameColumn = new TableColumn<>();

    @FXML
    private TableColumn<UserDto, String> roleColumn = new TableColumn<>();

    private static final Logger logger = LogManager.getLogger(IssuesController.class);

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<UserDto, String>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<UserDto, String>("role"));
        fillUserstable();
    }



    public ObservableList<UserDto> getUsers(List<UserDto> loadedUsers) {
        ObservableList<UserDto> users = FXCollections.observableArrayList();
        users.addAll(loadedUsers);
        return users;
    }

    private void fillUserstable() {
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
                    usernameColumn.setCellValueFactory(new PropertyValueFactory<UserDto, String>("username"));
                    roleColumn.setCellValueFactory(new PropertyValueFactory<UserDto, String>("role"));
                    List<UserDto> users = fillTableWithUsers(response);
                    fillFilteredUsers(users);
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

    private List<UserDto> fillTableWithUsers(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.reader().forType(new TypeReference<List<UserDto>>() {
        }).readValue(Objects.requireNonNull(response.body().string()));
    }

    private void fillFilteredUsers(List<UserDto> users) {
        ObservableList localList = getUsers(users);
        FilteredList<UserDto> flPerson = new FilteredList(localList, p -> true);;//Pass the data to a filtered list
        usersTableView.setItems(flPerson);//Set the table's items using the filtered list
        choiceBox.getItems().addAll("username", "role");
        search.setPromptText("Search here!");
        search.setOnKeyReleased(keyEvent ->
        {
            //zatím choicebox nemáme, asi bych nechal jen na username
            switch (choiceBox.getValue())//Switch on choiceBox value
            {
                case "username":
                    flPerson.setPredicate(p -> p.getUsername().toLowerCase().contains(search.getText().toLowerCase().trim()));//filter table by username
                    break;
                case "role":
                    flPerson.setPredicate(p -> p.getRole().toLowerCase().contains(search.getText().toLowerCase().trim()));//filter table by role
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {//reset table and textfield when new choice is selected
            if (newVal != null) {
                search.setText("");
                flPerson.setPredicate(null);
            }
        });


    }
}
