package gabe.zabi.fitme_demo.ui.searchPlanActivity;

import android.os.Bundle;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.ui.BaseActivity;

/**
 * Created by Gabe on 2017-02-14.
 */

public class SearchPlanActivity extends BaseActivity implements PlanListFragment.PlanCommunicator{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_plan);

        if (savedInstanceState == null){
            // create fragment and add it to the activity using a fragment transaction.
            PlanListFragment planListFragment = new PlanListFragment();
            getFragmentManager().beginTransaction().add(R.id.search_plan_container, planListFragment).commit();
        }
    }

    @Override
    public void sendPlanUid(android.app.Fragment fragment, String uid) {
        Bundle args = new Bundle();
        args.putString("KEY_PLAN_UID", uid);
        fragment.setArguments(args);
    }
}