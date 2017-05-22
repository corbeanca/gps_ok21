package a_barbu.gps_agenda;

/**
 * Created by Alex on 22-May-17.
 */

public class User {
    public String userName;
    public String userEmail;
    String hour_start;
    String hour_stop;

    public User(){
    }

    public User(String userName, String userEmail){
        this.userEmail=userEmail;
        this.userName=userName;
    }

    public void setUserName(String n){
        this.userName=n;
    }

    public void setUserEmail(String e){
        this.userEmail=e;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public void setH(String h1, String h2){
        this.hour_start=h1;
        this.hour_stop=h2;
    }
}
