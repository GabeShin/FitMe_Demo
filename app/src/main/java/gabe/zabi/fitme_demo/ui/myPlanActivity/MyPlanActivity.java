package gabe.zabi.fitme_demo.ui.myPlanActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.ui.BaseActivity;

/**
 * Created by Gabe on 2017-02-13.
 */

public class MyPlanActivity extends BaseActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plan);

        initializeScreen();
    }

    public void initializeScreen(){
        mToolbar = (Toolbar) findViewById(R.id.plan_activity_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    public void onAddPlanClicked(View view){
        Intent intent = new Intent(MyPlanActivity.this, SearchPlanActivity.class);
        startActivity(intent);
    }
}
