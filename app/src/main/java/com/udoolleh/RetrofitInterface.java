package com.udoolleh;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitInterface {
    //@통신 방식("통신 API명")
    @POST("/login")
    Call<LoginResponse> getLoginResponse(@Body LoginRequest loginRequest);

    @POST("/user")
    Call<SignUpResponse> getSignUpResponse(@Body SignUpRequest signUpRequest);

    @GET("/restaurant")
    Call<FoodResponse> getFoodResponse(@Query("status") String status, @Query("list") List<String> foodList);

    /*
    @GET("/restaurant")
    Call<FoodResponse> getFoodResponse(
            @Query("name") String name,
            @Query("placeType") String placeType,
            @Query("category") String category,
            @Query("address") String address,
            @Query("imagesUrl") String imagesUrl,
            @Query("totalGrade") String totalGrade
    );
     */
}
