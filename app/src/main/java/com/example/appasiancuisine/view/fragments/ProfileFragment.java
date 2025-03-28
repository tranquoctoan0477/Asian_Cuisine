package com.example.appasiancuisine.view.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.SettingAdapter;
import com.example.appasiancuisine.model.SettingItem;
import com.example.appasiancuisine.utils.PreferenceManager;
import com.example.appasiancuisine.view.LoginActivity;
import com.example.appasiancuisine.view.ResetPasswordActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerSetting;
    private List<SettingItem> settingItemList;
    private SettingAdapter settingAdapter;

    private PreferenceManager pref;
    private ImageView imageAvatar;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageAvatar.setImageURI(uri);
                    pref.saveAvatarUri(uri.toString());
                }
            });

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pref = new PreferenceManager(requireContext());

        imageAvatar = view.findViewById(R.id.image_avatar);
        ImageButton buttonEdit = view.findViewById(R.id.button_edit_profile);
        TextView textUsername = view.findViewById(R.id.text_user_name);
        TextView textEmail = view.findViewById(R.id.text_email);
        TextView textPhone = view.findViewById(R.id.text_user_phone);

        String phone = pref.getPhone(); // bạn cần thêm getPhone() nếu chưa có
        if (phone != null && !phone.isEmpty()) {
            textPhone.setText(phone);
        }

        // Load dữ liệu từ SharedPreferences
        textUsername.setText(pref.getUsername());
        textEmail.setText(pref.getEmail());
        textPhone.setText("+84 912 345 678");

        String avatarUri = pref.getAvatarUri();
        if (avatarUri != null) {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(Uri.parse(avatarUri));
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageAvatar.setImageBitmap(bitmap);
                    inputStream.close();
                } else {
                    imageAvatar.setImageResource(R.drawable.img_logo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageAvatar.setImageResource(R.drawable.img_logo);
            }
        } else {
            // Chưa có ảnh → dùng ảnh mặc định
            imageAvatar.setImageResource(R.drawable.img_logo);
        }


        // Xử lý bấm nút chỉnh sửa avatar
        buttonEdit.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        // Danh sách setting
        recyclerSetting = view.findViewById(R.id.recycler_setting);
        settingItemList = new ArrayList<>();
        settingItemList.add(new SettingItem(1, R.drawable.ic_profile, "Thông tin cá nhân"));
        settingItemList.add(new SettingItem(2, R.drawable.ic_refund, "Yêu cầu hoàn tiền"));
        settingItemList.add(new SettingItem(3, R.drawable.ic_password, "Đổi mật khẩu"));
        settingItemList.add(new SettingItem(4, R.drawable.ic_language, "Ngôn ngữ"));
        settingItemList.add(new SettingItem(5, R.drawable.face_id, "Cập nhật sinh trắc học"));
        settingItemList.add(new SettingItem(6, R.drawable.ic_logout, "Đăng xuất"));

        // Adapter + xử lý sự kiện
        settingAdapter = new SettingAdapter(settingItemList, item -> {
            switch (item.getId()) {
                case 1:
                    Toast.makeText(getContext(), "Mở thông tin cá nhân", Toast.LENGTH_SHORT).show();
                    break;

                case 3:
                    if (pref.isLoggedIn()) {
                        Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Vui lòng đăng nhập trước khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 5:
                    Toast.makeText(getContext(), "Mở cập nhật sinh trắc học", Toast.LENGTH_SHORT).show();
                    break;

                case 6:
                    new AlertDialog.Builder(getContext())
                            .setTitle("Xác nhận")
                            .setMessage("Bạn có chắc muốn đăng xuất?")
                            .setPositiveButton("Đăng xuất", (dialog, which) -> {
                                pref.clearLoginData();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                    break;

                default:
                    Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        recyclerSetting.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSetting.setAdapter(settingAdapter);
    }
}
