package com.example.jegarcia.volunteer.volunteerMatchRecyclerView;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jegarcia.volunteer.R;
import com.example.jegarcia.volunteer.fragments.OpportunityFragment;
import com.example.jegarcia.volunteer.models.Opportunities;
import com.squareup.picasso.Picasso;

import org.apache.axis.utils.StringUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static com.example.jegarcia.volunteer.R.id.imageView;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.OpportunitiesViewHolder> {

    private final Context mContext;
    private List<Opportunities> mDataset;
    private int daysSince;

    public int getdaysSince() {
        return daysSince;
    }

    public static class OpportunitiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView id;
        private TextView title;
        private TextView updated;
        private TextView status;
        private ImageView image;

        public OpportunitiesClickListener mListener;

        public OpportunitiesViewHolder(View itemView, OpportunitiesClickListener listener) {
            super(itemView);
            mListener = listener;
            this.id = (TextView) itemView.findViewById(R.id.idView);
            this.title = (TextView) itemView.findViewById(R.id.titleView);
            this.updated = (TextView) itemView.findViewById(R.id.updatedView);
            this.status = (TextView) itemView.findViewById(R.id.statusView);
            this.image = (ImageView) itemView.findViewById(imageView);
        }

        @Override
        public void onClick(View v) {
            mListener.onClickHolder(v);
        }

        public interface OpportunitiesClickListener {
            void onClickHolder(View caller);
        }
    }

    public class OpportunitiesClickListener {
        public void onClickHolder(View v) {
        };
    }

    public SearchResultAdapter(List<Opportunities> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager) all views from viewholder
    @Override
    public OpportunitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolderLayout = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.oppportunity_row, parent, false);
        //set view's size, margins, padding here, layout params

        OpportunitiesViewHolder viewHolder = new OpportunitiesViewHolder(viewHolderLayout, new OpportunitiesViewHolder.OpportunitiesClickListener() {
            @Override
            public void onClickHolder(View v) {
                //You can change the fragment, something like this, not tested, please correct for your desired output:
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                OpportunityFragment organizationFragment = new OpportunityFragment();
                //Create a bundle to pass data, add data, set the bundle to your fragment and:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, organizationFragment).addToBackStack(null).commit();
            }
        });

        return viewHolder;
    }

    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(OpportunitiesViewHolder holder, int position) {
        holder.id.setText(String.valueOf(mDataset.get(position).getOppId()));
        holder.title.setText(mDataset.get(position).getTitle());
        holder.updated.setText(mDataset.get(position).getUpdated());
        holder.status.setText(mDataset.get(position).getStatus());

        if (!StringUtils.isEmpty(mDataset.get(position).getImageUrl())) {
            String decodedUrl = URLDecoder.decode(mDataset.get(position).getImageUrl());
            Picasso.with(mContext).load(decodedUrl).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItems(List<Opportunities> opportunities) {
        if (mDataset == null) {
            mDataset = new ArrayList<>();
        }
        mDataset.addAll(opportunities);
        for (Opportunities opportunity: opportunities) {
            opportunity.save();
        }
        for (Opportunities opportunity: opportunities) {
            opportunity.delete();
        }
        notifyDataSetChanged();
    }
}
