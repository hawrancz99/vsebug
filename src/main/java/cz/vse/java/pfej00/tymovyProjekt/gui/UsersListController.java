package cz.vse.java.pfej00.tymovyProjekt.gui;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UsersListController implements Initializable {

    @FXML
    private TableView<UserDto> usersTableView;

    @FXML
    private TextField search;

    @FXML
    private Button back;

    private List<UserDto> listOfUsers;

    @FXML
    private TableColumn<UserDto, String> usernameColumn = new TableColumn<>();

    @FXML
    private TableColumn<UserDto, String> roleColumn = new TableColumn<>();

    @Override
    public void initialize(URL location, ResourceBundle resources){
        usernameColumn.setCellValueFactory(new PropertyValueFactory<UserDto, String>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<UserDto, String>("role"));
        usersTableView.setItems(getUsers());
    }


    public void setListOfUsers(List<UserDto> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }


    public ObservableList<UserDto> getUsers(){
        ObservableList<UserDto> users = FXCollections.observableArrayList();
        users.addAll(listOfUsers);
       return users;
    }

}
