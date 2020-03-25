package app.vitamiin.com.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.Window;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import app.vitamiin.com.R;
import app.vitamiin.com.common.ImageSlidingAdapter;
/**
 * Created by dong8 on 2017-01-06.
 */

public class ContentPhotoViewerDialog  extends Dialog implements View.OnClickListener {
    Context m_context;

    ViewPager m_uiVwpShopPhoto;
    CirclePageIndicator mHelpIndicator;
    private ArrayList<String> m_strIMGs = new ArrayList<>();
    private ArrayList<Bitmap> m_btmIMGs = new ArrayList<>();

    public ContentPhotoViewerDialog(Context context, ArrayList<Bitmap> btmIMGs, int index, int type) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        setContentView(R.layout.activity_tutorial);

        m_context = context;
        m_uiVwpShopPhoto = (ViewPager) findViewById(R.id.vwp_shop_photo);
        mHelpIndicator = (CirclePageIndicator) findViewById(R.id.cpi_photo);
        m_btmIMGs = btmIMGs;
//
//        m_strIMGs = (getIntent().getStringArrayListExtra("IMGs"));
//        for(int i=0;i<m_strIMGs.size();i++){
//            m_btmIMGs.add(BitmapFactory.decodeFile(m_strIMGs.get(i)));
//        }

        ImageSlidingAdapter adapter = new ImageSlidingAdapter(this.m_context, m_btmIMGs, type);
        //m_uiVwpShopPhoto.setCurrentItem(index);
        m_uiVwpShopPhoto.setAdapter(adapter);
        for(int i=0;i<index;i++)
        m_uiVwpShopPhoto.arrowScroll(View.FOCUS_RIGHT);
        mHelpIndicator.setViewPager(m_uiVwpShopPhoto);

        findViewById(R.id.imv_close).setOnClickListener(this);
        findViewById(R.id.tv_start).setVisibility(View.GONE);
    }

    public void setPhotoPage(int pos) {
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.imv_close:
                dismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_close));
    }
}
