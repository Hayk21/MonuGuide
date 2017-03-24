package blue_team.com.monuguide.adapter.viewHolder;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.models.Monument;

public class MonumentListViewHolder extends RecyclerView.ViewHolder{

    private ImageView mMonumnetImageVH;
    private TextView mMonumentNameVH;
    private TextView mMonumentDescVH;
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
        mMonumnetImageVH = (ImageView) itemView.findViewById(R.id.monument_image_rv);
        mMonumentNameVH = (TextView) itemView.findViewById(R.id.monument_name_rv);
        mMonumentDescVH = (TextView) itemView.findViewById(R.id.monument_desc_rv);
    }

    public void setDataSource(Monument monument){
        Picasso.with(context).load(monument.getImage()).into(mMonumnetImageVH);
        mMonumentNameVH.setText(monument.getName());
        mMonumentDescVH.setText(monument.getDesc());
    }

    public interface IOnClickListener{
        public void onItemClick(int position);
    }

    public void setOnClickListener(IOnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }
}