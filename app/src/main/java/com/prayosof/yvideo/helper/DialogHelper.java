package com.prayosof.yvideo.helper;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class DialogHelper {

    public static void showYesNoDialog(Context context, int msg, DialogInterface.OnClickListener dialogClickListener)
    {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    public static void showYesNoDialog(Context context, String msg, DialogInterface.OnClickListener dialogClickListener)
    {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
