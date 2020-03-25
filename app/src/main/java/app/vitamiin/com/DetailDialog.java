package app.vitamiin.com;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.Model.IngredientInfo;
import app.vitamiin.com.home.PhotoListLayout;

public class DetailDialog extends Dialog implements View.OnClickListener {
    private Context _context;
    private IngredientInfo m_Info;
    private ArrayList<IngredientInfo> m_arrInfo;
    private LinearLayout m_llyNumIngre;

    public DetailDialog(Context context, ArrayList<IngredientInfo> arrIwnfo) {
        super(context);
        _context = context;
        m_arrInfo = arrIwnfo;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_ingredient);

        initView(0);

        m_llyNumIngre = (LinearLayout) findViewById(R.id.lly_num_ingredient);
        for(int i = 0; i< m_arrInfo.size(); i++)
            addIngreNum(i);
    }

    private void addIngreNum(int i){
        final PhotoListLayout layout = new PhotoListLayout(_context, i, "", new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                initView((Integer) view.getTag());
            }
        }, 4);
        layout.setIngreNum(i+1);
        m_llyNumIngre.addView(layout, i);
    }

    private void initView(int i){
        m_Info = m_arrInfo.get(i);
        findViewById(R.id.tv_close).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_name)).setText(m_Info._name_kor);

        ((TextView) findViewById(R.id.tv_name_origin)).setText("선택한 원재료명: " + m_Info._name);
        if(m_Info._type!=3)
            findViewById(R.id.tv_grade).setVisibility(View.GONE);
        if(m_Info._name_eng.length()==0)
            findViewById(R.id.tv_name_eng).setVisibility(View.GONE);

        if(m_Info._type==1 || m_Info._type==2 || m_Info._type==3){
            if(m_Info._type==1)
                ((TextView) findViewById(R.id.tv_type)).setText("영양소");
            else if(m_Info._type==2)
                ((TextView) findViewById(R.id.tv_type)).setText("기능성 원료");
            else
                ((TextView) findViewById(R.id.tv_type)).setText("개별인정형 원료");

            Glide.with(_context).load(R.drawable.new_ingredient_icon_symptom).into((ImageView) findViewById(R.id.imv_icon1));
            Glide.with(_context).load(R.drawable.new_ingredient_icon_daily).into((ImageView) findViewById(R.id.imv_icon2));
            Glide.with(_context).load(R.drawable.new_ingredient_icon_caution).into((ImageView) findViewById(R.id.imv_icon3));

            ((TextView) findViewById(R.id.tv_title1)).setText("기능");
            ((TextView) findViewById(R.id.tv_description1)).setText(m_Info._functionality);
            ((TextView) findViewById(R.id.tv_title2)).setText("하루 섭취량");
            ((TextView) findViewById(R.id.tv_description2)).setText(m_Info._daily);
            if(m_Info._caution.length()==0){
                findViewById(R.id.tv_title3).setVisibility(View.GONE);
                findViewById(R.id.tv_description3).setVisibility(View.GONE);
                findViewById(R.id.imv_icon3).setVisibility(View.GONE);
            }else{
                ((TextView) findViewById(R.id.tv_title3)).setText("주의 사항");
                ((TextView) findViewById(R.id.tv_description3)).setText(m_Info._caution);
            }
            if(m_Info._class.length()!=0) {
                findViewById(R.id.tv_grade).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.tv_grade)).setText(m_Info._class);
            }else
                findViewById(R.id.tv_grade).setVisibility(View.GONE);
        }
        if(m_Info._type==4 || m_Info._type==5 || m_Info._type==6) {
            ((TextView) findViewById(R.id.tv_type)).setText("식품첨가물");
            ((TextView) findViewById(R.id.tv_name_eng)).setText(m_Info._name_eng);

            Glide.with(_context).load(R.drawable.new_ingredient_icon_use).into((ImageView) findViewById(R.id.imv_icon1));
            Glide.with(_context).load(R.drawable.new_ingredient_icon_definition).into((ImageView) findViewById(R.id.imv_icon2));
            Glide.with(_context).load(R.drawable.new_ingredient_icon_class).into((ImageView) findViewById(R.id.imv_icon3));

            ((TextView) findViewById(R.id.tv_title1)).setText("주요용도");
            ((TextView) findViewById(R.id.tv_description1)).setText(m_Info._functionality);
            ((TextView) findViewById(R.id.tv_title2)).setText("정의");
            ((TextView) findViewById(R.id.tv_description2)).setText(m_Info._definition);
            ((TextView) findViewById(R.id.tv_title3)).setText("식품첨가물 구분");
            if (m_Info._type == 4)
                ((TextView) findViewById(R.id.tv_description3)).setText("화학적합성품");
            else if (m_Info._type == 5)
                ((TextView) findViewById(R.id.tv_description3)).setText("천연첨가물");
            else
                ((TextView) findViewById(R.id.tv_description3)).setText("EWG 12가지 유해성분");
        }
        if(m_Info._type==7) {
            ((TextView) findViewById(R.id.tv_type)).setText("기타원재료");
            ((TextView) findViewById(R.id.tv_name_eng)).setText(m_Info._name_eng);

            Glide.with(_context).load(R.drawable.new_ingredient_icon_use).into((ImageView) findViewById(R.id.imv_icon1));
            Glide.with(_context).load(R.drawable.new_ingredient_icon_definition).into((ImageView) findViewById(R.id.imv_icon2));
            Glide.with(_context).load(R.drawable.new_ingredient_icon_caution).into((ImageView) findViewById(R.id.imv_icon3));

            ((TextView) findViewById(R.id.tv_title1)).setText("주요용도");
            ((TextView) findViewById(R.id.tv_description1)).setText(m_Info._functionality);
            ((TextView) findViewById(R.id.tv_title2)).setText("정의");
            ((TextView) findViewById(R.id.tv_description2)).setText(m_Info._definition);
            if(m_Info._caution.length()==0){
                findViewById(R.id.tv_title3).setVisibility(View.GONE);
                findViewById(R.id.tv_description3).setVisibility(View.GONE);
                findViewById(R.id.imv_icon3).setVisibility(View.GONE);
            }else{
                ((TextView) findViewById(R.id.tv_title3)).setText("주의 사항");
                ((TextView) findViewById(R.id.tv_description3)).setText(m_Info._caution);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_close:
                dismiss();
                break;
        }
    }
}
