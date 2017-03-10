package gabe.zabi.fitme_demo.model;

/**
 * Created by Gabe on 2017-03-07.
 */

public class OneSet {
    private String exerciseName;
    private String weight;
    private String reps;

    public OneSet() {
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
}
