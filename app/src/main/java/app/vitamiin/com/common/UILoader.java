package app.vitamiin.com.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import cn.lightsky.infiniteindicator.loader.ImageLoader;

/**
 * Created by Puma on 8/30/2016.
 */
public class UILoader implements ImageLoader {
    private DisplayImageOptions options;
    private boolean isInited;
    private CircleImageView m_cli = null;

    public UILoader getImageLoader(Context context) {
        UILoader uilLoader = new UILoader();
        initLoader(context);
        return uilLoader;
    }

    @Override
    public void initLoader(Context context) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));

        options = new DisplayImageOptions.Builder()
//                    .showImageOnLoading(R.drawable.placeholder)
//                    .showImageOnFail(R.drawable.error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .build();

        isInited = true;
    }

    @Override
    public void load(Context context, ImageView targetView, Object res) {
        targetView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (!isInited)
            initLoader(context);

        //////////////////////동현 수정/////////////////////////////////////////
//        if(m_cli == null) {
//            m_cli = (CircleImageView)targetView;
//        }
        //////////////////////동현 수정/////////////////////////////////////////

        if (res == null) {
            return;
        }
        if (res instanceof String) {
            String strPath = (String) res;
            if (strPath.length() > 0)
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage((String) res, targetView, options);
//                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage((String) res, m_cli, options);
            else {
                targetView.setImageBitmap(null);
            }
        }
    }
}
