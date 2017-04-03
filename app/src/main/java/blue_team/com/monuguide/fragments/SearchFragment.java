package blue_team.com.monuguide.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.MainActivity;
import blue_team.com.monuguide.adapter.MonumentListAdapter;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class SearchFragment extends Fragment {

    public static final String MAP_FRAGMENT = "MapFragment";
    public static final String SEARCH_FRAGMENT = "SearchFragment";

    public MonumentListAdapter mAdapter;
    private RecyclerView recyclerView;
    public List<Monument> monumentList;
    private FireHelper fh;
    private FragmentManager fragmentManager;
    private FrameLayout frameLayout;
    private Animation animation_close;
    onSearchViewChangeListener searchViewChangeListener;
    private TextView mNoResult;

    FireHelper.IOnFavMonSuccessListener iOnFavMonSuccessListener = new FireHelper.IOnFavMonSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            monumentList.clear();
            monumentList.addAll(mMap.values());
            mAdapter.setMonumentList(monumentList);
            mAdapter.notifyDataSetChanged();
            if(mAdapter.getItemCount() == 0){
                mNoResult.setText(getActivity().getString(R.string.no_monuments));
            }
            else {
                mNoResult.setText("");
            }

        }
    };

    FireHelper.IOnSearchSuccessListener iOnSearchSuccessListener = new FireHelper.IOnSearchSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            monumentList.clear();
            monumentList.addAll(mMap.values());
            mAdapter.setMonumentList(monumentList);
            mAdapter.notifyDataSetChanged();
            if(mAdapter.getItemCount() == 0){
                mNoResult.setText(getActivity().getString(R.string.no_result));
            }
            else {
                mNoResult.setText("");
            }

        }
    } ;

    public interface onSearchViewChangeListener{
        void ViewChanged();
    }

    public void setOnSearchViewChangeListener(onSearchViewChangeListener searchViewChangeListener){
        this.searchViewChangeListener = searchViewChangeListener;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        monumentList = new ArrayList<>();
        fh = new FireHelper();
        if(getArguments() != null){
            if(getArguments().getString(MainActivity.ARGUMENT_FOR_FAVORITE)!=null){
                fh.setOnFavMonSuccessListener(iOnFavMonSuccessListener);
                fh.getFavMonList(fh.getCurrentUid());
            }
            else
                fh.setOnSearchSuccessListener(iOnSearchSuccessListener);
        }else
        fh.setOnSearchSuccessListener(iOnSearchSuccessListener);
        animation_close = AnimationUtils.loadAnimation(getActivity(), R.anim.close_up);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.search_container);


        mNoResult = (TextView)view.findViewById(R.id.no_result_text);
        mAdapter = new MonumentListAdapter(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_id);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemSelectedListener(new MonumentListAdapter.IOnItemSelectedListener() {
            @Override
            public void onItemSelected(Monument monument) {

                System.out.println("Item click");
                iOnFavMonSuccessListener = null;
                fragmentManager = getActivity().getFragmentManager();
                ((MapStatueFragment) fragmentManager.findFragmentByTag(MAP_FRAGMENT)).setMonumentFromSearch(monument);

                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                if(fragmentManager.findFragmentByTag(SEARCH_FRAGMENT)!= null) {
                    fragmentTransaction1.remove(fragmentManager.findFragmentByTag(SEARCH_FRAGMENT));
                    searchViewChangeListener.ViewChanged();
                }
                else {
                    fragmentTransaction1.remove(fragmentManager.findFragmentByTag(MainActivity.FAVORITE_FRAGMENT));
                }
                fragmentTransaction1.commit();
                frameLayout.setVisibility(View.INVISIBLE);
                frameLayout.startAnimation(animation_close);
            }
        });
    }

    public FireHelper getFh() {
        return fh;
    }

    public void setText(){
        mNoResult.setText("");
    }

}
