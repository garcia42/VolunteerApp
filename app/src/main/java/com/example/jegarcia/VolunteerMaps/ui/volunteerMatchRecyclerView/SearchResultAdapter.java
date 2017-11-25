package com.example.jegarcia.VolunteerMaps.ui.volunteerMatchRecyclerView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.apache.axis.utils.StringUtils;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static android.view.View.GONE;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class SearchResultAdapter extends RealmRecyclerViewAdapter<Opportunities, RecyclerView.ViewHolder> {

    private final Context mContext;
    private final boolean mIsSaved;
    private RecyclerViewClickListener mListener;
    private TextView mEmptyListTextView;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private static final String TAG = SearchResultAdapter.class.getName();

    public void setEmptyView(TextView emptyListTextView) {
        mEmptyListTextView = emptyListTextView;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public TextView emptyListText;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            emptyListText = (TextView) itemView.findViewById(R.id.emptyListTextView);
        }
    }

    static class OpportunitiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView parentOrg;
        private TextView oppDay;
        private ImageView image;
        private SparkButton likeButton;
        RecyclerViewClickListener mListener;

        OpportunitiesViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            this.title = (TextView) itemView.findViewById(R.id.titleView);
            this.image = (ImageView) itemView.findViewById(R.id.imageView);
            this.parentOrg = (TextView) itemView.findViewById(R.id.parentOrg);
            this.oppDay = (TextView) itemView.findViewById(R.id.oppDay);
            this.likeButton = (SparkButton) itemView.findViewById(R.id.star_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }

    public SearchResultAdapter(RealmResults<Opportunities> myDataset, Context context, RecyclerViewClickListener listener, boolean isSaved) {
        super(myDataset, true);
        this.mContext = context;
        mListener = listener;
        this.mIsSaved = isSaved;
    }

    // Create new views (invoked by the layout manager) all views from viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View viewHolderLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.oppportunity_row, parent, false);
            //set view's size, margins, padding here, layout params

            return new OpportunitiesViewHolder(viewHolderLayout, mListener);

        }
        return null;
    }

    @Override public int getItemViewType(int position) {
        // loader can't be at position 0
        // loader can only be at the last position
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        // If no items are present, there's no need for loader
//        if (getData() == null || getData().size() == 0) {
//            return 0;
//        }
//
//        return super.getItemCount() + 1; //This is for the loader :D
        return super.getItemCount();
    }

    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mEmptyListTextView.setVisibility(GONE);

        if (holder instanceof OpportunitiesViewHolder) {
            OpportunitiesViewHolder oppViewHolder = (OpportunitiesViewHolder) holder;
            Opportunities opportunity = getItem(position);
            final int oppId = opportunity.getOppId();
            oppViewHolder.title.setText(opportunity.getTitle());
            if (!StringUtils.isEmpty(opportunity.getAvailability().getStartDate())) {
                try {
                    StringBuilder dateText = new StringBuilder();
                    java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(opportunity.getAvailability().getStartDate());
                    dateText.append(new SimpleDateFormat("EEEE, MMMM d yyyy").format(date));
                    //End date not empty and not same as start date
                    if (!StringUtils.isEmpty(opportunity.getAvailability().getStartDate()) &&
                            !opportunity.getAvailability().getStartDate().equals(opportunity.getAvailability().getEndDate())) {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(opportunity.getAvailability().getEndDate());
                        dateText.append(" - ");
                        dateText.append(new SimpleDateFormat("EEEE, MMMM d yyyy").format(date));
                    }
                    oppViewHolder.oppDay.setText(dateText.toString());
                } catch (ParseException e) {
                    oppViewHolder.oppDay.setText(opportunity.getAvailability().getStartDate());
                    e.printStackTrace();
                }
            } else {
                oppViewHolder.oppDay.setText(mContext.getString(R.string.opp_day));
            }
            oppViewHolder.parentOrg.setText(opportunity.getParentOrg().getName());

            if (!StringUtils.isEmpty(opportunity.getImageUrl())) {
                String decodedUrl = URLDecoder.decode(opportunity.getImageUrl());
                Picasso.with(mContext).load(decodedUrl).into(oppViewHolder.image);
            } else {
                Picasso.with(mContext).load(R.drawable.volunteer).into(oppViewHolder.image);
            }

            oppViewHolder.likeButton.setEventListener(new SparkEventListener(){
                @Override
                public void onEvent(ImageView imageView, boolean b) {
                    Realm realm = Realm.getDefaultInstance();
                    if (b) {
                        realm.executeTransactionAsync(new Realm.Transaction() {

                            @Override
                            public void execute(Realm realm) {
                                Opportunities modifyOpp = realm.where(Opportunities.class).equalTo("id", oppId).findFirst();
                                if (modifyOpp != null) {
                                    modifyOpp.setLiked(true);
                                }
                            }
                        });
                    } else {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Opportunities modifyOpp = realm.where(Opportunities.class).equalTo("id", oppId).findFirst();
                                if (modifyOpp != null) {
                                    modifyOpp.setLiked(false);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onEventAnimationEnd(ImageView imageView, boolean b) {

                }

                @Override
                public void onEventAnimationStart(ImageView imageView, boolean b) {

                }
            });
            oppViewHolder.likeButton.setChecked(opportunity.isLiked());
        } else if (holder instanceof LoadingViewHolder) {
            String text = this.mIsSaved ? "No Saved Opportunities Yet" : "Looking for Volunteering Opportunities";
            ((LoadingViewHolder) holder).emptyListText.setText(text);
        }
    }

    @Override
    public long getItemId(int index) {
        return getItem(index) == null ? super.getItemId(index) : getItem(index).getOppId();
    }

    @Nullable
    @Override
    public Opportunities getItem(int index) {
//        if (index == getItemCount() - 1) { //If it is the last element then it should be a progress bar
//            return null;
//        }
        return super.getItem(index);
    }
}
