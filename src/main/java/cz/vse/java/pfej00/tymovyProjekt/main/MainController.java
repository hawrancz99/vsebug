package cz.vse.java.pfej00.tymovyProjekt.main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.*;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainController {
    @FXML
    public TextField input;
    @FXML
    public ListView output;
    @FXML
    public Button register;
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

    private final OkHttpClient httpClient;

    public MainController()
    {
        this.httpClient = new OkHttpClient();
        this.registerNewUser = new Button();
        this.register = new Button();
    }

    @FXML
    public void initialize() {
        ROLES.add("Developer");
        ROLES.add("Tester");
        rolesOption.getItems().setAll(ROLES);
        register.setDisable(false);
        registerNewUser.setDisable(true);
        usernameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                if(!newValue.isEmpty()){
                    registerNewUser.setDisable(false);
                }else{
                    registerNewUser.setDisable(true);
                }
            }
        });
        //zavolá se getUsers
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
        register.setId("register");
        register.setDisable(true);
        System.out.println(register.getId());
        Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }



    public void setRegisterNewUser(ActionEvent actionEvent) throws Exception {
        //tady zavolat piči jakoby znovu inicilizaci toho login okna, aby měl k dispozici users
        System.out.println(register.getId());

        register.setDisable(false);
        Stage stage = (Stage) registerNewUser.getScene().getWindow();
        stage.close();
        initialize();

    }

    public void getValueFromCombobox(MouseEvent mouseEvent) throws Exception {

        String chosenRole = (String) rolesOption.getSelectionModel().getSelectedItem();
        if(chosenRole == null) {
            chosenRole = rolesOption.getEditor().getText();
        }
        System.out.println(chosenRole);
    }

}