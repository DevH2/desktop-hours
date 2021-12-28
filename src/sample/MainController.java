package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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


    public void handleSignIn(ActionEvent event) throws IOException, InterruptedException {
        UserData currentUser = UserDataAccess.getInstance().get(signInInput.getText());
        String signedInOrOut = UserDataAccess.getInstance().signInOrOut(currentUser.getIsSignedIn(), signInInput.getText(), currentUser.getName());
        if(signedInOrOut.equals("Successfully signed in")){
            getUserPane(currentUser.getName()).getTimeline().playFromStart();
            System.out.println("playing timeline");
        } else if(signedInOrOut.equals("Successfully signed out")){
            System.out.println("Stopped timeline");
            getUserPane(currentUser.getName()).getTimeline().pause();
        } else return;
        signInInput.setText("");

        try {
            updateListView();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Doing this synchronously also causes the Rippler to wait
    }

    public void handleCreateUser(){

    }

    public void handleDeleteUser(){

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String formattedDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(LocalDateTime.now());
        leftTitle.setText("Users - " + formattedDate);

        loadFXMLComponents();

        try {
            initListView();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        //usersContainer.getChildren().stream().forEach(userPane -> { ((UserPane) userPane).initLabels(); });
    }
    private void updateListView() throws MalformedURLException {
        users = UserDataAccess.getInstance().getAll();
        users.forEach(user -> {
            try {
                //Need to resolve updating total time not only when the timer is moving
                UserPane pane = getUserPane(user.getName());
                pane.setTotalTime(user.getTotalTime());
                //Resorting to changing prop here to update totalTime on signout
                // as you can't bind 2 properties to one unless its a BooleanProp.
                String currentValue = pane.getDisplayedTimeInProp().getValue();
                pane.getDisplayedTimeInProp().setValue(currentValue.split(" ")[0] +" "+ user.getTotalTime());

                pane.getSignInStatusLabel().setText(user.getIsSignedIn() ? "SIGNED OUT" : "SIGNED IN");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    //Formerly known as refreshListView()
    private void initListView() throws MalformedURLException {
        users = UserDataAccess.getInstance().getAll();
        usersContainer.getChildren().clear();
        users.forEach(user -> {
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
            if(!((UserPane)(userPane)).getSignInStatus()){
                ((UserPane)(userPane)).getTimeline().play();
            }
            //System.out.println(userPane);
        });
        return;
    }

    private void showSnackBar(String message){
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                actionEvent -> {
                    snackBar.enqueue(new JFXSnackbar.SnackbarEvent(new Label(message)));
                })
        );
        timeline.setCycleCount(1);
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

    private UserPane getUserPane(String name) throws IOException, InterruptedException {
        LinkedHashMap<String, UserPane> userPanes = new LinkedHashMap<>();
        usersContainer.getChildren().forEach(userPane -> {
            userPanes.put(((UserPane)(userPane)).getName(),((UserPane)(userPane)));
        });
        return userPanes.get(name);
    }
}
