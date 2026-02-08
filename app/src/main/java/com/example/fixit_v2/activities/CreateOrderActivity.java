package com.example.fixit_v2.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.databinding.ActivityCreateOrderBinding;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.models.Service;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateOrderActivity extends AppCompatActivity {

    private ActivityCreateOrderBinding binding;
    private OrderDataSource orderDataSource;
    private ServiceDataSource serviceDataSource;
    private int serviceId;
    private int userId;
    private String selectedPaymentMethod = "";
    private String userBankDetails = "";
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);
        serviceId = getIntent().getIntExtra("SERVICE_ID", -1);

        if (serviceId == -1 || userId == -1) {
            Toast.makeText(this, "Error: Service or User not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderDataSource = new OrderDataSource(this);
        serviceDataSource = new ServiceDataSource(this);

        setupToolbar();
        loadServiceInfo();
        setupListeners();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadServiceInfo() {
        serviceDataSource.open();
        Service service = serviceDataSource.getServiceById(serviceId);
        if (service != null) {
            binding.textViewServiceName.setText(service.getServiceName());
            binding.textViewServicePrice.setText(String.format(Locale.GERMAN, "Rp %,d", (long) service.getPrice()));
        }
        serviceDataSource.close();
    }

    private void setupListeners() {
        binding.editTextOrderDate.setOnClickListener(v -> showDateTimePicker());
        binding.textViewSelectedPayment.setOnClickListener(v -> showPaymentMethodSelectionSheet());
        binding.buttonPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void showDateTimePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                // Format for display
                SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm", Locale.getDefault());
                binding.editTextOrderDate.setText(displayFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showPaymentMethodSelectionSheet() {
        final BottomSheetDialog selectionSheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_payment, null);
        selectionSheet.setContentView(sheetView);

        sheetView.findViewById(R.id.payment_cash).setOnClickListener(v -> {
            selectedPaymentMethod = "Cash";
            binding.textViewSelectedPayment.setText(selectedPaymentMethod);
            selectionSheet.dismiss();
        });

        sheetView.findViewById(R.id.payment_qris).setOnClickListener(v -> {
            selectionSheet.dismiss();
            showQrisPaymentSheet();
        });

        sheetView.findViewById(R.id.payment_bank_transfer).setOnClickListener(v -> {
            selectionSheet.dismiss();
            showBankTransferSheet();
        });

        selectionSheet.show();
    }

    private void showQrisPaymentSheet() {
        // This logic remains the same, but you might want to modernize its layout too.
        final BottomSheetDialog qrisSheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_qris_payment, null);
        qrisSheet.setContentView(sheetView);
        sheetView.findViewById(R.id.buttonConfirmQris).setOnClickListener(v -> {
            selectedPaymentMethod = "QRIS";
            binding.textViewSelectedPayment.setText(selectedPaymentMethod);
            qrisSheet.dismiss();
        });
        qrisSheet.show();
    }

    private void showBankTransferSheet() {
        // This logic also remains the same.
        final BottomSheetDialog bankSheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_bank_payment, null);
        bankSheet.setContentView(sheetView);

        Spinner spinnerBankName = sheetView.findViewById(R.id.spinnerBankName);
        EditText editTextAccountNumber = sheetView.findViewById(R.id.editTextAccountNumber);
        Button buttonConfirmBankPayment = sheetView.findViewById(R.id.buttonConfirmBankPayment);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bank_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBankName.setAdapter(adapter);

        buttonConfirmBankPayment.setOnClickListener(v -> {
            String bankName = spinnerBankName.getSelectedItem().toString();
            String accountNumber = editTextAccountNumber.getText().toString();
            if (accountNumber.isEmpty()) {
                Toast.makeText(this, "Please enter your account number.", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedPaymentMethod = "Bank Transfer";
            userBankDetails = bankName + " - " + accountNumber;
            binding.textViewSelectedPayment.setText("Bank Transfer: " + bankName);
            bankSheet.dismiss();
        });
        bankSheet.show();
    }

    private void placeOrder() {
        String address = binding.editTextAddress.getText().toString();
        String orderDate = binding.editTextOrderDate.getText().toString();

        if (address.isEmpty() || orderDate.isEmpty() || selectedPaymentMethod.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format for database (yyyy-MM-dd HH:mm:ss)
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dbFormattedDate = dbFormat.format(calendar.getTime());

        orderDataSource.open();
        orderDataSource.createOrder(userId, serviceId, address, dbFormattedDate, "Pending");
        orderDataSource.close();

        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
        // Navigate to bookings screen or home
        Intent intent = new Intent(this, UserDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the data source is closed if the activity is destroyed.
        // orderDataSource.close(); // Already closed in placeOrder
    }
}
