package com.bilalbaloch.countries;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bilalbaloch.countries.Service.CountryService;

/**
 * @author bilalbaloch
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        loadCountryActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        startService(new Intent(LauncherActivity.this, CountryService.class));
    }

    public void loadCountryActivity() {
        startActivity(new Intent(LauncherActivity.this, CountryActivity.class));
    }
}