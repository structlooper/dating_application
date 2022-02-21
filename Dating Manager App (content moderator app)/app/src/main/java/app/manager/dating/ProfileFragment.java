package app.manager.dating;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.manager.dating.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.manager.dating.app.App;
import app.manager.dating.constants.Constants;
import app.manager.dating.model.Profile;
import app.manager.dating.util.CustomRequest;

public class ProfileFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private ProgressDialog pDialog;

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private static final int SELECT_PHOTO = 1;
    private static final int SELECT_COVER = 2;
    private static final int PROFILE_EDIT = 3;
    private static final int PROFILE_NEW_POST = 4;
    private static final int CREATE_PHOTO = 5;
    private static final int CREATE_COVER = 6;
    private static final int PROFILE_CHAT = 7;
    private static final int PROFILE_FEELINGS = 8;

    RelativeLayout mProfileLoadingScreen, mProfileErrorScreen, mProfileDisabledScreen;

    LinearLayout mLocationContainer;

    SwipeRefreshLayout mProfileRefreshLayout;
    NestedScrollView mNestedScrollView;

    CircularImageView mProfilePhoto, mProfileIcon, mProfileProIcon;
    ImageView mProfileCover, mProfileOnlineIcon;
    TextView mProfileLocation, mProfileFullname, mProfileUsername;

    Button mProfileActionBtn;

    Toolbar mToolbar;

    Profile profile;

    private Boolean loadingComplete = false;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;

    private String profile_mention;
    public long profile_id;
    int itemId = 0;
    int arrayLength = 0;
    int accessMode = 0;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;

    private Boolean isMainScreen = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();

        Intent i = getActivity().getIntent();

        profile_id = i.getLongExtra("profileId", 0);
        profile_mention = i.getStringExtra("profileMention");

        if (profile_id == 0 && (profile_mention == null || profile_mention.length() == 0)) {

            profile_id = App.getInstance().getId();
            isMainScreen = true;
        }

        profile = new Profile();
        profile.setId(profile_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (savedInstanceState != null) {

            itemId = savedInstanceState.getInt("itemId");

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");

            profile = savedInstanceState.getParcelable("profileObj");

        } else {

            itemId = 0;

            restore = false;
            loading = false;
            preload = false;
        }

        if (loading) {


            showpDialog();
        }


        mProfileRefreshLayout = rootView.findViewById(R.id.profileRefreshLayout);
        mProfileRefreshLayout.setOnRefreshListener(this);

        mProfileLoadingScreen = rootView.findViewById(R.id.profileLoadingScreen);
        mProfileErrorScreen = rootView.findViewById(R.id.profileErrorScreen);
        mProfileDisabledScreen = rootView.findViewById(R.id.profileDisabledScreen);

        ((ProfileActivity)getActivity()).mFabButton.hide();

        mNestedScrollView = rootView.findViewById(R.id.nestedScrollView);

        mProfileActionBtn = rootView.findViewById(R.id.profileActionBtn);

        mProfileFullname = rootView.findViewById(R.id.profileFullname);
        mProfileUsername = rootView.findViewById(R.id.profileUsername);

        mProfileOnlineIcon = rootView.findViewById(R.id.profileOnlineIcon);


        mLocationContainer = rootView.findViewById(R.id.profileLocationContainer);
        mProfileLocation = rootView.findViewById(R.id.profileLocation);

        mProfilePhoto = rootView.findViewById(R.id.profilePhoto);
        mProfileIcon = rootView.findViewById(R.id.profileIcon);
        mProfileProIcon = rootView.findViewById(R.id.profileProIcon);
        mProfileCover = rootView.findViewById(R.id.profileCover);

        mProfileActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getAccessLevel() < ADMIN_ACCESS_LEVEL_MODERATOR_RIGHTS) {

                    blockProfile();

                } else {

                    Toast.makeText(getActivity(), getText(R.string.msg_access_rights_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (profile.getNormalPhotoUrl().length() > 0) {

                    Intent i = new Intent(getActivity(), PhotoViewActivity.class);
                    i.putExtra("imgUrl", profile.getNormalPhotoUrl());
                    startActivity(i);
                }
            }
        });

        if (profile.getFullname() == null || profile.getFullname().length() == 0) {

            if (App.getInstance().isConnected()) {

                showLoadingScreen();
                getData();

                Log.e("Profile", "OnReload");

            } else {

                showErrorScreen();
            }

        } else {

            if (App.getInstance().isConnected()) {

                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                    showContentScreen();

                    loadingComplete();
                    updateProfile();

                } else {

                    showDisabledScreen();
                }

            } else {

                showErrorScreen();
            }
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putInt("itemId", itemId);

        outState.putBoolean("restore", restore);
        outState.putBoolean("loading", loading);
        outState.putBoolean("preload", preload);

        outState.putParcelable("profileObj", profile);
    }

    private Bitmap resize(String path){

        int maxWidth = 512;
        int maxHeight = 512;

        // create the options
        BitmapFactory.Options opts = new BitmapFactory.Options();

        //just decode the file
        opts.inJustDecodeBounds = true;
        Bitmap bp = BitmapFactory.decodeFile(path, opts);

        //get the original size
        int orignalHeight = opts.outHeight;
        int orignalWidth = opts.outWidth;

        //initialization of the scale
        int resizeScale = 1;

        //get the good scale
        if (orignalWidth > maxWidth || orignalHeight > maxHeight) {

            final int heightRatio = Math.round((float) orignalHeight / (float) maxHeight);
            final int widthRatio = Math.round((float) orignalWidth / (float) maxWidth);
            resizeScale = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        //put the scale instruction (1 -> scale to (1/1); 8-> scale to 1/8)
        opts.inSampleSize = resizeScale;
        opts.inJustDecodeBounds = false;

        //get the futur size of the bitmap
        int bmSize = (orignalWidth / resizeScale) * (orignalHeight / resizeScale) * 4;

        //check if it's possible to store into the vm java the picture
        if (Runtime.getRuntime().freeMemory() > bmSize) {

            //decode the file
            bp = BitmapFactory.decodeFile(path, opts);

        } else {

            return null;
        }

        return bp;
    }

    public void save(String outFile, String inFile) {

        try {

            Bitmap bmp = resize(outFile);

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, inFile);
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception ex) {

            Log.e("Error", ex.getMessage());
        }
    }

    private static int exifToDegrees(int exifOrientation) {

        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {

            return 90;

        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {

            return 180;

        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {

            return 270;
        }

        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            getData();

        } else {

            mProfileRefreshLayout.setRefreshing(false);
        }
    }

    public void updateProfile() {

        updateFullname();

        mProfileUsername.setText("@" + profile.getUsername());
        mProfileLocation.setText(profile.getLocation());

        mProfileActionBtn.setEnabled(true);

        if (profile.getState() == ACCOUNT_STATE_BLOCKED) {

            mProfileActionBtn.setEnabled(false);
        }

        if (profile.getLocation() != null && profile.getLocation().length() != 0) {

            mLocationContainer.setVisibility(View.VISIBLE);

        } else {

            mLocationContainer.setVisibility(View.GONE);
        }

        showPhoto(profile.getLowPhotoUrl());
        showCover(profile.getNormalCoverUrl());

        showContentScreen();

        if (profile.isOnline()) {

            // user Online

            mProfileOnlineIcon.setVisibility(View.VISIBLE);

        } else {

            mProfileOnlineIcon.setVisibility(View.GONE);
        }

        if (profile.isVerify()) {

            mProfileIcon.setVisibility(View.VISIBLE);

        } else {

            mProfileIcon.setVisibility(View.GONE);
        }

        if (profile.isProMode()) {

            mProfileProIcon.setVisibility(View.VISIBLE);

        } else {

            mProfileProIcon.setVisibility(View.GONE);
        }

        if (this.isVisible()) {

            try {

                getActivity().invalidateOptionsMenu();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void updateFullname() {

        if (profile.getFullname() == null || profile.getFullname().length() == 0) {

            mProfileFullname.setText(profile.getUsername());
            if (!isMainScreen) getActivity().setTitle(profile.getUsername());

        } else {

            mProfileFullname.setText(profile.getFullname());
            if (!isMainScreen) getActivity().setTitle(profile.getFullname());
        }
    }

    public void getData() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_PROFILE_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                profile = new Profile(response);

                                showContentScreen();

                                updateProfile();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Log.e("Profile Success",  response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                Log.e("Profile Error",  error.toString() + error.getMessage() + error.getLocalizedMessage());
                showErrorScreen();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        jsonReq.setRetryPolicy(new RetryPolicy() {

            @Override
            public int getCurrentTimeout() {

                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {

                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void showPhoto(String photoUrl) {

        ImageLoader imageLoader = App.getInstance().getImageLoader();

        if (photoUrl != null && photoUrl.length() > 0) {

            imageLoader.get(photoUrl, ImageLoader.getImageListener(mProfilePhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            imageLoader.get(photoUrl, ImageLoader.getImageListener(mProfilePhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }
    }

    public void showCover(String coverUrl) {

        ImageLoader imageLoader = App.getInstance().getImageLoader();

        if (coverUrl != null && coverUrl.length() > 0) {

            imageLoader.get(coverUrl, ImageLoader.getImageListener(mProfileCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));

            mProfileCover.setImageAlpha(200);

        } else {

            imageLoader.get(coverUrl, ImageLoader.getImageListener(mProfileCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));
        }
    }


    public void loadingComplete() {

        viewMore = arrayLength == LIST_ITEMS;

        mProfileRefreshLayout.setRefreshing(false);

        loadingMore = false;

//        if (this.isVisible()) getActivity().invalidateOptionsMenu();
    }

    public void showLoadingScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_profile));

        mProfileRefreshLayout.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);
//
        mProfileLoadingScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showErrorScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_profile));

        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileRefreshLayout.setVisibility(View.GONE);
//
        mProfileErrorScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showDisabledScreen() {

        if (profile.getState() != ACCOUNT_STATE_ENABLED) {

            //mProfileDisabledScreenMsg.setText(getText(R.string.msg_account_blocked));
        }

        getActivity().setTitle(getText(R.string.label_account_disabled));

        mProfileRefreshLayout.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);
//
        mProfileDisabledScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showContentScreen() {

        if (!isMainScreen) {

            getActivity().setTitle(profile.getFullname());
        }

        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);
//
        mProfileRefreshLayout.setVisibility(View.VISIBLE);
        mProfileRefreshLayout.setRefreshing(false);

        loadingComplete = true;
        restore = true;
    }

    public void blockProfile() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getText(R.string.action_block));

        alertDialog.setMessage(getText(R.string.msg_block_user_request));
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                loading = true;

                showpDialog();

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_PROFILE_BLOCK, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (!response.getBoolean("error")) {

                                        profile.setNormalPhotoUrl("");
                                        profile.setLowPhotoUrl("");
                                        profile.setNormalCoverUrl("");

                                        profile.setState(ACCOUNT_STATE_BLOCKED);

                                        Log.d("Block", "Block success");
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                } finally {

                                    loading = false;

                                    hidepDialog();

                                    updateProfile();

                                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        loading = false;

                        hidepDialog();

                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("clientId", CLIENT_ID);
                        params.put("accountId", Long.toString(App.getInstance().getId()));
                        params.put("accessToken", App.getInstance().getAccessToken());
                        params.put("profileId", Long.toString(profile.getId()));

                        return params;
                    }
                };

                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                jsonReq.setRetryPolicy(policy);

                App.getInstance().addToRequestQueue(jsonReq);
            }
        });

        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}