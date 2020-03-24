package de.nivram710.crowd_stock_supermarket.ui.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import de.nivram710.crowd_stock_supermarket.MainActivity;
import de.nivram710.crowd_stock_supermarket.R;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnContinue;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // when this activity is about to be launched we need to check
        // whether it was opened before or not
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }

        setContentView(R.layout.activity_intro);

        // ini views
        btnContinue = findViewById(R.id.button_continue);
        tabIndicator = findViewById(R.id.tab_indicator);

        // fill list screen
        List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem(getString(R.string.app_name), getString(R.string.onboarding_message_1), R.drawable.ic_onboarding1));
        mList.add(new ScreenItem(getString(R.string.app_name), getString(R.string.onboarding_message_2), R.drawable.ic_onboarding2));
        mList.add(new ScreenItem(getString(R.string.app_name), getString(R.string.onboarding_message_3), R.drawable.ic_onboarding3));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // Continue button click Listener
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < 3) { // mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == 3 ) { // mList.size()) {
                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // open main activity
                            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(mainActivity);

                            // also we need to save a boolean value to storage so next time when the user runs the app
                            // we would know that he has already checked the intro screen activity
                            savePrefsData();
                        }
                    });
                }
            }
        });

        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isIntroOpened", false);
    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.apply();
    }

}
