package com.example.moni;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import java.util.concurrent.TimeUnit;

public class OfferDialog extends Dialog {
    private final Offer offer;
    private CountDownTimer timer;
    private OnOfferActionListener listener;

    public interface OnOfferActionListener {
        void onLearnMore(Offer offer);
        void onDismiss(Offer offer);
    }

    public OfferDialog(@NonNull Context context, Offer offer, OnOfferActionListener listener) {
        super(context);
        this.offer = offer;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_offer);

        // Initialize views
        TextView tvTitle = findViewById(R.id.tvOfferTitle);
        TextView tvDescription = findViewById(R.id.tvOfferDescription);
        TextView tvTimeRemaining = findViewById(R.id.tvTimeRemaining);
        ImageView ivOfferImage = findViewById(R.id.ivOfferImage);
        MaterialButton btnLearnMore = findViewById(R.id.btnLearnMore);
        MaterialButton btnDismiss = findViewById(R.id.btnDismiss);

        // Set content
        tvTitle.setText(offer.getTitle());
        tvDescription.setText(offer.getDescription());

// Load image
        if (offer.getImageUrl() != null) {
            try {
                Uri imageUri = Uri.parse(offer.getImageUrl());
                // Add logging
                Log.d("OfferDialog", "Loading image from URI: " + imageUri);

                // Request permission to read URI
                getContext().getContentResolver().takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                ivOfferImage.setImageURI(imageUri);

                // Add error checking
                if (ivOfferImage.getDrawable() == null) {
                    Log.e("OfferDialog", "Failed to load image, falling back to default");
                    ivOfferImage.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } catch (Exception e) {
                Log.e("OfferDialog", "Error loading image: " + e.getMessage());
                e.printStackTrace();
                ivOfferImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            Log.d("OfferDialog", "No image URL provided");
            ivOfferImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Set timer
        long timeRemaining = offer.getEndTime() - System.currentTimeMillis();
        if (timeRemaining > 0) {
            startTimer(timeRemaining, tvTimeRemaining);
        }

        // Set click listeners
        btnLearnMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLearnMore(offer);
            }
            dismiss();
        });

        btnDismiss.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDismiss(offer);
            }
            dismiss();
        });
    }

    private void startTimer(long millisUntilFinished, TextView tvTimeRemaining) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisLeft) {
                long days = TimeUnit.MILLISECONDS.toDays(millisLeft);
                millisLeft -= TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(millisLeft);
                millisLeft -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisLeft);

                String timeText = String.format("Ends in: %dd %dh %dm", days, hours, minutes);
                tvTimeRemaining.setText(timeText);
            }

            @Override
            public void onFinish() {
                dismiss();
            }
        }.start();
    }

    @Override
    public void dismiss() {
        if (timer != null) {
            timer.cancel();
        }
        super.dismiss();
    }
}
