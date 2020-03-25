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

import app.vitamiin.com.TutorialActivity;

public class ImageSlidingAdapter extends PagerAdapter {

    private ArrayList<?> arrReviewImgInfo;
    private ArrayList<Bitmap> arrBitmapReviewImgInfo;
    private Context _context;
    private int m_nIdx;

    public ImageSlidingAdapter(Context con, ArrayList<?> imgInfo, int idx) {
        this._context = con;
        if(imgInfo.get(0).getClass().equals(Integer.valueOf("0").getClass())){
            this.arrReviewImgInfo = (ArrayList<Integer>)imgInfo;
        }else{
            this.arrReviewImgInfo = (ArrayList<Bitmap>)imgInfo;
        }
        this.m_nIdx = idx;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if(this.arrReviewImgInfo.get(0).getClass().equals(Integer.valueOf("0").getClass())){
            ((TutorialActivity) _context).setPhotoPage(position);
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return arrReviewImgInfo.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((ImageView) arg1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(_context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if(this.arrReviewImgInfo.get(0).getClass().equals(Integer.valueOf("0").getClass())){
            Glide.with(_context).load(arrReviewImgInfo.get(position)).into(imageView);
        }else{
            Glide.with(_context).load((Bitmap)arrReviewImgInfo.get(position)).into(imageView);
        }
        ((ViewPager) container).addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
