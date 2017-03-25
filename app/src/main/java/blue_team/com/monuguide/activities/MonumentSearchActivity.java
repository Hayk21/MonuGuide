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
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.adapter.MonumentListAdapter;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class MonumentSearchActivity extends AppCompatActivity {

    private MonumentListAdapter mAdapter;
    private Toolbar toolbar;

    private EditText searchET;
    private Button searchBtn;
    private RecyclerView recyclerView;
    private List<Monument> monumentList;
    private FireHelper fh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monument_search);

        FireHelper.IOnSearchSuccessListener iOnSearchSuccessListener = new FireHelper.IOnSearchSuccessListener() {
            @Override
            public void onSuccess(HashMap<String, Monument> mMap) {
                monumentList.clear();
                monumentList.addAll(mMap.values());
                mAdapter.setMonumentList(monumentList);
                mAdapter.notifyDataSetChanged();


                //Lus es sra xml fili mej ban em poxel click@ cher mtnum vabshe,,, hima hastat galis en tvyalner@
                //bayc xi cuyc chi talis chgidem
                //et mi hat du nayi
                //karox e mi ban kisat es grel
            }
        } ;

        searchET = (EditText) findViewById(R.id.searchEditText);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        monumentList = new ArrayList<>();
        fh = new FireHelper();

        fh.setOnSearchSuccessListener(iOnSearchSuccessListener);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fh.getSearchMonument(searchET.getText().toString());

            }
        });
        mAdapter = new MonumentListAdapter(this);
        //mAdapter.setMonumentList(monumentList); //search result
        recyclerView = (RecyclerView) findViewById(R.id.monumentsListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        init();
    }

    private void init(){


        mAdapter.setOnItemSelectedListener(new MonumentListAdapter.IOnItemSelectedListener() {
            @Override
            public void onItemSelected(long id) {
                System.out.println("Item click");
                //openTaskItemFragment(id);
            }
        });
    }
}
