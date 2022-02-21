package app.manager.dating;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.manager.dating.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.manager.dating.app.App;
import app.manager.dating.common.ActivityBase;
import app.manager.dating.util.CustomRequest;

public class AppActivity extends ActivityBase {

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;

    Button loginBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app);

        // Get Firebase token

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {

                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {

                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        App.getInstance().setFcmToken(token);
                    }
                });

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    if (task.isSuccessful() && task.getResult() != null) {

                        mLastLocation = task.getResult();

                        // Set geo data to App class

                        App.getInstance().setLat(mLastLocation.getLatitude());
                        App.getInstance().setLng(mLastLocation.getLongitude());

                    } else {

                        Log.d("GPS", "AppActivity getLastLocation:exception", task.getException());
                    }
                }
            });
        }

        loginBtn = findViewById(R.id.loginBtn);

        progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AppActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void  onStart() {

        super.onStart();

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            showLoadingScreen();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_AUTHORIZE, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (App.getInstance().authorize(response)) {

                                App.getInstance().updateGeoLocation();

                                Intent intent = new Intent(AppActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {

                                showContentScreen();
                                App.getInstance().logout();
                            }

                            Log.e("Auth", response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    showContentScreen();

                    Log.e("Auth", "Error");
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", CLIENT_ID);
                    params.put("appType", Integer.toString(APP_TYPE_MANAGER));
                    params.put("fcm_regId", App.getInstance().getFcmToken());
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());

                    return params;
                }
            };

            RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(15), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            App.getInstance().addToRequestQueue(jsonReq);

        } else {

            showContentScreen();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    public void showContentScreen() {

        progressBar.setVisibility(View.GONE);

        loginBtn.setVisibility(View.VISIBLE);
    }

    public void showLoadingScreen() {

        progressBar.setVisibility(View.VISIBLE);

        loginBtn.setVisibility(View.GONE);
    }
}
