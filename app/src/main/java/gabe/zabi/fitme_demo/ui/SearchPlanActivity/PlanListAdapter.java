package gabe.zabi.fitme_demo.ui.searchPlanActivity;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.Plan;

/**
 * Created by Gabe on 2017-02-22.
 */

public class PlanListAdapter extends FirebaseListAdapter<Plan> {

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public PlanListAdapter(Activity activity, Class<Plan> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    /**
     * Protected method that populates the view attached to the adapter (list_view_active_lists)
     * with items inflated from single_active_list.xml
     * populateView also handles data changes and updates the listView accordingly
     */


    @Override
    protected void populateView(View v, Plan model) {
        /**
         * Grab the needed Textivews and strings
         */
        TextView listTitle = (TextView) v.findViewById(R.id.plan_item_title);
        TextView listGoal = (TextView) v.findViewById(R.id.plan_item_goal);
        TextView listDifficulty = (TextView) v.findViewById(R.id.plan_item_difficulty);
        TextView listAuthor = (TextView) v.findViewById(R.id.plan_item_author);
        TextView listTarget = (TextView) v.findViewById(R.id.plan_item_target);

        listTitle.setText(model.getOverview().getTitle());
        listGoal.setText(model.getOverview().getGoal());
        listDifficulty.setText(model.getOverview().getDifficulty());
        listAuthor.setText(model.getOverview().getAuthor());
        listTarget.setText(model.getOverview().getTarget());
    }
}
