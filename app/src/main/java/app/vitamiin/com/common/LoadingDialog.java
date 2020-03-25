package app.vitamiin.com.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.victor.loading.rotate.RotateLoading;

import app.vitamiin.com.R;

public class LoadingDialog extends Dialog {
    private Context _context;

    public LoadingDialog(Context context) {
        super(context);
        _context = context;
        // TODO Auto-generated constructor stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_loading);

        RotateLoading rotateloading = (RotateLoading) findViewById(R.id.rotateloading);
        rotateloading.start();
    }
}
