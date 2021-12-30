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

import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.*;

public class MainController implements Initializable {
    //Since no websockets exist currently, this app cannot detect and respond to rest operations done by the web app.

    private List <User> users = UserDataAccess.getInstance().getAll();
    private ObservableList<MondayCleaner> mondayCleaners;
    private Connection connection = MondayCleanerDataAccess.getConnection();

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
    @FXML
    private TextField tableViewSearchBar;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField emailPasswordField;

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
        //For add and del, still need to refresh db entries
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
        MondayCleanerDataAccess.insert(entry);
    }

    public void deleteMondayCleanupEntry(ActionEvent event) {
        if(mondayCleaners.size() == 0 || tableViewSearchBar.getText().isEmpty()) return;
        //JFXTreeTableView and ObservableList is op :0
        MondayCleanerDataAccess.deleteByName(tableViewSearchBar.getText());
        mondayCleaners.remove(getMondayCleaner(tableViewSearchBar.getText()));
    }

    public void emailMondayCleanupRoster(){
        //Note: Using google's mail server here requires the person calling this function
        //to have 2FA and Use Less Secure Apps in Gmail Account > Security disabled since
        //this is not a Google app.

        //Forming entries into the string
        StringBuilder builder = new StringBuilder();
        mondayCleaners.forEach(mondayCleaner -> {
            builder
                    .append(mondayCleaner.getNameProperty().getValue() + " | ")
                    .append("In: ")
                    .append(mondayCleaner.getTimeInProperty().getValue() + " | ")
                    .append("Out: ")
                    .append(mondayCleaner.getTimeOutProperty().getValue() + "\n\n");
        });
        System.out.println(builder.toString());

        //Configure emailing properties, send email, clear text fields
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port", String.valueOf(Constants.SMTP_PORT));
        properties.put("mail.smtp.ssl.trust", "*");
        Authenticator authenticator = new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailField.getText(), "c1489e5d1c1918"); //:I
            }
        };
        Session session = Session.getInstance(properties, authenticator);
        Message message = new MimeMessage(session);
        try {
            String formattedDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(LocalDateTime.now());
            message.setFrom(new InternetAddress(emailField.getText()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailField.getText()));
            message.setSubject("Monday Cleanup Roster");

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(builder.toString(), "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Sent email");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        emailField.setText("");
        emailPasswordField.setText("");
    }

    public void handleCreateUser() throws MalformedURLException {
        //Note: The update function only updates current pane values.
        //Send post req, do a get request for the new user,
        //Then add a new UserPane to usersContainer corresponding to added user.

        //UserDataAccess.getInstance().save("Dude", "Guy", "333333");
        //User newUser = UserDataAccess.getInstance().get();
        //users.add(newUser)
        //usersContainer.getChildren().add(new UserPane())

    }

    public void handleDeleteUser(){
        //Send post req, then delete the corresponding userPane from usersContainer
        //Also delete that User in users along with the pane.
        //users.remove(getUser());
        //usersContainer.getChildren().remove(getUserPane(""));
        //UserDataAccess.getInstance().delete();
    }

    public void initMondayCleanupEntries(){
        //Get all sqlite entries, which are mapped to a list.
        //Then initialize and set the type of cell data and make them editable by making the cell edit text fields
        //Then initialize and create the jfxtreetableview root node and map it to the list
        mondayCleaners = MondayCleanerDataAccess.getAll();

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
        if(!DayOfWeek.from(LocalDate.now()).name().equals("MONDAY")){
           // mondayCleaners.clear();
        }
        MondayCleanerDataAccess.createNewTable();

        //usersContainer.getChildren().stream().forEach(userPane -> { ((UserPane) userPane).initLabels(); });
    }

    public void refreshDB(){
        //Refreshes db when the app is closed to respond to cell edits in the
        // treetableview and prep the tableview when the app is opened.
        MondayCleanerDataAccess.deleteAll();
        mondayCleaners.forEach(MondayCleanerDataAccess::insert);
    }

    private void updateListView() throws MalformedURLException {
        //Updates the CURRENT user panes' values.
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

    private User getUser(String name){
        LinkedHashMap<String, User> userList = new LinkedHashMap<>();
        users.forEach(user -> {
            userList.put(user.getName(), user);
        });
        return userList.get(name);
    }

    private MondayCleaner getMondayCleaner(String name){
        LinkedHashMap<String, MondayCleaner> m_mondayCleaners = new LinkedHashMap<>();
        mondayCleaners.forEach(mondayCleaner -> {
            m_mondayCleaners.put(mondayCleaner.getNameProperty().getValue(), mondayCleaner);
        });
        return m_mondayCleaners.get(name);
    }
}
