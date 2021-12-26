package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class UserCardController implements Initializable {
    @FXML
    private Label usernameLabel;

    @FXML
    private Label signInStatusLabel;

    @FXML
    private Label totalTimeLabel;

    @FXML
    private Label timeInLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
