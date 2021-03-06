package org.nearbyshops.enduserappnew;


import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import org.nearbyshops.enduserappnew.Carts.CartsList.CartsListFragment;
import org.nearbyshops.enduserappnew.Interfaces.LocationUpdated;
import org.nearbyshops.enduserappnew.Interfaces.NotifySearch;
import org.nearbyshops.enduserappnew.Interfaces.ShowFragment;
import org.nearbyshops.enduserappnew.ItemsByCategoryTypeSimple.Interfaces.NotifyBackPressed;
import org.nearbyshops.enduserappnew.ItemsByCategoryTypeSimple.ItemCategoriesFragmentSimple;
import org.nearbyshops.enduserappnew.Login.NotifyAboutLogin;
import org.nearbyshops.enduserappnew.LoginPlaceholder.FragmentSignInMessage;
import org.nearbyshops.enduserappnew.Markets.Interfaces.MarketSelected;
import org.nearbyshops.enduserappnew.Markets.MarketsFragmentNew;
import org.nearbyshops.enduserappnew.OneSignal.PrefOneSignal;
import org.nearbyshops.enduserappnew.OneSignal.UpdateOneSignalID;
import org.nearbyshops.enduserappnew.OrderHistoryNew.OrdersFragmentNew;
import org.nearbyshops.enduserappnew.Preferences.PrefGeneral;
import org.nearbyshops.enduserappnew.Preferences.PrefLocation;
import org.nearbyshops.enduserappnew.Preferences.PrefServiceConfig;
import org.nearbyshops.enduserappnew.Markets.MarketsFragment;
import org.nearbyshops.enduserappnew.Services.UpdateServiceConfiguration;
import org.nearbyshops.enduserappnew.Shops.ListFragment.FragmentShopNew;
import org.nearbyshops.enduserappnew.TabProfile.ProfileFragment;
import org.nearbyshops.enduserappnew.Preferences.PrefLogin;


public class Home extends AppCompatActivity implements ShowFragment, NotifyAboutLogin, MarketSelected {


    public static final String TAG_LOGIN = "tag_login";
    public static final String TAG_PROFILE = "tag_profile_fragment";

    public static final String TAG_ITEMS_FRAGMENT = "tag_items_fragment";
    public static final String TAG_SHOPS_FRAGMENT = "tag_shops_fragment";
    public static final String TAG_CARTS_FRAGMENT = "tag_carts_fragment";
    public static final String TAG_ORDERS_FRAGMENT = "tag_orders_fragment";

    public static final String TAG_MARKET_FRAGMENT = "tag_market_fragment";


    private static final int REQUEST_CHECK_SETTINGS = 100;


    BottomBar bottomBar;



    LocationManager locationManager;
    LocationListener locationListener;

    // fragments
    ItemCategoriesFragmentSimple itemsFragment;


    boolean isFirstLaunch = true;


    public Home() {

        DaggerComponentBuilder.getInstance()
                .getNetComponent()
                .Inject(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
//        bottomBar.setDefaultTab(R.id.tab_search);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        if (PrefGeneral.getMultiMarketMode(this)) {
            bottomBar.getTabWithId(R.id.tab_profile).setTitle("Markets");
        } else {
            bottomBar.getTabWithId(R.id.tab_profile).setTitle("Profile");
        }






        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {

            @Override
            public void onTabSelected(@IdRes int tabId) {

//
//                if(tabId==R.id.tab_local)
//                {
//                    showLocal();
//                }
//                else


                if (tabId == R.id.tab_items) {
                    showItemsFragment();
                } else if (tabId == R.id.tab_shops) {

//                    bottomBar.getTabWithId(R.id.tab_).setBadgeCount(0);
//                    PrefBadgeCount.saveBadgeCountTripRequests(0,HomeNew.this);

                    showShopsFragment();
                } else if (tabId == R.id.tab_cart) {
//                    bottomBar.getTabWithId(R.id.tab_trips).setBadgeCount(0);
//                    PrefBadgeCount.saveBadgeCountCurrentTrips(0,HomeNew.this);

                    showCartFragment();
                } else if (tabId == R.id.tab_orders) {
//                    bottomBar.getTabWithId(R.id.tab_trips).setBadgeCount(0);
//                    PrefBadgeCount.saveBadgeCountCurrentTrips(0,HomeNew.this);

                    showOrdersFragment();
                } else if (tabId == R.id.tab_profile) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.


//                    bottomBar.getTabWithId(R.id.tab_requests).setBadgeCount(2);
                    showProfileFragment();
                }
            }
        });


        int screenToOpen = getIntent().getIntExtra("screen_to_open", -1);


        if (screenToOpen == 1) {
            bottomBar.selectTabWithId(R.id.tab_items);
        } else if (screenToOpen == 2) {
            bottomBar.selectTabWithId(R.id.tab_shops);
        } else if (screenToOpen == 3) {
            bottomBar.selectTabWithId(R.id.tab_cart);
        } else if (screenToOpen == 4) {

            bottomBar.selectTabWithId(R.id.tab_orders);
        } else if (screenToOpen == 5) {

            bottomBar.selectTabWithId(R.id.tab_profile);
        }


