package gabe.zabi.fitme_demo.ui.searchPlanActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.ui.BaseActivity;

/**
 * Created by Gabe on 2017-02-14.
 */

public class SearchPlanActivity extends BaseActivity implements PlanListFragment.PlanCommunicator{

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_plan);

        if (savedInstanceState == null){
            // create fragment and add it to the activity using a fragment transaction.
            PlanListFragment planListFragment = new PlanListFragment();
            getFragmentManager().beginTransaction().add(R.id.search_plan_container, planListFragment).commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void sendPlanUid(android.app.Fragment fragment, String uid) {
        Bundle args = new Bundle();
        args.putString("KEY_PLAN_UID", uid);
        fragment.setArguments(args);
    }
}
