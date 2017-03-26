package blue_team.com.monuguide.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.adapter.MonumentListAdapter;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class SearchFragment extends Fragment {

    private MonumentListAdapter mAdapter;
    private RecyclerView recyclerView;
    private List<Monument> monumentList;
    private FireHelper fh;
    ImageView searchBtn;

    FireHelper.IOnSearchSuccessListener iOnSearchSuccessListener = new FireHelper.IOnSearchSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            monumentList.clear();
            monumentList.addAll(mMap.values());
            mAdapter.setMonumentList(monumentList);
            mAdapter.notifyDataSetChanged();

        }
    } ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        monumentList = new ArrayList<>();
        fh = new FireHelper();
        fh.setOnSearchSuccessListener(iOnSearchSuccessListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        searchBtn = (ImageView) getActivity().findViewById(R.id.cancel_search);
        mAdapter = new MonumentListAdapter(getActivity());
        //mAdapter.setMonumentList(monumentList); //search result
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_id);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
    }

    public FireHelper getFh() {
        return fh;
    }

}
