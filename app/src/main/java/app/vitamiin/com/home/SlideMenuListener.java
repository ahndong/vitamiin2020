package app.vitamiin.com.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.GregorianCalendar;

import app.vitamiin.com.R;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;

public class SlideMenuListener implements OnClickListener {
    private Context m_context;
    private SlideMenuClickListener m_listener;

    TextView m_tvEmail, m_tvNickName, m_tvName, m_tvAge;
    ImageView m_imvImage;

    public interface SlideMenuClickListener {
        void gotoMyPage();
        void gotoHealth();
        void gotoSetting();
        void gotoNotice();
        void gotoFaq();
        void gotoCenter();
        void gotoRecommendToSns();
        void gotoLogout();
    }

    public SlideMenuListener(Context context, SlideMenuClickListener listener, View v) {
        m_context = context;
        m_listener = listener;

        m_tvEmail = (TextView) v.findViewById(R.id.tv_email);
        m_tvNickName = (TextView) v.findViewById(R.id.tv_nickname);
        m_tvName = (TextView) v.findViewById(R.id.tv_name);
        m_tvAge = (TextView) v.findViewById(R.id.tv_age);
        m_imvImage = (ImageView) v.findViewById(R.id.imv_profile);
        updateMenu();

        v.findViewById(R.id.imv_profile).setOnClickListener(this);

        v.findViewById(R.id.tv_mypage).setOnClickListener(this);
        v.findViewById(R.id.tv_health).setOnClickListener(this);
        v.findViewById(R.id.tv_setting).setOnClickListener(this);
        v.findViewById(R.id.tv_notice).setOnClickListener(this);
        v.findViewById(R.id.tv_faq).setOnClickListener(this);
        v.findViewById(R.id.tv_center).setOnClickListener(this);
        v.findViewById(R.id.tv_recommend_to_sns).setOnClickListener(this);
    }

    public void updateMenu() {
        GregorianCalendar cal = new GregorianCalendar();
        int curY = cal.get(Calendar.YEAR);
        int user_age = 0;
        if(UserManager.getInstance().member_birth!=null)
            user_age = curY - Integer.parseInt((UserManager.getInstance().member_birth).substring(0, 4)) + 1;

        m_tvEmail.setText(UserManager.getInstance().member_id);
        m_tvNickName.setText(UserManager.getInstance().member_nick_name);
        m_tvName.setText(UserManager.getInstance().member_name);
        m_tvAge.setText("(" + String.valueOf(user_age) + "세)");

        int resArr[] = {R.drawable.ic_male_1, R.drawable.ic_male_2, R.drawable.ic_male_3, R.drawable.ic_male_4,
                R.drawable.ic_female_1, R.drawable.ic_female_2, R.drawable.ic_female_3, R.drawable.ic_female_4};
        if(UserManager.getInstance().arr_profile_photo_resID.size()!=0){
            if(UserManager.getInstance().arr_profile_photo_resID.get(0)!=-1){
                Glide.with(m_context).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(m_imvImage);
            }else{
                if(UserManager.getInstance().arr_profile_photo_file.get(0)!=null)
                    Glide.with(m_context).load(UserManager.getInstance().arr_profile_photo_file.get(0).getPath()).into(m_imvImage);
                else{
                    if(new Util().getAutoImageResID(0)!=-1)
                        Glide.with(m_context).load(resArr[new Util().getAutoImageResID(0)]).into(m_imvImage);
                    else
                        Glide.with(m_context).load(resArr[1]).into(m_imvImage);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mypage:
                m_listener.gotoMyPage();
                break;
            case R.id.tv_health:
                m_listener.gotoHealth();
                break;
            case R.id.tv_setting:
                m_listener.gotoSetting();
                break;
            case R.id.tv_notice:
                m_listener.gotoNotice();
                break;
            case R.id.tv_faq:
                m_listener.gotoFaq();
                break;
            case R.id.tv_center:
                m_listener.gotoCenter();
                break;
            case R.id.tv_recommend_to_sns:
                m_listener.gotoRecommendToSns();
                break;
        }
    }
}
