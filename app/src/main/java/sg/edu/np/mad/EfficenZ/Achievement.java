package sg.edu.np.mad.EfficenZ;

import java.time.LocalDateTime;

public class Achievement {

    String name;



    String description;
    int progress;
    int completionTarget;
    boolean isCompleted;

    public Achievement(String name, int progress, boolean isCompleted, int completionTarget, String description) {
        this.name = name;
        this.progress = progress;
        this.isCompleted = isCompleted;
        this.completionTarget = completionTarget;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {return description;}

    public int getProgress() {
        return progress;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public int CompletionTarget() {
        return completionTarget;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void incrementProgress(int amount) {
        progress += amount;
    }

    public void resetProgress() {
        progress = 0;
    }
}
