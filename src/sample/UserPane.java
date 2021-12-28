package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class UserPane extends VBox {
    private Timeline timeline;
    
    @FXML
    private final VBox userContainer = FXMLLoader.load(getClass().getResource("UserCard.fxml"));;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label signInStatusLabel;

    @FXML
    private Label timeInLabel;

    //@FXML
    //private Label totalTimeLabel;

    private String name;
    private long timeIn, totalTime;
    private boolean signInStatus;
    private int displayedTimeIn;
    private SimpleStringProperty displayedTimeInProp = new SimpleStringProperty(""+displayedTimeIn);
    public UserPane(String name, boolean signInStatus, long timeIn, long totalTime) throws IOException, InterruptedException {
        displayedTimeIn = 0;
        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            displayedTimeIn++;
            displayedTimeInProp.setValue(displayedTimeIn+"");
            System.out.println("Timeline running " + displayedTimeInProp.getValue());
            System.out.println(timeInLabel.textProperty().toString());
        }));
        timeline.setCycleCount(Animation.INDEFINITE);

        //Initialize userContainer values
        this.name = name;
        this.signInStatus = signInStatus;
        this.timeIn = timeIn;
        this.totalTime = totalTime;
        //below is null
        this.getChildren().add(userContainer);
        //initLabels();
    }
    public void initLabels(String name, boolean signInStatus, long timeIn, long totalTime){
        //BRUH USING THE INDEX WORKS BUT NOT LOOKUP WHAT???!
        usernameLabel = (Label) (userContainer.getChildren().get(0));
        signInStatusLabel = (Label) (userContainer.getChildren().get(1));
        GridPane gridPane = (GridPane) (userContainer.getChildren().get(2));
        timeInLabel = (Label) (gridPane.getChildren().get(0));
        //totalTimeLabel = (Label) (gridPane.getChildren().get(1));
        usernameLabel.setText(name);
        signInStatusLabel.setText(signInStatus ? "SIGNED OUT":"SIGNED IN");
        timeInLabel.setText(displayedTimeIn + " " + totalTime);//too lazy to deal with adaptability for totalTimeLabel
        timeInLabel.textProperty().bind(displayedTimeInProp);
        //totalTimeLabel.setText(totalTime + "");
    }
    public String getName(){
        return name;
    }
    public boolean getSignInStatus(){
        return signInStatus;
    }
    public long getTimeIn(){
        return timeIn;
    }
    public long getTotalTime(){
        return totalTime;
    }
    public ReadOnlyObjectProperty<Scene> getSceneProperty(){
        return userContainer.sceneProperty();
    }
    public void update(){
        //Update and format time
        displayedTimeIn++;

        timeInLabel.setText(String.valueOf(displayedTimeIn));
    }
    public Timeline getTimeline(){
        return timeline;
    }



}

