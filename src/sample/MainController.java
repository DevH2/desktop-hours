package sample;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTableCell;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController implements Initializable {
    //Since no websockets exist currently, this app cannot detect and respond to rest operations done by the web app.

    private List <User> users = UserDataAccess.getInstance().getAll();
    private ObservableList<MondayCleaner> mondayCleaners;

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
    @FXML
    private JFXTreeTableColumn nameColumn;
    @FXML
    private JFXTreeTableColumn timeInColumn;
    @FXML
    private JFXTreeTableColumn timeOutColumn;
    @FXML
    private JFXTreeTableView treeTableView;


    public MainController() throws MalformedURLException {
    }


    public void handleSignIn(ActionEvent event) throws IOException, InterruptedException {
        UserData currentUser = UserDataAccess.getInstance().get(signInInput.getText());
        String signedInOrOut = UserDataAccess.getInstance().signInOrOut(currentUser.getIsSignedIn(), signInInput.getText(), currentUser.getName());
        if(signedInOrOut.equals("Successfully signed in")){
            getUserPane(currentUser.getName()).getTimeline().playFromStart();
            //System.out.println("playing timeline");
        } else if(signedInOrOut.equals("Successfully signed out")){
            //System.out.println("Stopped timeline");
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

    public void addMondayCleanupEntry(ActionEvent event) {
        String day = DayOfWeek.from(LocalDate.now()).name();
        if(!day.equals("MONDAY")){
            System.out.println("Today is not Monday");
            //return
        }
        if(mondayCleaners.size() > 80){
            System.out.println("Too many entries present");
            //return
        }
        MondayCleaner entry = new MondayCleaner("Enter name","Enter time in","Enter time out");
        mondayCleaners.add(entry);
    }

    public void deleteMondayCleanupEntry(ActionEvent event) {
        if(mondayCleaners.size() == 0) return;
        mondayCleaners.remove(mondayCleaners.size()-1);
    }

    public void emailMondayCleanupRoster(){
        StringBuilder builder = new StringBuilder();
        mondayCleaners.forEach(mondayCleaner -> {
            builder
                    .append(mondayCleaner.getNameProperty().getValue() + "   ")
                    .append("Time In: ")
                    .append(mondayCleaner.getTimeInProperty().getValue() + "   ")
                    .append("Time Out: ")
                    .append(mondayCleaner.getTimeOutProperty().getValue() + "\n\n");
        });
        System.out.println(builder.toString());
    }

    public void handleCreateUser(){

    }

    public void handleDeleteUser(){

    }

    public void initMondayCleanupEntries(){
        mondayCleaners = FXCollections.observableArrayList();
        mondayCleaners.add(new MondayCleaner("Enter name","Enter time in","Enter time out"));
        nameColumn.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<MondayCleaner, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<MondayCleaner,String> cellDataFeatures) {
                        return cellDataFeatures.getValue().getValue().getNameProperty();
                    }
                }
        );

        nameColumn.setCellFactory(new Callback<TreeTableColumn, TreeTableCell>() {
            @Override
            public TreeTableCell call(TreeTableColumn treeTableColumn) {
                return new GenericEditableTreeTableCell<MondayCleaner, String>(
                        new TextFieldEditorBuilder()
                );
            }
        });

        timeInColumn.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<MondayCleaner, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<MondayCleaner,String> cellDataFeatures) {
                        return cellDataFeatures.getValue().getValue().getTimeInProperty();
                    }
                }
        );

        timeInColumn.setCellFactory(new Callback<TreeTableColumn, TreeTableCell>() {
            @Override
            public TreeTableCell call(TreeTableColumn treeTableColumn) {
                return new GenericEditableTreeTableCell<MondayCleaner, String>(
                        new TextFieldEditorBuilder()
                );
            }
        });
        timeOutColumn.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<MondayCleaner, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<MondayCleaner,String> cellDataFeatures) {
                        return cellDataFeatures.getValue().getValue().getTimeOutProperty();
                    }
                }
        );

        timeOutColumn.setCellFactory(new Callback<TreeTableColumn, TreeTableCell>() {
            @Override
            public TreeTableCell call(TreeTableColumn treeTableColumn) {
                return new GenericEditableTreeTableCell<MondayCleaner, String>(
                        new TextFieldEditorBuilder()
                );
            }
        });

        TreeItem<MondayCleaner> root = new RecursiveTreeItem<>(mondayCleaners, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
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
        initMondayCleanupEntries();
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
            UserPane m_userPane = (UserPane) userPane;
            m_userPane.initLabels(
                    m_userPane.getName(),
                    m_userPane.getSignInStatus(),
                    m_userPane.getTimeIn(),
                    m_userPane.getTotalTime()
            );
            if(!m_userPane.getSignInStatus()){
                (m_userPane).getTimeline().play();
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
        timeline.playFromStart();
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
