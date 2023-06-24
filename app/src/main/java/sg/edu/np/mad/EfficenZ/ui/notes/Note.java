package sg.edu.np.mad.EfficenZ.ui.notes;


// NOTE TAKING
public class Note {

    // attributes
    String title;
    String content;
    String id;
    String folderid;

    // constructor
    public Note() {}

    // getters and setters
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolderId() {return folderid;}

    public void setFolderId(String folderid) {this.folderid = folderid;}
}

