package com.udoolleh;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodFragment extends Fragment {
    private ViewPager2 viewpager_slider;
    private LinearLayout layout_indicator;
    Context context;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    RecyclerView foodGridView;

    private String[] images = new String[]{
            "https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/11/04/15/29/coffee-beans-5712780_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/03/08/21/41/landscape-4913841_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/09/02/18/03/girl-5539094_1280.jpg"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        context = container.getContext();

        viewpager_slider = view.findViewById(R.id.viewpager_slider);
        layout_indicator = view.findViewById(R.id.layout_indicators);
        foodGridView = view.findViewById(R.id.foodGridView);

        //ViewPager
        viewpager_slider.setOffscreenPageLimit(1);
        viewpager_slider.setAdapter(new ImageSliderAdapter(context, images));
        viewpager_slider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
        setupIndicators(images.length);

        //맛집 리스트
        FoodResponse();

        return view;
    }

    //인디케이터 설정
    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(context);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.layout_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layout_indicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    //현재 인디케이터 설정
    private void setCurrentIndicator(int position) {
        int childCount = layout_indicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layout_indicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.layout_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.layout_indicator_inactive));
            }
        }
    }

    //맛집 리스트 조회
    public void FoodResponse() {
        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance(null);
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        //FoodResponse에 저장된 데이터와 함께 RetrofitInterface에서 정의한 getFoodSesponse 함수를 실행한 후 응답을 받음
        retrofitInterface.getFoodResponse(0).enqueue(new Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {

                //통신 성공
                if (response.isSuccessful() && response.body() != null) {

                    //response.body()를 result에 저장
                    FoodResponse result = response.body();

                    //받은 코드 저장
                    String resultCode = result.getStatus().toString();

                    //맛집 조회 성공
                    String success = "0";

                    if (resultCode.equals(success)) {
                        String id = result.getId();
                        String dateTime = result.getDateTime();
                        Integer status = result.getStatus();
                        String message = result.getMessage();
                        List<FoodResponse.FoodList> foodList = result.getList();

                        Log.d("food", "맛집 리스트\n" +
                                "Id: " + id + "\n" +
                                "dateTime: " + dateTime + "\n" +
                                "status: " + status + "\n" +
                                "message: " + message + "\n"
                        );

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
                        foodGridView.setLayoutManager(gridLayoutManager);

                        FoodListAdapter foodListAdapter = new FoodListAdapter();
                        for (FoodResponse.FoodList food : foodList) {
                            Log.d("food", "맛집 리스트\n" +
                                    "name: " + food.getName() + "\n" +
                                    "placeType: " + food.getPlaceType() + "\n" +
                                    "category: " + food.getCategory() + "\n" +
                                    "address: " + food.getAddress() + "\n" +
                                    "imagesUrl: " + food.getImagesUrl() + "\n" +
                                    "totalGrade: " + food.getTotalGrade() + "\n" +
                                    "xcoordinate: " + food.getXcoordinate() + "\n" +
                                    "ycoordinate: " + food.getYcoordinate() + "\n"
                            );
                            foodListAdapter.addItem(new FoodListItem(food.getName(), food.getPlaceType(), food.getCategory(), food.getAddress(), food.getImagesUrl().toString(), food.getTotalGrade().toString(), food.getXcoordinate(), food.getYcoordinate()));
                        }
                        foodGridView.setAdapter(foodListAdapter);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("알림")
                                .setMessage("맛집 조회 예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                                .setPositiveButton("확인", null)
                                .create()
                                .show();
                    }
                }
            }

            //통신 실패
            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("알림")
                        .setMessage("통신실패 예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        });
    }
}
