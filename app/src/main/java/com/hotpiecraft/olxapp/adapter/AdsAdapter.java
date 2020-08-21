package com.hotpiecraft.olxapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hotpiecraft.olxapp.R;
import com.hotpiecraft.olxapp.model.ItemModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.MyItemsViewHolder> {

    private Context context;
    private List<ItemModel> adsList;

    public AdsAdapter(Context context, List<ItemModel> adsList) {
        this.context = context;
        this.adsList = adsList;
    }

    class MyItemsViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView value;
        ImageView photo;

        public MyItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textProductTitle);
            value = itemView.findViewById(R.id.textProductValue);
            photo = itemView.findViewById(R.id.imageAd);
        }
    }

    @NonNull
    @Override
    public MyItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_list_adapter ,parent, false);

        return new MyItemsViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemsViewHolder holder, int position) {
            ItemModel item = adsList.get(position);

            holder.title.setText(item.getTitle());
            holder.value.setText(item.getValue());

            //Get first image
        List<String> urlPhotos = item.getPhotoPath();
        String urlCover = urlPhotos.get(0);

        Picasso.get().load(urlCover).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }

}
