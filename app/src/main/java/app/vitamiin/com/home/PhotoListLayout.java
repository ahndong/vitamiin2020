package app.vitamiin.com.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import app.vitamiin.com.R;
import app.vitamiin.com.common.SquareImageView;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.common.CircleImageView;

public class PhotoListLayout extends LinearLayout {
    Context mContext;

    private Boolean m_bLocalImg;
    private ImageView m_uiImvPhoto, m_uiImvDel;
    private SquareImageView m_uiImvPhotoSquare01, m_uiImvPhotoSquare02;
    private CircleImageView m_uiImvfamlist;
    private String photoUrl, photoUrl2;
    private TextView m_tv_famname, m_tv_text, m_tv_businessname, m_tv_ingredient_num;
    private int m_type;

    public PhotoListLayout(Context context, int index, String pUrl, OnClickListener listener, int type) {
        super(context);
        mContext = context;
        m_bLocalImg = false;
        photoUrl = pUrl;
        m_type = type;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setTag(index);

        if(m_type ==0){
            View view = inflater.inflate(R.layout.layout_review_photo, this, true);
            initViews(view, listener);
        } else if(m_type ==1){
            View view2 = inflater.inflate(R.layout.layout_famlist_photo, this, true);
            initViews_fam(view2, listener);
        } else if(m_type ==2){
            View view2 = inflater.inflate(R.layout.layout_knowhowlist, this, true);
            initViews_knowhow(view2, listener);
        } else if(m_type ==3){
            View view2 = inflater.inflate(R.layout.layout_goodlist_photo, this, true);
            initViews_good(view2, listener);
        } else if(m_type ==4){
            View view2 = inflater.inflate(R.layout.layout_ingredient_pager, this, true);
            initViews_ingre(view2, listener);
        }
    }

    public PhotoListLayout(Context context, int index, String pUrl_01, String pUrl_02, OnClickListener listener, int ident) {
        super(context);
        mContext = context;
        m_bLocalImg = false;
        photoUrl = pUrl_01;
        photoUrl2 = pUrl_02;
        m_type = ident;

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setTag(index);

        View view = inflater.inflate(R.layout.layout_photo_list, this, true);
        initViews_photolist(view, listener);
    }

