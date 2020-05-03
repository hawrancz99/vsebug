package cz.vse.java.pfej00.tymovyProjekt.main;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.Model.IssueDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import okhttp3.*;

import java.io.IOException;
import java.util.List;


public class MainController {
    @FXML
    public TextField input;
    @FXML
    public ListView output;
    @FXML
    public Button send;

    private final OkHttpClient httpClient;

    public MainController()
    {
        this.httpClient = new OkHttpClient();
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
}