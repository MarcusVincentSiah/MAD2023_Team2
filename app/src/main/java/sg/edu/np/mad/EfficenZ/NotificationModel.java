package sg.edu.np.mad.EfficenZ;


import com.google.firebase.Timestamp;


public class NotificationModel {
    String title;
    String content;
    Timestamp timestamp;

    public NotificationModel(){}
    public NotificationModel(String title, String content, Timestamp timestamp) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
