package cz.vse.java.pfej00.tymovyProjekt.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import cz.vse.java.pfej00.tymovyProjekt.Model.TokenDto;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.main.ServerClient;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;


public class MainController {
    @FXML
    public TextField input;
    @FXML
    public ListView output;
    @FXML
    public Button register;
    @FXML
    public Button login;

    @FXML
    public TextField passwordLoginField = new TextField();
    @FXML
    public TextField usernameLoginField = new TextField();

    private final OkHttpClient httpClient;

    private final String LOGIN_USER = "sendLoginUser";


    public MainController()
    {
        this.httpClient = new OkHttpClient();
        this.register = new Button();
        this.login = new Button();
    }

    @FXML
    public void initialize() {

    }

    public void sendMessage(ActionEvent actionEvent) throws Exception {
        String text = input.getText();

        System.out.println("Testing 1 - Send Http GET request");
       // System.out.println(sendGet());

    }


    private void sendPost() throws Exception {

        // form parameters
        RequestBody formBody = new FormBody.Builder()
                .add("username", "abc")
                .add("password", "123")
                .add("custom", "secret")
                .build();

        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/issues/")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            System.out.println(response.body().string());
        }
    }

    public void getRegister(ActionEvent actionEvent) throws IOException {
        register.setDisable(true);
        System.out.println(register.getId());
        //Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));

        /*
        RegisterController c = new RegisterController(register);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/register.fxml"));
        fxmlLoader.setController(c);
        Parent root = (Parent)fxmlLoader.load();*/

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

    private boolean isUserInDatabase(String username, String password) throws Exception {
        JSONObject post = new JSONObject();
        post.put("username", username);
        post.put("password", password);

        ClientCallerTask clientCallerTask = new ClientCallerTask(LOGIN_USER, post.toString());
        Response response = clientCallerTask.call();
        if(response.isSuccessful()){
            String json = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            final ObjectNode node = new ObjectMapper().readValue(json, ObjectNode.class);
            JsonNode token = node.get("access");
            TokenDto.getTOKEN().setTokenValue(token.asText());
            return true;
        }
        return false;
    }

    public void onSuccessLoginOpenProjects(MouseEvent mouseEvent) throws Exception {
        if(usernameLoginField.getText().isEmpty() || passwordLoginField.getText().isEmpty()){
            PopupBuilder.loadPopup("/allFieldsValid.html");
            clear();
        } else if(isUserInDatabase(usernameLoginField.getText(), passwordLoginField.getText())){
            //tady je success - otevřou se "projekty" - neni zatim screena
            Stage stage = (Stage) usernameLoginField.getScene().getWindow();
            stage.close();
            Parent root = new FXMLLoader(getClass().getResource("/main.fxml")).load();
            Stage primaryStage = new Stage();
            primaryStage.initStyle(StageStyle.UTILITY);
            primaryStage.setTitle("");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }else{
            PopupBuilder.loadPopup("/unknownCredentials.html");
            clear();
        }
    }

    private void clear(){
        usernameLoginField.clear();
        passwordLoginField.clear();
    }
}