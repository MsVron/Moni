package com.example.moni;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {
    private boolean isAdmin;
    private List<Offer> offers;
    private Context context;
    private OnOfferClickListener listener;

    public interface OnOfferClickListener {
        void onOfferClick(Offer offer);
        void onDeleteClick(Offer offer);
    }

    public OffersAdapter(Context context, OnOfferClickListener listener, boolean isAdmin) {
        this.context = context;
        this.listener = listener;
        this.offers = new ArrayList<>();
        this.isAdmin = isAdmin;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offers.get(position);

        holder.tvTitle.setText(offer.getTitle());
        holder.tvDescription.setText(offer.getDescription());

        // Load image
        if (offer.getImageUrl() != null) {
            try {
                Uri imageUri = Uri.parse(offer.getImageUrl());
                holder.ivOfferImage.setImageURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                holder.ivOfferImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        // Set time remaining
        long timeRemaining = offer.getEndTime() - System.currentTimeMillis();
        if (timeRemaining > 0) {
            long days = TimeUnit.MILLISECONDS.toDays(timeRemaining);
            timeRemaining -= TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(timeRemaining);
            String timeText = String.format("Ends in: %dd %dh", days, hours);
            holder.tvTimeRemaining.setText(timeText);
            holder.tvTimeRemaining.setVisibility(View.VISIBLE);
        } else {
            holder.tvTimeRemaining.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOfferClick(offer);
            }
        });

        if (isAdmin) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(offer);
                }
            });
        } else {
            holder.ivDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOfferImage, ivDelete;  // Add ivDelete
        TextView tvTitle, tvDescription, tvTimeRemaining;

        OfferViewHolder(View view) {
            super(view);
            ivOfferImage = view.findViewById(R.id.ivOfferImage);
            ivDelete = view.findViewById(R.id.ivDelete);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvTimeRemaining = view.findViewById(R.id.tvTimeRemaining);
        }
    }
}