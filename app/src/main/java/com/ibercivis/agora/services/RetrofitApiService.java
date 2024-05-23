package com.ibercivis.agora.services;
import com.ibercivis.agora.classes.UserProfile;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
public interface RetrofitApiService {

    @Multipart
    @PUT("api/users/profile/")
    Call<ResponseBody> uploadProfileImage(
            @Part MultipartBody.Part file
    );

    @Multipart
    @PUT("api/users/profile/")
    Call<ResponseBody> deleteProfileImage(
            @Part("profile_image") RequestBody empty
    );

    @GET("/api/users/profile/")
    Call<UserProfile> getUserProfile(@Header("Authorization") String token);

    @PUT("/api/users/profile/update/")
    Call<UserProfile> updateUserProfile(@Header("Authorization") String token, @Body UserProfileUpdateRequest updateRequest);
}
