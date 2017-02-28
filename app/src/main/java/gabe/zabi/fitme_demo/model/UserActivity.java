package gabe.zabi.fitme_demo.model;

import java.util.ArrayList;

/**
 * Created by Gabe on 2017-02-28.
 */

public class UserActivity {
    private String current_plan_uid;
    private int current_workout_day;

    public UserActivity() {
    }

    public String getCurrent_plan_uid() {
        return current_plan_uid;
    }

    public void setCurrent_plan_uid(String current_plan_uid) {
        this.current_plan_uid = current_plan_uid;
    }

    public int getCurrent_workout_day() {
        return current_workout_day;
    }

    public void setCurrent_workout_day(int current_workout_day) {
        this.current_workout_day = current_workout_day;
    }
}
