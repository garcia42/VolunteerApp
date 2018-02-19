package com.example.jegarcia.VolunteerMaps.ui.instagram;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.jegarcia.VolunteerMaps.R;
import com.squareup.picasso.Picasso;


public class InstagramPhotoAdapter extends RecyclerView.Adapter<InstagramPhotoAdapter.ImageViewHolder> {

    private final Context mContext;
    private InstagramData[] data;

    public InstagramPhotoAdapter(Context context, InstagramData[] data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View viewHolderLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.instagram_photo, viewGroup, false);
        return new ImageViewHolder(viewHolderLayout);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder imageViewHolder, int i) {
        InstagramData instagramData = data[i];
        String url = instagramData.getImages().getStandard_resolution().getUrl();
        Picasso.with(mContext).load(url).into(imageViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.instagramImage);
        }
    }
}
