package com.valobasa.moja;

import android.content.Context;
import android.content.SharedPreferences;

public class CustomPref {
    Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CustomPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public Boolean getSound() {
        return sharedPreferences.getBoolean("sound", true);
    }

    public void setSound(Boolean setSound) {
        editor.putBoolean("sound", setSound);
        editor.apply();
    }


    public Boolean getHint(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void setHint(String key,Boolean setHint) {
        editor.putBoolean(key, setHint);
        editor.apply();
    }





}
