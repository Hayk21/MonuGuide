package blue_team.com.monuguide.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.adapter.MonumentListAdapter;
import blue_team.com.monuguide.models.Monument;

public class MonumentSearchActivity extends AppCompatActivity {

    private MonumentListAdapter mAdapter;
    private Toolbar toolbar;

    private EditText searchET;
    private Button searchBtn;
    private RecyclerView recyclerView;
    private List<Monument> monumentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monument_search);

        searchET = (EditText) findViewById(R.id.searchEditText);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        monumentList = new ArrayList<>();


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO  Seeeeeeeeeeeeeeeeeeeeed estexic vercru sa searchET.getText().toString(); u poxanci qo metodin
                //stacvac Liste pahi monumentList-i mej u es et Liste poxancum em adapterin
                //mnacac kazmakerpchakane qo aneluc heto kanem 
            }
        });

        init();
    }

    private void init(){
        mAdapter = new MonumentListAdapter(this);
        mAdapter.setMonumentList(monumentList); //search result
        recyclerView = (RecyclerView) findViewById(R.id.monumentsListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemSelectedListener(new MonumentListAdapter.IOnItemSelectedListener() {
            @Override
            public void onItemSelected(long id) {
                System.out.println("Item click");
                //openTaskItemFragment(id);
            }
        });
    }
}
