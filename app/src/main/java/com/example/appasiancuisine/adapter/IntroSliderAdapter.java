package com.example.appasiancuisine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.model.IntroSlide;

import java.util.List;

public class IntroSliderAdapter extends RecyclerView.Adapter<IntroSliderAdapter.SlideViewHolder> {

    private final List<IntroSlide> introSlideList;

    public IntroSliderAdapter(List<IntroSlide> introSlideList) {
        this.introSlideList = introSlideList;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intro_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.bind(introSlideList.get(position));
    }

    @Override
    public int getItemCount() {
        return introSlideList.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iconImageView;
        private final TextView titleTextView;
        private final TextView descriptionTextView;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.imageSlideIcon);
            titleTextView = itemView.findViewById(R.id.textSlideTitle);
            descriptionTextView = itemView.findViewById(R.id.textSlideDescription);
        }

        void bind(IntroSlide slide) {
            iconImageView.setImageResource(slide.getIconResId());
            titleTextView.setText(slide.getTitle());
            descriptionTextView.setText(slide.getDescription());
        }
    }
}
