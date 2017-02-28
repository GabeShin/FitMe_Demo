package gabe.zabi.fitme_demo.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Gabe on 2017-02-22.
 */

public class Workouts implements Serializable {
    private ArrayList<Exercise> exercises;

    public Workouts() {
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }
}
