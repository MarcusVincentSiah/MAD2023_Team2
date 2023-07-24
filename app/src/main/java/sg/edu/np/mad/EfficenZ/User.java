package sg.edu.np.mad.EfficenZ;

public class User {

    private String first_name;

    private String last_name;

    public String email;

    public User() { }

    public User(String first_name, String last_name, String email) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
    }
}
