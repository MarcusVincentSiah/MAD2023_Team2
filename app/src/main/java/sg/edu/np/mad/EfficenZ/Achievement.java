package sg.edu.np.mad.EfficenZ;

public class Achievement {

    String name;
    int progress;
    int completionTarget;
    boolean isCompleted;

    public Achievement(String name, int progress, boolean isCompleted, int completionTarget) {
        this.name = name;
        this.progress = progress;
        this.isCompleted = isCompleted;
        this.completionTarget = completionTarget;
    }

    public String getName() {
        return name;
    }

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
