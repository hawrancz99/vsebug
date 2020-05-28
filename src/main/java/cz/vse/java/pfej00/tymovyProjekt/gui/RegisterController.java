package cz.vse.java.pfej00.tymovyProjekt.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.ProjectDto;
import cz.vse.java.pfej00.tymovyProjekt.Model.RoleDto;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterController {

    @FXML
    public Button registerNewUser;

    @FXML
    public ComboBox<String> rolesOption = new ComboBox<>();

    @FXML
    public TextField usernameField = new TextField();

    @FXML
    public TextField passwordField = new TextField();


    private final List<String> ROLES = new ArrayList<>();
    //private HashMap<int, String> ROLES = new HashMap<int, String>();

    private Button registerButton;

    private final String REGISTER_USER = "sendRegisterNewUser";

    private static final Logger logger = LogManager.getLogger(RegisterController.class);

    @FXML
    public void initialize() {
        getRoles();
        ROLES.add("Developer");
        ROLES.add("Tester");
        ROLES.add("Analytic");

        rolesOption.getItems().setAll(ROLES);
        registerNewUser.setDisable(false);
        registerNewUser.setOnAction(this::handle);
    }


    public void setRegisterButton(Button registerButton){
        this.registerButton = registerButton;
    }

    private void handle(Event event) {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || rolesOption.getSelectionModel().isEmpty()) {
            PopupBuilder.loadPopup("/allFieldsValid.html");
            clear();
            //v tomhle ifu bude pak REGEX, teď nedává smysl
        }else if (passwordField.getText().isEmpty()){
            PopupBuilder.loadPopup("/passwordIsWeak.html");
            clear();
        }
        else {
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("username", usernameField.getText());
            post.put("password", passwordField.getText());
            //Role
            post.put("role", 1);
            ClientCallerTask task = new ClientCallerTask(REGISTER_USER, post.toString());
            task.setOnRunning((successEvent) -> {
                registerNewUser.setDisable(true);
                stg.show();
            });

            task.setOnSucceeded((succeededEvent) -> {
                registerNewUser.setDisable(false);
                stg.hide();
                try {
                    Response response = task.get();
                    if(response.isSuccessful()){
                        registerButton.setDisable(false);
                        Stage stage = (Stage) registerNewUser.getScene().getWindow();
                        stage.close();
                        logger.info("New user: {} registered successfully", usernameField.getText());
                    }else{
                        PopupBuilder.loadPopup("/userNotUnique.html");
                        logger.info("Registering new user failed, username: {} already exists", usernameField.getText());
                        clear();
                    }
                } catch (InterruptedException | ExecutionException e) {
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
    }

    private void getRoles(){
        ClientCallerTask clientCallerTask = new ClientCallerTask("sendGetRoles", null);
        try {
            Response response = clientCallerTask.call();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            List<RoleDto> projectDtos = objectMapper.reader().forType(new TypeReference<List<RoleDto>>() {}).readValue(Objects.requireNonNull(response.body()).string());
            System.out.println(response.body());
            Iterable<RoleDto> iterable = projectDtos;
            for (RoleDto s : iterable) {
                //ROLES.add(s.getRole());
                System.out.println(s.getRole());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }


        //loadIssues(projectDtos.get(0));
    }

    private void clear(){
        usernameField.clear();
        passwordField.clear();
        rolesOption.getSelectionModel().clearSelection();
    }

}
