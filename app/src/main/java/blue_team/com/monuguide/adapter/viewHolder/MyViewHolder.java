package blue_team.com.monuguide.adapter.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.models.Monument;

/**
 * Created by Hayk on 25.03.2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder{

    public ImageView cardImg;
    public TextView cardText;
    public MyViewHolder.IOnClickListener mOnClickListener;
    private Context context;

    public MyViewHolder(View itemView, Context context) {
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
       cardImg = (ImageView)itemView.findViewById(R.id.card_img);
        cardText = (TextView)itemView.findViewById(R.id.card_text);
    }

    public void setDataSource(Monument monument){
        Picasso.with(context).load(monument.getImage()).into(cardImg);
        cardText.setText(monument.getName());
    }

    public interface IOnClickListener{
        void onItemClick(int position);
    }

    public void setOnClickListener(MyViewHolder.IOnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }
}
