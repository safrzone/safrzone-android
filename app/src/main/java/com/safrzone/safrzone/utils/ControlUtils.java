package com.safrzone.safrzone.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import java.math.BigDecimal;

public class ControlUtils {
    public static void dismiss(ListPopupWindow w) {
        try {
            if (w != null && w.isShowing()) {
                w.dismiss();
            }
            // Bug: http://stackoverflow.com/questions/2745061/java-lang-illegalargumentexception-view-not-attached-to-window-manager
        } catch (Exception e) {
        }
    }

    public static void dismiss(DialogInterface d) {
        try {
            if (d != null) {
                d.dismiss();
            }
            // Bug: http://stackoverflow.com/questions/2745061/java-lang-illegalargumentexception-view-not-attached-to-window-manager
        } catch (Exception e) {
        }
    }

    public static void dismiss(Dialog d) {
        try {
            if (d != null && d.isShowing()) {
                d.dismiss();
            }
            // Bug: http://stackoverflow.com/questions/2745061/java-lang-illegalargumentexception-view-not-attached-to-window-manager
        } catch (Exception e) {
        }
    }
}
