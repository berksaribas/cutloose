package com.cutloose.cutloose.ui.event;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cutloose.cutloose.CutLoose;
import com.cutloose.cutloose.R;
import com.cutloose.cutloose.databinding.EventActivityBinding;
import com.cutloose.cutloose.model.Clicks;
import com.cutloose.cutloose.model.Event;
import com.cutloose.cutloose.ui.chat.ChatActivity;
import com.cutloose.cutloose.ui.common.Action.Action;
import com.cutloose.cutloose.ui.common.Action.BasicAction;
import com.cutloose.cutloose.ui.common.BaseActivity;
import com.cutloose.cutloose.ui.profile.ProfileActivity;
import com.cutloose.cutloose.utils.FirebaseActions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EventActivity extends BaseActivity {

    static private final String TAG = "EventActivity";
    EventRecyclerViewModel eventRecyclerViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.event_activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.event_title);

        eventRecyclerViewModel = ViewModelProviders.of(this).get(EventRecyclerViewModel.class);

        EventRecyclerViewAdapter eventRecyclerViewAdapter = new EventRecyclerViewAdapter(eventRecyclerViewModel, this);

        observeActions(eventRecyclerViewModel);

        observeDataLoad(eventRecyclerViewModel);

        RecyclerView recyclerView = findViewById(R.id.event_recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(eventRecyclerViewAdapter);

        ((EventActivityBinding) mViewDataBinding).setViewModel(eventRecyclerViewModel);

        eventRecyclerViewModel.fetchData();
    }

    private void observeActions(EventRecyclerViewModel eventRecyclerViewModel) {
        eventRecyclerViewModel.observeAction(this, new Observer<Action<Event, BasicAction>>() {
            @Override
            public void onChanged(@Nullable Action<Event, BasicAction> eventAction) {

                if (eventAction == null) return;

                switch (eventAction.getActionType()) {
                    case RECYCLER_ITEM_CLICK:
                        ((CutLoose) (getApplication())).getDatabase().clicksDao().insertClicks(new Clicks(eventAction.getModel().getEventId(), FirebaseAuth.getInstance().getUid()));
                        eventAction.getModel().setPopularity(eventAction.getModel().getPopularity() + 1);
                        Intent intent = new Intent(EventActivity.this, ChatActivity.class);
                        intent.putExtra("event", eventAction.getModel());
                        startActivity(intent);
                }
            }
        });
    }

    private void observeDataLoad(EventRecyclerViewModel eventRecyclerViewModel) {
        eventRecyclerViewModel.getLiveData().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Event> events) {
                eventRecyclerViewModel.loading.set(false);

                for (Event event : events) {
                    event.setPopularity(((CutLoose) (getApplication())).getDatabase().clicksDao().getCount(event.getEventId(), FirebaseAuth.getInstance().getUid()));
                    FirebaseActions.getInstance().getActiveness(event.getEventId()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                event.setActiveEventsCount(task.getResult().size());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.titlebar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                startProfileActivity();
                return true;
            case R.id.refresh:
                eventRecyclerViewModel.fetchData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
