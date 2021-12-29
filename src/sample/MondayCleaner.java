package sample;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

public class MondayCleaner extends RecursiveTreeObject<MondayCleaner> {
    private SimpleStringProperty name, timeIn, timeOut;

    public MondayCleaner(String name, String timeIn, String timeOut){
        this.name = new SimpleStringProperty(name);
        this.timeIn = new SimpleStringProperty(timeIn);
        this.timeOut = new SimpleStringProperty(timeOut);
    }

    public SimpleStringProperty getNameProperty(){return name;}
    public SimpleStringProperty getTimeInProperty(){return timeIn;}
    public SimpleStringProperty getTimeOutProperty(){return timeOut;}


}

