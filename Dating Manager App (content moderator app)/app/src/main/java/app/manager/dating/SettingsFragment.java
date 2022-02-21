package app.manager.dating;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.manager.dating.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.manager.dating.app.App;
import app.manager.dating.constants.Constants;
import app.manager.dating.util.CustomRequest;



public class SettingsFragment extends PreferenceFragmentCompat implements Constants {

    private Preference logoutPreference, aboutPreference, changePassword, itemTerms, itemThanks, itemNotifications;
    private PreferenceScreen screen;

    private ProgressDialog pDialog;

    LinearLayout aboutDialogContent;
    TextView aboutDialogAppName, aboutDialogAppVersion, aboutDialogAppCopyright;

    private Boolean loading = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();

        screen = this.getPreferenceScreen();

        Preference pref = findPreference("settings_version");

        pref.setTitle(getString(R.string.app_name) + " v" + getString(R.string.app_version));

        pref = findPreference("settings_logout");

        pref.setSummary(App.getInstance().getUsername());

//        pref = findPreference("settings_copyright_info");
//
//        pref.setSummary(APP_COPYRIGHT + " © " + APP_YEAR);

        logoutPreference = findPreference("settings_logout");
        aboutPreference = findPreference("settings_version");
        changePassword = findPreference("settings_change_password");
        itemTerms = findPreference("settings_terms");
        itemThanks = findPreference("settings_thanks");
        itemNotifications = findPreference("settings_push_notifications");

        itemNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), NotificationsSettingsActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemThanks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_THANKS);
                i.putExtra("title", getText(R.string.settings_thanks));
                startActivity(i);

                return true;
            }
        });

        itemTerms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_TERMS);
                i.putExtra("title", getText(R.string.settings_terms));
                startActivity(i);

                return true;
            }
        });

        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getText(R.string.action_about));

                aboutDialogContent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);

                alertDialog.setView(aboutDialogContent);

                aboutDialogAppName = aboutDialogContent.findViewById(R.id.aboutDialogAppName);
                aboutDialogAppVersion = aboutDialogContent.findViewById(R.id.aboutDialogAppVersion);
                aboutDialogAppCopyright = aboutDialogContent.findViewById(R.id.aboutDialogAppCopyright);

                aboutDialogAppName.setText(getString(R.string.app_name));
                aboutDialogAppVersion.setText("Version " + getString(R.string.app_version));
                aboutDialogAppCopyright.setText("Copyright © " + getString(R.string.app_year) + " " + getString(R.string.app_copyright));

//                    alertDialog.setMessage("Version " + APP_VERSION + "/r/n" + APP_COPYRIGHT);
                alertDialog.setCancelable(true);
                alertDialog.setNeutralButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                alertDialog.show();

                return false;
            }
        });

        logoutPreference.setSummary(App.getInstance().getUsername());

        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getText(R.string.action_logout));

                alertDialog.setMessage(getText(R.string.msg_action_logout));
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

                        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_LOGOUT, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {

                                            if (!response.getBoolean("error")) {

                                                Log.d("Logout", "Logout success");
                                            }

                                        } catch (JSONException e) {

                                            e.printStackTrace();

                                        } finally {

                                            loading = false;

                                            hidepDialog();

                                            App.getInstance().removeData();
                                            App.getInstance().readData();

                                            App.getInstance().setId(0);
                                            App.getInstance().setUsername("");
                                            App.getInstance().setFullname("");

                                            Intent intent = new Intent(getActivity(), AppActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                loading = false;

                                App.getInstance().removeData();
                                App.getInstance().readData();

                                App.getInstance().setId(0);
                                App.getInstance().setUsername("");
                                App.getInstance().setFullname("");

                                Intent intent = new Intent(getActivity(), AppActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                hidepDialog();
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

                        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                        jsonReq.setRetryPolicy(policy);

                        App.getInstance().addToRequestQueue(jsonReq);
                    }
                });

                alertDialog.show();

                return true;
            }
        });

        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(i);

                return true;
            }
        });
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

        if (loading) {

            showpDialog();
        }
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}