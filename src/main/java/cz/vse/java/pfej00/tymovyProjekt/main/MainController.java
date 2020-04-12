package cz.vse.java.pfej00.tymovyProjekt.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import okhttp3.*;

import java.io.IOException;

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
    private String sendGet() throws Exception {

        Request request = new Request.Builder()
                .url("https://www.google.com/search?q=mkyong")
                .addHeader("custom-key", "mkyong")  // add request headers
                .addHeader("User-Agent", "OkHttp Bot")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            return response.body().string();
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
                .url("https://httpbin.org/post")
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