package com.example.jegarcia.volunteer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.OpportunitiesViewHolder> {

    private List<OppSearchResult.Opportunities> mDataset;

    public static class OpportunitiesViewHolder extends RecyclerView.ViewHolder {
        private TextView id;
        private TextView title;
        private TextView updated;
        private TextView status;

        public OpportunitiesViewHolder(View itemView) {
            super(itemView);
            this.id = (TextView) itemView.findViewById(R.id.idView);
            this.title = (TextView) itemView.findViewById(R.id.titleView);
            this.updated = (TextView) itemView.findViewById(R.id.updatedView);
            this.status = (TextView) itemView.findViewById(R.id.statusView);
        }
    }

    public SearchResultAdapter(List<OppSearchResult.Opportunities> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager) all views from viewholder
    @Override
    public OpportunitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolderLayout = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.oppportunity_row, parent, false);
        //set view's size, margins, padding here, layout params

        OpportunitiesViewHolder viewHolder = new OpportunitiesViewHolder(viewHolderLayout);
        return viewHolder;
    }

    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(OpportunitiesViewHolder holder, int position) {
        holder.id.setText(mDataset.get(position).getId());
        holder.title.setText(mDataset.get(position).getId());
        holder.updated.setText(mDataset.get(position).getId());
        holder.status.setText(mDataset.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItems(List<OppSearchResult.Opportunities> opportunities) {
        mDataset.addAll(opportunities);
        notifyDataSetChanged();
    }
}


