package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;

public class MondayCleanerDataAccess {
    private static Connection connection;

    public static Connection getConnection(){
        if(connection == null) connectToDb();
        return connection;
    }

    private static void connectToDb() {
        try {
            //String path = "jdbc:sqlite:C:\\sqlite\\java\\connect\\mondaycleaners.db";
            String path = "jdbc:sqlite:src/mondaycleaners.db";
            connection = DriverManager.getConnection(path);
            System.out.println("Connected to db");
        } catch(SQLException e){
            e.printStackTrace();
        } /*finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/
    }

    public static void createNewTable(){
        if(connection == null){
            System.out.println("Did not create table");
            return;
        }
        String sql = "CREATE TABLE IF NOT EXISTS MondayCleanupRoster(" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "timeIn TEXT NOT NULL, " +
                "timeOut TEXT);";
        try{
            Statement statement = connection.createStatement();
            statement.execute(sql);
            System.out.println("Created table");
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    public static ObservableList<MondayCleaner> getAll(){
        if(connection == null){
            System.out.println("Did not get all");
            return FXCollections.observableArrayList(); //Literally nothing
        }
        String sql = "SELECT name, timeIn, timeOut FROM MondayCleanupRoster";
        ObservableList<MondayCleaner> mondayCleaners = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                //System.out.println(
                //        resultSet.getString("name")
                //);
                mondayCleaners.add(new MondayCleaner(
                        resultSet.getString("name"),
                        resultSet.getString("timeIn"),
                        resultSet.getString("timeOut")
                ));
            }
            return mondayCleaners;

        } catch(SQLException e){
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }

    public static void insert(MondayCleaner mondayCleaner){
        if(connection == null){
            System.out.println("Did not insert");
            return;
        }
       String sql = "INSERT INTO MondayCleanupRoster(name,timeIn,timeOut) VALUES(?,?,?)";
       try {
           PreparedStatement prepStatement = connection.prepareStatement(sql);
           prepStatement.setString(1, mondayCleaner.getNameProperty().getValue());
           prepStatement.setString(2, mondayCleaner.getTimeInProperty().getValue());
           prepStatement.setString(3, mondayCleaner.getTimeOutProperty().getValue());
           prepStatement.executeUpdate();
           //System.out.println("Inserted entry into db");
       } catch(SQLException e){
           e.printStackTrace();
       }
    }

    public static void deleteAll(){
        if(connection == null){
            System.out.println("Did not delete all");
            return;
        }

        String sql = "DELETE FROM MondayCleanupRoster";
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Deleted all");
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void deleteByName(String name){
        if(connection == null) return;
        String sql = "DELETE FROM MondayCleanupRoster WHERE name = ?";
        try{
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            prepStatement.setString(1, name);
            prepStatement.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
