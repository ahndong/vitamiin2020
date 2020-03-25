package app.vitamiin.com;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import app.vitamiin.com.common.LoadingDialog;
import app.vitamiin.com.common.UserManager;

public abstract class BaseActivity extends AppCompatActivity {
    protected VitaminApp m_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_app = (VitaminApp) getApplication();
        m_app.setMainAct(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        init();
    }

    public void init() {

    }


    public void startActivity(Intent paramIntent) {
        super.startActivity(paramIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable("user_info", UserManager.getInstance());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        UserManager userManager = (UserManager) savedInstanceState.getSerializable("user_info");
        if(userManager != null)
            UserManager.getInstance().setUser(userManager);
        else {// 앱을 종료한다.
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    LoadingDialog roateDialog = null;

    public void showProgress() {
        roateDialog = new LoadingDialog(this);
        roateDialog.setCancelable(false);
        roateDialog.show();
    }

    public void closeProgress() {
        if (roateDialog != null) {
            roateDialog.dismiss();
            roateDialog = null;
        }
    }
}