    private void initViews_photolist(View v, final OnClickListener listener) {
        m_uiImvPhotoSquare01 = (SquareImageView) v.findViewById(R.id.imv_photo1);
        m_uiImvPhotoSquare01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag((Integer)PhotoListLayout.this.getTag()*2);
                listener.onClick(view);
            }
        });
        m_uiImvPhotoSquare02 = (SquareImageView) v.findViewById(R.id.imv_photo2);
        m_uiImvPhotoSquare02.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag((Integer)PhotoListLayout.this.getTag()*2+1);
                listener.onClick(view);
            }
        });
    }

    private void initViews(View v, final OnClickListener listener) {
        m_uiImvPhoto = (ImageView) v.findViewById(R.id.imv_good);
        m_uiImvDel = (ImageView) v.findViewById(R.id.imv_delete);
        m_uiImvDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(PhotoListLayout.this.getTag());
                listener.onClick(view);
            }
        });
    }

    private void initViews_fam(View v, final OnClickListener listener) {
        m_uiImvfamlist = (CircleImageView) v.findViewById(R.id.imv_listoffam);
        //m_uiImvfamlist.setBorderWidth(2);
        //m_uiImvfamlist.setBorderColor(0xFFff2e74);
        m_uiImvfamlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(PhotoListLayout.this.getTag());
                listener.onClick(view);
            }
        });

        m_uiImvDel = (ImageView) v.findViewById(R.id.imv_delete);
        m_uiImvDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(PhotoListLayout.this.getTag());
                listener.onClick(view);
            }
        });
        m_tv_famname= (TextView) v.findViewById(R.id.tv_famname);
    }

    private void initViews_knowhow(View v, final OnClickListener listener) {
        m_tv_text = (TextView) v.findViewById(R.id.tv_text);
        m_uiImvDel = (ImageView) v.findViewById(R.id.imv_delete);
        m_uiImvDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(PhotoListLayout.this.getTag());
                listener.onClick(view);
            }
        });
    }

    private void initViews_good(View v, final OnClickListener listener) {
        m_uiImvfamlist = (CircleImageView) v.findViewById(R.id.imv_listoffam);
        //m_uiImvfamlist.setBorderWidth(2);
        //m_uiImvfamlist.setBorderColor(0xFFff2e74);
        m_uiImvfamlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(PhotoListLayout.this.getTag());
                listener.onClick(view);
            }
        });

        m_uiImvDel = (ImageView) v.findViewById(R.id.imv_delete);
        m_uiImvDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(PhotoListLayout.this.getTag());
                listener.onClick(view);
            }
        });
        m_tv_businessname= (TextView) v.findViewById(R.id.tv_businessname);
        m_tv_famname= (TextView) v.findViewById(R.id.tv_famname);
    }

    private void initViews_ingre(View v, final OnClickListener listener) {
        m_tv_ingredient_num = (TextView) v.findViewById(R.id.tv_ingredient_num);
        m_tv_ingredient_num.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(PhotoListLayout.this.getTag());
                listener.onClick(view);
            }
        });
    }

    public void setLocalImage(Boolean bLocal) {
        m_bLocalImg = bLocal;

        switch (m_type){
            case 0:
                if (m_bLocalImg) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 4;
                    Bitmap bmp = BitmapFactory.decodeFile(photoUrl, opts);

                    Glide.with(mContext).load(bmp).into(m_uiImvPhoto);
                } else {
                    if(m_uiImvPhoto!=null){
                        Glide.with(mContext).load(Net.URL_SERVER1 + photoUrl).into(m_uiImvPhoto);
                    }
                }
                break;
            case 1:
                if (m_bLocalImg) {
                    Glide.with(mContext).load(photoUrl).into(m_uiImvfamlist);
                } else {
                    int famindex = (Integer)PhotoListLayout.this.getTag();
                    if(UserManager.getInstance().arr_profile_photo_resID.get(famindex) != -1){
                        int resArr[] = {R.drawable.ic_male_1, R.drawable.ic_male_2, R.drawable.ic_male_3, R.drawable.ic_male_4,
                                R.drawable.ic_female_1, R.drawable.ic_female_2, R.drawable.ic_female_3, R.drawable.ic_female_4};
                        Glide.with(mContext).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(famindex)]).into(m_uiImvfamlist);
                    }else{
                        if(UserManager.getInstance().arr_profile_photo_bitmap.get(famindex) != null)
                            Glide.with(mContext).load(UserManager.getInstance().arr_profile_photo_bitmap.get(famindex)).into(m_uiImvfamlist);
                        else
                            Glide.with(mContext).load(UserManager.getInstance().arr_profile_photo_file.get(famindex).toString()).into(m_uiImvfamlist);
                    }
                }
                break;
            case 3:
                if (m_bLocalImg) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 4;
                    Bitmap bmp = BitmapFactory.decodeFile(photoUrl, opts);
                    Glide.with(mContext).load(bmp).into(m_uiImvfamlist);
                } else {
                    if(m_uiImvfamlist!=null){
                        Glide.with(mContext).load(photoUrl).into(m_uiImvfamlist);
                    }
                }
                break;
        }
    }

    public void setLocalImage(Boolean bLocal1, Boolean bLocal2) {
        switch (m_type){
            case 4:
                if (bLocal1) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 4;
                    Bitmap bmp = BitmapFactory.decodeFile(photoUrl, opts);

                    Glide.with(mContext).load(bmp).into(m_uiImvPhotoSquare01);
                }else{
                    m_uiImvPhotoSquare01.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(mContext).load(Net.URL_SERVER1 + photoUrl).into(m_uiImvPhotoSquare01);
                }
                if(bLocal2!=null){
                    if(bLocal2){
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inSampleSize = 4;
                        Bitmap bmp = BitmapFactory.decodeFile(photoUrl2, opts);
                        Glide.with(mContext).load(bmp).into(m_uiImvPhotoSquare02);
                    }else{
                        if(photoUrl2!="" && bLocal2==false){
                            m_uiImvPhotoSquare02.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(mContext).load(Net.URL_SERVER1 + photoUrl2).into(m_uiImvPhotoSquare02);
                        }else{
                            ((ViewGroup) m_uiImvPhotoSquare02.getParent()).removeView(m_uiImvPhotoSquare02);
                        }
                    }
                }else{
                    ((ViewGroup) m_uiImvPhotoSquare02.getParent()).removeView(m_uiImvPhotoSquare02);
                }
                break;
        }
    }

    public void setLocalImage(String ImgURL) {
        switch (m_type){
            case 0:
                break;
            case 1:
                Glide.with(mContext).load(Net.URL_SERVER1 + ImgURL).into(m_uiImvfamlist);
                break;
        }
    }

    public void setNoDelButton(){
        m_uiImvDel.setVisibility(GONE);
    }

    public void setFamName() {
        int i = (Integer)this.getTag();
        m_tv_famname.setText(UserManager.getInstance().HealthBaseInfo.get(i).member_name);
    }

    public void setName(String str_text) {
        switch (m_type) {
            case 0:
                break;
            case 1:
                m_tv_famname.setText(str_text);
                break;
            case 2:
                m_tv_text.setText(str_text);
                break;
            case 3:
                m_tv_famname.setText(str_text);
                break;
        }
    }
    public void setBusiness(String str_text) {
        m_tv_businessname.setText(str_text);
    }
    public void hideDeleteButton(){
        m_uiImvDel.setVisibility(GONE);
    }

    public void setIngreNum(int num) {
        int i = (Integer)this.getTag();
        m_tv_ingredient_num.setText(String.valueOf(num));
    }
}
