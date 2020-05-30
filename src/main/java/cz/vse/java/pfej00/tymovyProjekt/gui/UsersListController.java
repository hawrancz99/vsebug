package cz.vse.java.pfej00.tymovyProjekt.gui;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private Button back;

    private List<UserDto> listOfUsers;

    @FXML
    private TableColumn<UserDto, String> usernameColumn = new TableColumn<>();

    @FXML
    private TableColumn<UserDto, String> roleColumn = new TableColumn<>();

    @FXML
    public void initialize(URL location, ResourceBundle resources){
        usernameColumn.setCellValueFactory(new PropertyValueFactory<UserDto, String>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<UserDto, String>("role"));
        fillUserstable();
    }


    public void setListOfUsers(List<UserDto> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }


    public ObservableList<UserDto> getUsers(List<UserDto> loadedUsers){
        ObservableList<UserDto> users = FXCollections.observableArrayList();
        users.addAll(loadedUsers);
       return users;
    }

    private void fillUserstable(){
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
                    List<UserDto> users = fillTable(response);
                    usersTableView.setItems(getUsers(users));


                } else System.out.println("RIP");;
            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
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

    private List<UserDto> fillTable(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.reader().forType(new TypeReference<List<UserDto>>() {
        }).readValue(Objects.requireNonNull(response.body().string()));
    }


}
