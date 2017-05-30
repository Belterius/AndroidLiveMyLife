package ClassPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francois on 01/05/2017.
 */

public class Story {
    private String idStory;
    private String title;
    private String description;
    private String highlight;
    private boolean isPublished;
    private MyUser author;
    private List<Step> steps;

    public Story(String idStory, String title, String description, String highlight, boolean isPublished) {
        this.idStory = idStory;
        this.title = title;
        this.description = description;
        this.highlight = highlight;
        this.isPublished = isPublished;
        this.steps = new ArrayList<Step>();
    }

    public Story(String idStory, String title, String description, String highlight, boolean isPublished, List<Step> steps ) {
        this.idStory = idStory;
        this.title = title;
        this.description = description;
        this.highlight = highlight;
        this.isPublished = isPublished;
        this.steps = steps;
    }

    public Story() {
        this.steps = new ArrayList<Step>();
    }

    public Story(String idStory){
        this.idStory = idStory;
        this.steps = new ArrayList<Step>();
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

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return title + "\n" + description;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(Step step){
        this.steps.add(step);
    }
}
