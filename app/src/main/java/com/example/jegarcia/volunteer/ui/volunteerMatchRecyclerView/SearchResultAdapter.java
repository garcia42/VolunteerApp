package com.example.jegarcia.volunteer.ui.volunteerMatchRecyclerView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jegarcia.volunteer.R;
import com.example.jegarcia.volunteer.models.volunteerMatchModels.Opportunities;
import com.squareup.picasso.Picasso;

import org.apache.axis.utils.StringUtils;

import java.net.URLDecoder;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class SearchResultAdapter extends RealmRecyclerViewAdapter<Opportunities, SearchResultAdapter.OpportunitiesViewHolder> {

    private final Context mContext;
    private RecyclerViewClickListener mListener;

    static class OpportunitiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView updated;
        private TextView status;
        private TextView parentOrg;
        private TextView oppDay;
        private ImageView image;
        RecyclerViewClickListener mListener;

        OpportunitiesViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            this.title = (TextView) itemView.findViewById(R.id.titleView);
            this.updated = (TextView) itemView.findViewById(R.id.updatedView);
            this.status = (TextView) itemView.findViewById(R.id.statusView);
            this.image = (ImageView) itemView.findViewById(R.id.imageView);
            this.parentOrg = (TextView) itemView.findViewById(R.id.parentOrg);
            this.oppDay = (TextView) itemView.findViewById(R.id.oppDay);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }

    public SearchResultAdapter(RealmResults<Opportunities> myDataset, Context context, RecyclerViewClickListener listener) {
        super(myDataset, true);
        this.mContext = context;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager) all views from viewholder
    @Override
    public OpportunitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolderLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.oppportunity_row, parent, false);
        //set view's size, margins, padding here, layout params

        OpportunitiesViewHolder viewHolder = new OpportunitiesViewHolder(viewHolderLayout, mListener);

        return viewHolder;
    }

    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(OpportunitiesViewHolder holder, int position) {
        Opportunities opportunity = getItem(position);
        holder.title.setText(opportunity.getTitle());
        holder.updated.setText(opportunity.getUpdated());
        holder.status.setText(opportunity.getStatus());
        if (!StringUtils.isEmpty(opportunity.getAvailability().getStartDate())) {
            holder.oppDay.setText(opportunity.getAvailability().getStartDate());
        } else {
            holder.oppDay.setText(mContext.getString(R.string.opp_day));
        }
        holder.parentOrg.setText(opportunity.getParentOrg().getName());

        if (!StringUtils.isEmpty(opportunity.getImageUrl())) {
            String decodedUrl = URLDecoder.decode(opportunity.getImageUrl());
            Picasso.with(mContext).load(decodedUrl).into(holder.image);
        }
    }

    @Nullable
    @Override
    public Opportunities getItem(int index) {
        return super.getItem(index);
    }
}
