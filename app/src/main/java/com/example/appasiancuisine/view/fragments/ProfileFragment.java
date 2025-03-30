package com.example.appasiancuisine.view.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;
import android.content.Context;

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
                    showBiometricOptions();
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


    private void showBiometricOptions() {
        PreferenceManager preferenceManager = new PreferenceManager(requireContext());
        boolean isFingerprintEnabled = preferenceManager.isFingerprintEnabled();
        boolean isFaceRecognitionEnabled = preferenceManager.isFaceRecognitionEnabled();

        String[] options = new String[]{
                (isFingerprintEnabled ? "Tắt đăng nhập vân tay" : "Đăng ký vân tay"),
                (isFaceRecognitionEnabled ? "Tắt đăng nhập khuôn mặt" : "Đăng ký khuôn mặt")
        };

        new AlertDialog.Builder(requireContext())
                .setTitle("Cập nhật sinh trắc học")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) { // Vân tay
                        if (isFingerprintEnabled) {
                            preferenceManager.setFingerprintEnabled(false);
                            Toast.makeText(requireContext(), "Đã tắt đăng nhập bằng vân tay", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isBiometricAvailable(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                                if (isBiometricEnrolled(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                                    showBiometricPrompt(); // Mở xác thực vân tay
                                } else {
                                    Toast.makeText(requireContext(), "Bạn chưa đăng ký vân tay trong Cài đặt hệ thống.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(requireContext(), "Thiết bị không hỗ trợ vân tay.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (which == 1) { // Khuôn mặt
                        if (isFaceRecognitionEnabled) {
                            preferenceManager.setFaceRecognitionEnabled(false);
                            Toast.makeText(requireContext(), "Đã tắt đăng nhập bằng khuôn mặt", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isBiometricAvailable(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                                if (isBiometricEnrolled(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                                    showFaceRecognitionPrompt(); // Mở xác thực khuôn mặt
                                } else {
                                    Toast.makeText(requireContext(), "Bạn chưa đăng ký khuôn mặt trong Cài đặt hệ thống.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(requireContext(), "Thiết bị không hỗ trợ nhận diện khuôn mặt.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private boolean isBiometricAvailable(int authenticatorType) {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        int canAuthenticate = biometricManager.canAuthenticate(authenticatorType);

        // Kiểm tra vân tay
        if (authenticatorType == BiometricManager.Authenticators.BIOMETRIC_STRONG) {
            Log.d("Biometric", "Kiểm tra vân tay: " + (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS ? "Có thể sử dụng" : "Không thể sử dụng"));
        }
        // Kiểm tra khuôn mặt
        if (authenticatorType == BiometricManager.Authenticators.BIOMETRIC_WEAK) {
            Log.d("Biometric", "Kiểm tra khuôn mặt: " + (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS ? "Có thể sử dụng" : "Không thể sử dụng"));
        }

        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }


    private boolean isBiometricEnrolled(int authenticatorType) {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        int canAuthenticate = biometricManager.canAuthenticate(authenticatorType);

        // Log chi tiết về việc đã đăng ký sinh trắc học hay chưa
        if (authenticatorType == BiometricManager.Authenticators.BIOMETRIC_STRONG) {
            Log.d("Biometric", "Kiểm tra vân tay đã đăng ký: " +
                    (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS ? "Đã đăng ký" : "Chưa đăng ký"));
        } else if (authenticatorType == BiometricManager.Authenticators.BIOMETRIC_WEAK) {
            Log.d("Biometric", "Kiểm tra khuôn mặt đã đăng ký: " +
                    (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS ? "Đã đăng ký" : "Chưa đăng ký"));
        }

        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }



    private boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        switch (canAuthenticate) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("Biometric", "Thiết bị hỗ trợ sinh trắc học.");
                return true;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.d("Biometric", "Thiết bị không có phần cứng hỗ trợ sinh trắc học.");
                return false;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.d("Biometric", "Phần cứng sinh trắc học hiện không khả dụng.");
                return false;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.d("Biometric", "Chưa có vân tay hoặc khuôn mặt nào được đăng ký.");
                return false;

            default:
                Log.d("Biometric", "Thiết bị không hỗ trợ sinh trắc học.");
                return false;
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(requireContext());

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(requireContext(), "Đăng ký vân tay thành công!", Toast.LENGTH_SHORT).show();

                // Lưu trạng thái vào PreferenceManager
                pref.setFingerprintEnabled(true);
                Log.d("Biometric", "Đăng ký vân tay thành công và đã được lưu.");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(requireContext(), "Đăng ký thất bại, thử lại.", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng ký vân tay")
                .setDescription("Sử dụng cảm biến vân tay của bạn để đăng ký.")
                .setNegativeButtonText("Hủy")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void showFaceRecognitionPrompt() {
        Executor executor = ContextCompat.getMainExecutor(requireContext());

        // Kiểm tra xem thiết bị có hỗ trợ nhận diện khuôn mặt và đã đăng ký chưa
        if (isBiometricAvailable(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            if (isBiometricEnrolled(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                Log.d("Biometric", "Khuôn mặt đã đăng ký và có thể sử dụng.");
                biometricPromptAuthenticate(executor);  // Gọi hàm để hiển thị BiometricPrompt
            } else {
                Log.d("Biometric", "Khuôn mặt chưa được đăng ký.");
                Toast.makeText(requireContext(), "Bạn chưa đăng ký khuôn mặt trong Cài đặt hệ thống.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("Biometric", "Thiết bị không hỗ trợ khuôn mặt.");
            Toast.makeText(requireContext(), "Thiết bị không hỗ trợ nhận diện khuôn mặt.", Toast.LENGTH_SHORT).show();
        }
    }


    // BiometricPrompt cho khuôn mặt
    private void biometricPromptAuthenticate(Executor executor) {
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(requireContext(), "Đăng ký khuôn mặt thành công!", Toast.LENGTH_SHORT).show();

                // Lưu trạng thái vào PreferenceManager
                pref.setFaceRecognitionEnabled(true);
                Log.d("Biometric", "Đăng ký khuôn mặt thành công và đã được lưu.");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(requireContext(), "Đăng ký thất bại, thử lại.", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng ký khuôn mặt")
                .setDescription("Sử dụng nhận diện khuôn mặt để đăng ký.")
                .setNegativeButtonText("Hủy")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }






}
