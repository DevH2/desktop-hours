package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController implements Initializable {
    private List <User> users = UserDataAccess.getInstance().getAll();

    //FXML
    @FXML
    private JFXSnackbar snackBar;
    @FXML
    private TextField searchBar;
    @FXML
    private VBox usersContainer;
    @FXML
    private Label leftTitle;
    @FXML
    private Button signInButton;
    @FXML
    private AnchorPane userPanel;
    @FXML
    private JFXPasswordField signInInput;
    @FXML
    private Node userCard;

    public MainController() throws MalformedURLException {
    }


    public void handleSignIn(ActionEvent event) throws IOException {
        System.out.println(signInInput.getText());
        UserData currentUser = UserDataAccess.getInstance().get(signInInput.getText());
        System.out.println(currentUser.getIsSignedIn());
        UserDataAccess.getInstance().signInOrOut(currentUser.getIsSignedIn(), signInInput.getText(), currentUser.getName());
        signInInput.setText("");
        refreshListView(); //Doing this synchronously also causes the Rippler to wait
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String formattedDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(LocalDateTime.now());
        leftTitle.setText("Users - " + formattedDate);

        loadFXMLComponents();
        try {
            refreshListView();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //usersContainer.getChildren().stream().forEach(userPane -> { ((UserPane) userPane).initLabels(); });
    }

    private void refreshListView() throws MalformedURLException {
        users = UserDataAccess.getInstance().getAll();
        usersContainer.getChildren().removeAll();
        users.stream().forEach(user -> {
            UserPane component = null;
            try {
                component = new UserPane(user.getName(), user.getIsSignedIn(), user.getTimeIn(), user.getTotalTime());
                //System.out.println(""+ user.getName() + user.getIsSignedIn() + user.getTimeIn() + user.getTotalTime());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            usersContainer.getChildren().add(component);
        });
        usersContainer.getChildren().forEach(userPane -> {
            ((UserPane) userPane).initLabels(
                    ((UserPane) userPane).getName(),
                    ((UserPane) userPane).getSignInStatus(),
                    ((UserPane) userPane).getTimeIn(),
                    ((UserPane) userPane).getTotalTime()
            );
            //System.out.println(userPane);
        });
        return;
    }

    private void showSnackBar(String message){
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                actionEvent -> {
                    snackBar.enqueue(new JFXSnackbar.SnackbarEvent(new Label(message)));
                })
        );
        timeline.play();
        snackBar.close();
        return;
    }

    private void loadFXMLComponents(){
        try {
            URL userCardURL = getClass().getResource("UserCard.fxml");
            userCard = FXMLLoader.load(userCardURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
