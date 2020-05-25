package cz.vse.java.pfej00.tymovyProjekt.gui;

import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
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
import javafx.util.Builder;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateProjectController {

    @FXML
    private TextField inputOfProjects = new TextField();

    @FXML
    private Button createNewProject = new Button();

    private final String CREATE_PROJECT = "sendCreateProject";

    private static final Logger logger = LogManager.getLogger(MainController.class);



    @FXML
    public void initialize() {
        createNewProject.setOnAction(this::handle);
    }

    private void handle(Event event) {
        if (!inputOfProjects.getText().isEmpty()) {
            Stage stg = new Stage();
            JSONObject post = new JSONObject();
            post.put("name", inputOfProjects.getText());
            //zatim takhle než zjistíme formát
            post.put("created", "2020-05-22T21:03:41Z");
            ClientCallerTask task = new ClientCallerTask(CREATE_PROJECT, post.toString());
            task.setOnRunning((successEvent) -> {
                stg.show();
            });

            task.setOnSucceeded((succeededEvent) -> {
                stg.hide();
                try {
                    Response response = task.get();
                    if (response.isSuccessful()) {
                        Stage stage = (Stage) createNewProject.getScene().getWindow();
                        stage.close();
                        
                        //budou se muset všechny nasetovat :)
                 //       primaryStage.setOnCloseRequest(event1 -> enableAllButtons());
                        logger.info("Project created successfully");
                    } else logger.error("Error while creating project");
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while creating project, caused by {}", e.getMessage());
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
        } else {
            PopupBuilder.loadPopup("/allFieldsValid.html");
        }
    }
}
