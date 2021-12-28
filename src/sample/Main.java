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
import java.time.LocalTime;

public class Main extends Application {

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
        return FXMLLoader.load(getClass().getResource(name+".fxml"));
    }

    public static void main(String... args) throws Exception {
        launch(args);
    }
}
