package app.vitamiin.com.common;

import android.content.Context;
import android.graphics.Bitmap;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.http.Net;

public class FoodImageSlidingAdapter extends PagerAdapter {

    private ArrayList<String> arrReviewImgInfo;
    private BaseActivity _context;
    private int m_nIdx;

    public FoodImageSlidingAdapter(BaseActivity con, ArrayList<String> imgInfo, int idx) {
        this._context = con;
        this.arrReviewImgInfo = imgInfo;
        this.m_nIdx = idx;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (m_nIdx == 1)
            ((MainActivity) _context).setFamilyPhotoPage(position);
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrReviewImgInfo.size();
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == ((ImageView) arg1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(_context);
        if (m_nIdx == 0)
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        else if (m_nIdx == 1)
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        else if (m_nIdx == 2)
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        else if (m_nIdx == 3)
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (arrReviewImgInfo.get(position).equals("")) {
            if (m_nIdx == 0)
                Glide.with(_context).load(R.drawable.default_review).into(imageView);
            else if (m_nIdx == 1)
                Glide.with(_context).load(R.drawable.ic_female_3).into(imageView);
            else if (m_nIdx == 2)
                Glide.with(_context).load(R.drawable.default_review).into(imageView);
            else if (m_nIdx == 3)
                Glide.with(_context).load(R.drawable.default_review).into(imageView);
        } else {
            if (m_nIdx == 0)
                Glide.with(_context).load(Net.URL_SERVER1 + arrReviewImgInfo.get(position)).into(imageView);
            else if (m_nIdx == 1) {
                Glide.with(_context).load(Net.URL_SERVER1 + arrReviewImgInfo.get(position)).into(imageView);
            }else if (m_nIdx == 2)
                Glide.with(_context).load(Net.URL_SERVER1 + arrReviewImgInfo.get(position)).into(imageView);
            else if (m_nIdx == 3)
                Glide.with(_context).load(Net.URL_SERVER1 + arrReviewImgInfo.get(position)).into(imageView);
        }

        ((ViewPager) container).addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
