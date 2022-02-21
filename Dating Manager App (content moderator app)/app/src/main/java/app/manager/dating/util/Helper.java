package app.manager.dating.util;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.manager.dating.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper extends Application {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private AppCompatActivity activity;
    private Context context;

    public Helper(Context current){

        this.context = current;
    }

    public Helper(AppCompatActivity activity) {

        this.activity = activity;
    }

    public Helper(FragmentActivity activity) {

    }

    public static String getDate(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mma", cal).toString();

        return date;
    }


    public static int getGridSpanCount(FragmentActivity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth  = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.item_size);
        return Math.round(screenWidth / cellWidth);
    }

    public boolean isValidEmail(String email) {

    	if (TextUtils.isEmpty(email)) {

    		return false;

    	} else {

    		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    	}
    }
    
    public boolean isValidLogin(String login) {

        String regExpn = "^([a-zA-Z]{4,24})?([a-zA-Z][a-zA-Z0-9_]{4,24})$";
        CharSequence inputStr = login;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    public boolean isValidSearchQuery(String query) {

        String regExpn = "^([a-zA-Z]{1,24})?([a-zA-Z][a-zA-Z0-9_]{1,24})$";
        CharSequence inputStr = query;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }
    
    public boolean isValidPassword(String password) {

        String regExpn = "^[a-z0-9_]{6,24}$";
        CharSequence inputStr = password;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    public static String md5(final String s) {

        final String MD5 = "MD5";

        try {

            // Create MD5 Hash

            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());

            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();

            for (byte aMessageDigest : messageDigest) {

                String h = Integer.toHexString(0xFF & aMessageDigest);

                while (h.length() < 2) h = "0" + h;

                hexString.append(h);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }

        return "";
    }
}
