package cz.vse.java.pfej00.tymovyProjekt.gui;
import cz.vse.java.pfej00.tymovyProjekt.Model.UserDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class UsersListController {

    @FXML
    private TextField search;

    @FXML
    private Button back;

    private List<UserDto> listOfUsers;


    public void setListOfUsers(List<UserDto> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }


}
