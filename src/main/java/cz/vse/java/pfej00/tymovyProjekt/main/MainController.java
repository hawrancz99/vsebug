package cz.vse.java.pfej00.tymovyProjekt.main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.*;

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
        System.out.println(sendGet());

    }
    private List<IssueDto> sendGet() throws Exception {

        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/issues/")
                .addHeader("custom-key", "mkyong")  // add request headers
                .addHeader("User-Agent", "OkHttp Bot")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String json = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<IssueDto> issueDto = objectMapper.reader().forType(new TypeReference<List<IssueDto>>() {}).readValue(json);
            System.out.println(issueDto.get(0).getName());
            output.getItems().addAll(issueDto);
            return issueDto;


            // Get response body
        }
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
        primaryStage.setOnCloseRequest(Event::consume);
    }

    private boolean isUserInDatabase(String username, String password){
        //zatim takhle
        return false;
    }

    public void onSuccessLoginOpenProjects(MouseEvent mouseEvent) throws IOException {
        if(usernameLoginField.getText().isEmpty() || passwordLoginField.getText().isEmpty()){
            PopupBuilder.loadPopup("/allFieldsValid.html");
        } else if(isUserInDatabase(usernameLoginField.getText(), passwordLoginField.getText())){

            //tady je success - otevřou se "projekty" - neni zatim screena
            Stage stage = (Stage) usernameLoginField.getScene().getWindow();
            stage.close();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = fxmlLoader.load();
            Stage primaryStage = new Stage();
            primaryStage.initStyle(StageStyle.UTILITY);
            primaryStage.setTitle("");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            primaryStage.setOnCloseRequest(Event::consume);
        }else{
            PopupBuilder.loadPopup("/unknownCredentials.html");
        }
    }
}