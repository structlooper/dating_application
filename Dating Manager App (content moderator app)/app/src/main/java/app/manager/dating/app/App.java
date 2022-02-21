package app.manager.dating.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.manager.dating.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.manager.dating.constants.Constants;
import app.manager.dating.model.Settings;
import app.manager.dating.util.CustomRequest;
import app.manager.dating.util.LruBitmapCache;

public class App extends Application implements Constants {

	public static final String TAG = App.class.getSimpleName();

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static App mInstance;

    private SharedPreferences sharedPref;

    private String username, fullname, accessToken, fcmToken = "", createDate = "", photoUrl, coverUrl, area = "", country = "", city = "";
    private Double lat = 0.000000, lng = 0.000000;
    private long id;
    private int accessLevel, verify, balance = 0, errorCode;
    private int allowNewProfilePhotosFCM, allowNewProfileCoversFCM, allowNewMediaItemsFCM, newProfilePhotosCount, newProfileCoversCount, newMediaItemsCount;

    private Settings settings;

    private Boolean circle_items = true;

	@Override
	public void onCreate() {

		super.onCreate();

        mInstance = this;

        FirebaseApp.initializeApp(this);

        sharedPref = this.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);

        this.settings = new Settings();

        this.readData();

        this.loadSettings();
	}
    
    public boolean isConnected() {
    	
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	
    	NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    public void logout() {

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_LOGOUT, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {



                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    App.getInstance().removeData();
                    App.getInstance().readData();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", CLIENT_ID);
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);

        }

        App.getInstance().removeData();
        App.getInstance().readData();
    }

    public void loadSettings() {

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_GET_SETTINGS, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {

                                    if (response.has("newProfilePhotosCount")) {

                                        App.getInstance().setNewProfilePhotosCount(response.getInt("newProfilePhotosCount"));
                                    }

                                    if (response.has("newProfileCoversCount")) {

                                        App.getInstance().setNewProfileCoversCount(response.getInt("newProfileCoversCount"));
                                    }

                                    if (response.has("newMediaItemsCount")) {

                                        App.getInstance().setNewMediaItemsCount(response.getInt("newMediaItemsCount"));
                                    }
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("loadSettings()", error.toString());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", CLIENT_ID);
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public Settings getSettings() {

	    return this.settings;
    }

    public void updateGeoLocation() {

        // Now it is empty
        // In this application, there is no a web version and this code has been deleted
    }

    public Boolean authorize(JSONObject authObj) {

        try {

            if (authObj.has("error_code")) {

                this.setErrorCode(authObj.getInt("error_code"));
            }

            if (!authObj.has("error")) {

                return false;
            }

            if (authObj.getBoolean("error")) {

                return false;
            }

            if (!authObj.has("account")) {

                return false;
            }

            JSONObject accountObj = authObj.getJSONObject("account");

            if (!accountObj.getBoolean("error")) {

                this.setUsername(accountObj.getString("username"));
                this.setFullname(accountObj.getString("fullname"));
                this.setAccessLevel(accountObj.getInt("access_level"));

                this.setCreateDate(accountObj.getString("createDate"));

                Log.d("Account", accountObj.toString());
            }

            this.setId(authObj.getLong("accountId"));
            this.setAccessToken(authObj.getString("accessToken"));

            this.saveData();

            this.loadSettings();

            if (getFcmToken().length() != 0) {

                setFcmToken(getFcmToken());
            }

            return true;

        } catch (JSONException e) {

            e.printStackTrace();
            return false;
        }
    }

    public long getId() {

        return this.id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public void setFcmToken(final String fcmToken) {

        if (this.getId() != 0 && this.getAccessToken().length() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_SET_FCM_TOKEN, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {

//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("setFcmToken()", error.toString());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());

                    params.put("fcm_regId", fcmToken);

                    return params;
                }
            };

            int socketTimeout = 0;//0 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            App.getInstance().addToRequestQueue(jsonReq);
        }

        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {

        return this.fcmToken;
    }

    public void setCreateDate(String createDate) {

        this.createDate = createDate;
    }

    public String getCreateDate() {

        return this.createDate;
    }

    public void setAccessLevel(int accessLevel) {

        this.accessLevel = accessLevel;
    }

    public int getAccessLevel() {

        return this.accessLevel;
    }

    public void setNewProfilePhotosCount(int newProfilePhotosCount) {

        this.newProfilePhotosCount = newProfilePhotosCount;
    }

    public int getNewProfilePhotosCount() {

        return this.newProfilePhotosCount;
    }

    public void setNewProfileCoversCount(int newProfileCoversCount) {

        this.newProfileCoversCount = newProfileCoversCount;
    }

    public int getNewProfileCoversCount() {

        return this.newProfileCoversCount;
    }

    public void setNewMediaItemsCount(int newMediaItemsCount) {

        this.newMediaItemsCount = newMediaItemsCount;
    }

    public int getNewMediaItemsCount() {

        return this.newMediaItemsCount;
    }

    public void setAllowNewProfilePhotosFCM(int allowNewProfilePhotosFCM) {

        this.allowNewProfilePhotosFCM = allowNewProfilePhotosFCM;
    }

    public int getAllowNewProfilePhotosFCM() {

        return this.allowNewProfilePhotosFCM;
    }

    public void setAllowNewProfileCoversFCM(int allowNewProfileCoversFCM) {

        this.allowNewProfileCoversFCM = allowNewProfileCoversFCM;
    }

    public int getAllowNewProfileCoversFCM() {

        return this.allowNewProfileCoversFCM;
    }

    public void setAllowNewMediaItemsFCM(int allowNewMediaItemsFCM) {

        this.allowNewMediaItemsFCM = allowNewMediaItemsFCM;
    }

    public int getAllowNewMediaItemsFCM() {

        return this.allowNewMediaItemsFCM;
    }

    public void setErrorCode(int errorCode) {

        this.errorCode = errorCode;
    }

    public int getErrorCode() {

        return this.errorCode;
    }

    public String getUsername() {

        if (this.username == null) {

            this.username = "";
        }

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getAccessToken() {

        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public void setFullname(String fullname) {

        this.fullname = fullname;
    }

    public String getFullname() {

        if (this.fullname == null) {

            this.fullname = "";
        }

        return this.fullname;
    }

    public void setVerify(int verify) {

        this.verify = verify;
    }

    public int getVerify() {

        return this.verify;
    }

    public void setPhotoUrl(String photoUrl) {

        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {

        if (this.photoUrl == null) {

            this.photoUrl = "";
        }

        return this.photoUrl;
    }

    public void setCoverUrl(String coverUrl) {

        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() {

        if (coverUrl == null) {

            this.coverUrl = "";
        }

        return this.coverUrl;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public String getCountry() {

        if (this.country == null) {

            this.setCountry("");
        }

        return this.country;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public String getCity() {

        if (this.city == null) {

            this.setCity("");
        }

        return this.city;
    }

    public void setArea(String area) {

        this.area = area;
    }

    public String getArea() {

        if (this.area == null) {

            this.setArea("");
        }

        return this.area;
    }

    public void setLat(Double lat) {

        if (this.lat == null) {

            this.lat = 0.000000;
        }

        this.lat = lat;
    }

    public Double getLat() {

        if (this.lat == null) {

            this.lat = 0.000000;
        }

        return this.lat;
    }

    public void setLng(Double lng) {

        if (this.lng == null) {

            this.lng = 0.000000;
        }

        this.lng = lng;
    }

    public Double getLng() {

        return this.lng;
    }

    public void setCircleItems(Boolean circle_items) {

        this.circle_items = circle_items;
    }

    public Boolean getCircleItems() {

        return this.circle_items;
    }

    public void readData() {

        this.setId(sharedPref.getLong(getString(R.string.settings_account_id), 0));
        this.setUsername(sharedPref.getString(getString(R.string.settings_account_username), ""));
        this.setAccessToken(sharedPref.getString(getString(R.string.settings_account_access_token), ""));

        this.setFullname(sharedPref.getString(getString(R.string.settings_account_fullname), ""));
        this.setPhotoUrl(sharedPref.getString(getString(R.string.settings_account_photo_url), ""));
        this.setCoverUrl(sharedPref.getString(getString(R.string.settings_account_cover_url), ""));

        this.setVerify(sharedPref.getInt(getString(R.string.settings_account_verified), 0));

        this.setAllowNewProfilePhotosFCM(sharedPref.getInt(getString(R.string.settings_account_allow_new_profile_photos_fcm), 1));
        this.setAllowNewProfileCoversFCM(sharedPref.getInt(getString(R.string.settings_account_allow_new_profile_covers_fcm), 1));
        this.setAllowNewMediaItemsFCM(sharedPref.getInt(getString(R.string.settings_account_allow_new_media_items_fcm), 1));

        this.setCircleItems(sharedPref.getBoolean(getString(R.string.settings_account_circle_items), true));

        if (App.getInstance().getLat() == 0.000000 && App.getInstance().getLng() == 0.000000) {

            this.setLat(Double.parseDouble(sharedPref.getString(getString(R.string.settings_account_lat), "0.000000")));
            this.setLng(Double.parseDouble(sharedPref.getString(getString(R.string.settings_account_lng), "0.000000")));
        }
    }

    public void saveData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), this.getId()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_username), this.getUsername()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), this.getAccessToken()).apply();

        sharedPref.edit().putString(getString(R.string.settings_account_fullname), this.getFullname()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_photo_url), this.getPhotoUrl()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_cover_url), this.getCoverUrl()).apply();

        sharedPref.edit().putInt(getString(R.string.settings_account_verified), this.getVerify()).apply();

        sharedPref.edit().putInt(getString(R.string.settings_account_allow_new_profile_photos_fcm), this.getAllowNewProfilePhotosFCM()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_account_allow_new_profile_covers_fcm), this.getAllowNewProfileCoversFCM()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_account_allow_new_media_items_fcm), this.getAllowNewMediaItemsFCM()).apply();

        sharedPref.edit().putBoolean(getString(R.string.settings_account_circle_items), this.getCircleItems()).apply();

        sharedPref.edit().putString(getString(R.string.settings_account_lat), Double.toString(this.getLat())).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_lng), Double.toString(this.getLng())).apply();
    }

    public void removeData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), 0).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_username), "").apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), "").apply();
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public static synchronized App getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {

		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}