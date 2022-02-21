package app.manager.dating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.manager.dating.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import app.manager.dating.app.App;
import app.manager.dating.common.ActivityBase;

public class MainActivity extends ActivityBase {

    private AppBarLayout mAppBarLayout;

    private Menu mNavMenu;

    Toolbar mToolbar;

    private NavigationView mNavView;
    private DrawerLayout mDrawerLayout;

    private CardView mSearchBar;

    private View mNavHeaderLayout;

    private TextView mNavHeaderFullname, mNavHeaderUsername;
    private CircularImageView mNavHeaderPhoto, mNavHeaderIcon;
    private ImageView mNavHeaderCover;

    // used to store app title
    private CharSequence mTitle;

    Fragment fragment;
    Boolean action = false;
    int page = 0;

    private Boolean isSearchBarHide = false;

    private int pageId = PAGE_MEDIA_STREAM;

    private Boolean restore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get intent data

        Intent i = getIntent();

        pageId = i.getIntExtra("pageId", PAGE_PROFILE_PHOTOS_MODERATION);

        if (savedInstanceState != null) {

            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");
            mTitle = savedInstanceState.getString("mTitle");

        } else {

            //fragment = new Fragment();

            restore = false;
            mTitle = getString(R.string.app_name);
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
        }

        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mTitle);

        //

        mAppBarLayout = findViewById(R.id.appbar_layout);

        //

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSearchBar = (CardView) findViewById(R.id.search_bar);

        initDrawerMenu();


        if (!restore) {

            switch (pageId) {

                case PAGE_PROFILE_COVERS_MODERATION: {

                    displayFragment(mNavMenu.findItem(R.id.nav_profile_covers_moderation).getItemId(), mNavMenu.findItem(R.id.nav_profile_photos_moderation).getTitle().toString());

                    break;
                }

                case PAGE_MEDIA_ITEMS_MODERATION: {

                    displayFragment(mNavMenu.findItem(R.id.nav_media_items_moderation).getItemId(), mNavMenu.findItem(R.id.nav_media_items_moderation).getTitle().toString());

                    break;
                }

                default: {

                    // Show default section "Photos Moderation"

                    displayFragment(mNavMenu.findItem(R.id.nav_profile_photos_moderation).getItemId(), mNavMenu.findItem(R.id.nav_profile_photos_moderation).getTitle().toString());

                    break;
                }
            }
        }
    }

    private void initDrawerMenu() {

        mNavView = (NavigationView) findViewById(R.id.nav_view);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.nav_view_open, R.string.nav_view_close) {

            public void onDrawerOpened(View drawerView) {

                refreshMenu();

                hideKeyboard();

                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {

                displayFragment(menuItem.getItemId(), menuItem.getTitle().toString());

                mAppBarLayout.setExpanded(true, true);

                mDrawerLayout.closeDrawers();

                return true;
            }
        });

        mNavMenu = mNavView.getMenu();

        mNavView.setItemIconTintList(getResources().getColorStateList(R.color.nav_state_list));

        mNavHeaderLayout = mNavView.getHeaderView(0);
        mNavHeaderFullname = (TextView) mNavHeaderLayout.findViewById(R.id.userFullname);
        mNavHeaderUsername = (TextView) mNavHeaderLayout.findViewById(R.id.userUsername);

        mNavHeaderPhoto = (CircularImageView) mNavHeaderLayout.findViewById(R.id.userPhoto);
        mNavHeaderIcon = (CircularImageView) mNavHeaderLayout.findViewById(R.id.verified);
        mNavHeaderCover = (ImageView) mNavHeaderLayout.findViewById(R.id.userCover);
    }

    private void refreshMenu() {

        if (App.getInstance().getVerify() == 1) {

            mNavHeaderIcon.setVisibility(View.VISIBLE);

        } else {

            mNavHeaderIcon.setVisibility(View.GONE);
        }

        if (App.getInstance().getId() > 0) {

            updateNavItemCounter(mNavView, R.id.nav_profile_photos_moderation, App.getInstance().getNewProfilePhotosCount());
            updateNavItemCounter(mNavView, R.id.nav_profile_covers_moderation, App.getInstance().getNewProfileCoversCount());
            updateNavItemCounter(mNavView, R.id.nav_media_items_moderation, App.getInstance().getNewMediaItemsCount());

            mNavHeaderCover.setScaleType(ImageView.ScaleType.CENTER_CROP);

            mNavHeaderFullname.setVisibility(View.VISIBLE);
            mNavHeaderUsername.setVisibility(View.VISIBLE);
            mNavHeaderPhoto.setVisibility(View.VISIBLE);

            mNavHeaderFullname.setText(App.getInstance().getFullname());
            mNavHeaderUsername.setText("@" + App.getInstance().getUsername());

            if (App.getInstance().getPhotoUrl() != null && App.getInstance().getPhotoUrl().length() > 0) {

                ImageLoader imageLoader = App.getInstance().getImageLoader();

                imageLoader.get(App.getInstance().getPhotoUrl(), ImageLoader.getImageListener(mNavHeaderPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

            } else {

                mNavHeaderPhoto.setImageResource(R.drawable.profile_default_photo);
            }

            if (App.getInstance().getCoverUrl() != null && App.getInstance().getCoverUrl().length() > 0) {

                ImageLoader imageLoader = App.getInstance().getImageLoader();

                imageLoader.get(App.getInstance().getCoverUrl(), ImageLoader.getImageListener(mNavHeaderCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));

            } else {

                mNavHeaderCover.setImageResource(R.drawable.profile_default_cover);
            }

        } else {

            mNavHeaderFullname.setVisibility(View.GONE);
            mNavHeaderUsername.setVisibility(View.GONE);
            mNavHeaderPhoto.setVisibility(View.GONE);

            mNavHeaderCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mNavHeaderCover.setImageResource(R.drawable.profile_default_cover);
        }
    }

    private void updateNavItemCounter(NavigationView nav, @IdRes int itemId, int count) {

        TextView view = (TextView) nav.getMenu().findItem(itemId).getActionView().findViewById(R.id.counter);
        view.setText(String.valueOf(count));

        if (count <= 0) {

            view.setVisibility(View.GONE);

        } else {

            view.setVisibility(View.VISIBLE);
        }
    }

    private void displayFragment(int id, String title) {

        action = false;

        switch (id) {

            case R.id.nav_profile_photos_moderation: {

                page = PAGE_PROFILE_PHOTOS_MODERATION;

                fragment = new StreamProfilePhotosFragment(0);
                getSupportActionBar().setTitle(R.string.page_100);

                action = true;

                break;
            }

            case R.id.nav_profile_covers_moderation: {

                page = PAGE_PROFILE_COVERS_MODERATION;

                fragment = new StreamProfilePhotosFragment(1);
                getSupportActionBar().setTitle(R.string.page_101);

                action = true;

                break;
            }

            case R.id.nav_media_items_moderation: {

                page = PAGE_MEDIA_ITEMS_MODERATION;

                fragment = new StreamMediaItemsFragment();
                getSupportActionBar().setTitle(R.string.page_102);

                action = true;

                break;
            }

            case R.id.nav_settings: {

                page = PAGE_SETTINGS;

                fragment = new SettingsFragment();
                getSupportActionBar().setTitle(R.string.page_12);

                action = true;

                break;
            }
        }

        if (action && fragment != null) {

            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(title);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
        }
    }

    private void hideKeyboard() {

        View view = this.getCurrentFocus();

        if (view != null) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putString("mTitle", getSupportActionBar().getTitle().toString());
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onResume() {

        super.onResume();

        refreshMenu();

        registerReceiver(mMessageReceiver, new IntentFilter(TAG_UPDATE_BADGES));
    }

    @Override
    public void onPause() {

        super.onPause();

        unregisterReceiver(mMessageReceiver);
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            // String message = intent.getStringExtra("message");

            refreshMenu();
        }
    };

    public void animateSearchBar(final boolean hide) {

        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;

        isSearchBarHide = hide;

        int moveY = hide ? -(2 * mSearchBar.getHeight()) : 0;
        mSearchBar.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }
}
