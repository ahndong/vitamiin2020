package app.vitamiin.com.login;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.home.ReviewWriteActivity;

public class ReviewSelectDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context _context;

//    ListView m_lsvLife;
    //LifeAdapter m_adapterCategory;
    private LifeAdapter2 m_adapterCategory;
    private String _title, category_myset;
    private int[] m_nSelectedIndex;
    private String[] arr_category_fromXML;

    private int _type = 0;
    private int[] _value;
    private int user_age = 0;
    private TextView m_tv_examinee, m_tv_pregnant, m_tv_lactating, m_tv_climacterium, m_tv_mysetting, m_tv_male, m_tv_female,
                      m_tv_infants, m_tv_young, m_tv_age_10s, m_tv_age_2040s, m_tv_age_over50s, m_tvcategory;
    private GridView m_lsvViewCategory;
    private ArrayList<String> m_arr_selected_category = new ArrayList<>();
    private ImageView category_expand, category_collapse;
    private LinearLayout lly_upper_area, lly_rower_area;
    private LinearLayout category_view, btn_category_expand, btn_category_collapse, m_lly_background;

    public ReviewSelectDialog(Context context, String title, String[] m_arr_category, String[] m_arr_disease_health, int type, int[] value) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        _context = context;
        //setBackgroundDrawable 이거 모지? by 동현
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_review_select);

        _title = title;
        ((TextView)findViewById(R.id.tv_title)).setText(_title);
        _type = type;
        _value = value;

        m_tv_examinee = (TextView) findViewById(R.id.tv_examinee);
        m_tv_pregnant = (TextView) findViewById(R.id.tv_pregnant);
        m_tv_lactating = (TextView) findViewById(R.id.tv_lactating);
        m_tv_climacterium = (TextView) findViewById(R.id.tv_climacterium);
        m_tv_mysetting = (TextView) findViewById(R.id.tv_mysetting);
        m_tv_male = (TextView) findViewById(R.id.tv_male);
        m_tv_female = (TextView) findViewById(R.id.tv_female);
        m_tv_infants = (TextView) findViewById(R.id.tv_infants);
        m_tv_young = (TextView) findViewById(R.id.tv_young);
        m_tv_age_10s = (TextView) findViewById(R.id.tv_age_10s);
        m_tv_age_2040s = (TextView) findViewById(R.id.tv_age_2040s);
        m_tv_age_over50s = (TextView) findViewById(R.id.tv_age_over50s);

        lly_upper_area = (LinearLayout) findViewById(R.id.lly_upper_area);
        lly_rower_area = (LinearLayout) findViewById(R.id.lly_rower_area);
        lly_upper_area.setSelected(false);
        lly_rower_area.setSelected(false);

        findViewById(R.id.tv_default).setOnClickListener(this);
        findViewById(R.id.tv_view_all).setOnClickListener(this);

        m_tv_examinee.setOnClickListener(this);
        m_tv_pregnant.setOnClickListener(this);
        m_tv_lactating.setOnClickListener(this);
        m_tv_climacterium.setOnClickListener(this);
        m_tv_mysetting.setOnClickListener(this);
        m_tv_male.setOnClickListener(this);
        m_tv_female.setOnClickListener(this);
        m_tv_infants.setOnClickListener(this);
        m_tv_young.setOnClickListener(this);
        m_tv_age_10s.setOnClickListener(this);
        m_tv_age_2040s.setOnClickListener(this);
        m_tv_age_over50s.setOnClickListener(this);

        if(_type==11){
            arr_category_fromXML = m_arr_category;
            m_nSelectedIndex = new int[11+arr_category_fromXML.length];
            for (int i = 0; i < 11+arr_category_fromXML.length; i++) {  m_nSelectedIndex[i] = 0;     }

            m_tvcategory = (TextView) findViewById(R.id.review_category);
            category_expand = (ImageView) findViewById(R.id.category_expand);
            category_collapse = (ImageView) findViewById(R.id.category_collapse);
            btn_category_expand = (LinearLayout) findViewById(R.id.btn_category_expand);
            btn_category_collapse = (LinearLayout) findViewById(R.id.btn_category_collapse);

            m_lsvViewCategory = (GridView) findViewById(R.id.lsv_category);

            findViewById(R.id.review_category).setOnClickListener(this);
            findViewById(R.id.category_expand).setOnClickListener(this);
            findViewById(R.id.category_collapse).setOnClickListener(this);
            findViewById(R.id.btn_category_expand).setOnClickListener(this);
            findViewById(R.id.btn_category_collapse).setOnClickListener(this);

            category_view = (LinearLayout) findViewById(R.id.lly_category_view);

            //카테고리 list 적용하여 adapter 초기화 생성
            m_adapterCategory = new LifeAdapter2(_context, new ArrayList<String>());
            for (int i = 0; i < arr_category_fromXML.length; i++){
                m_adapterCategory.add(arr_category_fromXML[i]);}

            m_lsvViewCategory.setAdapter(m_adapterCategory);
            m_lsvViewCategory.setOnItemClickListener(this);
        }else if(_type==12){
            m_nSelectedIndex = new int[11];
            for (int i = 0; i < 11; i++) {  m_nSelectedIndex[i] = 0;     }

            findViewById(R.id.lly_row_initbtn_allbtn).setVisibility(View.GONE);
            findViewById(R.id.lly_categoty_title).setVisibility(View.GONE);
            findViewById(R.id.lly_category_view).setVisibility(View.GONE);

            m_lly_background = (LinearLayout)findViewById(R.id.background);
            ViewGroup.LayoutParams params = m_lly_background.getLayoutParams();
            params.height = this.getContext().getResources().getDimensionPixelSize(R.dimen.dialog_small_hight);
            //params.height = 700;
            m_lly_background.setLayoutParams(params);
        }

        findViewById(R.id.tv_apply).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);

        m_tv_mysetting.setSelected(true);
        settingMyInfo();
