package com.udoolleh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity{
    public static Context context;
    Toolbar toolbar;
    ImageView  map_img, food_img, main_img, tour_img, board_img;
    FloatingActionButton addBoardBtn;
    private final int MapFragment = 1;
    private final int FoodFragment = 2;
    private final int MainFragment = 3;
    private final int TourFragment = 4;
    private final int BoardFragment = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        addBoardBtn = findViewById(R.id.addBoardBtn);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("");
        toolBarLayout.setCollapsedTitleTextColor(Color.alpha(0));
        toolBarLayout.setExpandedTitleColor(Color.alpha(0));

        //네비게이션 메뉴
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Toast.makeText(getApplicationContext(), "home", Toast.LENGTH_SHORT).show();

                    case R.id.nav_setting:
                        Toast.makeText(getApplicationContext(), "setting", Toast.LENGTH_SHORT).show();

                    case R.id.nav_example:
                        Toast.makeText(getApplicationContext(), "example", Toast.LENGTH_SHORT).show();
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });

        map_img = findViewById(R.id.map_img);
        food_img = findViewById(R.id.food_img);
        main_img = findViewById(R.id.main_img);
        tour_img = findViewById(R.id.tour_img);
        board_img = findViewById(R.id.board_img);

        FragmentView(MainFragment);
        map_img.setImageResource((R.drawable.category_map));
        food_img.setImageResource((R.drawable.category_food));
        main_img.setImageResource(R.drawable.category_main_selected);
        tour_img.setImageResource((R.drawable.category_tour));
        board_img.setImageResource((R.drawable.category_board));


        LinearLayout map_fragment = findViewById(R.id.map_fragment);
        map_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(MapFragment);
                map_img.setImageResource((R.drawable.category_map_selected));
                food_img.setImageResource((R.drawable.category_food));
                main_img.setImageResource(R.drawable.category_main);
                tour_img.setImageResource((R.drawable.category_tour));
                board_img.setImageResource((R.drawable.category_board));
            }
        });

        LinearLayout food_fragment = findViewById(R.id.food_fragment);
        food_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(FoodFragment);
                map_img.setImageResource((R.drawable.category_map));
                food_img.setImageResource((R.drawable.category_food_selected));
                main_img.setImageResource(R.drawable.category_main);
                tour_img.setImageResource((R.drawable.category_tour));
                board_img.setImageResource((R.drawable.category_board));
            }
        });

        LinearLayout main_fragment = findViewById(R.id.main_fragment);
        main_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(MainFragment);
                map_img.setImageResource((R.drawable.category_map));
                food_img.setImageResource((R.drawable.category_food));
                main_img.setImageResource(R.drawable.category_main_selected);
                tour_img.setImageResource((R.drawable.category_tour));
                board_img.setImageResource((R.drawable.category_board));
            }
        });

        LinearLayout tour_fragment = findViewById(R.id.tour_fragment);
        tour_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(TourFragment);
                map_img.setImageResource((R.drawable.category_map));
                food_img.setImageResource((R.drawable.category_food));
                main_img.setImageResource(R.drawable.category_main);
                tour_img.setImageResource((R.drawable.category_tour_selected));
                board_img.setImageResource((R.drawable.category_board));
            }
        });

        LinearLayout board_fragment = findViewById(R.id.board_fragment);
        board_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(BoardFragment);
                map_img.setImageResource((R.drawable.category_map));
                food_img.setImageResource((R.drawable.category_food));
                main_img.setImageResource(R.drawable.category_main);
                tour_img.setImageResource((R.drawable.category_tour));
                board_img.setImageResource((R.drawable.category_board_selected));
            }
        });

        //게시판 작성
        addBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BoardWrite.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case R.id.drawer:
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                }
                else if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.openDrawer(GravityCompat.END);
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void FragmentView(int fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment) {
            case 1:
                addBoardBtn.setVisibility(View.GONE);
                MapFragment mapFragment = new MapFragment();
                transaction.replace(R.id.fragment_container, mapFragment);
                transaction.commit();
                break;

            case 2:
                addBoardBtn.setVisibility(View.GONE);
                FoodFragment foodFragment = new FoodFragment();
                transaction.replace(R.id.fragment_container, foodFragment);
                transaction.commit();
                break;

            case 3:
                addBoardBtn.setVisibility(View.GONE);
                MainFragment mainFragment = new MainFragment();
                transaction.replace(R.id.fragment_container, mainFragment);
                transaction.commit();
                break;

            case 4:
                addBoardBtn.setVisibility(View.GONE);
                TourFragment tourFragment = new TourFragment();
                transaction.replace(R.id.fragment_container, tourFragment);
                transaction.commit();
                break;

            case 5:
                addBoardBtn.setVisibility(View.VISIBLE);
                BoardFragment boardFragment = new BoardFragment();
                transaction.replace(R.id.fragment_container, boardFragment);
                transaction.commit();
                break;
        }
    }
}