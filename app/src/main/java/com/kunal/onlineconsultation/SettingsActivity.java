package com.kunal.onlineconsultation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
Button change_language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        change_language = findViewById(R.id.change_language);
        change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
    }

    private void showChangeLanguageDialog() {
        final String[] items = {"English","हिन्दी","ગુજરાતી","मराठी","বাংলা","اردو","தமிழ்","తెలుగు","ਪੰਜਾਬੀ","മലയാളം"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Choose Language... ");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    if (which == 0) {
                        setLocale("en");
                        recreate();
                    }
                    else if (which == 1){
                        setLocale("hi");
                        recreate();
                    }
                    else if (which == 2){
                        setLocale("gu");
                        recreate();
                    }
                    else if (which == 3){
                        setLocale("mr");
                        recreate();
                    }
                    else if (which == 4){
                        setLocale("bn");
                        recreate();
                    }
                    else if (which == 5){
                        setLocale("ur");
                        recreate();
                    }
                    else if (which == 6){
                        setLocale("ta");
                        recreate();
                    }
                    else if (which == 7){
                        setLocale("te");
                        recreate();
                    }
                    else if (which == 8){
                        setLocale("pa");
                        recreate();
                    }
                    else if (which == 9){
                        setLocale("ml");
                        recreate();
                    }
                    dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setLocale(String s) {
        Locale locale = new Locale(s);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",s);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = preferences.getString("My_Lang","");
        setLocale(lang);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
