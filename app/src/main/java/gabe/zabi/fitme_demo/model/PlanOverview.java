package gabe.zabi.fitme_demo.model;

import java.io.Serializable;

/**
 * Created by Gabe on 2017-02-22.
 */

public class PlanOverview implements Serializable {
    private String backdrop;
    private String goal;
    private String target;
    private String title;
    private String author;
    private String difficulty;
    private int weeks;

    public PlanOverview() {
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getGoal() {
        String result;
        switch (goal){
            case "0":
                result = "Build Muscle";
                break;
            case "1":
                result = "Fat Loss";
                break;
            case "2":
                result = "Increase Strength";
                break;
            case "3":
                result = "For Women";
                break;
            case "4":
                result = "Body Weight";
                break;
            default:
                result = "Training";
                break;
        }

        return result;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getTarget() {
        String result;
        switch (target){
            case "0":
                result = "Male";
                break;
            case "1":
                result = "Female";
                break;
            default:
                result = "Male & Female";
                break;
        }
        return result;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDifficulty() {
        String result;
        switch (difficulty){
            case "0":
                result = "very easy";
                break;
            case "1":
                result = "easy";
                break;
            case "2":
                result = "intermediate";
                break;
            case "3":
                result = "advanced";
                break;
            case "4":
                result = "expert";
                break;
            default:
                result = "unknown";
                break;
        }
        return result;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }
}
