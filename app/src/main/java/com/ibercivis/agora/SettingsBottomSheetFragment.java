package com.ibercivis.agora;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.ibercivis.agora.classes.UserProfile;
import com.ibercivis.agora.services.RetrofitApiService;
import com.ibercivis.agora.services.RetrofitClient;
import com.ibercivis.agora.services.UserProfileUpdateRequest;
import com.ibercivis.agora.ui.ViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SettingsBottomSheetFragment.java
public class SettingsBottomSheetFragment extends BottomSheetDialogFragment {

    private View mainView;
    private View editProfileView;
    private View changePasswordView, aboutUsView, faqView, deleteAccountView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView backButton, imgProfile;
    private TextInputEditText usernameEditText;
    private TextInputEditText emailEditText;
    private AppCompatButton saveChangesButton, submitChangePasswordButton, deleteAccountButton, deleteConfirmButton;
    private String token;
    private RetrofitApiService apiService;
    private String BASE_URL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Configurar el fondo del diálogo completo para que sea transparente
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        mainView = view.findViewById(R.id.main_view);
        editProfileView = view.findViewById(R.id.edit_profile_view);
        changePasswordView = view.findViewById(R.id.change_password_view);
        aboutUsView = view.findViewById(R.id.about_us_view);
        faqView = view.findViewById(R.id.faq_view);
        deleteAccountView = view.findViewById(R.id.delete_account_view);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailSignUpEditText);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        submitChangePasswordButton = view.findViewById(R.id.submitChangePasswordButton);
        deleteAccountButton = view.findViewById(R.id.deleteUserButton);
        deleteConfirmButton = view.findViewById(R.id.deleteConfirmButton);
        backButton = view.findViewById(R.id.backButton);
        imgProfile = view.findViewById(R.id.imgProfile);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        mainView.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);

        view.findViewById(R.id.backButton).setOnClickListener(v -> showMainView());
        view.findViewById(R.id.tvEditProfile).setOnClickListener(v -> showEditProfileView());
        view.findViewById(R.id.tvChangePassword).setOnClickListener(v -> showChangePasswordView());
        view.findViewById(R.id.tvAboutUs).setOnClickListener(v -> showAboutUsView());
        view.findViewById(R.id.tvFAQ).setOnClickListener(v -> showFaqView());
        view.findViewById(R.id.tvLogout).setOnClickListener(v -> logOut());
        view.findViewById(R.id.deleteUserButton).setOnClickListener(v -> showDeleteConfirmAccount());
        setupViewPager();

        // Initialize API service and token
        BASE_URL = getString(R.string.base_url);
        SessionManager sessionManager = new SessionManager(getContext());
        token = sessionManager.getToken();
        apiService = RetrofitClient.getClient(BASE_URL, token).create(RetrofitApiService.class);

        // Load current user profile
        loadUserProfile();

        // Set save changes button click listener
        saveChangesButton.setOnClickListener(v -> saveUserProfileChanges());

        // Set image profile click listener
        imgProfile.setOnClickListener(v -> showImagePickerOptions());

        // Set submit change password button click listener
        submitChangePasswordButton.setOnClickListener(v -> submitChangePassword());

        deleteConfirmButton.setOnClickListener(v -> deleteUserAccount(token));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackground(new ColorDrawable(Color.TRANSPARENT));
            applyRoundedCorners(bottomSheet);
        }
    }

    private void applyRoundedCorners(View view) {
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(
                ShapeAppearanceModel.builder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, 48f)
                        .setTopRightCorner(CornerFamily.ROUNDED, 48f)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
                        .build()
        );
        shapeDrawable.setFillColor(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
        ViewCompat.setBackground(view, shapeDrawable);
    }

    public interface OnSettingsClosedListener {
        void onSettingsClosed();
    }

    private OnSettingsClosedListener listener;

    public void setOnSettingsClosedListener(OnSettingsClosedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onSettingsClosed();
        }
    }

    private void showEditProfileView() {
        mainView.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        editProfileView.setVisibility(View.VISIBLE);
    }

    private void showChangePasswordView() {
        mainView.setVisibility(View.GONE);
        editProfileView.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        changePasswordView.setVisibility(View.VISIBLE);
    }

    private void showAboutUsView() {
        mainView.setVisibility(View.GONE);
        editProfileView.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        aboutUsView.setVisibility(View.VISIBLE);
    }

    private void showDeleteConfirmAccount() {
        mainView.setVisibility(View.GONE);
        editProfileView.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        deleteAccountView.setVisibility(View.VISIBLE);
    }

    private void showFaqView() {
        faqView.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
        editProfileView.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        aboutUsView.setVisibility(View.GONE);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new FaqFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showMainView() {
        editProfileView.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        changePasswordView.setVisibility(View.GONE);
        aboutUsView.setVisibility(View.GONE);
        faqView.setVisibility(View.GONE);
        deleteAccountView.setVisibility(View.GONE);
        mainView.setVisibility(View.VISIBLE);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new AgoraProjectFragment(), "Agora Project");
        adapter.addFragment(new ContactUsFragment(), "Contact Us");
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(adapter.getPageTitle(position))).attach();
    }

    private void loadUserProfile() {
        SessionManager sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();
        String username = sessionManager.getUsername();

        String url = BASE_URL + "/api/users/profile/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String user_name = response.getString("username");
                        String email = response.getString("email");
                        String profileImageUrl = response.optString("profile_image", null);

                        usernameEditText.setText(user_name);
                        emailEditText.setText(email);

                        RequestOptions requestOptions = new RequestOptions()
                                .transform(new CircleCrop());

                        if (profileImageUrl != null && !profileImageUrl.equals("null")) {
                            Glide.with(SettingsBottomSheetFragment.this)
                                    .load(profileImageUrl)
                                    .apply(requestOptions)
                                    .into(imgProfile);
                        } else {
                            Glide.with(SettingsBottomSheetFragment.this)
                                    .load(R.drawable.ic_avatar_ranking_yellow)
                                    .apply(requestOptions)
                                    .into(imgProfile);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this.getActivity()).add(jsonObjectRequest);
    }

    private void deleteUserAccount(String token) {
        Call<ResponseBody> call = apiService.deleteUserAccount("Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 204) {
                    showToast("User account deleted successfully");
                    logOut();
                } else {
                    showToast("Something goes wrong. Please try again later.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void saveUserProfileChanges() {
        String newUsername = usernameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();

        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(newUsername, newEmail);
        Call<UserProfile> call = apiService.updateUserProfile("Token " + token, updateRequest);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    SessionManager sessionManager = new SessionManager(getContext());
                    sessionManager.setKeyUsername(updateRequest.getUsername());
                    showToast("Profile updated successfully");
                    showMainView();
                } else {
                    showToast("Failed to update profile");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Profile Image");
        builder.setItems(new CharSequence[]{"Change Profile Image", "Remove Profile Image"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    pickImage();
                    break;
                case 1:
                    deleteProfileImage();
                    break;
            }
        });
        builder.show();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri imageUri = data.getData();
                        try {
                            getActivity().getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            uploadProfileImage(imageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void logOut(){
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.logoutUser();
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        try {
            byte[] fileData = getFileDataFromUri(imageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), fileData);
            MultipartBody.Part body = MultipartBody.Part.createFormData("profile_image", "profile_image.jpg", requestFile);

            Call<ResponseBody> call = apiService.uploadProfileImage(body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        showToast("Profile image updated successfully");
                        loadUserProfile();
                    } else {
                        showToast("Failed to update profile image");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showToast("Error: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteProfileImage() {
        RequestBody empty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
        Call<ResponseBody> call = apiService.deleteProfileImage(empty);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    showToast("Profile image removed successfully");
                    loadUserProfile();
                } else {
                    showToast("Failed to remove profile image");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private byte[] getFileDataFromUri(Uri uri) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = getActivity().getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void submitChangePassword() {
        String email = ((TextInputEditText) changePasswordView.findViewById(R.id.emailEditText)).getText().toString().trim();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/api/users/authentication/password/reset/",
                null,
                response -> showToast("Password reset email sent successfully"),
                error -> {
                    String errorMessage = "An error occurred";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorResponse = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorObject = new JSONObject(errorResponse);
                            // Assuming the error message is within the "email" field
                            if (errorObject.has("email")) {
                                JSONArray emailErrors = errorObject.getJSONArray("email");
                                if (emailErrors.length() > 0) {
                                    errorMessage = emailErrors.getString(0);
                                }
                            }
                        } catch (UnsupportedEncodingException | JSONException e) {
                            errorMessage = "An error occurred";
                        }
                    } else {
                        errorMessage = "Failed to send password reset email";
                    }
                    showToast(errorMessage);
                }) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", email);
                    return jsonBody.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void handleError(VolleyError error) {
        String errorMessage;
        if (error instanceof NetworkError || error instanceof NoConnectionError) {
            errorMessage = "Cannot connect to the server. Please check your internet connection.";
        } else if (error instanceof TimeoutError) {
            errorMessage = "Request timeout. Please try again.";
        } else if (error instanceof ServerError) {
            errorMessage = "Server error. Please try again later.";
        } else if (error instanceof AuthFailureError) {
            errorMessage = "Authentication error. Please log in again.";
        } else {
            errorMessage = "An unknown error occurred. Please try again later.";
        }
        showToast(errorMessage);
    }
}
