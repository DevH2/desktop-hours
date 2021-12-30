package sample;

public class AddUserData {
    private String firstName, lastName, password;

    public AddUserData(String firstName, String lastName, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public String getPassword(){return password;}

}
