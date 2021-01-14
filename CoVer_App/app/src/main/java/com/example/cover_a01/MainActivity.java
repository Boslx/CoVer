package com.example.cover_a01;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.cover_a01.data.api.CoVerApi;
import com.example.cover_a01.ui.homescreen.HomeFragment;
import com.example.cover_a01.ui.homescreen.TrackingFragment;
import com.example.cover_a01.ui.infection_status.InfectionRiskTesting;
import com.example.cover_a01.ui.infection_status.StatusInfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    private static MainActivity mainActivity;

    private AppBarConfiguration mAppBarConfiguration;
    private CoVerApi coverApi;
    private InfectionRiskTesting infectionRiskTesting;

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    private ConstraintLayout debugMenu;
    private ImageView debugLogo;

    private SharedPreferences preferences;

    boolean tracking = true; //muss eigentlich gesetzt werden, wenn
                            // tahtsächlichtracking initialisiert wird

    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String Height ="Height";
    public static final String Width ="Width";
    private int width;
    private int height;

    private Handler bgHandler;
    //delay until infection analysis is triggered automatically, default 10minutes = 600.000ms
    private int bgCheckDelay = App.getApp().getResources().getInteger(R.integer.bgCheckDelay);

    private BottomNavigationView navMenu;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        preferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        boolean trackingOn = preferences.getBoolean("trackingOn",true);
        //todo: enable/disable tracking

        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new StatusInfoFragment());
        fragmentList.add(new TrackingFragment());

        preferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        Toolbar topBar = findViewById(R.id.toolbar3);
        BottomNavigationView bottomView = findViewById(R.id.bottomNavigationView);
        switch (preferences.getString("Theme", "MODE_NIGHT_NO")){
            case "MODE_NIGHT_YES":
                bottomView.setBackgroundColor(getResources().getColor(R.color.cover_dark_grey));
                topBar.setBackgroundColor(getResources().getColor(R.color.cover_dark_grey));
                break;
            case "MODE_NIGHT_NO":
                bottomView.setBackgroundColor(getResources().getColor(R.color.blue_NICE));
                topBar.setBackgroundColor(getResources().getColor(R.color.blue_NICE));
                break;
        }

        pager = findViewById(R.id.pager);
        pagerAdapter = new SlidePageAdapter(getSupportFragmentManager(),fragmentList);

        pager.setAdapter(pagerAdapter);

        navMenu =  findViewById(R.id.bottomNavigationView);
        navMenu.setOnNavigationItemSelectedListener(navListener);

        debugMenu = findViewById(R.id.debugLayout);
        debugMenu.setVisibility(View.INVISIBLE);
        debugLogo = findViewById(R.id.image_Logo);
        debugLogo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toggleDebug();
            }
        });

        checkPermission();

        width=Resources.getSystem().getDisplayMetrics().widthPixels;
        height= Resources.getSystem().getDisplayMetrics().heightPixels;
        saveData();

        // TODO: Remove these two lines. We should not use this ThreadPolicy in the final Release
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        infectionRiskTesting = new InfectionRiskTesting();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navMenu.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        navMenu.setSelectedItemId(R.id.info);
                        break;
                    case 2:
                        navMenu.setSelectedItemId(R.id.settings);
                        break;
                }
            }
        });

        bgHandler = new Handler();
        // run a continuous check on Infection Analysis.
        bgHandler.postDelayed(bgCheckAnalysis,bgCheckDelay);


        mainActivity = this;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * background thread to continuously check Infection status
     */
    Runnable bgCheckAnalysis = new Runnable(){
        @Override
        public void run() {
            Analytics.getAnalytics().executeAnalysisAsync();
            bgHandler.postDelayed(bgCheckAnalysis,bgCheckDelay);
        }
    };

    /**
     * we need to make sure that both bluetooth and location permissions are granted for this app to work.
     */
    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    try{
                        if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("CoVer Tracing requires background location access");
                            builder.setMessage("Please grant background location access so this app can continuously detect other phones.");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @TargetApi(23)
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                            PERMISSION_REQUEST_BACKGROUND_LOCATION);
                                }
                            });
                            builder.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            PERMISSION_REQUEST_FINE_LOCATION);
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Missing Permission");
                    builder.setMessage("Without location access this App cannot function as intended. Please change Permissions via Settings menu");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
        }
        return false;
    }

    /**
     * todo : Ephids / UUID methods should be moved outside the MainActivity
     * Gets the EphIDs from the Backend
     */

    //todo: "openFAQ, openURL" needs to be moved out of the main Activity
    /**
     * method to open the website of Cover
     * calls on the openURL method
     */
    public void openFAQ(View v){
        openUrl("https://cover.<domain>/");
    }

    public void openAPI(View v){
        openUrl("https://api.cover.<domain>/index.html");
    }

    /**
     * Method that opens URLS in the device standard browser
     * @param url String of the HTTPS link of the website that is called
     */
    public void openUrl(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    public void testButton(View v){

    }

    public void toggleDebug(){
        //todo :toggle visibility of debug menu ( fragment_bluetooth )
        if(pager.getVisibility()==View.VISIBLE){
            pager.setVisibility(View.INVISIBLE);
        }else{
            pager.setVisibility(View.VISIBLE);
        }
        if(debugMenu.getVisibility()==View.VISIBLE){
            debugMenu.setVisibility(View.INVISIBLE);
        }else{
            debugMenu.setVisibility(View.VISIBLE);
        }
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Height, height);
        editor.putFloat(Width,width);
        double width75 =width*0.75;
        double height10 =height*0.1;
        editor.putFloat("Width75%",(float)width75);
        editor.putFloat("Height10%",(float)height10);
        editor.apply();
    }
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view_tag, fragment);
        transaction.commit();
    }
    //todo funktioniert noch nicht
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int selectedFragment = 0;

                    switch(item.getItemId()){
                        case R.id.home:
                            selectedFragment = 0;
                            break;
                        case R.id.info:
                            selectedFragment = 1;
                            break;
                        case R.id.settings:
                            selectedFragment = 2;
                            break;
                    }
                    pager.setCurrentItem(selectedFragment);
                    return true;
                }
            };
    public void populateList(){
        fragmentList.clear();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new StatusInfoFragment());
        fragmentList.add(new TrackingFragment());
        pagerAdapter = new SlidePageAdapter(getSupportFragmentManager(),fragmentList);

        pager.setAdapter(pagerAdapter);
    }
}