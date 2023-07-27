package sg.edu.np.mad.EfficenZ;


import android.net.Uri;

public class User  {

    String first_name;

    String last_name;

    String email;

    String userId;

    //Uri imageUri;



    public User() { }

    public User(String first_name, String last_name, String email, String userId) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.userId = userId;
        // this.imageUri = imageUri;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

/*    public Uri getImageUri() { return imageUri; }

    public void setImageUri(Uri imageUri) { this.imageUri = imageUri; }*/
}
