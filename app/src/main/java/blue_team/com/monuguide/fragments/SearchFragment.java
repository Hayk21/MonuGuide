package blue_team.com.monuguide.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    public List<Monument> mMonumentList;
    private FireHelper mFireHelper;
    private FragmentManager mFragmentManager;
    private FrameLayout mFrameLayout;
    private Animation mAnimation_close;
    private TextView mNoResult;
    private Toolbar mToolbar;
    onSearchViewChangeListener searchViewChangeListener;

    FireHelper.IOnFavMonSuccessListener iOnFavMonSuccessListener = new FireHelper.IOnFavMonSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            mMonumentList.clear();
            mMonumentList.addAll(mMap.values());
            mAdapter.setMonumentList(mMonumentList);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() == 0) {
                mNoResult.setText(getActivity().getString(R.string.no_monuments));
            } else {
                mNoResult.setText("");
            }

        }
    };

    FireHelper.IOnSearchSuccessListener iOnSearchSuccessListener = new FireHelper.IOnSearchSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            mMonumentList.clear();
            mMonumentList.addAll(mMap.values());
            mAdapter.setMonumentList(mMonumentList);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() == 0) {
                mNoResult.setText(getActivity().getString(R.string.no_result));
            } else {
                mNoResult.setText("");
            }

        }
    };

    public interface onSearchViewChangeListener {
        void ViewChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMonumentList = new ArrayList<>();
        mFireHelper = new FireHelper();
        if (getArguments() != null) {
            if (getArguments().getString(MainActivity.ARGUMENT_FOR_FAVORITE) != null) {
                mFireHelper.setOnFavMonSuccessListener(iOnFavMonSuccessListener);
                mFireHelper.getFavMonList(mFireHelper.getCurrentUid());
            } else
                mFireHelper.setOnSearchSuccessListener(iOnSearchSuccessListener);
        } else
            mFireHelper.setOnSearchSuccessListener(iOnSearchSuccessListener);
        mAnimation_close = AnimationUtils.loadAnimation(getActivity(), R.anim.close_up);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mFrameLayout = (FrameLayout) getActivity().findViewById(R.id.search_container);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        mNoResult = (TextView) view.findViewById(R.id.no_result_text);
        mAdapter = new MonumentListAdapter(getActivity());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_id);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemSelectedListener(new MonumentListAdapter.IOnItemSelectedListener() {
            @Override
            public void onItemSelected(Monument monument) {

                System.out.println("Item click");
                mFragmentManager = getActivity().getFragmentManager();
                ((MapStatueFragment) mFragmentManager.findFragmentByTag(MAP_FRAGMENT)).setMonumentFromSearch(monument);

                FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
                if (mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT) != null) {
                    fragmentTransaction1.remove(mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT));
                    searchViewChangeListener.ViewChanged();
                } else {
                    fragmentTransaction1.remove(mFragmentManager.findFragmentByTag(MainActivity.FAVORITE_FRAGMENT));
                    mToolbar.setTitle(getActivity().getString(R.string.name_of_app));
                }
                fragmentTransaction1.commit();
                mFrameLayout.setVisibility(View.INVISIBLE);
                mFrameLayout.startAnimation(mAnimation_close);
            }
        });
    }

    public FireHelper getFh() {
        return mFireHelper;
    }

    public void setOnSearchViewChangeListener(onSearchViewChangeListener searchViewChangeListener) {
        this.searchViewChangeListener = searchViewChangeListener;
    }

    public void setText() {
        mNoResult.setText("");
    }

}
