package com.android.iparking.views;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarGenerator {

    public static void snackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void snackbar(View view, Throwable throwable) {
        Snackbar.make(view, "ERROR: " + throwable.getMessage(), Snackbar.LENGTH_LONG).show();
    }
}
