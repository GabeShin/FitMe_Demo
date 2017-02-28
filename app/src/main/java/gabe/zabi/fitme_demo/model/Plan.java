package gabe.zabi.fitme_demo.model;

import java.util.ArrayList;

/**
 * Created by Gabe on 2017-02-22.
 */

public class Plan {
    private PlanOverview overview;
    private ArrayList<Workouts> workouts;

    public Plan() {
    }

    public PlanOverview getOverview() {
        return overview;
    }

    public void setOverview(PlanOverview overview) {
        this.overview = overview;
    }

    public ArrayList<Workouts> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(ArrayList<Workouts> workouts) {
        this.workouts = workouts;
    }

}