//        findViewById(R.id.grv_list_disease).setOnClickListener(this);
//        findViewById(R.id.grv_list_disease).setOnClickListener(this);

//        if (!UserManager.getInstance().HealthBaseInfo.get(0).member_disease.isEmpty()) {
//            String[] arr = UserManager.getInstance().HealthBaseInfo.get(0).member_disease.split(",");
//            t1 = "";
//
//            for (int i = 0; i < arr.length; i++) {
//                if (i+1 < arr.length){
//                    t1 = t1 + list[Integer.parseInt(arr[i])] + ", ";
//                } else if (i+1 == arr.length) {
//                    t1 = t1 + list[Integer.parseInt(arr[i])];
//                }
//            }
//            m_tvdisease.setText(t1);
//        }

        //        LinearLayout llyView = (LinearLayout) findViewById(R.id.lly_view);
//        ViewGroup.LayoutParams params = llyView.getLayoutParams();
//        if (type == 14) {
//            params.height = Util.dipToPixels(context, 100);
//        } else if (type == 10 || type == 16) {
//            params.height = Util.dipToPixels(context, 65);
//        } else if (type == 3) {
//            params.height = Util.dipToPixels(context, 220);
//        } else if (type == 5) {
//            params.height = Util.dipToPixels(context, 190);
//        } else if (type == 11) {
//            params.height = Util.dipToPixels(context, 220);
//        } else if (type == 18 || type == 19 || type == 20 || type == 21 || type == 22) {
//            params.height = Util.dipToPixels(context, 220);
//        }
//        llyView.setLayoutParams(params);
        //
