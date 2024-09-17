package com.valobasa.moja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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




import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusActivity extends AppCompatActivity {


    public static ArrayList<HashMap<String, String>> statusList = new ArrayList<>();
    Toolbar toolbar;
    RecyclerView recyclerview;
    StatusAdapter statusAdapter;

    CustomPref customPref;
    MediaPlayer mediaPlayer;



    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f;
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
        toolbar = findViewById(R.id.toolbar);
        customPref = new CustomPref(StatusActivity.this);


        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        statusAdapter = new StatusAdapter(StatusActivity.this, new Onclick() {
            @Override
            public void CategoryOnclick(String category_title, int position) {

            }

            @Override
            public void StatusOnclick() {

            }
        });


        recyclerview = findViewById(R.id.recyclerview);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Screen is in portrait mode
            recyclerview.setLayoutManager(new GridLayoutManager(StatusActivity.this, 1));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Screen is in landscape mode
            recyclerview.setLayoutManager(new GridLayoutManager(StatusActivity.this, 2));
        }
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(statusAdapter);




    }





    public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder>{
        Context context;

        Onclick onclick;
        public StatusAdapter(Context context, Onclick onclick) {
            this.context = context;
            this.onclick = onclick;
        }

        @NonNull
        @Override
        public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            HashMap<String, String> status = statusList.get(position);
            holder.status_text.setText(status.get("status"));
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_from_left);
            holder.cardMain.startAnimation(animation);

            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            ContentValues fav = new ContentValues();

            if (databaseHelper.getFavouriteById(status.get("id"))){
                holder.like.setImageResource(R.drawable.ic_liked);}
            else {holder.like.setImageResource(R.drawable.ic_like);}


            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.click);
                    if (databaseHelper.getFavouriteById(status.get("id"))){
                        databaseHelper.removeFavouriteById(status.get("id"));
                        holder.like.setImageResource(R.drawable.ic_like);
                        Toast.makeText(context, "Unlike Success..", Toast.LENGTH_SHORT).show();
                    }else {
                        fav.put(DatabaseHelper.KEY_ID, status.get("id"));
                        fav.put(DatabaseHelper.KEY_TITLE, status.get("status"));
                        databaseHelper.addFavourite(fav, null);
                        holder.like.setImageResource(R.drawable.ic_liked);
                        Toast.makeText(context, "Like Successful", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            holder.copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.click);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("status", status.get("status"));
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                }
            });

            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_TEXT, status.get("status"));
                    startActivity(Intent.createChooser(i, "Share Text Status"));

                }
            });


        }

        @Override
        public int getItemCount() {
            return statusList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView like, copy, share;
            TextView status_text;
            CardView cardMain;

            CircleImageView applogo;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                like = itemView.findViewById(R.id.btnLike);
                copy = itemView.findViewById(R.id.btnCopy);
                share = itemView.findViewById(R.id.btnShare);
                status_text = itemView.findViewById(R.id.status_text);
                cardMain = itemView.findViewById(R.id.cardMain);
                applogo = itemView.findViewById(R.id.appLogo);
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
        //startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}