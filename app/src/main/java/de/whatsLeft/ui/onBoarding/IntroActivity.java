package de.whatsLeft.ui.onBoarding;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import de.whatsLeft.MainActivity;
import de.whatsLeft.R;

/**
 * Activity for user on boarding
 * <p>Displayed data is manged by IntroViewPagerAdapter</p>
 *
 * @author Chris de Machaut
 * @version 1.1
 * @see IntroViewPagerAdapter
 * @since 1.0.0
 */
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
        mList.add(new ScreenItem(getString(R.string.on_boarding_title_1), getString(R.string.on_boarding_message_1), R.drawable.ic_on_boarding1));
        mList.add(new ScreenItem(getString(R.string.on_boarding_title_2), getString(R.string.on_boarding_message_2), R.drawable.ic_on_boarding2));
        mList.add(new ScreenItem(getString(R.string.on_boarding_title_3), getString(R.string.on_boarding_message_3), R.drawable.ic_on_boarding3));
        mList.add(new ScreenItem(getString(R.string.on_boarding_title_4), getString(R.string.on_boarding_message_4), R.drawable.ic_on_boarding4));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tab layout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // Continue button click Listener
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < 4) { // mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }
            }
        });

        // tab layout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 3) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        btnContinue.setText(getString(R.string.grant_access_location));

                        // check if the android system need to ask for location access permission
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            btnContinue.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    // ask for access fine location permission
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

                                    // return back to lets begin button
                                    createLetsBeginButton();
                                }
                            });
                        }
                    } else {
                        // if app already asked for permission just continue with lets begin button
                        createLetsBeginButton();
                    }
                } else {
                    btnContinue.setText(getString(R.string.button_continue));
                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            position = screenPager.getCurrentItem();
                            if (position < 4) { // mList.size()) {
                                position++;
                                screenPager.setCurrentItem(position);
                            }
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void createLetsBeginButton() {
        btnContinue.setText(getString(R.string.begin));
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

    /**
     * This method returns whether the on boarding screen has already been displayed
     *
     * @return isIntroOpened boolean; if true no need to display on boarding screen again
     * @since 1.0.0
     */
    private boolean restorePrefData() {
        // load sharedPreferences
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);

        // return saved boolean from sharedPreferences
        return pref.getBoolean("isIntroOpened", false);
    }

    /**
     * set the field isIntroOpened to true in sharedPreferences
     *
     * @since 1.0.0
     */
    private void savePrefsData() {

        // load sharedPreferences
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);

        // open sharedPreferences editor
        SharedPreferences.Editor editor = pref.edit();

        // store true in isIntroOpened to indicate that there is no need to display on boarding again
        editor.putBoolean("isIntroOpened", true);

        // save changes in sharedPreferences
        editor.apply();
    }

}
