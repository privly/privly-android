package ly.priv.mobile.gui;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertMessageBox {
    public enum AlertMessageBoxIcon
    {
        Error,
        Info,


    }
    public static void Show(Context context,String title,String message,AlertMessageBoxIcon alertMessageBoxIcon)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        switch (alertMessageBoxIcon)
        {
            case Error:
                alertDialog.setIcon(R.drawable.stat_notify_error);
                break;
            case Info:
                alertDialog.setIcon(R.drawable.ic_dialog_info);
                break;
        }
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }
}