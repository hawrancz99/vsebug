package cz.vse.java.pfej00.tymovyProjekt.gui;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.vse.java.pfej00.tymovyProjekt.Model.TokenDto;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainController {
    @FXML
    public Button register;
    @FXML
    public Button login;

    @FXML
    public TextField passwordLoginField = new TextField();
    @FXML
    public TextField usernameLoginField = new TextField();


    private final String LOGIN_USER = "sendLoginUser";

    private static final Logger logger = LogManager.getLogger(ClientCallerTask.class);


    public MainController() {
        this.register = new Button();
        this.login = new Button();
    }

    @FXML
    public void initialize() {
        login.setOnAction(this::handle);
    }


    public void getRegister(ActionEvent actionEvent) throws IOException {
        register.setDisable(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/register.fxml"));
        Parent root = fxmlLoader.load();
        RegisterController registerController = fxmlLoader.getController();
        registerController.setRegisterButton(register);
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //tohle umožní kliknout znovu na editovat v případě, že se zavře přes křížek
        primaryStage.setOnCloseRequest(Event ->
                register.setDisable(false));
    }

    private void handle(Event event) {
        if (usernameLoginField.getText().isEmpty() || passwordLoginField.getText().isEmpty()) {
            PopupBuilder.loadPopup("/allFieldsValid.html");
            clear();
        } else {
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("username", usernameLoginField.getText());
            post.put("password", passwordLoginField.getText());
            ClientCallerTask task = new ClientCallerTask(LOGIN_USER, post.toString());
            task.setOnRunning((successEvent) -> {
                login.setDisable(true);
                stg.show();
            });

            task.setOnSucceeded((succeededEvent) -> {
                login.setDisable(false);
                stg.hide();
                try {
                    Response response = task.get();
                    if (response.isSuccessful()) {
                        try {
                            String json = Objects.requireNonNull(response.body()).string();
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                            final ObjectNode node = new ObjectMapper().readValue(json, ObjectNode.class);
                            JsonNode token = node.get("access");
                            TokenDto.getTOKEN().setTokenValue(token.asText());
                            logger.info("User: {} logged successfully", usernameLoginField.getText());
                            loadProjects();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        PopupBuilder.loadPopup("/unknownCredentials.html");
                        logger.warn("Unknown credentials used for login, username: {}, password: {}", usernameLoginField.getText(), passwordLoginField.getText());
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


    private void loadProjects() throws IOException {
        Stage stage = (Stage) usernameLoginField.getScene().getWindow();
        stage.close();
        Parent root = new FXMLLoader(getClass().getResource("/main.fxml")).load();
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void clear() {
        usernameLoginField.clear();
        passwordLoginField.clear();
    }
}