//        startSettingsCheck();

        checkPermissions();

        fetchLocation();


        if (PrefGeneral.getServiceURL(this) != null) {
            if (PrefOneSignal.getToken(this) != null) {

                startService(new Intent(getApplicationContext(), UpdateOneSignalID.class));

//                showToastMessage("Update One Signal ID !");
            }
        }


        if (PrefServiceConfig.getServiceConfigLocal(this) == null && PrefGeneral.getServiceURL(this) != null) {
            // get service configuration when its null ... fetches config at first install or changing service
            startService(new Intent(getApplicationContext(), UpdateServiceConfiguration.class));
        }



        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if(fragment instanceof MarketsFragmentNew)
                {
                    bottomBar.selectTabWithId(R.id.tab_profile);
                }
                else if(fragment instanceof ItemCategoriesFragmentSimple)
                {
//                    bottomBar.selectTabWithId(R.id.tab_items);
                    BottomBarTab tab = bottomBar.getTabWithId(R.id.tab_items);

                    bottomBar.removeOnTabSelectListener();


                }
            }
        });

    }


    void showLogMessage(String message) {
        Log.d("log_home_screen", message);
    }


    @Override
    public void loginSuccess() {

//        showProfileFragment();
//        bottomBar.getTabWithId(R.id.tab_profile).setTitle("Profile");


//        bottomBar.selectTabWithId(R.id.tab_profile);
        marketSelected();
    }


    @Override
    public void loggedOut() {
//        bottomBar.selectTabWithId(R.id.tab_profile);

//        bottomBar.getTabWithId(R.id.tab_profile).setTitle("Markets");
        showProfileFragment();
    }


    @Override
    public void showLoginFragment() {

        if (getSupportFragmentManager().findFragmentByTag(TAG_LOGIN) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FragmentSignInMessage(), TAG_LOGIN)
                    .commit();
        }
    }


    void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            //
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    2);
            return;
        }


//        fetchLocation();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //
//        if(requestCode==2)
//        {
//            // If request is cancelled, the result arrays are empty.
//
//
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            //                startService(new Intent(this,LocationUpdateServiceLocal.class));

            showToastMessage("Permission Granted !");

