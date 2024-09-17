package com.valobasa.moja;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Liked extends AppCompatActivity {

    ArrayList<StatusModel> likedStatusList;
    Toolbar toolbar;
    RecyclerView recyclerview;
    LikedAdapter likedAdapter;
    CustomPref customPref;
    MediaPlayer mediaPlayer;
    DatabaseHelper databaseHelper;


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


        setContentView(R.layout.activity_status);


        customPref = new CustomPref(Liked.this);
        toolbar = findViewById(R.id.toolbar);
        setTitle("Liked Status");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseHelper = new DatabaseHelper(Liked.this);
        likedStatusList = new ArrayList<>();
        likedStatusList = databaseHelper.getFavourite();
        likedAdapter = new LikedAdapter(Liked.this, new Onclick() {
            @Override
            public void CategoryOnclick(String category_title, int position) {
            }

            @Override
            public void StatusOnclick() {
                mSound(R.raw.click);
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Screen is in portrait mode
            recyclerview.setLayoutManager(new GridLayoutManager(Liked.this, 1));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Screen is in landscape mode
            recyclerview.setLayoutManager(new GridLayoutManager(Liked.this, 2));
        }
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(likedAdapter);





    }


    public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.ViewHolder>{
        Context context;

        Onclick onclick;

        public LikedAdapter(Context context, Onclick onclick) {
            this.context = context;
            this.onclick = onclick;
        }

        @NonNull
        @Override
        public LikedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LikedAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            StatusModel statusModel = likedStatusList.get(position);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_from_left);
            holder.cardMain.startAnimation(animation);
            holder.status_text.setText(statusModel.getStatus());
            holder.like.setImageResource(R.drawable.ic_liked);
            DatabaseHelper databaseHelper = new DatabaseHelper(context);

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.click);
                    databaseHelper.removeFavouriteById(String.valueOf(statusModel.getId()));
                    likedStatusList.remove(position);
                    likedAdapter.notifyItemRemoved(position);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recreate();                        }
                    }, 500);
                    Toast.makeText(context, "Unlike Successful", Toast.LENGTH_SHORT).show();
                }
            });


            holder.copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.click);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("status", statusModel.getStatus());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                }
            });
            // Create a new file with a unique name
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_TEXT, statusModel.getStatus());
                    startActivity(Intent.createChooser(i, "Share Text Status"));

                }
            });
        }

        @Override
        public int getItemCount() {
            return likedStatusList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView like,copy, share;
            TextView status_text;
            CardView cardMain;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                like = itemView.findViewById(R.id.btnLike);
                copy = itemView.findViewById(R.id.btnCopy);
                share = itemView.findViewById(R.id.btnShare);
                cardMain = itemView.findViewById(R.id.cardMain);
                status_text = itemView.findViewById(R.id.status_text);
            }
        }
    }


    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    @Override
    public void onBackPressed() {
        mSound(R.raw.click);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
