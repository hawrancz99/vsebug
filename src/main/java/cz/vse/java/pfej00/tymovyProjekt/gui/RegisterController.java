package cz.vse.java.pfej00.tymovyProjekt.gui;

import cz.vse.java.pfej00.tymovyProjekt.builders.PopupBuilder;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.*;

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

    @FXML
    public Button closeBtn;

    @FXML
    public void initialize() {
        ROLES.add("Developer");
        ROLES.add("Tester");
        ROLES.add("Analytic");
        rolesOption.getItems().setAll(ROLES);
        registerNewUser.setDisable(false);

        //tohle pro případ, když bych chtěl mít disalovaný tlačítko registrace, když neni text vyplněn - stávající logika mi přijde ale fajn - přes popup
   /*     passwordField.textProperty().addListener(new ChangeListener<String>() {
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
    /*    usernameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                if(!newValue.isEmpty()){
                    registerNewUser.setDisable(false);
                }else{
                    registerNewUser.setDisable(true);
                }
            }
        }); */
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                registerButton.setDisable(false);
                Stage stage = (Stage) closeBtn.getScene().getWindow();
                stage.close();
            }
        });
        //zavolá se getUsers? abych si ušetřil čekání při registraci? zjistit dobu trvání
    }

    public void setRegisterNewUser(ActionEvent actionEvent) throws Exception {
        Set<String> allDatabaseUsers = new HashSet<>();//zjistit všechny uživatele - pokud ne už v inicializaci - to je možná moudřejší

        if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || rolesOption.getSelectionModel().getSelectedItem() == null){
           PopupBuilder.loadPopup("/allFieldsValid.html");
            clear();
        } else if(isUserUnique(allDatabaseUsers, usernameField.getText())){
            if(!passwordField.getText().isEmpty())
            {
                JSONObject post = new JSONObject();
                post.put("username", usernameField.getText());
                post.put("password", passwordField.getText());
                post.put("role", 1);
                ClientCallerTask clientCallerTask = new ClientCallerTask("registerNewUser", post.toString());
                Response response = clientCallerTask.call();
                if(!response.isSuccessful()){
                    PopupBuilder.loadPopup("/userNotUnique.html");
                    clear();
                }else {
                    registerButton.setDisable(false);
                    Stage stage = (Stage) registerNewUser.getScene().getWindow();
                    stage.close();
                }
            }
            else{
                PopupBuilder.loadPopup("/popup.html");
                clear();
            }
        }
        else {
            PopupBuilder.loadPopup("/userNotUnique.html");
            clear();
        }
    }

    public void setRegisterButton(Button registerButton){
        this.registerButton = registerButton;
    }


    public void getValueFromCombobox(MouseEvent mouseEvent) throws Exception {

        String chosenRole = (String) rolesOption.getSelectionModel().getSelectedItem();
        if(chosenRole == null) {
            chosenRole = rolesOption.getEditor().getText();
        }
        System.out.println(chosenRole);
    }


    private boolean isUserUnique(Set<String> allUsers, String username){
        boolean isUnique = allUsers.stream().anyMatch(userFromDatabase -> userFromDatabase.equals(username));
        //až budou data, vrátím isUnique
        return  true;
    }

    private void clear(){
        usernameField.clear();
        passwordField.clear();
        rolesOption.getSelectionModel().clearSelection();
    }

}