//        if (UserManager.getInstance().HealthBaseInfo.get(0).member_disease.length() > 0) {
//            String[] arr = UserManager.getInstance().HealthBaseInfo.get(0).member_disease.split(",");
//            for (int i = 0; i < arr.length; i++)
//                m_arr_selected_category.add(arr[i]);
//            m_adapterDisease.notifyDataSetChanged();
//        }
    }

    public void settingMyInfo(){
        activateUpperArea();

        m_tv_examinee.setSelected(false);
        m_tv_pregnant.setSelected(false);
        m_tv_lactating.setSelected(false);
        m_tv_climacterium.setSelected(false);
        //수험생, 임산부, 수유부, 갱년기 여부
        if (UserManager.getInstance().HealthBaseInfo.get(0).member_examinee == 0) {
            m_tv_examinee.setSelected(true);
        }else if (UserManager.getInstance().HealthBaseInfo.get(0).member_pregnant==0){
            m_tv_pregnant.setSelected(true);
        }else if (UserManager.getInstance().HealthBaseInfo.get(0).member_lactating==0){
            m_tv_lactating.setSelected(true);
        }else if (UserManager.getInstance().HealthBaseInfo.get(0).member_climacterium==0){
            m_tv_climacterium.setSelected(true);
        }else{
            activateLowerArea();
            if (UserManager.getInstance().member_sex == 0){
                m_tv_male.setSelected(true);
            } else{
                m_tv_female.setSelected(true);
            }

            GregorianCalendar cal = new GregorianCalendar();
            int curY = cal.get(Calendar.YEAR);
            user_age = curY - Integer.parseInt((UserManager.getInstance().member_birth).substring(0, 4)) + 1;

            if(user_age<4){
                m_tv_infants.setSelected(true);
            }else if(user_age<10){
                m_tv_young.setSelected(true);
            }else if(user_age<20){
                m_tv_age_10s.setSelected(true);
            }else if(user_age<50){
                m_tv_age_2040s.setSelected(true);
            }else{
                m_tv_age_over50s.setSelected(true);
            }
        }
        m_tv_mysetting.setSelected(true);
/*
        //disease 세팅
        m_arr_selected_disease.clear();
        disease_myset = "";
        //개인정보 기반 disease 항목에 들어가는 text 생성, disease grid view에 사용자 정보 적용
        if (UserManager.getInstance().HealthBaseInfo.get(0).member_disease.length() > 0) {
            String[] arr = UserManager.getInstance().HealthBaseInfo.get(0).member_disease.split(",");
            for (int i = 0; i < arr.length; i++) {
                m_arr_selected_disease.add(arr[i]);
                disease_myset = disease_myset + arr_disease_fromXML[Integer.parseInt(arr[i])] + ", ";
            }
            m_adapterDisease.notifyDataSetChanged();
            disease_myset = disease_myset.substring(0, disease_myset.length() -2);
            m_tvdisease.setText(disease_myset);
        }
*/
        if(_type==11){
            //category 세팅 시작
            m_arr_selected_category.clear();
            category_myset = "";
            //category list view에 사용자 정보 적용 지정한다. by 동현
            if (UserManager.getInstance().HealthBaseInfo.get(0).member_interest_health.length() > 0) {
                String[] arr = UserManager.getInstance().HealthBaseInfo.get(0).member_interest_health.split(",");
                for (int i = 0; i < arr.length; i++) {
                    m_arr_selected_category.add(arr[i]);
                    category_myset = category_myset + arr_category_fromXML[Integer.parseInt(arr[i])] + ", ";
                }
                m_adapterCategory.notifyDataSetChanged();
                category_myset = category_myset.substring(0, category_myset.length() -2);
                m_tvcategory.setText(category_myset);
            }
        }

        //m_nSelectedIndex[38] = Integer.toString(-1);
//        m_tvcategory.setText("전체 카테고리");
//        m_adapterCategory.notifyDataSetChanged();
//        if (_value > -1) {
//            m_nSelectedIndex = _value;
//            m_adapterDisease.setPos(m_nSelectedIndex);
//            m_adapterDisease.notifyDataSetChanged();
//        }

        //category 세팅
//        if (_value > -1) {
//            m_nSelectedIndex[38] = Integer.toString(_value);
//            m_adapterCategory.setPos(Integer.parseInt(m_nSelectedIndex[38]));
//            m_adapterCategory.notifyDataSetChanged();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_apply:
                if((!m_tv_male.isSelected() && !m_tv_female.isSelected() && !m_tv_infants.isSelected() &&
                   !m_tv_young.isSelected() && !m_tv_age_10s.isSelected() && !m_tv_age_2040s.isSelected() &&
                    !m_tv_age_over50s.isSelected())
                    &&
                    (!m_tv_examinee.isSelected() && !m_tv_pregnant.isSelected() && !m_tv_lactating.isSelected() && !m_tv_climacterium.isSelected() && !m_tv_mysetting.isSelected())
                    &&
                    (m_arr_selected_category.isEmpty())) {
                    new NoticeDialog(this.getContext(), "검색 조건을 입력해주세요~^^", "복용 대상과 성별 필터 중에서 \n최소 하나 이상 선택해주세요.","", true).show();
                    break;
                }
                /*if(!m_tv10s.isSelected() && !m_tv20s.isSelected() && !m_tv30s.isSelected() && !m_tv40s.isSelected() && !m_tvOver50s.isSelected()) {
                    new NoticeDialog(this.getContext(), "연령대 필터", "연령대 필터 중에서 \n하나 이상 선택해주세요.").show();
                    break;
                }*/
                /*if(m_arr_selected_category.size()==0) {
                    new NoticeDialog(this.getContext(), "카테고리 필터", "카테고리 필터 중에서 \n하나 이상 선택해주세요.").show();
                    break;
                }*/
                if (m_tv_examinee.isSelected()) m_nSelectedIndex[0] = 1;
                if (m_tv_pregnant.isSelected()) m_nSelectedIndex[1] = 1;
                if (m_tv_lactating.isSelected()) m_nSelectedIndex[2] = 1;
                if (m_tv_climacterium.isSelected()) m_nSelectedIndex[3] = 1;
                if (m_tv_male.isSelected()) m_nSelectedIndex[4] = 1;
                if (m_tv_female.isSelected()) m_nSelectedIndex[5] = 1;
                if (m_tv_infants.isSelected())       m_nSelectedIndex[6] = 1;
                if (m_tv_young.isSelected())       m_nSelectedIndex[7] = 1;
                if (m_tv_age_10s.isSelected())       m_nSelectedIndex[8] = 1;
                if (m_tv_age_2040s.isSelected())       m_nSelectedIndex[9] = 1;
                if (m_tv_age_over50s.isSelected())       m_nSelectedIndex[10] = 1;

/*                    //disease 선택사항
                for (int i = 9; i < 9+ arr_disease_fromXML.length; i++) {       m_nSelectedIndex[i] = 0;             }
                if (!m_arr_selected_disease.isEmpty()) {
                    for (int i = 0; i < m_arr_selected_disease.size(); i++) {
                        m_nSelectedIndex[9+Integer.parseInt(m_arr_selected_disease.get(i))] = 1;
                    }
                }
//                    for (int i=9; i < 9+arr_disease_fromXML.length;i++) {
//                        m_nSelectedIndex[9 + Integer.parseInt(m_arr_selected_disease.get(i))] = 1;
//                    }*/

                if(_type==11){
                    //category 선택사항
                    for (int i = 11; i < 11+arr_category_fromXML.length; i++) {       m_nSelectedIndex[i] = 0;             }
                    if (!m_arr_selected_category.isEmpty()) {
                        for (int i = 0; i < m_arr_selected_category.size(); i++) {
                            m_nSelectedIndex[11+Integer.parseInt(m_arr_selected_category.get(i))] = 1;
                        }
                    }
//                    for (int i=9+arr_disease_fromXML.length; i < 9+arr_disease_fromXML.length+arr_category_fromXML.length;i++) {
//                        m_nSelectedIndex[9+arr_disease_fromXML.length + Integer.parseInt(m_arr_selected_category.get(i))] = 1;
//                    }

                    ((MainActivity) _context).setReviewDetailCategory(m_nSelectedIndex);
                }else if(_type==12){
                    ((ReviewWriteActivity) _context).setPerson(m_nSelectedIndex);
                }

//                    if(m_arr_selected_category.isEmpty()){
//                        for(int i=9; i<32;i++){
//                            m_nSelectedIndex[i] = "";
//                        }} else {
//                        for(int i=9; i<32; i++){
//                            m_nSelectedIndex[i] = "";
//                        }
//                            for(int i=0; i<m_arr_selected_category.size(); i++){
//                                m_nSelectedIndex[9+ Integer.parseInt(m_arr_selected_category.get(i))] = m_adapterCategory.getItem(Integer.parseInt(m_arr_selected_category.get(i))).replaceAll("\n", "") + ", ";
//                        }
//                    }

                /*//현재 입력이 되어 있는 index 찾기
                if (m_arr_selected_category.isEmpty()) {     m_nSelectedIndex[38] = "";           }
                else {                                       m_nSelectedIndex[38] = m_adapterCategory.getItem(0);
                }*/
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_examinee:
                activateUpperArea();
                if (m_tv_examinee.isSelected()) {
                    m_tv_examinee.setSelected(false);
                } else {
                    m_tv_examinee.setSelected(true);
                    m_tv_pregnant.setSelected(false);
                    m_tv_lactating.setSelected(false);
                    m_tv_climacterium.setSelected(false);
                    m_tv_mysetting.setSelected(false);
                }
                break;
            case R.id.tv_pregnant:
                activateUpperArea();
                if (m_tv_pregnant.isSelected()) {
                    m_tv_pregnant.setSelected(false);
                } else {
                    m_tv_examinee.setSelected(false);
                    m_tv_pregnant.setSelected(true);
                    m_tv_lactating.setSelected(false);
                    m_tv_climacterium.setSelected(false);
                    m_tv_mysetting.setSelected(false);
                }
                break;
            case R.id.tv_lactating:
                activateUpperArea();
                if (m_tv_lactating.isSelected()) {
                    m_tv_lactating.setSelected(false);
                } else {
                    m_tv_examinee.setSelected(false);
                    m_tv_pregnant.setSelected(false);
                    m_tv_lactating.setSelected(true);
                    m_tv_climacterium.setSelected(false);
                    m_tv_mysetting.setSelected(false);
                }
                break;
            case R.id.tv_climacterium:
                activateUpperArea();
                if (m_tv_climacterium.isSelected()) {
                    m_tv_climacterium.setSelected(false);
                } else {
                    m_tv_examinee.setSelected(false);
                    m_tv_pregnant.setSelected(false);
                    m_tv_lactating.setSelected(false);
                    m_tv_climacterium.setSelected(true);
                    m_tv_mysetting.setSelected(false);
                }
                break;
            case R.id.tv_male:
                activateLowerArea();
                if (m_tv_male.isSelected()) {
                    m_tv_male.setSelected(false);
                    if(_type==12){
                        m_tv_female.setSelected(true);
                    }
                } else {
                    m_tv_male.setSelected(true);
                    if(_type==12){
                        m_tv_female.setSelected(false);
                    }
                }
                break;
            case R.id.tv_female:
                activateLowerArea();
                if (m_tv_female.isSelected()) {
                    m_tv_female.setSelected(false);
                    if(_type==12){
                        m_tv_male.setSelected(true);
                    }
                } else {
                    m_tv_female.setSelected(true);
                    if(_type==12){
                        m_tv_male.setSelected(false);
                    }
                }
                break;
            case R.id.tv_infants:
                activateLowerArea();
                if(_type==11){
                    if (m_tv_infants.isSelected()) {
                        m_tv_infants.setSelected(false);
                    } else {
                        m_tv_infants.setSelected(true);
                    }
                }else if(_type==12){
                    m_tv_infants.setSelected(true);
                    m_tv_young.setSelected(false);
                    m_tv_age_10s.setSelected(false);
                    m_tv_age_2040s.setSelected(false);
                    m_tv_age_over50s.setSelected(false);
                }
                break;
            case R.id.tv_young:
                activateLowerArea();
                if(_type==11){
                    if (m_tv_young.isSelected()) {
                        m_tv_young.setSelected(false);
                    } else {
                        m_tv_young.setSelected(true);
                    }
                }else if(_type==12){
                    m_tv_infants.setSelected(false);
                    m_tv_young.setSelected(true);
                    m_tv_age_10s.setSelected(false);
                    m_tv_age_2040s.setSelected(false);
                    m_tv_age_over50s.setSelected(false);
                }
                break;
            case R.id.tv_age_10s:
                activateLowerArea();
                if(_type==11){
                    if (m_tv_age_10s.isSelected()) {
                        m_tv_age_10s.setSelected(false);
                    } else {
                        m_tv_age_10s.setSelected(true);
                    }
                }else if(_type==12){
                    m_tv_infants.setSelected(false);
                    m_tv_young.setSelected(false);
                    m_tv_age_10s.setSelected(true);
                    m_tv_age_2040s.setSelected(false);
                    m_tv_age_over50s.setSelected(false);
                }
                break;
            case R.id.tv_age_2040s:
                activateLowerArea();
                if(_type==11){
                    if (m_tv_age_2040s.isSelected()) {
                        m_tv_age_2040s.setSelected(false);
                    } else {
                        m_tv_age_2040s.setSelected(true);
                    }
                }else if(_type==12){
                    m_tv_infants.setSelected(false);
                    m_tv_young.setSelected(false);
                    m_tv_age_10s.setSelected(false);
                    m_tv_age_2040s.setSelected(true);
                    m_tv_age_over50s.setSelected(false);
                }
                break;
            case R.id.tv_age_over50s:
                activateLowerArea();
                if(_type==11){
                    if (m_tv_age_over50s.isSelected()) {
                        m_tv_age_over50s.setSelected(false);
                    } else {
                        m_tv_age_over50s.setSelected(true);
                    }
                }else if(_type==12){
                    m_tv_infants.setSelected(false);
                    m_tv_young.setSelected(false);
                    m_tv_age_10s.setSelected(false);
                    m_tv_age_2040s.setSelected(false);
                    m_tv_age_over50s.setSelected(true);
                }
                break;
            case R.id.tv_default:
                lly_upper_area.setVisibility(View.GONE);
                lly_rower_area.setVisibility(View.GONE);

                m_tv_mysetting.setSelected(false);
                m_tv_examinee.setSelected(false);
                m_tv_pregnant.setSelected(false);
                m_tv_lactating.setSelected(false);
                m_tv_climacterium.setSelected(false);
                m_tv_male.setSelected(false);
                m_tv_female.setSelected(false);
                m_tv_infants.setSelected(false);
                m_tv_young.setSelected(false);
                m_tv_age_10s.setSelected(false);
                m_tv_age_2040s.setSelected(false);
                m_tv_age_over50s.setSelected(false);

                //카테고리 초기화
                m_arr_selected_category = new ArrayList<>();
                m_adapterCategory.notifyDataSetChanged();
                m_tvcategory.setText("선택하지 않음");
                break;
            case R.id.tv_view_all:
                lly_upper_area.setVisibility(View.GONE);
                lly_rower_area.setVisibility(View.GONE);

                //아래 코드는 전체 보기임. by 동현
                m_tv_mysetting.setSelected(false);
                m_tv_examinee.setSelected(true);
                m_tv_pregnant.setSelected(true);
                m_tv_lactating.setSelected(true);
                m_tv_climacterium.setSelected(true);
                m_tv_male.setSelected(true);
                m_tv_female.setSelected(true);
                m_tv_infants.setSelected(true);
                m_tv_young.setSelected(true);
                m_tv_age_10s.setSelected(true);
                m_tv_age_2040s.setSelected(true);
                m_tv_age_over50s.setSelected(true);

                //카테고리 모두 선택
                m_arr_selected_category = new ArrayList<>();
                for (int i = 0; i < arr_category_fromXML.length; i++) { m_arr_selected_category.add(Integer.toString(i)); }
                m_adapterCategory.notifyDataSetChanged();
                m_tvcategory.setText("전체 카테고리");
                break;
            case R.id.tv_mysetting:
                settingMyInfo();
                break;
/*            case R.id.btn_category_expand:
                expendCategory();
                break;
            case R.id.category_expand:
                expendCategory();
                break;
            case R.id.btn_category_collapse:
                collapseCategory();
                category_view.setVisibility(View.VISIBLE);
                break;
            case R.id.category_collapse:
                collapseCategory();
                category_view.setVisibility(View.VISIBLE);
                break;
            case R.id.review_category:
                if(btn_category_collapse.getVisibility()==View.VISIBLE) {
                    collapseCategory();
                    category_view.setVisibility(View.VISIBLE);
                }
                else
                    expendCategory();
                break;*/
        }
    }

    public void activateUpperArea(){
        lly_upper_area.setVisibility(View.VISIBLE);
        lly_rower_area.setVisibility(View.VISIBLE);
        lly_upper_area.setSelected(true);
        lly_rower_area.setSelected(false);

        m_tv_mysetting.setSelected(false);

        m_tv_male.setSelected(false);
        m_tv_female.setSelected(false);
        m_tv_infants.setSelected(false);
        m_tv_young.setSelected(false);
        m_tv_age_10s.setSelected(false);
        m_tv_age_2040s.setSelected(false);
        m_tv_age_over50s.setSelected(false);
    }
    public void activateLowerArea(){
        lly_upper_area.setVisibility(View.VISIBLE);
        lly_rower_area.setVisibility(View.VISIBLE);
        lly_upper_area.setSelected(false);
        lly_rower_area.setSelected(true);

        m_tv_mysetting.setSelected(false);

        m_tv_examinee.setSelected(false);
        m_tv_pregnant.setSelected(false);
        m_tv_lactating.setSelected(false);
        m_tv_climacterium.setSelected(false);
    }
