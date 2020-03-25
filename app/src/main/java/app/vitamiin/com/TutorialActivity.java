package app.vitamiin.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import app.vitamiin.com.common.ImageSlidingAdapter;
import app.vitamiin.com.login.LoginActivity;


public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {
    Context m_context;

    ViewPager m_uiVwpShopPhoto;
    CirclePageIndicator mHelpIndicator;
    private ArrayList<Integer> m_arrPhotoName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        m_context = this;

        m_uiVwpShopPhoto = (ViewPager) findViewById(R.id.vwp_shop_photo);
        mHelpIndicator = (CirclePageIndicator) findViewById(R.id.cpi_photo);

        m_arrPhotoName.add(R.drawable.bg_tutorial_1);
        m_arrPhotoName.add(R.drawable.bg_tutorial_2);
        m_arrPhotoName.add(R.drawable.bg_tutorial_3);
        m_arrPhotoName.add(R.drawable.bg_tutorial_4);
        m_arrPhotoName.add(R.drawable.bg_tutorial_5);
        m_arrPhotoName.add(R.drawable.bg_tutorial_6);
        ImageSlidingAdapter adapter = new ImageSlidingAdapter(this, m_arrPhotoName, 1);
        m_uiVwpShopPhoto.setAdapter(adapter);
        mHelpIndicator.setViewPager(m_uiVwpShopPhoto);

        findViewById(R.id.imv_close).setOnClickListener(this);
        findViewById(R.id.tv_start).setOnClickListener(this);
        findViewById(R.id.tv_start).setVisibility(View.GONE);
    }

    public void setPhotoPage(int pos) {
        if (pos == 5) {
            if (!getIntent().getBooleanExtra("fromSetting", false))
                findViewById(R.id.tv_start).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tv_start).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start:
            case R.id.imv_close:
                if (!getIntent().getBooleanExtra("fromSetting", false)) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
                break;
        }
    }
}
