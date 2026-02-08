package com.example.fixit_v2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.databinding.ItemReviewBinding;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Review;
import com.example.fixit_v2.models.User;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final Context context;
    private final List<Review> reviewList;
    private final UserDataSource userDataSource;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        this.userDataSource = new UserDataSource(context);
        this.userDataSource.open(); // Open the data source once
    }

    // Call this method when the adapter is no longer needed
    public void closeDataSource() {
        if (userDataSource != null) {
            userDataSource.close();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemReviewBinding binding = ItemReviewBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReviewBinding binding;

        public ViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Review review) {
            User user = userDataSource.getUserById(review.getUserId());
            if (user != null) {
                binding.textViewReviewerName.setText(user.getUsername());
                // Load user avatar here
            }

            binding.textViewReviewComment.setText(review.getComment());
            addRatingStars(binding.layoutRating, review.getRating());
        }

        private void addRatingStars(LinearLayout ratingLayout, int rating) {
            ratingLayout.removeAllViews();
            for (int i = 0; i < 5; i++) {
                ImageView star = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMarginEnd(4);
                star.setLayoutParams(params);

                if (i < rating) {
                    star.setImageResource(R.drawable.ic_star);
                    star.setColorFilter(ContextCompat.getColor(context, R.color.rating_star_yellow));
                } else {
                    star.setImageResource(R.drawable.ic_star_outline);
                    star.setColorFilter(ContextCompat.getColor(context, R.color.icon_inactive_dark));
                }
                ratingLayout.addView(star);
            }
        }
    }
}
