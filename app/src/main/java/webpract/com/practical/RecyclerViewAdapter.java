package webpract.com.practical;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.Collections;
import java.util.List;

import webpract.com.practical.database.DBModel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private List<DBModel> DBModelList;

    public RecyclerViewAdapter(List<DBModel> DBModelList) {

        this.DBModelList = DBModelList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        DBModel DBModel = DBModelList.get(position);
        holder.nameTextView.setText(DBModel.getName());
        holder.brandTextView.setText(DBModel.getDescription());
        holder.dateTextView.setText(String.valueOf(DBModel.getDate()));
        holder.itemView.setTag(DBModel);
    }

    @Override
    public int getItemCount() {
        return DBModelList.size();
    }

    public void addItems(List<DBModel> DBModelList) {
        Collections.reverse(DBModelList);
        this.DBModelList = DBModelList;
        notifyDataSetChanged();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView brandTextView;
        private TextView dateTextView;

        RecyclerViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            brandTextView = view.findViewById(R.id.discTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
        }
    }
}