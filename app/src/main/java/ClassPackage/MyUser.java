package ClassPackage;

/**
 * Created by Gimlibéta on 30/04/2017.
 */

public class MyUser {

    private String idUser;
    private String email;
    private String pseudo;
    private String firstname;
    private String lastname;
    private String description;
    private String picture;
    private Story myCurrentStory;

    public MyUser(String idUser, String email, String pseudo, String firstname, String lastname, String description, String picture) {
        this.idUser = idUser;
        this.email = email;
        this.pseudo = pseudo;
        this.firstname = firstname;
        this.lastname = lastname;
        this.description = description;
        this.picture = picture;
    }

    public MyUser(String idUser, String email, String pseudo, String firstname, String lastname, String description, String picture, Story myCurrentStory) {
        this.idUser = idUser;
        this.email = email;
        this.pseudo = pseudo;
        this.firstname = firstname;
        this.lastname = lastname;
        this.description = description;
        this.picture = picture;
        this.myCurrentStory = myCurrentStory;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Story getMyCurrentStory(){return this.myCurrentStory;}

    public void setMyCurrentStory(Story myCurrentStory) {
        this.myCurrentStory = myCurrentStory;
    }
}
