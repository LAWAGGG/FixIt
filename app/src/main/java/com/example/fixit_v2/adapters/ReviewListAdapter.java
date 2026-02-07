package com.example.fixit_v2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Review;
import com.example.fixit_v2.models.User;

import java.util.List;

public class ReviewListAdapter extends ArrayAdapter<Review> {

    private Context context;
    private UserDataSource userDataSource;

    public ReviewListAdapter(@NonNull Context context, @NonNull List<Review> reviews, UserDataSource userDS) {
        super(context, R.layout.list_item_review, reviews);
        this.context = context;
        this.userDataSource = userDS;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        }

        Review review = getItem(position);

        if (review != null) {
            RatingBar ratingBar = convertView.findViewById(R.id.ratingBarReview);
            TextView comment = convertView.findViewById(R.id.textViewComment);
            TextView user = convertView.findViewById(R.id.textViewUser);

            ratingBar.setRating(review.getRating());
            comment.setText(review.getComment());

            User reviewUser = userDataSource.getUserById(review.getUserId());
            String userName = (reviewUser != null) ? reviewUser.getUsername() : "Anonymous";
            user.setText("- oleh " + userName);
        }

        return convertView;
    }

    // No need for a close method, the Activity/Fragment will handle it.
}