/*    public void expendCategory(){
        category_expand.setVisibility(View.GONE);
        category_collapse.setVisibility(View.VISIBLE);
        btn_category_expand.setVisibility(View.GONE);
        btn_category_collapse.setVisibility(View.VISIBLE);
        category_view.setVisibility(View.VISIBLE);
        m_lsvViewCategory.setVisibility(View.VISIBLE);

//        disease_view.setVisibility(View.GONE);
//        m_grvViewDisease.setVisibility(View.GONE);
//        if(disease_collapse.getVisibility() == View.VISIBLE){
//            disease_collapse.setVisibility(View.GONE);
//            disease_expand.setVisibility(View.VISIBLE);}

//        if(m_nSelectedIndex[38] != Integer.toString(-1)){
//            String l1 = m_adapterCategory.getItem(Integer.parseInt(m_nSelectedIndex[38]));
////                    for(int i = 0; i < m_arr_selected_category.size(); i++){
////                        if (i+1 < m_arr_selected_category.size()){
////                            l1 = l1 + m_adapterCategory.getItem(Integer.parseInt(m_arr_selected_category.get(i))) + ", ";
////                        } else if (i+1 == m_arr_selected_category.size()) {
////                            l1 = l1 + m_adapterCategory.getItem(Integer.parseInt(m_arr_selected_category.get(i)));
////                        }
////                    }
//            m_tvcategory.setText(l1);
//        } else {m_tvcategory.setText("전체 카테고리");}
//        if(!m_arr_selected_disease.isEmpty()){
//            String l2 = "";
//            for(int i = 0; i < m_arr_selected_disease.size(); i++){
//                if (i+1 < m_arr_selected_disease.size()){
//                    l2 = l2 + m_adapterDisease.getItem(Integer.parseInt(m_arr_selected_disease.get(i))) + ", ";
//                } else if (i+1 == m_arr_selected_disease.size()) {
//                    l2 = l2 + m_adapterDisease.getItem(Integer.parseInt(m_arr_selected_disease.get(i)));
//                }
//            }
//            m_tvdisease.setText(l2);
//        } else {m_tvdisease.setText("전체 질환");}
    }

    public void collapseCategory(){
        category_expand.setVisibility(View.VISIBLE);
        btn_category_expand.setVisibility(View.VISIBLE);
        category_collapse.setVisibility(View.GONE);
        btn_category_collapse.setVisibility(View.GONE);
        category_view.setVisibility(View.GONE);
        m_lsvViewCategory.setVisibility(View.GONE);

//        disease_view.setVisibility(View.GONE);
//        m_grvViewdisease.setVisibility(View.GONE);
//        if(disease_collapse.getVisibility() == View.VISIBLE){
//            disease_collapse.setVisibility(View.GONE);
//            disease_expand.setVisibility(View.VISIBLE);}
//        if(m_nSelectedIndex[38] != Integer.toString(-1)){
//            String l1 = m_adapterCategory.getItem(Integer.parseInt(m_nSelectedIndex[38]));
////                    for(int i = 0; i < m_arr_selected_category.size(); i++){
////                        if (i+1 < m_arr_selected_category.size()){
////                            l1 = l1 + m_adapterCategory.getItem(Integer.parseInt(m_arr_selected_category.get(i))) + ", ";
////                        } else if (i+1 == m_arr_selected_category.size()) {
////                            l1 = l1 + m_adapterCategory.getItem(Integer.parseInt(m_arr_selected_category.get(i)));
////                        }
////                    }
//            m_tvcategory.setText(l1);
//        } else {m_tvcategory.setText("선택하지 않음");}

        if(!m_arr_selected_category.isEmpty()){
            if(m_arr_selected_category.size()== arr_category_fromXML.length){
                m_tvcategory.setText("전체 카테고리");
            }else{
                String l2 = "";
                for(int i = 0; i < m_arr_selected_category.size(); i++){
                    l2 = l2 + m_adapterCategory.getItem(Integer.parseInt(m_arr_selected_category.get(i))) + ", ";
                }
                l2 = l2.substring(0, l2.length()-2);
                m_tvcategory.setText(l2);
            }
        } else {m_tvcategory.setText("선택하지 않음");}
//        if(!m_arr_selected_disease.isEmpty()){
//            String l2 = "";
//            for(int i = 0; i < m_arr_selected_disease.size(); i++){
//                if (i+1 < m_arr_selected_disease.size()){
//                    l2 = l2 + m_adapterDisease.getItem(Integer.parseInt(m_arr_selected_disease.get(i))) + ", ";
//                } else if (i+1 == m_arr_selected_disease.size()) {
//                    l2 = l2 + m_adapterDisease.getItem(Integer.parseInt(m_arr_selected_disease.get(i)));
//                }
//            }
//            m_tvdisease.setText(l2);
//        } else {m_tvdisease.setText("전체 질환");}
    }*/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(category_view.getVisibility()==View.VISIBLE){
            if (m_arr_selected_category.contains("" + position)){
                m_arr_selected_category.remove("" + position);}
            else {
                m_arr_selected_category.add("" + position);
            }
//            if(m_nSelectedIndex[38] == Integer.toString(position)){
//                m_nSelectedIndex[38] = Integer.toString(-1);
//            } else{
//            m_nSelectedIndex[38] = Integer.toString(position);
//            m_adapterCategory.setPos(Integer.parseInt(m_nSelectedIndex[38]));
//            m_adapterCategory.notifyDataSetChanged();
//            }
            m_adapterCategory.notifyDataSetChanged();
        }/* else if(disease_view.getVisibility()==View.VISIBLE){
            if (m_arr_selected_disease.contains("" + position)){
                m_arr_selected_disease.remove("" + position);}
            else {
                    m_arr_selected_disease.add("" + position);
            }
            m_adapterDisease.notifyDataSetChanged();
        }*/
    }

    public class LifeAdapter2 extends ArrayAdapter<String> {

        private Context m_context;
        int m_SelectedIndex = -1;

        public LifeAdapter2(Context context, ArrayList<String> arrayItem) {
            super(context, 0, arrayItem);
            m_context = context;
        }

        public void setPos(int index) {
            m_SelectedIndex = index;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder itemHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_row_simple_height65dp,
                        parent, false);
                itemHolder = new ItemHolder(convertView);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) convertView.getTag();
            }

            itemHolder.showInfo(position);

            return convertView;
        }


        public class ItemHolder {

            //                        TextView m_tvTitle;
            TextView m_uiTvItem;

            public ItemHolder(View v) {
                m_uiTvItem = (TextView) v.findViewById(R.id.tv_item);
            }

            public void showInfo(int position) {

                String info = getItem(position);
                m_uiTvItem.setText(info);

                if (m_SelectedIndex == position) {
                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                } else {
                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey));
                }
                if(category_view.getVisibility()==View.VISIBLE){
                    if (m_arr_selected_category.contains("" + position)) {
                        int div = position % 8;
                        if (div == 1 || div == 3 || div == 4 || div == 6) {
                            m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                        } else {
                            m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_2));
                        }

                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.white_color));
                    } else {
//                    if (m_arr_selected_disease.size() == 5)
//                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey_1));
//                    else
//                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.black_color));
                        int div = position % 8;
                        if (div == 1 || div == 3 || div == 4 || div == 6) {
                            m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
                        } else {
                            m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey_white));
                        }
                    }
                }
                //m_tvcategory.setText("click item");
                if(!m_arr_selected_category.isEmpty()){
                    if(m_arr_selected_category.size()== arr_category_fromXML.length){
                        m_tvcategory.setText("전체 카테고리");
                    }else{
                        String l2 = "";
                        for(int i = 0; i < m_arr_selected_category.size(); i++){
                            l2 = l2 + m_adapterCategory.getItem(Integer.parseInt(m_arr_selected_category.get(i))) + ", ";
                        }
                        l2 = l2.substring(0, l2.length()-2);
                        m_tvcategory.setText(l2);
                    }
                } else {m_tvcategory.setText("선택하지 않음");}
                /*
                else if(disease_view.getVisibility()==View.VISIBLE){
                    if (m_arr_selected_disease.contains("" + position)) {
                        int div = position % 8;
                        if (div == 1 || div == 3 || div == 4 || div == 6) {
                            m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                        } else {
                            m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_2));
                        }

                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.white_color));
                    } else {
//                    if (m_arr_selected_disease.size() == 5)
//                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey_1));
//                    else
//                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.black_color));
                        int div = position % 8;
                        if (div == 1 || div == 3 || div == 4 || div == 6) {
                            m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
                        } else {
                            m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey_white));
                        }
                    }
                }*/
            }
        }
    }
