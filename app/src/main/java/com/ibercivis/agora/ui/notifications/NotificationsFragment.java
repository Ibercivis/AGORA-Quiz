package com.ibercivis.agora.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ibercivis.agora.LoginActivity;
import com.ibercivis.agora.MainActivity;
import com.ibercivis.agora.R;
import com.ibercivis.agora.SessionManager;
import com.ibercivis.agora.classes.UserProfile;
import com.ibercivis.agora.databinding.FragmentNotificationsBinding;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.ibercivis.agora.services.RetrofitApiService;
import com.ibercivis.agora.services.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class NotificationsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FragmentNotificationsBinding binding;
    private String BASE_URL;

    private RetrofitApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BASE_URL = getString(R.string.base_url);
        SessionManager sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();

        apiService = RetrofitClient.getClient(BASE_URL, token).create(RetrofitApiService.class);


        // Cargar los datos del perfil de usuario
        loadUserProfile();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogout.setOnClickListener(v -> logoutUser());

        binding.imgProfile.setOnClickListener(v -> showImagePickerOptions());

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
                        int totalPoints = response.getInt("total_points");
                        int totalGamesPlayed = response.getInt("total_games_played");
                        int totalGamesAbandoned = response.getInt("total_games_abandoned");
                        int totalCorrectAnswers = response.getInt("total_correct_answers");
                        int totalIncorrectAnswers = response.getInt("total_incorrect_answers");
                        String profileImageUrl = response.optString("profile_image", null);

                        updateUI(new UserProfile(
                                totalPoints,
                                totalGamesPlayed,
                                totalGamesAbandoned,
                                totalCorrectAnswers,
                                totalIncorrectAnswers,
                                profileImageUrl
                        ), username);

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

    private void updateUI(UserProfile userProfile, String username) {
        binding.tvUsername.setText(username);
        binding.tvPoints.setText(String.format(Locale.getDefault(), "%d Points", userProfile.getTotalPoints()));
        binding.ptCorrectAnswers.setText(String.valueOf(userProfile.getTotalCorrectAnswers()));
        binding.ptIncorrectAnswers.setText(String.valueOf(userProfile.getTotalIncorrectAnswers()));
        binding.ptTotalQuestions.setText(String.valueOf(userProfile.getTotalGamesPlayed()));
        binding.ptTotalGames.setText(String.valueOf(userProfile.getTotalGamesPlayed() - userProfile.getTotalGamesAbandoned()));
        binding.ptTotalPoints.setText(String.valueOf(userProfile.getTotalPoints()));

        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());

        if (!userProfile.getProfileImageUrl().equalsIgnoreCase("null") && !userProfile.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(userProfile.getProfileImageUrl())
                    .apply(requestOptions)
                    .into(binding.imgProfile);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_avatar_ranking_yellow)
                    .apply(requestOptions)
                    .into(binding.imgProfile);
        }
    }

    private void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Profile Image");
        builder.setItems(new CharSequence[]{"Change Profile Image", "Remove Profile Image"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Cambiar imagen de perfil
                    pickImage();
                    break;
                case 1:
                    // Eliminar imagen de perfil
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
                            // Toma la persistencia del permiso para acceder al archivo después de cerrar el selector
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

    private void logoutUser() {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.logoutUser();
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
