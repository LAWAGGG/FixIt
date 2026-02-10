package com.example.fixit_v2.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class AdminServiceCategoryActivity extends AppCompatActivity {

    private ListView listViewCategories;
    private Button buttonAddCategory;
    private ServiceCategoryDataSource dataSource;
    private ArrayAdapter<ServiceCategory> adapter;
    private List<ServiceCategory> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service_category);

        listViewCategories = findViewById(R.id.listViewCategories);
        buttonAddCategory = findViewById(R.id.buttonAddCategory);

        dataSource = new ServiceCategoryDataSource(this);
        dataSource.open();

        categories = dataSource.getAllServiceCategories();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        listViewCategories.setAdapter(adapter);
        registerForContextMenu(listViewCategories);

        buttonAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEditCategoryDialog(null);
            }
        });
    }

    private void showAddEditCategoryDialog(final ServiceCategory category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_service_category, null);
        builder.setView(dialogView);

        final EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);

        builder.setTitle(category == null ? "Add Category" : "Edit Category");
        if (category != null) {
            editTextCategoryName.setText(category.getCategoryName());
        }

        builder.setPositiveButton(category == null ? "Add" : "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextCategoryName.getText().toString();

                if (category == null) {
                    dataSource.createServiceCategory(name, null);
                } else {
                    category.setCategoryName(name);
                    dataSource.updateServiceCategory(category);
                }
                refreshCategoryList();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.service_category_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ServiceCategory selectedCategory = categories.get(info.position);

        int itemId = item.getItemId();
        if (itemId == R.id.edit_category) {
            showAddEditCategoryDialog(selectedCategory);
            return true;
        } else if (itemId == R.id.delete_category) {
            dataSource.deleteServiceCategory(selectedCategory);
            refreshCategoryList();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void refreshCategoryList() {
        categories.clear();
        categories.addAll(dataSource.getAllServiceCategories());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}
