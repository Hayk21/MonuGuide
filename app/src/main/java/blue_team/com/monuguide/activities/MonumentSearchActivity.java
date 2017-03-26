package blue_team.com.monuguide.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.adapter.MonumentListAdapter;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.fragments.DetailsFragment;
import blue_team.com.monuguide.models.Monument;

public class MonumentSearchActivity extends AppCompatActivity {

    public static final String ARGUMENT_WITH_MONUMENT = "CurrentMonument";
    public static final String DETAILS_FRAGMENT = "DeatilsFragment";
    public static final String HEADER_BACKSTACK = "HeaderBackStack";
    private MonumentListAdapter mAdapter;
    private Toolbar toolbar;

    private EditText searchET;
    private Button searchBtn;
    private RecyclerView recyclerView;
    private List<Monument> monumentList;
    private FireHelper fh;

    Bundle args;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


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
                System.out.println("onSuccess = " + monumentList.get(0).getName());
                mAdapter.notifyDataSetChanged();
            }
        } ;

        monumentList = new ArrayList<>();
        fh = new FireHelper();

        fh.setOnSearchSuccessListener(iOnSearchSuccessListener);
        init();
    }

    private void init(){
        monumentList = new ArrayList<>();
        mAdapter = new MonumentListAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.monumentsListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemSelectedListener(new MonumentListAdapter.IOnItemSelectedListener() {
            @Override
            public void onItemSelected(Monument monument) {
                System.out.println("Item click");
                openDetailsFragment(monument);
            }
        });
    }

    public void openDetailsFragment(Monument monument){
        args = new Bundle();
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment mDetailsFragment = new DetailsFragment();
        args.putParcelable(ARGUMENT_WITH_MONUMENT, monument);
        mDetailsFragment.setArguments(args);
        fragmentTransaction.add(R.id.start_activity_container, mDetailsFragment, DETAILS_FRAGMENT);
        fragmentTransaction.addToBackStack(HEADER_BACKSTACK);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fh.getSearchMonument(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
