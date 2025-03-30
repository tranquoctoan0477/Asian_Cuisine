package com.example.appasiancuisine.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.CheckoutItemAdapter;
import com.example.appasiancuisine.data.dto.CheckoutItemDTO;
import com.example.appasiancuisine.presenter.CheckoutContract;
import com.example.appasiancuisine.presenter.CheckoutPresenter;
import com.example.appasiancuisine.utils.PreferenceManager;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity implements CheckoutContract.View {

    private EditText editAddress, editPhone, editNote, editVoucher;
    private RecyclerView recyclerCheckoutItems;
    private CheckoutItemAdapter adapter;
    private List<CheckoutItemDTO> checkoutItems;

    private CheckoutPresenter presenter;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Log.d("CheckoutActivity", "📦 Bắt đầu CheckoutActivity");

        presenter = new CheckoutPresenter(this);

        editAddress = findViewById(R.id.edit_address);
        editPhone = findViewById(R.id.edit_phone);
        editNote = findViewById(R.id.edit_address_note);
        editVoucher = findViewById(R.id.edit_voucher);
        recyclerCheckoutItems = findViewById(R.id.recycler_checkout_items);

        checkoutItems = (List<CheckoutItemDTO>) getIntent().getSerializableExtra("checkout_items");
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            Log.e("CheckoutActivity", "❌ Không nhận được dữ liệu giỏ hàng từ Intent");
            finish();
            return;
        }

        Log.d("CheckoutActivity", "✅ Nhận được " + checkoutItems.size() + " sản phẩm từ giỏ hàng");

        adapter = new CheckoutItemAdapter(this, checkoutItems);
        recyclerCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerCheckoutItems.setAdapter(adapter);

        updateTotalReceipt();

        PreferenceManager prefManager = new PreferenceManager(this);
        accessToken = prefManager.getAccessToken();
        Log.d("CheckoutActivity", "🔐 AccessToken (actual): " + accessToken);

        findViewById(R.id.button_confirm_payment).setOnClickListener(v -> handleCheckout());
    }

    private void handleCheckout() {
        String address = editAddress.getText().toString().trim();
        String phoneNumber = editPhone.getText().toString().trim(); // ✅ rename biến cho khớp
        String addressNote = editNote.getText().toString().trim(); // ✅ rename biến
        String voucherCode = editVoucher.getText().toString().trim();

        Log.d("CheckoutActivity", "📤 Thực hiện checkout với:");
        Log.d("CheckoutActivity", "  - Địa chỉ: " + address);
        Log.d("CheckoutActivity", "  - SĐT: " + phoneNumber);
        Log.d("CheckoutActivity", "  - Ghi chú: " + addressNote);
        Log.d("CheckoutActivity", "  - Mã giảm giá: " + voucherCode);

        if (address.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ địa chỉ và số điện thoại!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (accessToken == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        presenter.checkout(address, phoneNumber, addressNote, voucherCode, checkoutItems, accessToken); // ✅ truyền đúng tên
    }


    private void updateTotalReceipt() {
        double subtotal = 0;
        int discount = 0;
        int deliveryFee = 15000;

        for (CheckoutItemDTO item : checkoutItems) {
            double price = item.getPrice();
            int quantity = item.getQuantity();
            Log.d("CheckoutActivity", "🧾 " + item.getProductName() + ": " + quantity + " x " + price);
            subtotal += price * quantity;
        }

        int total = (int) (subtotal - discount + deliveryFee);

        ((TextView) findViewById(R.id.text_subtotal)).setText(String.format("%,d₫", (int) subtotal));
        ((TextView) findViewById(R.id.text_discount)).setText("-" + String.format("%,d₫", discount));
        ((TextView) findViewById(R.id.text_delivery_fee)).setText(String.format("%,d₫", deliveryFee));
        ((TextView) findViewById(R.id.text_total)).setText(String.format("%,d₫", total));

        Log.d("CheckoutActivity", "💰 Tổng phụ: " + subtotal);
        Log.d("CheckoutActivity", "🚚 Phí ship: " + deliveryFee);
        Log.d("CheckoutActivity", "🔖 Giảm giá: " + discount);
        Log.d("CheckoutActivity", "💵 Tổng cộng: " + total);
    }

    @Override
    public void onCheckoutSuccess(String message) {
        Log.d("CheckoutActivity", "✅ Đặt hàng thành công: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 2500);
    }

    @Override
    public void onCheckoutError(String error) {
        Log.e("CheckoutActivity", "❌ Đặt hàng thất bại: " + error);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
