package app.manager.dating.util;

import android.app.Application;
import android.content.Context;

import app.manager.dating.constants.Constants;

public class Api extends Application implements Constants {

    Context context;

    public Api (Context context) {

        this.context = context;
    }
}
