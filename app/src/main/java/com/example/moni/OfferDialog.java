package com.example.moni;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
public class OfferDialog extends Dialog {
    private final List<Offer> offers;
    private Offer currentOffer;
    private int currentIndex = 0;
    private CountDownTimer timer;
    private OnOfferActionListener listener;
    private List<ImageView> dots;
    private MaterialButton btnNextOffer;
    private MaterialButton btnLearnMore;
    private MaterialButton btnDismiss;

    public interface OnOfferActionListener {
        void onLearnMore(Offer offer);
        void onDismiss(Offer offer);
    }

    public OfferDialog(@NonNull Context context, List<Offer> offers, OnOfferActionListener listener) {
        super(context);
        this.offers = offers;
        this.listener = listener;
        if (!offers.isEmpty()) {
            this.currentOffer = offers.get(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_offer);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setAttributes(windowParams);
        }

        initializeViews();
        setupDotIndicators();
        updateContent();
    }

    private void initializeViews() {
        btnNextOffer = findViewById(R.id.btnNextOffer);
        btnLearnMore = findViewById(R.id.btnLearnMore);
        btnDismiss = findViewById(R.id.btnDismiss);

        btnNextOffer.setVisibility(offers.size() > 1 ? View.VISIBLE : View.GONE);

        btnNextOffer.setOnClickListener(v -> navigateToNextOffer());
        btnLearnMore.setOnClickListener(v -> {
            if (listener != null && currentOffer != null) {
                listener.onLearnMore(currentOffer);
            }
            dismiss();
        });
        btnDismiss.setOnClickListener(v -> {
            if (listener != null && currentOffer != null) {
                listener.onDismiss(currentOffer);
            }
            dismiss();
        });
    }

    private void setupDotIndicators() {
        LinearLayout dotsContainer = findViewById(R.id.dotsIndicator);
        dots = new ArrayList<>();

        if (offers.size() <= 1) {
            dotsContainer.setVisibility(View.GONE);
            return;
        }

        for (int i = 0; i < offers.size(); i++) {
            ImageView dot = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setImageResource(R.drawable.dot_inactive);
            dotsContainer.addView(dot);
            dots.add(dot);
        }
        updateDotIndicators();
    }

    private void updateDotIndicators() {
        for (int i = 0; i < dots.size(); i++) {
            dots.get(i).setImageResource(
                    i == currentIndex ? R.drawable.dot_active : R.drawable.dot_inactive
            );
        }
    }

    private void navigateToNextOffer() {
        currentIndex = (currentIndex + 1) % offers.size();
        updateContent();
        updateDotIndicators();
    }

    private void updateContent() {
        if (currentOffer == null) return;

        TextView tvTitle = findViewById(R.id.tvOfferTitle);
        TextView tvDescription = findViewById(R.id.tvOfferDescription);
        TextView tvTimeRemaining = findViewById(R.id.tvTimeRemaining);
        ImageView ivOfferImage = findViewById(R.id.ivOfferImage);

        currentOffer = offers.get(currentIndex);
        tvTitle.setText(currentOffer.getTitle());
        tvDescription.setText(currentOffer.getDescription());

        if (currentOffer.getImageUrl() != null) {
            try {
                Uri imageUri = Uri.parse(currentOffer.getImageUrl());
                ivOfferImage.setImageURI(imageUri);
            } catch (Exception e) {
                Log.e("OfferDialog", "Error loading image: " + e.getMessage());
                ivOfferImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        long timeRemaining = currentOffer.getEndTime() - System.currentTimeMillis();
        if (timeRemaining > 0) {
            startTimer(timeRemaining, tvTimeRemaining);
        } else {
            tvTimeRemaining.setText("Offer expired");
        }
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