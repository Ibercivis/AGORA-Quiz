package com.ibercivis.agora;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.ibercivis.agora.classes.UserProfile;
import com.ibercivis.agora.services.RetrofitApiService;
import com.ibercivis.agora.services.RetrofitClient;
import com.ibercivis.agora.services.UserProfileUpdateRequest;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private ImageView backButton, imgProfile;
    private TextInputEditText usernameEditText;
    private TextInputEditText emailEditText;
    private AppCompatButton saveChangesButton;
    private String token;
    private RetrofitApiService apiService;
    private String BASE_URL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mainView = view.findViewById(R.id.main_view);
        editProfileView = view.findViewById(R.id.edit_profile_view);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailSignUpEditText);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        backButton = view.findViewById(R.id.backButton);
        imgProfile = view.findViewById(R.id.imgProfile);

        mainView.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);

        view.findViewById(R.id.tvEditProfile).setOnClickListener(v -> showEditProfileView());
        view.findViewById(R.id.backButton).setOnClickListener(v -> showMainView());

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

        return view;
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

    private void showMainView() {
        editProfileView.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        mainView.setVisibility(View.VISIBLE);
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
