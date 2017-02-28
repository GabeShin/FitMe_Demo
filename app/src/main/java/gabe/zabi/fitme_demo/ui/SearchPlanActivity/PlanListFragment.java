package gabe.zabi.fitme_demo.ui.searchPlanActivity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.Firebase;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.Plan;
import gabe.zabi.fitme_demo.ui.detailPlanActivity.DetailPlanActivity;
import gabe.zabi.fitme_demo.utils.Constants;

/**
 * Created by Gabe on 2017-02-22.
 */

public class PlanListFragment extends Fragment {

    private PlanListAdapter mAdapter;
    private ListView mListView;

    private PlanCommunicator communicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() !=null){
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plan_list, container, false);
        initializeScreen(rootView);

        Firebase planListRef = new Firebase(Constants.FIREBASE_URL_PLAN_LISTS);

        /**
         * Create the adapter, giving it the activity, model class, layout for each row in
         * the list and finally, a reference to the Firebase location with the list data
         */
        mAdapter = new PlanListAdapter(getActivity(), Plan.class, R.layout.single_plan_item, planListRef);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Plan selectedPlan = mAdapter.getItem(position);
                if (selectedPlan != null) {
                    String uid = mAdapter.getRef(position).getKey();
                    Intent intent = new Intent(getActivity(), DetailPlanActivity.class);
                    intent.putExtra("KEY_PLAN_UID", uid);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    private void initializeScreen(View view){
        mListView = (ListView) view.findViewById(R.id.search_plan_list_view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    public interface PlanCommunicator{
        public void sendPlanUid(Fragment fragment, String uid);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communicator = (PlanCommunicator) getActivity();
        } catch (Exception e){
            throw new ClassCastException(context.toString() + " must implement onButtonPressed");
        }
    }
}
