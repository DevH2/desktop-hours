package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserDataAccess {
    //Google GSON
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.setPrettyPrinting().create();

    //HTTP
    private final URL getUsersURL = new URL(Constants.REST_URL+"/getusers");

    private String password ="bob"; //fallback val
    private URL getUserURL = new URL(Constants.REST_URL+"/getuserdata?password="+password);
    private final URL signInURL = new URL(Constants.REST_URL+"/signin");
    private final URL signOutURL = new URL(Constants.REST_URL+"/signout");
    private final URL addUserURL = new URL(Constants.REST_URL+"/adduser");
    private final URL deleteUserURL = new URL(Constants.REST_URL+"/deleteuser");

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    private final HttpRequest getUsersRequest = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(getUsersURL.toString()))
            .build();
    //Initializing these for reference
    private HttpRequest getUserDataRequest = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(getUserURL.toString()))
            .build();
    private HttpRequest signInRequest = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString("Dummy body"))
            .uri(URI.create(signInURL.toString()))
            .header("Content-Type", "application/json")
            .build();
    private HttpRequest signOutRequest = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString("Dummy body"))
            .uri(URI.create(signOutURL.toString()))
            .header("Content-Type", "application/json")
            .build();

    private HttpResponse getUsersResponse;
    private HttpResponse getUserDataResponse;
    private HttpResponse signInResponse;
    private HttpResponse signOutResponse;

    private List<User> users = new ArrayList<>();
    private static UserDataAccess userDataAccess;


    public UserDataAccess() throws MalformedURLException {

    }
    public static UserDataAccess getInstance() throws MalformedURLException {
        if(userDataAccess != null) return userDataAccess;
        return new UserDataAccess();
    }

    //Creates user
    public void save(String name, String password){
        HashMap<String, String> requestBody = new HashMap<>();
        //requestBody.put()
        System.out.println("Created user");
    }

    public List<User> getAll(){
        try {
            getUsersResponse = httpClient.send(getUsersRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error, may not have internet connection");
        }
        assert getUsersResponse != null;
        String json = getUsersResponse.body().toString();
        //System.out.println(getUsersResponse.getClass().toString());
        users = gson.fromJson(json, new TypeToken<List<User>>(){}.getType());
        return users;
    }
    /*public List<User> getAllAsync(){
        getUsersResponse = httpClient.sendAsync(
                getUsersRequest,
                HttpResponse.BodyHandlers.ofString()
        ).thenApply();
    }*/
    public UserData get(String password) {
        //Reinitialize fields
        this.password = password;
        try {
            getUserURL = new URL(Constants.REST_URL + "/getuserdata?password=" + this.password);
        } catch (MalformedURLException e){
            System.out.println("getUserURL error");
            e.printStackTrace();
        }
        getUserDataRequest = HttpRequest.newBuilder().GET().uri(URI.create(getUserURL.toString())).build();

        try {
            getUserDataResponse = httpClient.send(getUserDataRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.out.println("Could not get user's data");
            e.printStackTrace();
        }
        assert getUserDataResponse != null;
        String json = getUserDataResponse.body().toString();
        System.out.println(json);
        UserData userData = gson.fromJson(json, UserData.class);
        return userData;
    }

    public User getAsync(String password){
        return new User(0, "Dummy user", 0, 0,0,0);
    }

    public String signInOrOut(boolean signInStatus, String password, String name){
        this.password = password;
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("password", this.password);
        String requestBodyJSON = gson.toJson(requestBody);
        System.out.println(requestBodyJSON);
        if(signInStatus){
            signOutRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON))
                    .uri(URI.create(signOutURL.toString()))
                    .header("Content-Type", "application/json")
                    .build();
            try{
                signOutResponse = httpClient.send(signOutRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println(signOutResponse.body().toString());
                System.out.println("Signed out " + name);
                return "Successfully signed out";
            } catch(IOException | InterruptedException e) {
                System.out.println("Could not sign out " + name);
                e.printStackTrace();
                return "Error";
            }
        } else {
            signInRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON))
                    .uri(URI.create(signInURL.toString()))
                    .header("Content-Type", "application/json")
                    .build();
            try{
                signInResponse = httpClient.send(signInRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println(signInResponse.body().toString());
                System.out.println("Signed in " + name);
                return "Successfully signed in";
            } catch (InterruptedException | IOException e) {
                System.out.println("Could not sign in " + name);
                e.printStackTrace();
                return "Error";
            }
        }
    }

    public void delete(){

    }

    public User serializeUser(String json){
        return gson.fromJson(json, User.class);
    }

    public String deserializeUser(User... users){
        return gson.toJson(users);
    }

    public UserData serializeUserData(String json){
        return gson.fromJson(json, UserData.class);
    }

    public String deserializeUserData(UserData... userDataObjects){
        return gson.toJson(userDataObjects);
    }

}
