package gabe.zabi.fitme_demo.model;

/**
 * Created by Gabe on 2017-02-22.
 */

public class Exercise {
    private String exerciseName;
    private String numberOfSets;
    private String recommendedReps;
    private String weight;

    public Exercise() {
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getNumberOfSets() {
        return numberOfSets;
    }

    public void setNumberOfSets(String numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public String getRecommendedReps() {
        return recommendedReps;
    }

    public void setRecommendedReps(String recommendedReps) {
        this.recommendedReps = recommendedReps;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
