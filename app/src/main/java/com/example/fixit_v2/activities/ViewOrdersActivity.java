package com.example.fixit_v2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.models.Order;

import java.util.List;

public class ViewOrdersActivity extends AppCompatActivity {

    private ListView listViewOrders;
    private OrderDataSource orderDataSource;
    private List<Order> orders;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listViewOrders = findViewById(R.id.listViewOrders);
        orderDataSource = new OrderDataSource(this);
        orderDataSource.open();

        orders = orderDataSource.getOrdersByUserId(userId);

        ArrayAdapter<Order> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orders);
        listViewOrders.setAdapter(adapter);

        listViewOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order selectedOrder = orders.get(position);
                if ("Selesai".equals(selectedOrder.getStatus())) {
                    Intent intent = new Intent(ViewOrdersActivity.this, CreateReviewActivity.class);
                    intent.putExtra("ORDER_ID", selectedOrder.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewOrdersActivity.this, "You can only review completed orders.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderDataSource.open();
        // Refresh the list in case a review was added or status changed
        if (orders != null && listViewOrders.getAdapter() != null) {
            orders.clear();
            orders.addAll(orderDataSource.getOrdersByUserId(userId));
            ((ArrayAdapter) listViewOrders.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        orderDataSource.close();
    }
}
