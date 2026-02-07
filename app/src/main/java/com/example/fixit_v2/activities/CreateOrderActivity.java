package com.example.fixit_v2.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CreateOrderActivity extends AppCompatActivity {

    private EditText editTextAddress;
    private EditText editTextOrderDate;
    private TextView textViewSelectedPayment;
    private Button buttonPlaceOrder;

    private OrderDataSource orderDataSource;
    private int serviceId;
    private int userId;
    private String selectedPaymentMethod = "";
    private String userBankDetails = ""; // To store bank details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        serviceId = getIntent().getIntExtra("SERVICE_ID", -1);

        if (serviceId == -1 || userId == -1) {
            Toast.makeText(this, "Error: Service or User not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextAddress = findViewById(R.id.editTextAddress);
        editTextOrderDate = findViewById(R.id.editTextOrderDate);
        textViewSelectedPayment = findViewById(R.id.textViewSelectedPayment);
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder);

        orderDataSource = new OrderDataSource(this);
        orderDataSource.open();

        textViewSelectedPayment.setOnClickListener(v -> showPaymentMethodSelectionSheet());
        buttonPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void showPaymentMethodSelectionSheet() {
        final BottomSheetDialog selectionSheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_payment, null);
        selectionSheet.setContentView(sheetView);

        sheetView.findViewById(R.id.payment_cash).setOnClickListener(v -> {
            selectedPaymentMethod = "Cash";
            textViewSelectedPayment.setText(selectedPaymentMethod);
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
        final BottomSheetDialog qrisSheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_qris_payment, null);
        qrisSheet.setContentView(sheetView);

        sheetView.findViewById(R.id.buttonConfirmQris).setOnClickListener(v -> {
            selectedPaymentMethod = "QRIS";
            textViewSelectedPayment.setText(selectedPaymentMethod);
            qrisSheet.dismiss();
        });

        qrisSheet.show();
    }

    private void showBankTransferSheet() {
        final BottomSheetDialog bankSheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_bank_payment, null);
        bankSheet.setContentView(sheetView);

        Spinner spinnerBankName = sheetView.findViewById(R.id.spinnerBankName);
        EditText editTextAccountNumber = sheetView.findViewById(R.id.editTextAccountNumber);
        Button buttonConfirmBankPayment = sheetView.findViewById(R.id.buttonConfirmBankPayment);

        // Setup Spinner for bank names
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
            userBankDetails = bankName + " - " + accountNumber; // Store the details
            textViewSelectedPayment.setText("Bank Transfer: " + bankName);
            bankSheet.dismiss();
        });

        bankSheet.show();
    }

    private void placeOrder() {
        String address = editTextAddress.getText().toString();
        String orderDate = editTextOrderDate.getText().toString();

        if (address.isEmpty() || orderDate.isEmpty() || selectedPaymentMethod.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }

        // In a real app, you would save `userBankDetails` to the database with the order or payment record.
        orderDataSource.createOrder(userId, serviceId, address, orderDate, "Menunggu");

        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orderDataSource.close();
    }
}
