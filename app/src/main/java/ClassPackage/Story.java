package ClassPackage;

/**
 * Created by Francois on 01/05/2017.
 */

public class Story {
    private String idStory;
    private String title;
    private String description;
    private String highlight;
    private boolean isPublished;
    private Personne author;

    public Story(String idStory, String title, String description, String highlight, boolean isPublished) {
        this.idStory = idStory;
        this.title = title;
        this.description = description;
        this.highlight = highlight;
        this.isPublished = isPublished;
    }

    public Story() {
    }

    public String getIdStory() {
        return idStory;
    }

    public void setIdStory(String idStory) {
        this.idStory = idStory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public Personne getAuthor() {
        return author;
    }

    public void setAuthor(Personne author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Story{" +
                "idStory='" + idStory + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", highlight='" + highlight + '\'' +
                ", isPublished=" + isPublished +
                '}';
    }
}
