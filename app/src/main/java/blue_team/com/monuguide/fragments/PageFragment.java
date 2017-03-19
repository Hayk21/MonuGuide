package blue_team.com.monuguide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.models.Note;

/**
 * Created by Hayk on 19.03.2017.
 */

public class PageFragment extends Fragment {

    public static final String PAGE_NUMBER = "PageNumber";
    public static final String PAGE_URL = "PageURL";
    int mPageNumber;
    String URL;
    ImageView mCurrentImage;

    public static PageFragment newInstance(int page,String url){
        PageFragment pageFragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER,page);
        args.putString(PAGE_URL,url);
        pageFragment.setArguments(args);
        return pageFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = this.getArguments().getInt(PAGE_NUMBER);
        URL = this.getArguments().getString(PAGE_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mCurrentImage = (ImageView)view.findViewById(R.id.image_page);
        Picasso.with(getActivity()).load(URL).into(mCurrentImage);
    }
}
