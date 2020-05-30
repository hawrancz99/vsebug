package cz.vse.java.pfej00.tymovyProjekt.gui;

import cz.vse.java.pfej00.tymovyProjekt.Model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectsController {
    @FXML
    private Button users_list_button;

    @FXML
    private Button createProject;

    @FXML
    private Button log_out = new Button();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    public AnchorPane allButtons;

    private List<UserDto> listOfUsers = new ArrayList<>();


    //tohle bude ještě sranda
    private TableColumn username = new TableColumn();
    private TableColumn role = new TableColumn();

    private List<ProjectDto> projects = new ArrayList<>();

    private final String GET_PROJECTS = "sendGetProjects";


    private static final Logger logger = LogManager.getLogger(ProjectsController.class);

    private ObservableList<Button> buttons = FXCollections.observableArrayList();


    public ProjectsController() {
    }

    @FXML
    public void initialize() {
        //   listOfUsers.getColumns().add(username);
        //   listOfUsers.getColumns().add(role);
        users_list_button.setOnAction(event -> {
            try {
                loadUsersByClick(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loadProjects();
    }


    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Stage stage = (Stage) log_out.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));

        //tohle by se samo přesetovalo, ale nechceme, aby to jiný uživatel měl na screeně
        TokenDto.getTOKEN().setTokenValue(null);
        Parent root = fxmlLoader.load();
        Stage primaryStage = new Stage();
        primaryStage.setTitle("VŠEBUG");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    @FXML
    public void loadUsersByClick(ActionEvent event) throws IOException {
                    disableAllButtons();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/list_of_users.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage primaryStage = new Stage();
                    primaryStage.initStyle(StageStyle.UTILITY);
                    primaryStage.setTitle("");
                    primaryStage.setScene(new Scene(root));
                    primaryStage.show();
                    primaryStage.setOnCloseRequest(event1 -> enableAllButtons());
    }


    void loadProjects() {
        Stage stg = new Stage();
        ClientCallerTask task = new ClientCallerTask(GET_PROJECTS, null);
        task.setOnRunning((runningEvent) -> {
            stg.show();
        });

        task.setOnSucceeded((succeededEvent) -> {
            stg.hide();
            try {
                Response response = task.get();
                if (response.isSuccessful()) {
                    fillProjects(response);
                    logger.info("Projects successfully loaded {}", response);
                } else logger.error("Error while loading project, caused by {}", response);
            } catch (InterruptedException | ExecutionException | IOException e) {
                logger.error("Error while loading projects, caused by {}", e.getMessage());
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

    //TODO
    //tohle padá kvůli tomu anchorpanu, ale to se opraví, až se to bude přidávat na normální místo
    private void fillProjects(Response response) throws IOException {
        projects.clear();
        buttons.clear();
        vbox.getChildren().clear();
        //aby byly tlačítka vedle sebe
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.TOP_RIGHT);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        projects = objectMapper.reader().forType(new TypeReference<List<ProjectDto>>() {
        }).readValue(Objects.requireNonNull(response.body()).string());
        for (ProjectDto projectDto : projects) {
            String projectName = projectDto.getName();
            ObservableList<String> list = FXCollections.observableArrayList(projectName);
            Button b = new Button(list.toString());
            Button deleteB = new Button();
            Button editB = new Button();
            HBox hBox = new HBox(b, deleteB, editB);
            hBox.setSpacing(5);

            ImageView imageView = new ImageView("/trashcan.png");
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            deleteB.setGraphic(imageView);
            deleteB.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

            ImageView imageView1 = new ImageView("/pen.png");
            imageView1.setFitHeight(20);
            imageView1.setFitWidth(20);
            editB.setGraphic(imageView1);
            editB.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

            deleteB.setOnAction(event -> {
                        hBox.getChildren().clear();
                        vbox.getChildren().remove(hBox);
                        buttons.remove(b);
                        buttons.remove(deleteB);
                        buttons.remove(editB);
                        callDeleteProject(projectDto.getId());
                    }
            );
            b.setText(projectName);
            b.setOnAction(event ->
                    {
                        try {
                            getIssues(projectDto);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            editB.setOnAction(event ->
                    {
                        try {
                            disableAllButtons();
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/edit_project.fxml"));
                            Parent root = fxmlLoader.load();
                            EditProjectController editProjectController = fxmlLoader.getController();
                            //aby save dělalo fakin enable tak se tam bude muset nasetovat zase úplně všechno :)))))))))))))))), řekl bych
                            editProjectController.setButtonWithProjectName(b);
                            editProjectController.setProjectsController(this);
                            editProjectController.setEditedProject(projectDto);
                            editProjectController.setButtons(buttons);
                            editProjectController.setLog_out(log_out);
                            editProjectController.setUsers_list_button(users_list_button);
                            editProjectController.setCreateProject(createProject);
                            Stage primaryStage = new Stage();
                            primaryStage.initStyle(StageStyle.UTILITY);
                            primaryStage.setTitle("");
                            primaryStage.setScene(new Scene(root));
                            primaryStage.show();
                            primaryStage.setOnCloseRequest(event1 -> enableAllButtons());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            b.setId("rich-blue");
            deleteB.setId("trashCan");
            editB.setId("pen");
            b.setPrefWidth(300);
            b.setPrefHeight(10);
            String style = getClass().getResource("/button.css").toExternalForm();
            Stage stage = (Stage) vbox.getScene().getWindow();
            stage.getScene().getStylesheets().add(style);
            buttons.add(b);
            buttons.add(deleteB);
            buttons.add(editB);

            vbox.getChildren().add(hBox);
        }
        vbox.prefHeightProperty().bind(allButtons.heightProperty());

    }

    private void getIssues(ProjectDto projectDto) throws IOException {
        disableAllButtons();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/issues.fxml"));
        Parent root = fxmlLoader.load();
        IssuesController issuesController = fxmlLoader.getController();
        if(projectDto.getIssues() != null) {
            issuesController.setListOfIssues(projectDto.getIssues());
        } else issuesController.setListOfIssues(new ArrayList<>());
        issuesController.setUsers_list_button(users_list_button);
        issuesController.setCreateProject(createProject);
        issuesController.setProjectDto(projectDto);
        issuesController.setListOfUsers(listOfUsers);
        issuesController.setLog_out(log_out);
        issuesController.setButtons(buttons);
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event ->
        {
            //je to nešťastný, ale stejně by se muselo volat načítání issues při otevření ... někde se to updatovat musí
            loadProjects();
            enableAllButtons();
        });
    }

    public void createNewProject(ActionEvent actionEvent) throws IOException {
        disableAllButtons();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/create_project.fxml"));
        Parent root = fxmlLoader.load();
        CreateProjectController createProjectController = fxmlLoader.getController();
        createProjectController.setUsers_list_button(users_list_button);
        createProjectController.setCreateProject(createProject);

        createProjectController.setLog_out(log_out);
        createProjectController.setButtons(buttons);
        createProjectController.setProjects(projects);
        createProjectController.setProjectsController(this);
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //tohle je přes křížek
        primaryStage.setOnCloseRequest(event ->
        {
            enableAllButtons();
        });
    }


    private void disableAllButtons() {
        users_list_button.setDisable(true);
        createProject.setDisable(true);

        log_out.setDisable(true);
        for (Button b : buttons) {
            b.setDisable(true);
        }
    }

    private void enableAllButtons() {
        users_list_button.setDisable(false);
        createProject.setDisable(false);
        log_out.setDisable(false);
        for (Button b : buttons) {
            b.setDisable(false);
        }
    }

    private void callDeleteProject(int id) {
        Stage stg = new Stage();
        //musim to tam poslat jako string
        String post = "" + id + "";
        ClientCallerTask task = new ClientCallerTask("sendDeleteProject", post);
        task.setOnRunning((successEvent) -> {
            stg.show();
        });

        task.setOnSucceeded((succeededEvent) -> {
            stg.hide();
            try {
                Response response = task.get();
                if (response.isSuccessful()) {
                    logger.info("Project was deleted");
                } else logger.error("Error while deleting project, caused by {}", response);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error while deleting project, caused by {}", e.getMessage());
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
