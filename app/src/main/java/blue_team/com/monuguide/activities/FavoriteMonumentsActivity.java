package blue_team.com.monuguide.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.adapter.MonumentListAdapter;

public class FavoriteMonumentsActivity extends AppCompatActivity {

    private MonumentListAdapter mAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_monuments);
    }

    private void init(View view){
        mAdapter = new MonumentListAdapter(this);
        //mAdapter.setMonumentList(); //favorite monument list
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.monumentsListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
    }
}
