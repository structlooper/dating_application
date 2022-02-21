package app.manager.dating;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.manager.dating.R;

import app.manager.dating.app.App;
import app.manager.dating.constants.Constants;

public class NotificationsSettingsFragment extends PreferenceFragment implements Constants {

    private CheckBoxPreference allowNewProfilePhotosFCM, allowNewProfileCoversFCM, allowNewMediaItemsFCM;

    private ProgressDialog pDialog;

    int mAllowNewProfilePhotosFCM, mAllowNewProfileCoversFCM, mAllowNewMediaItemsFCM;

    private Boolean loading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.notifications_settings);

        allowNewProfilePhotosFCM = (CheckBoxPreference) getPreferenceManager().findPreference("allowNewProfilePhotosFCM");

        allowNewProfilePhotosFCM.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowNewProfilePhotosFCM = 1;

                    } else {

                        mAllowNewProfilePhotosFCM = 0;
                    }

                    saveSettings();
                }

                return true;
            }
        });

        allowNewProfileCoversFCM = (CheckBoxPreference) getPreferenceManager().findPreference("allowNewProfileCoversFCM");

        allowNewProfileCoversFCM.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowNewProfileCoversFCM = 1;

                    } else {

                        mAllowNewProfileCoversFCM = 0;
                    }

                    saveSettings();
                }

                return true;
            }
        });

        allowNewMediaItemsFCM = (CheckBoxPreference) getPreferenceManager().findPreference("allowNewMediaItemsFCM");

        allowNewMediaItemsFCM.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowNewMediaItemsFCM = 1;

                    } else {

                        mAllowNewMediaItemsFCM = 0;
                    }

                    saveSettings();
                }

                return true;
            }
        });

        checkAllowNewProfilePhotosFCM(App.getInstance().getAllowNewProfilePhotosFCM());
        checkAllowNewProfileCoversFCM(App.getInstance().getAllowNewProfileCoversFCM());
        checkAllowMediaItemsFCM(App.getInstance().getAllowNewMediaItemsFCM());
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

    public void checkAllowNewProfilePhotosFCM(int value) {

        if (value == 1) {

            allowNewProfilePhotosFCM.setChecked(true);
            mAllowNewProfilePhotosFCM = 1;

        } else {

            allowNewProfilePhotosFCM.setChecked(false);
            mAllowNewProfilePhotosFCM = 0;
        }
    }

    public void checkAllowNewProfileCoversFCM(int value) {

        if (value == 1) {

            allowNewProfileCoversFCM.setChecked(true);
            mAllowNewProfileCoversFCM = 1;

        } else {

            allowNewProfileCoversFCM.setChecked(false);
            mAllowNewProfileCoversFCM = 0;
        }
    }

    public void checkAllowMediaItemsFCM(int value) {

        if (value == 1) {

            allowNewMediaItemsFCM.setChecked(true);
            mAllowNewMediaItemsFCM = 1;

        } else {

            allowNewMediaItemsFCM.setChecked(false);
            mAllowNewMediaItemsFCM = 0;
        }
    }

    public void saveSettings() {

        App.getInstance().setAllowNewProfilePhotosFCM(mAllowNewProfilePhotosFCM);
        App.getInstance().setAllowNewProfileCoversFCM(mAllowNewProfileCoversFCM);
        App.getInstance().setAllowNewMediaItemsFCM(mAllowNewMediaItemsFCM);

        App.getInstance().saveData();
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