package gabe.zabi.fitme_demo.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.Exercise;
import gabe.zabi.fitme_demo.model.Plan;
import gabe.zabi.fitme_demo.model.Workouts;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

/**
 * Created by Gabe on 2017-03-16.
 */

public class TodayWidgetRemoteViewsService extends RemoteViewsService {

    public TodayWidgetRemoteViewsService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        return (new TodayWidgetRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}
