package app.vitamiin.com.common;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.IntroActivity;

public class ShowNotifyActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        Intent w_intentFromNotifyReceiver = getIntent();
//        String w_strMessage = w_intentFromNotifyReceiver
//                .getStringExtra("Message");
//        new AlertDialog.Builder(this).setMessage(w_strMessage)
//                .setPositiveButton(android.R.string.ok, new OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
        Intent i;
        i = new Intent(ShowNotifyActivity.this, IntroActivity.class);
        i.putExtra("from_push", true);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
//                    }

//                }).setOnCancelListener(new OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                finish();
//            }
//        }).show();
    }
}