//            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_ITEMS_FRAGMENT);


            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);


            if (fragment instanceof LocationUpdated) {
                ((LocationUpdated) fragment).permissionGranted();
            }


            fetchLocation();

        } else {
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            showToastMessage("Permission Rejected");
        }


    }


    @Override
    public void showProfileFragment() {

        if (PrefGeneral.getMultiMarketMode(this)) {
            // no market selected therefore show available markets in users area
//            if (getSupportFragmentManager().findFragmentByTag(TAG_MARKET_FRAGMENT) == null) {
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, new MarketsFragmentNew(), TAG_MARKET_FRAGMENT)
//                        .addToBackStack(null)
//                        .commit();
//            }


            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MarketsFragmentNew(), TAG_MARKET_FRAGMENT)
                    .addToBackStack(null)
                    .commit();


        } else {
            // single market mode

            if (PrefLogin.getUser(getBaseContext()) == null) {


                showLoginFragment();

            } else if (getSupportFragmentManager().findFragmentByTag(TAG_PROFILE) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment(), TAG_PROFILE)
                        .commit();
            }

        }

    }








    @Override
    public void showOrdersFragment() {




        if (PrefGeneral.getMultiMarketMode(this) && PrefGeneral.getServiceURL(this) == null) {
            // no market selected therefore show available markets in users area
            if (getSupportFragmentManager().findFragmentByTag(TAG_MARKET_FRAGMENT) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MarketsFragmentNew(), TAG_MARKET_FRAGMENT)
                        .commit();

            }

        } else if (PrefLogin.getUser(getBaseContext()) == null) {

            showLoginFragment();

            return;
        } else if (getSupportFragmentManager().findFragmentByTag(TAG_ORDERS_FRAGMENT) == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrdersFragmentNew(), TAG_ORDERS_FRAGMENT)
                    .commit();
        }
    }


    @Override
    public void showCartFragment() {


        if (PrefGeneral.getMultiMarketMode(this) && PrefGeneral.getServiceURL(this) == null) {
            // no market selected therefore show available markets in users area
            if (getSupportFragmentManager().findFragmentByTag(TAG_MARKET_FRAGMENT) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MarketsFragmentNew(), TAG_MARKET_FRAGMENT)
                        .commit();

            }

        } else if (PrefLogin.getUser(getBaseContext()) == null) {

            showLoginFragment();
            return;

        } else if (getSupportFragmentManager().findFragmentByTag(TAG_CARTS_FRAGMENT) == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CartsListFragment(), TAG_CARTS_FRAGMENT)
                    .commit();
        }
    }


    @Override
    public void showShopsFragment() {


        if (PrefGeneral.getMultiMarketMode(this) && PrefGeneral.getServiceURL(this) == null) {
            // no market selected therefore show available markets in users area
            if (getSupportFragmentManager().findFragmentByTag(TAG_MARKET_FRAGMENT) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MarketsFragmentNew(), TAG_MARKET_FRAGMENT)
                        .commit();

            }

        } else {
            if (getSupportFragmentManager().findFragmentByTag(TAG_SHOPS_FRAGMENT) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, FragmentShopNew.newInstance(false), TAG_SHOPS_FRAGMENT)
                        .commit();
            }

        }

    }


    @Override
    public void showItemsFragment() {

        showToast("Show Items Triggered !");


        if (PrefGeneral.getMultiMarketMode(this) && PrefGeneral.getServiceURL(this) == null) {
            // no market selected therefore show available markets in users area
            if (getSupportFragmentManager().findFragmentByTag(TAG_MARKET_FRAGMENT) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MarketsFragmentNew(), TAG_MARKET_FRAGMENT)
                        .commit();

            }


        } else {

            if (getSupportFragmentManager().findFragmentByTag(TAG_ITEMS_FRAGMENT) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ItemCategoriesFragmentSimple(), TAG_ITEMS_FRAGMENT)
                        .commit();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }


    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    boolean isDestroyed = false;


    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(TAG_ITEMS_FRAGMENT);


        if (fragment == null) {
            fragment = getSupportFragmentManager()
                    .findFragmentByTag(TAG_SHOPS_FRAGMENT);
        }

        //notifyBackPressed !=null

        if (fragment instanceof NotifyBackPressed) {
//            showLogMessage("Fragment Instanceof NotifyBackPressed !");

            if (((NotifyBackPressed) fragment).backPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items_by_cat_simple, menu);


        // Associate searchable configuration with the SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        MenuItem item = menu.findItem(R.id.action_search);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (fragment instanceof NotifySearch) {
                    ((NotifySearch) fragment).endSearchMode();
                }

//                Toast.makeText(Home.this, "onCollapsed Called ", Toast.LENGTH_SHORT).show();

                return true;
            }
        });


        return true;
    }


    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

//            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();


            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);


            if (fragment instanceof NotifySearch) {
                ((NotifySearch) fragment).search(query);
            }
        }
    }


    @Override
    public void marketSelected() {

//            bottomBar.selectTabWithId(R.id.tab_items);
//            bottomBar.selectTabAtPosition(bottomBar.getCurrentTabPosition());
//            showItemsFragment();


//        showToastMessage("Market Selected : Home ");

        int tabId = bottomBar.getCurrentTabId();


        if (tabId == R.id.tab_items) {
            showItemsFragment();
        } else if (tabId == R.id.tab_shops) {

            showShopsFragment();
        } else if (tabId == R.id.tab_cart) {

            showCartFragment();
        } else if (tabId == R.id.tab_orders) {

            showOrdersFragment();
        } else if (tabId == R.id.tab_profile) {

//                showProfileFragment();
//                showItemsFragment();
            bottomBar.selectTabWithId(R.id.tab_items);

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }






    void fetchLocation() {

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                saveLocation(location);
                stopLocationUpdates();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


            // Register the listener with the Location Manager to receive location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }


            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);



            if(location==null)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100, locationListener);
            }
            else
            {
                saveLocation(location);
            }

    }




    void saveLocation(Location location)
    {
        PrefLocation.saveLatitude((float) location.getLatitude(), Home.this);
        PrefLocation.saveLongitude((float) location.getLongitude(), Home.this);



        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof LocationUpdated) {
            ((LocationUpdated) fragment).permissionGranted();
        }
    }



    protected void stopLocationUpdates() {



//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(locationManager!=null && locationListener!=null)
        {
            locationManager.removeUpdates(locationListener);
        }

//        stopSelf();
    }




}
