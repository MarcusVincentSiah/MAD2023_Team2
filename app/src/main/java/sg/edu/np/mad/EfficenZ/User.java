package sg.edu.np.mad.EfficenZ;


public class User  {

    String first_name;

    String last_name;

    String email;

    String userId;



    public User() { }

    public User(String first_name, String last_name, String email, String userId) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.userId = userId;
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
}
