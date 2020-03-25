package app.vitamiin.com.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

import app.vitamiin.com.R;

public class CornerNetImageView extends NetworkImageView {

    private static final int FADE_IN_TIME_MS = 250;

    private int _round = 0;

    private Context _context;
    private int _width = 0;
    private int _height = 0;

    public CornerNetImageView(Context context) {
        super(context);
        _context = context;
    }

    public CornerNetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }

    public CornerNetImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _context = context;
    }

    public void setSize(int width, int height) {

        _width = width;
        _height = height;
    }

    public void setRound(int round) {
        _round = round;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {

        if (bm != null) {

            if (getScaleType() == ScaleType.FIT_XY) {
                bm = Bitmap.createScaledBitmap(bm, _width, _height, true);
            }

            bm = BitmapUtils.getRoundedCornerBitmap(_context, bm, _round);
        }

        TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(R.color.transparent),
                new BitmapDrawable(getContext().getResources(), bm)});

        setImageDrawable(td);
        td.startTransition(FADE_IN_TIME_MS);
    }
}
