package cz.vse.java.pfej00.tymovyProjekt.gui;

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
import org.json.JSONObject;

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
    //TODO
    @FXML
    public TextField passwordField = new TextField();

    private final List<String> ROLES = new ArrayList<>();

    private Button registerButton;

    private final String REGISTER_USER = "sendRegisterNewUser";

    @FXML
    public Button closeBtn;

    @FXML
    public void initialize() {
        ROLES.add("Developer");
        ROLES.add("Tester");
        ROLES.add("Analytic");
        rolesOption.getItems().setAll(ROLES);
        registerNewUser.setDisable(false);
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                registerButton.setDisable(false);
                Stage stage = (Stage) closeBtn.getScene().getWindow();
                stage.close();
            }
        });
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
                    }else{
                        PopupBuilder.loadPopup("/userNotUnique.html");
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


    private void clear(){
        usernameField.clear();
        passwordField.clear();
        rolesOption.getSelectionModel().clearSelection();
    }

}
