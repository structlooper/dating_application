package app.manager.dating;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.manager.dating.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.manager.dating.app.App;
import app.manager.dating.constants.Constants;
import app.manager.dating.util.CustomRequest;
import app.manager.dating.util.Helper;

public class LoginFragment extends Fragment implements Constants {

    private ProgressDialog pDialog;

    Button signinBtn;
    EditText signinUsername, signinPassword;
    String username, password;

    private Boolean loading = false;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        if (loading) {

            showpDialog();
        }

        signinUsername = rootView.findViewById(R.id.signinUsername);
        signinPassword = rootView.findViewById(R.id.signinPassword);

        signinBtn = rootView.findViewById(R.id.signinBtn);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = signinUsername.getText().toString();
                password = signinPassword.getText().toString();

                if (!App.getInstance().isConnected()) {

                    Toast.makeText(getActivity(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();

                } else if (!checkUsername() || !checkPassword()) {


                } else {

                    signIn();
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
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
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void signIn() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_SIGNIN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (App.getInstance().authorize(response)) {

                            App.getInstance().updateGeoLocation();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_signin), Toast.LENGTH_SHORT).show();

                            Log.e("response", response.toString());
                        }

                        loading = false;

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("", error.toString());

                Toast.makeText(getActivity(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("clientId", CLIENT_ID);
                params.put("hash", Helper.md5(Helper.md5(username) + CLIENT_SECRET));
                params.put("appType", Integer.toString(APP_TYPE_MANAGER));
                params.put("fcm_regId", App.getInstance().getFcmToken());

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public Boolean checkUsername() {

        username = signinUsername.getText().toString();

        signinUsername.setError(null);

        Helper helper = new Helper(getActivity());

        if (username.length() == 0) {

            signinUsername.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (username.length() < 5) {

            signinUsername.setError(getString(R.string.error_small_username));

            return false;
        }

        if (!helper.isValidLogin(username) && !helper.isValidEmail(username)) {

            signinUsername.setError(getString(R.string.error_wrong_format));

            return false;
        }

        return  true;
    }

    public Boolean checkPassword() {

        password = signinPassword.getText().toString();

        signinPassword.setError(null);

        Helper helper = new Helper(getActivity());

        if (username.length() == 0) {

            signinPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            signinPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            signinPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        return  true;
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