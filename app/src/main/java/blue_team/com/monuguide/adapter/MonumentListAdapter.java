package blue_team.com.monuguide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.adapter.viewHolder.MonumentListViewHolder;
import blue_team.com.monuguide.models.Monument;


public class MonumentListAdapter extends RecyclerView.Adapter<MonumentListViewHolder> {

    private List<Monument> monumentList;
    public IOnItemSelectedListener mOnItemSelectedListener;
    public Context context;

    public MonumentListAdapter(Context context) {
        monumentList = new ArrayList<>();
        this.context = context;
    }

    @Override
    public MonumentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_monuments_list, parent, false);
        MonumentListViewHolder monumentListViewHolder = new MonumentListViewHolder(view, context);


        monumentListViewHolder.setOnClickListener(new MonumentListViewHolder.IOnClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mOnItemSelectedListener != null){

                    //mOnItemSelectedListener.onItemSelected(monumentList.get(position).getId());
                }
            }
        });

        return monumentListViewHolder;
    }


    @Override
    public void onBindViewHolder(MonumentListViewHolder holder, int position) {
        holder.setDataSource(monumentList.get(position));
    }

    @Override
    public int getItemCount() {
        return monumentList.size();
    }

    public List<Monument> getMonumentList() {
        return monumentList;
    }

    public void setMonumentList(List<Monument> monumentList) {
        this.monumentList = monumentList;
    }

    public interface IOnItemSelectedListener{
        public void onItemSelected(long id);
    }

    public void setOnItemSelectedListener(IOnItemSelectedListener onItemSelectedListener){
        mOnItemSelectedListener = onItemSelectedListener;
    }




}
