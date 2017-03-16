package gabe.zabi.fitme_demo.model;

import java.util.ArrayList;

/**
 * Created by Gabe on 2017-02-28.
 */

public class UserActivity {
    private String current_plan_uid;
    private int current_workout_day;
    private int current_workout_week;
    private int workout_length_in_weeks;

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

    public int getCurrent_workout_week() {
        return current_workout_week;
    }

    public void setCurrent_workout_week(int current_workout_week) {
        this.current_workout_week = current_workout_week;
    }

    public int getWorkout_length_in_weeks() {
        return workout_length_in_weeks;
    }

    public void setWorkout_length_in_weeks(int workout_length_in_weeks) {
        this.workout_length_in_weeks = workout_length_in_weeks;
    }
}