//    public class LifeAdapter1 extends ArrayAdapter<String> {
//
//        private Context m_context;
//        int m_SelectedIndex = -1;
//
//        public LifeAdapter1(Context context, ArrayList<String> arrayItem) {
//            super(context, 0, arrayItem);
//            m_context = context;
//        }
//
//        public void setPos(int index) {
//            m_SelectedIndex = index;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ItemHolder itemHolder;
//            if (convertView == null) {
//                LayoutInflater inflater = (LayoutInflater) getContext()
//                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                convertView = inflater.inflate(R.layout.list_row_disease,
//                        parent, false);
//                itemHolder = new ItemHolder(convertView);
//                convertView.setTag(itemHolder);
//            } else {
//                itemHolder = (ItemHolder) convertView.getTag();
//            }
//
//            itemHolder.showInfo(position);
//
//            return convertView;
//        }
//
//
//        public class ItemHolder {
//
////            TextView m_tvTitle;
//            TextView m_uiTvItem;
//
//            public ItemHolder(View v) {
//                m_uiTvItem = (TextView) v.findViewById(R.id.tv_item);
//            }
//
//            public void showInfo(int position) {
//
//                String info = getItem(position);
//                m_uiTvItem.setText(info);
//
//                if (m_SelectedIndex == position) {
//                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.main_color_1));
//                } else {
//                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey));
//                }
//                if (m_arr_selected_category.contains("" + position)) {
//                    int div = position % 8;
//                    if (div == 1 || div == 3 || div == 4 || div == 6) {
//                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_1));
//                    } else {
//                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_2));
//                    }
//
//                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.white_color));
//                } else {
////                    if (m_arr_selected_category.size() == 5)
////                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey_1));
////                    else
////                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.black_color));
//                    int div = position % 8;
//                    if (div == 1 || div == 3 || div == 4 || div == 6) {
//                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
//                    } else {
//                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey_white));
//                    }
//
//            }
//        }
//    }

    public class LifeAdapter extends ArrayAdapter<String> {

        private Context m_context;
        int m_SelectedIndex = -1;

        public LifeAdapter(Context context, ArrayList<String> arrayItem) {
            super(context, 0, arrayItem);
            m_context = context;
        }

        public void setPos(int index) {
            m_SelectedIndex = index;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ReviewSelectDialog.LifeAdapter.ItemHolder itemHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_row_simple_height30dp,
                        parent, false);
                itemHolder = new ReviewSelectDialog.LifeAdapter.ItemHolder(convertView);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ReviewSelectDialog.LifeAdapter.ItemHolder) convertView.getTag();
            }

            itemHolder.showInfo(position);

            return convertView;
        }

        public class ItemHolder {
            TextView m_tvTitle;

            public ItemHolder(View v) {
                m_tvTitle = (TextView) v.findViewById(R.id.tv_date);
            }

            public void showInfo(int position) {

                String info = getItem(position);
                m_tvTitle.setText(info);

                if(m_nSelectedIndex[38] != -1){
                    if (m_SelectedIndex == position) {
                        m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                    } else {
                        m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey));
                    }
                } else {
                    if (m_arr_selected_category.contains("" + position)) {
                        m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                    } else {
                        m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey));
                    }
                }
            }
        }
    }
}



