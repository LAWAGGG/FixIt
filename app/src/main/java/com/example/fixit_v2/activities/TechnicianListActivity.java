package com.example.fixit_v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Technician;

import java.util.List;

public class TechnicianListActivity extends AppCompatActivity {

    private ListView listViewTechnicians;
    private TextView textViewCategoryTitle;
    private TechnicianDataSource technicianDataSource;
    private List<Technician> technicianList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_list);

        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        textViewCategoryTitle = findViewById(R.id.textViewCategoryTitle);
        listViewTechnicians = findViewById(R.id.listViewTechnicians);

        textViewCategoryTitle.setText("Teknisi untuk " + categoryName);

        technicianDataSource = new TechnicianDataSource(this);
        technicianDataSource.open();

        if (categoryId != -1) {
            technicianList = technicianDataSource.getTechniciansByCategory(categoryId);
        } else {
            technicianList = technicianDataSource.getAllTechnicians(); // Fallback
        }

        ArrayAdapter<Technician> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, technicianList);
        listViewTechnicians.setAdapter(adapter);

        listViewTechnicians.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Technician selectedTechnician = technicianList.get(position);
                Intent intent = new Intent(TechnicianListActivity.this, TechnicianDetailActivity.class);
                intent.putExtra("TECHNICIAN_ID", selectedTechnician.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        technicianDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        technicianDataSource.close();
    }
}
