package com.valobasa.moja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.BuildConfig;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    DrawerLayout drawerLayout;
    ImageView openMenu, liked;
    NavigationView navigationView;
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    CustomPref customPref;
    MediaPlayer mediaPlayer;
    ImageSlider imageSlider;
    ArrayList<SlideModel> imageList = new ArrayList<>();

    LinearLayout banner_container;



    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f; // 50% volume
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.drawer);


        imageSlider = findViewById(R.id.image_slider);
        imageList.add(new SlideModel(R.drawable.slide110,ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slide2,ScaleTypes.FIT));
        imageSlider.setImageList(imageList, ScaleTypes.FIT);


        AddItem.addItem();
        customPref = new CustomPref(MainActivity.this);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        openMenu = findViewById(R.id.openMenu);
        liked = findViewById(R.id.fav);






//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });





        openMenu.setOnClickListener(view -> {
            mSound(R.raw.click);

            if (!drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        liked.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),Liked.class));
            mSound(R.raw.click);
        });




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mSound(R.raw.click);
                switch (item.getItemId()){

                    case R.id.nev_fav:
                        startActivity(new Intent(getApplicationContext(), Liked.class));
                        break;


                    case R.id.share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                        break;

                    case R.id.retus:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ BuildConfig.APPLICATION_ID)));
                        break;

                    case R.id.Privacy:
                        String privacyPolicyUrl = "https://sites.google.com/view/mojarstatusapp/home";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
                        startActivity(intent);
                        break;

                    case R.id.more:

                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id="+getResources().getString(R.string.DeveloperID))));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id="+getResources().getString(R.string.DeveloperID))));
                        }
                        break;

                    /*case R.id.app1:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ getResources().getString(R.string.package_1))));
                        break;

                    case R.id.app2:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ getResources().getString(R.string.package_2))));
                        break;


                    case R.id.app3:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ getResources().getString(R.string.package_3))));
                        break;

                    case R.id.app4:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ getResources().getString(R.string.package_4))));
                        break;*/



                }
                return true;
            }
        });
        Switch mySwitch = (Switch) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.menu_switch));
        mySwitch.setChecked(customPref.getSound());
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSound(R.raw.click);
            customPref.setSound(isChecked);
            if (customPref.getSound()){
                mySwitch.setChecked(customPref.getSound());
                Toast.makeText(getApplicationContext(), "Sound ON", Toast.LENGTH_SHORT).show();
                mSound(R.raw.click);
            }else {
                mySwitch.setChecked(customPref.getSound());
                Toast.makeText(getApplicationContext(), "Sound OFF", Toast.LENGTH_SHORT).show();
            }
        });


        //searchView();



        categoryAdapter = new CategoryAdapter(AddItem.categoryArrayList, this, new Onclick() {
            @Override
            public void CategoryOnclick(String category_title, int position) {
                mSound(R.raw.click);
                StatusActivity.statusList = AddItem.statusArrayList.get(position);
                Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                intent.putExtra("title", category_title);
                startActivity(intent);

            }

            @Override
            public void StatusOnclick() {

            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(categoryAdapter);



    }





    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

        ArrayList<HashMap<String, String>> arrayList;

        Context context;
        Onclick onclick;
        int current = 0;

        public CategoryAdapter(ArrayList<HashMap<String, String>> arrayList, Context context, Onclick onclick) {
            this.arrayList = arrayList;
            this.context = context;
            this.onclick = onclick;
        }

        @NonNull
        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
            HashMap<String, String> mHashMap = arrayList.get(position);
            List<Integer> colors = new ArrayList<Integer>();
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_from_left);
            holder.category_card.startAnimation(animation);
            colors.add(context.getResources().getColor(R.color.color_1));
            colors.add(context.getResources().getColor(R.color.color_2));
            colors.add(context.getResources().getColor(R.color.color_3));
            colors.add(context.getResources().getColor(R.color.color_4));
            colors.add(context.getResources().getColor(R.color.color_5));
            colors.add(context.getResources().getColor(R.color.color_6));

            holder.category_title.setText(mHashMap.get("category_name"));
            holder.category_logo.setImageResource(Integer.parseInt(mHashMap.get("logo")));
            holder.category_card.setOnClickListener(view -> {
                onclick.CategoryOnclick(mHashMap.get("category_name"), position);
            });




        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView category_title;
            CardView category_card;
            ImageView category_logo;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                category_title = itemView.findViewById(R.id.category_title);
                category_card = itemView.findViewById(R.id.category_card);
                category_logo = itemView.findViewById(R.id.category_logo);




            }
        }


    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            finish();
        }
    }
}