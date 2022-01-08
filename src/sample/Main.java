package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main extends Application {
    //Since no websockets exist currently, this app cannot detect and respond to rest operations done by the web app

    @FXML
    private Parent layout;

    @FXML
    private JFXButton signInButton;

    @FXML
    private JFXPasswordField signInInput;

    @FXML
    private TextField searchBar;

    @FXML
    private VBox usersContainer;

    private MainController controller;


    private final ChangeListener<Number> onWidthResize = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number prevWidth, Number currentWidth) {
            signInButton.setMinWidth(currentWidth.doubleValue()/3.5d);
            signInInput.setMinWidth(currentWidth.doubleValue()/2.8d);
            searchBar.setMinWidth(currentWidth.doubleValue()/4.2d);
            usersContainer.setMinWidth(currentWidth.doubleValue()/2d - 96);
            usersContainer.getChildren().forEach(userPane ->
                    ((VBox)((UserPane)(userPane)).getChildren().get(0)).setMinWidth(usersContainer.getMinWidth())
            );
        }
    };;
    private final ChangeListener<Number> onHeightResize = new ChangeListener<>(){
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number prevHeight, Number currentHeight) {
            signInButton.setMinHeight(currentHeight.doubleValue()/16d);
            //signInInput.setMinHeight(n2.doubleValue()/12d);
        }
    };

    public Main() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        initStage(primaryStage);
        signInButton = (JFXButton) (primaryStage.getScene().lookup("#signInButton"));
        //signInButton.styleProperty().bind(Bindings.concat("-fx-font"));
        signInInput = (JFXPasswordField) (primaryStage.getScene().lookup("#signInInput"));
        searchBar = (TextField) (primaryStage.getScene().lookup("#searchBar"));
        usersContainer = (VBox) (primaryStage.getScene().lookup("#usersContainer"));
        primaryStage.getScene().widthProperty().addListener(onWidthResize);
        primaryStage.getScene().heightProperty().addListener(onHeightResize);
        System.out.println(primaryStage.getScene() + " Primary Stage scene");

        primaryStage.setWidth(primaryStage.getWidth()+.1); //lazily call the change listeners
    }

    private void initStage(Stage primaryStage) throws IOException{
        layout = loadFXML("Main");
        primaryStage.setTitle(Constants.APP_TITLE);
        primaryStage.setScene(new Scene(layout, Constants.INIT_WIDTH, Constants.INIT_HEIGHT));
        primaryStage.show();
    }
    private void initNodes(Node... nodes) throws Exception{

    }

    private Parent loadFXML(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(name+".fxml"));
        Parent fxml = loader.load();
        controller = (MainController) loader.getController();
        return fxml;
    }

    public static void main(String... args) throws Exception {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("App closed");
        controller.refreshDB();
        if(DayOfWeek.from(LocalDate.now()).name().equals(Constants.RESET_TABLEVIEW_DAY))
            MondayCleanerDataAccess.deleteAll();
        super.stop();
    }

}
