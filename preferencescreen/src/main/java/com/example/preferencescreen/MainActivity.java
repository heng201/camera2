package com.example.preferencescreen;

import android.preference.PreferenceActivity;

import android.os.Bundle;

public class MainActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        findPreference(R.xml.mypreferencescreen);
//        addPreferencesFromResource(R.xml.mypreferencescreen);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingFragment()).commit();

    }
}
