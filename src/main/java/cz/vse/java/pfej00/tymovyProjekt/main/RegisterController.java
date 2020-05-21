package cz.vse.java.pfej00.tymovyProjekt.main;

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
           loadPopup("Musíš vyplnit všechna pole", "/allFieldsValid.html");
        } else if(isUserUnique(allDatabaseUsers, usernameField.getText())){
            if(passwordField.getText().matches("(?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40}"))
            {
                System.out.println("heslo splňuje podmínky");
                registerButton.setDisable(false);
                Stage stage = (Stage) registerNewUser.getScene().getWindow();
                stage.close();
            }
            else{
                loadPopup("Heslo je příliš slabé", "/popup.html");
            }
        }
        else {
            loadPopup("Username je obsazený", "/userNotUnique.html");
        }
    }

    public void setRegisterButton(Button registerButton){
        this.registerButton = registerButton;
    }


    private void loadPopup(String s, String popup) throws InterruptedException {
        WebView webView = new WebView();
        webView.getEngine().load(getClass().getResource(popup).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(new Scene(webView, 200, 200));
        stage.setTitle(s);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        usernameField.clear();
        passwordField.clear();
        rolesOption.getSelectionModel().clearSelection();
      /*  long mTime = System.currentTimeMillis();
        long end = mTime + 2000; // 5 seconds
        while (mTime < end)
        {
            mTime = System.currentTimeMillis();
        } */
      Thread.sleep(2000);
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
        return  false;
    }

}
