package com.yangezhu.forumproject;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

public class SettingsActivity extends PreferenceActivity {

    private ListPreference list_preference_fontsize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            getListView().setBackgroundColor(Color.parseColor("#222222"));
            setTheme(R.style.ForumProjectNight);
            super.onCreate(savedInstanceState);
        }else{
            getListView().setBackgroundColor(Color.parseColor("#ffffff"));
            setTheme(R.style.ForumProjectDay);
            super.onCreate(savedInstanceState);
        }


        addPreferencesFromResource(R.xml.settings);
        load_settings();
    }

    @Override
    protected void onResume() {
        load_settings();
        super.onResume();
    }

    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            getListView().setBackgroundColor(Color.parseColor("#222222"));
            setTheme(R.style.ForumProjectNight);
        }else{
            getListView().setBackgroundColor(Color.parseColor("#ffffff"));
            setTheme(R.style.ForumProjectDay);
        }

        CheckBoxPreference chk_night_instance = (CheckBoxPreference)findPreference("NIGHT");
        chk_night_instance.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                boolean yes = (boolean) o;

                if (yes){
                    getListView().setBackgroundColor(Color.parseColor("#222222"));
                    setTheme(R.style.ForumProjectNight);
                    finish();
                    startActivity(getIntent());
                }else{
                    getListView().setBackgroundColor(Color.parseColor("#ffffff"));
                    setTheme(R.style.ForumProjectDay);
                    finish();
                    startActivity(getIntent());
                }

                return true;
            }
        });

//        ListPreference listPreference = (ListPreference) findPreference("ORIENTATION");
//        String orien = sp.getString("ORIENTATION", "false");
//        if ("Auto".equals(orien)){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
//
//        }else if ("Portrait".equals(orien)){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }else if ("Landscape".equals(orien)){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//        listPreference.setSummary(listPreference.getEntry());
//        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//
//                String items = (String)o;
//                if (preference.getKey().equals("ORIENTATION")){
//                    switch (items){
//                        case "Auto":
//                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
//                            break;
//                        case "Portrait":
//                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                            break;
//                        case "Landscape":
//                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                            break;
//                    }
//
//                    ListPreference listPrefs = (ListPreference) preference;
//                    listPrefs.setSummary(listPrefs.getEntries()[listPrefs.findIndexOfValue(items)]);
//                }
//
//                return true;
//            }
//        });

        list_preference_fontsize = (ListPreference) findPreference("FONT_SIZE");
        String font_size = sp.getString("FONT_SIZE", "false");
        if ("Small".equals(font_size)){
            //this.setTheme(R.style.CustomizedLight_small);
            //Toast.makeText(Settings.this, "CustomizedLight_small", Toast.LENGTH_SHORT).show();
        }else if ("Medium".equals(font_size)){
            //this.setTheme(R.style.CustomizedLight_middle);
            //Toast.makeText(Settings.this, "CustomizedLight_middle", Toast.LENGTH_SHORT).show();
        }else if ("Large".equals(font_size)){
            //this.setTheme(R.style.CustomizedLight_large);
            // Toast.makeText(Settings.this, "CustomizedLight_large", Toast.LENGTH_SHORT).show();
        }
        list_preference_fontsize.setSummary(list_preference_fontsize.getEntry());

        list_preference_fontsize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                String items = (String)o;
                if (preference.getKey().equals("FONT_SIZE")){
//                    switch (items){
//                        case "Small":
//                            Toast.makeText(Settings.this, "CustomizedLight_small", Toast.LENGTH_SHORT).show();
//                            //Settings.this.setTheme(R.style.CustomizedLight_small);
//                            break;
//                        case "Medium":
//                            Toast.makeText(Settings.this, "CustomizedLight_middle", Toast.LENGTH_SHORT).show();
//                            //Settings.this.setTheme(R.style.CustomizedLight_middle);
//                            break;
//                        case "Large":
//                            Toast.makeText(Settings.this, "CustomizedLight_large", Toast.LENGTH_SHORT).show();
//                            //Settings.this.setTheme(R.style.CustomizedLight_large);
//                            break;
//                    }

                    list_preference_fontsize.setSummary(list_preference_fontsize.getEntries()[list_preference_fontsize.findIndexOfValue(items)]);
                }

                return true;
            }
        });


        ListPreference list_Preference_news_location = (ListPreference) findPreference("NEWS_FROM_LOCATION");
        //String news_location = sp.getString("NEWS_FROM_LOCATION", "Winnipeg");

        list_Preference_news_location.setSummary(list_Preference_news_location.getEntry());
        list_Preference_news_location.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                String items = (String)o;
                if (preference.getKey().equals("NEWS_FROM_LOCATION")){

                    ListPreference listPrefs = (ListPreference) preference;
                    listPrefs.setSummary(listPrefs.getEntries()[listPrefs.findIndexOfValue(items)]);
                }

                return true;
            }
        });

    }




}

