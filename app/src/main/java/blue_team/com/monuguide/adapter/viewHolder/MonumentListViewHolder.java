package blue_team.com.monuguide.adapter.viewHolder;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.models.Monument;

public class MonumentListViewHolder extends RecyclerView.ViewHolder{

    private ImageView mMonumnetImageVH;
    private TextView mMonumentNameVH;
    private ProgressBar mProgressBar;
    //private TextView mMonumentDescVH;
    public IOnClickListener mOnClickListener;
    private Context context;

    public MonumentListViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnClickListener != null){
                    mOnClickListener.onItemClick(getAdapterPosition());
                }
            }
        });
        mMonumnetImageVH = (ImageView) itemView.findViewById(R.id.card_img);
        mMonumentNameVH = (TextView) itemView.findViewById(R.id.card_text);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_id);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#d0dae9"), PorterDuff.Mode.MULTIPLY);
        //mMonumentDescVH = (TextView) itemView.findViewById(R.id.monument_desc_rv);
    }

    public void setDataSource(Monument monument){
        Picasso.with(context).load(monument.getImage()).into(mMonumnetImageVH, new Callback() {
            @Override
            public void onSuccess() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });
        mMonumentNameVH.setText(monument.getName());
        //mMonumentDescVH.setText(monument.getDesc());
        System.out.println("in view holder = " + monument.getName());
    }

    public interface IOnClickListener{
        void onItemClick(int position);
    }

    public void setOnClickListener(IOnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }
}