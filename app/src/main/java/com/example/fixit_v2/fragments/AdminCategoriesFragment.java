package com.example.fixit_v2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.adapters.CategoryAdapter;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.models.ServiceCategory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminCategoriesFragment extends Fragment {

    private RecyclerView recyclerViewCategories;
    private FloatingActionButton fabAddCategory;
    private ServiceCategoryDataSource dataSource;
    private CategoryAdapter adapter;
    private List<ServiceCategory> categories;

    private android.widget.EditText editTextSearch;

    private android.net.Uri selectedImageUri;
    private androidx.activity.result.ActivityResultLauncher<android.content.Intent> imagePickerLauncher;
    private android.widget.ImageView imageViewPreview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_categories, container, false);

        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        fabAddCategory = view.findViewById(R.id.fabAddCategory);
        editTextSearch = view.findViewById(R.id.editTextSearch);

        setupImagePicker();
        
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        dataSource = new ServiceCategoryDataSource(getContext());
        dataSource.open();

        categories = dataSource.getAllServiceCategories();

        adapter = new CategoryAdapter(getContext(), categories, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(ServiceCategory category) {
                showAddEditCategoryDialog(category);
            }

            @Override
            public void onDeleteClick(ServiceCategory category) {
                showDeleteConfirmationDialog(category);
            }
        });
        recyclerViewCategories.setAdapter(adapter);

        fabAddCategory.setOnClickListener(v -> showAddEditCategoryDialog(null));

        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        return view;
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (imageViewPreview != null && selectedImageUri != null) {
                        imageViewPreview.setImageURI(selectedImageUri);
                        imageViewPreview.setImageTintList(null); // Clear primary tint for real images
                    }
                }
            }
        );
    }

    private void openImagePicker() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private String saveImageToInternalStorage(String categoryName) {
        if (selectedImageUri == null) {
            return "";
        }

        try {
            java.io.File categoriesDir = new java.io.File(requireContext().getFilesDir(), "categories");
            if (!categoriesDir.exists()) {
                categoriesDir.mkdirs();
            }

            String sanitizedName = categoryName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            String filename = "category_" + sanitizedName + "_" + System.currentTimeMillis() + ".jpg";
            java.io.File imageFile = new java.io.File(categoriesDir, filename);

            java.io.InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedImageUri);
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(imageFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            outputStream.close();
            inputStream.close();

            return "categories/" + filename;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private void showDeleteConfirmationDialog(ServiceCategory category) {
        new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete " + category.getCategoryName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dataSource.deleteServiceCategory(category);
                    refreshCategoryList();
                    Toast.makeText(getContext(), "Category deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddEditCategoryDialog(final ServiceCategory category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_service_category, null);
        builder.setView(dialogView);

        final EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);
        android.widget.TextView textViewTitle = dialogView.findViewById(R.id.textViewDialogTitle);
        android.widget.Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        android.widget.Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        imageViewPreview = dialogView.findViewById(R.id.imageViewCategoryPreview);
        android.widget.Button buttonSelectImage = dialogView.findViewById(R.id.buttonSelectCategoryImage);

        selectedImageUri = null; // Reset for new/edit
        textViewTitle.setText(category == null ? "Add Category" : "Edit Category");
        buttonSave.setText(category == null ? "Add" : "Save");

        if (category != null) {
            editTextCategoryName.setText(category.getCategoryName());
            String imagePath = category.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    if (imagePath.startsWith("images/")) {
                        java.io.InputStream is = requireContext().getAssets().open(imagePath);
                        android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(is);
                        imageViewPreview.setImageBitmap(bitmap);
                        imageViewPreview.setImageTintList(null);
                        is.close();
                    } else {
                        java.io.File file = new java.io.File(requireContext().getFilesDir(), imagePath);
                        if (file.exists()) {
                            imageViewPreview.setImageURI(android.net.Uri.fromFile(file));
                            imageViewPreview.setImageTintList(null);
                        }
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }

        buttonSelectImage.setOnClickListener(v -> openImagePicker());

        AlertDialog dialog = builder.create();
        buttonSave.setOnClickListener(v -> {
            String name = editTextCategoryName.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a category name", Toast.LENGTH_SHORT).show();
                return;
            }

            String finalImagePath = category != null ? category.getImagePath() : null;
            if (selectedImageUri != null) {
                finalImagePath = saveImageToInternalStorage(name);
            }

            if (category == null) {
                dataSource.createServiceCategory(name, finalImagePath);
                Toast.makeText(getContext(), "Category added", Toast.LENGTH_SHORT).show();
            } else {
                category.setCategoryName(name);
                category.setImagePath(finalImagePath);
                dataSource.updateServiceCategory(category);
                Toast.makeText(getContext(), "Category updated", Toast.LENGTH_SHORT).show();
            }
            refreshCategoryList();
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity = android.view.Gravity.CENTER;
            params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            params.dimAmount = 0.8f;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    private void refreshCategoryList() {
        categories.clear();
        categories.addAll(dataSource.getAllServiceCategories());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        dataSource.close();
        super.onPause();
    }
}
