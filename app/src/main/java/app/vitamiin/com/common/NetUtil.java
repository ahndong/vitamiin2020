package app.vitamiin.com.common;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import app.vitamiin.com.Model.FoodInfo;
import app.vitamiin.com.Model.FoodInfoDeux;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.Model.SharedPref;
import app.vitamiin.com.Model.WikiInfo;
import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;

import static android.R.id.message;

public class NetUtil {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////  사용자 정보 관련 API///////////////////////////////////////////
    public static final int apis_REG_FCMTOKEN_TO_SERVER = 1001;
    public static final int apis_CHECK_ID = 1003;
    public static final int apis_CHECK_NICK = 1004;
//    public static final int POST_FIELD_ACT_FINDID = 1988645127624871;
    public static final int apis_FIND_PW = 1002;
    public static final int apis_UPDATE_PW = 1015;
    public static final int apis_REGISTER_USER = 1501;
    public static final int apis_SIGNOUT = 1016;
    public static final int apis_LOGIN = 1005;
    public static final int apis_UPDATE_PROFILE = 1006;
    public static final int apis_CHANGE_PHOTO = 872761;
    public static final int apis_REGISTER_FAMILY = 1018;
    public static final int apis_UPDATE_BASE = 1009;
    public static final int apis_GET_FAMILY = 1017;
    public static final int apis_DELETE_FAMILY = 1019;
    public static final int apis_STORE_INFO_LIVING = 1007;
    public static final int apis_STORE_INFO_BODY_ALL = 1008;
    public static final int apis_STORE_INFO_DETAIL_1OF3 = 1010;
    public static final int apis_STORE_INFO_DETAIL_2OF3 = 1011;
//    public static final int apis_STORE_INFO_DETAIL_3OF3_1_Allergy = 12341053;
//    public static final int apis_STORE_INFO_DETAIL_3OF3_2_Drug = 12341070;
//    public static final int apis_STORE_INFO_DETAIL_3OF3_3_DrugHealthFood = 12341071;
//    public static final int apis_STORE_INFO_DETAIL_3OF3_4_Healthstate = 12341073;
    public static final int apis_STORE_INFO_DETAIL_3OF3 = 1012;
    public static final int apis_STORE_INFO_DETAIL_ALL = 1013;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////      리뷰 관련 API   ///////////////////////////////////////////
    public static final int GET_REVIEW_BYID = 105;
    public static final int apis_GET_MORE_REVIEW_IN_DETAIL_GOOD = 2004;
    public static final int GET_REVIEWEXP_BYID = 106;
    public static final int EXP_WRITEorUPDATE = 2009;
    public static final int apis_GET_REVIEW_WRITE_CATEGORY = 2008;
    public static final int apis_REG_COMMENT_REV_AND_EXP_AND_EVENT = 4003;
    public static final int apis_DEL_COMMENT_REV_AND_EXP_AND_EVENT = 4004;
    public static final int apis_CHANGE_ISOK = 3100;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////    이벤트 관련 API   ///////////////////////////////////////////
    public static final int apis_GET_EVENT_LIST = 4001;
    public static final int apis_GET_EVENT_BY_ID = 4002;
    public static final int apis_GET_EVENT_DETAIL = 4005;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////      비타위키 관련 API   ///////////////////////////////////////////
    public static final int apis_GET_POWER_REVIEW_LIST = 5001;
    public static final int apis_GET_HEALTH_FOOD_LIST = 5002;
    public static final int apis_GET_EXP_COLUMN_LIST = 5003;
    public static final int apis_ADD_VIEWCNT_POWER_REVIEW = 5004;
    public static final int apis_GET_HEALTH_FOOD_DETAIL = 5005;
    public static final int apis_ADD_VIEWCNT_EXP_COLUMN = 5006;
    public static final int apis_GET_HEALTH_FOOD_BY_ID = 5007;
    public static final int apis_SET_LIKE_FOOD = 5008;

    public static final int no_more_use_apis_GET_POWER_REVIEW_AND_EXP_COLUMN_DETAIL = 5999;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////      기타 관련 API   ///////////////////////////////////////////
    public static final int GET_PRESET_ACCOUNT = 9001;

    public static final int GET_POWERCOLUMN_BYID = 107;
    public static final int GET_RECOMMEND_FOOD = 1024;

    public static final int GET_PRODUCT_LIST = 2001;
    public static final int GET_SEARCH_KEYWORD = 2002;
    public static final int GET_PRODUCT_DETAIL = 2003;
    public static final int LIKE_PRODUCT = 2005;
    public static final int REPORT_PRODUCT = 2006;
    public static final int GET_PRODUCT_BY_ID = 2007;

    private PhotoDownCompleteCallback mUpdateCallback = null;

    public interface PhotoDownCompleteCallback {
        void updatePhotoListView(Integer index);
    }

    public void setPhotoDownCompleteCallback(PhotoDownCompleteCallback cb) {
        mUpdateCallback = cb;
    }

    public class connectAndgetServer {
        Context m_context;
        int m_nConnectType;
        private Handler handler = null;


        public connectAndgetServer(Context context) {
            super();
            m_context = context;
        }

        public void connectServer(Object _listener, Handler _handler, int strAct, Serializable _srzb) {
            m_nConnectType = strAct;
            handler = _handler;
            File m_uploadImageFile;
            String[] params;
            int[] intP;
            UserHealthBase healthBase;
            String[] paramFileNames = null;
            String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                                                Net.NET_POST_FIELD_REQUEST};
            String[] paramValues = null;
            JSONObject w_objJSonData = new JSONObject();

            int f_index = UserManager.getInstance().currentFamIndex;
            try {
                w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
                switch (m_nConnectType){
                    case apis_REG_FCMTOKEN_TO_SERVER:
                        String[] deviceId = {""};
                        String[] osVer = {""};
                        String[] model = {""};
                        Util.getDeviceInfo(m_context, osVer, model, deviceId);

                        w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, "TokenSender");
                        w_objJSonData.put(Net.NET_VALUE_DEVICE_TYPE, "android");
                        w_objJSonData.put(Net.NET_VALUE_DEVICE_ID, deviceId[0]);
                        w_objJSonData.put(Net.NET_VALUE_DEVICE_OS_VER, osVer[0]);
                        w_objJSonData.put(Net.NET_VALUE_DEVICE_MODEL, model[0]);
                        w_objJSonData.put(Net.NET_VALUE_FCM_TOKEN, String.valueOf(_srzb));

                        paramValues = new String[]{
                                Net.apis_REG_FCMTOKEN_TO_SERVER,
                                w_objJSonData.toString()};
                        break;
                    case apis_FIND_PW:
                        params = (String[])_srzb;
                        w_objJSonData.put("f_name", params[0]);
                        w_objJSonData.put("f_birth", params[1]);
                        w_objJSonData.put("f_id", params[2]);

                        paramValues = new String[]{
                                Net.apis_FIND_PW,
                                w_objJSonData.toString()};
                        break;
                    case apis_CHECK_ID:
                        w_objJSonData.put("mail", _srzb);

                        paramValues = new String[]{
                                Net.apis_CHECK_ID,
                                w_objJSonData.toString()};
                        break;
                    case apis_CHECK_NICK:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NICK, _srzb);

                        paramValues = new String[]{
                                Net.apis_CHECK_NICK,
                                w_objJSonData.toString()};
                        break;
                    case apis_LOGIN:
                        switch (((SharedPref)_srzb).m_strUserType){
                            case "facebook":
                                w_objJSonData.put("f_id", ((SharedPref)_srzb).m_strUseremail);
                                w_objJSonData.put(Net.NET_VALUE_FB_ID, ((SharedPref)_srzb).m_strUserid);
                                w_objJSonData.put("fb_token", ((SharedPref)_srzb).m_strUserPass);
                                break;
                            case "kakao":
                                w_objJSonData.put("f_id", ((SharedPref)_srzb).m_strUseremail);
                                w_objJSonData.put(Net.NET_VALUE_KAKAO_ID, ((SharedPref)_srzb).m_strUserid);
                                break;
                            case "emailaccount":
                                w_objJSonData.put("f_id", ((SharedPref)_srzb).m_strUserid);
                                w_objJSonData.put("f_password", ((SharedPref)_srzb).m_strUserPass);
                                break;
                        }
                        w_objJSonData.put(Net.NET_VALUE_DEVICE_NO, Const.g_nDeviceNo);
                        w_objJSonData.put(Net.NET_VALUE_FCM_TOKEN, Const.g_fcmToken);

                        paramValues = new String[]{
                                Net.apis_LOGIN,
                                w_objJSonData.toString()};
                        break;
                    case apis_REGISTER_USER:
                        w_objJSonData.put(Net.NET_VALUE_DEVICE_NO, Const.g_nDeviceNo);
                        w_objJSonData.put(Net.NET_VALUE_FCM_TOKEN, Const.g_fcmToken);
                        if (UserManager.getInstance().member_type.equals("facebook")) {
                            w_objJSonData.put(Net.NET_VALUE_FB_ID, UserManager.getInstance().member_fb_id);
                            w_objJSonData.put("fb_token", UserManager.getInstance().fb_token);
                        } else if (UserManager.getInstance().member_type.equals("kakao")) {
                            w_objJSonData.put(Net.NET_VALUE_KAKAO_ID, UserManager.getInstance().member_kakao_id);
                            w_objJSonData.put("kakao_pass", UserManager.getInstance().member_kakao_id);
                        } else if (UserManager.getInstance().member_type.equals("emailaccount")){
                            w_objJSonData.put("f_password", UserManager.getInstance().member_password);
                        }

                        w_objJSonData.put("f_nick", UserManager.getInstance().HealthBaseInfo.get(f_index).member_nick_name);
                        w_objJSonData.put("f_name", UserManager.getInstance().HealthBaseInfo.get(f_index).member_name);
                        w_objJSonData.put("f_sex", UserManager.getInstance().HealthBaseInfo.get(f_index).member_sex);
                        w_objJSonData.put("f_birth", UserManager.getInstance().HealthBaseInfo.get(f_index).member_birth);
                        w_objJSonData.put("f_examinee", UserManager.getInstance().HealthBaseInfo.get(f_index).member_examinee);
                        w_objJSonData.put("f_pregnant", UserManager.getInstance().HealthBaseInfo.get(f_index).member_pregnant);
                        w_objJSonData.put("f_lactating", UserManager.getInstance().HealthBaseInfo.get(f_index).member_lactating);
                        w_objJSonData.put("f_climacterium", UserManager.getInstance().HealthBaseInfo.get(f_index).member_climacterium);

                        w_objJSonData.put("f_dwelling", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_dwelling));

                        w_objJSonData.put("f_height", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_height));
                        w_objJSonData.put("f_weight", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_weight));
                        w_objJSonData.put("f_bmi", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_bmi));
                        w_objJSonData.put("f_disease", UserManager.getInstance().HealthBaseInfo.get(f_index).member_disease);
                        w_objJSonData.put("f_in_h", UserManager.getInstance().HealthBaseInfo.get(f_index).member_interest_health);
                        w_objJSonData.put("f_pr_h", UserManager.getInstance().HealthBaseInfo.get(f_index).member_prefer_healthfood);

                        w_objJSonData.put("f_res_id", UserManager.getInstance().arr_profile_photo_resID.get(f_index));
                        w_objJSonData.put("f_oldname", UserManager.getInstance().oldname);

                        if(UserManager.getInstance().arr_profile_photo_resID.get(UserManager.getInstance().currentFamIndex)==-1) {
                            m_uploadImageFile = UserManager.getInstance().arr_profile_photo_file.get(UserManager.getInstance().currentFamIndex);
                            paramFileNames = new String[1];
                            if (m_uploadImageFile != null && m_uploadImageFile.exists())
                                paramFileNames[0] = m_uploadImageFile.getPath();
                            else
                                paramFileNames[0] = "";
                        }

                        paramValues = new String[]{
                                Net.apis_REGISTER_USER,
                                w_objJSonData.toString()};
                        break;
                    case apis_UPDATE_PROFILE:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("is_family", String.valueOf(f_index));
                        w_objJSonData.put("f_nick", UserManager.getInstance().HealthBaseInfo.get(f_index).member_nick_name);
                        w_objJSonData.put("f_name", UserManager.getInstance().HealthBaseInfo.get(f_index).member_name);
                        w_objJSonData.put("f_sex", UserManager.getInstance().HealthBaseInfo.get(f_index).member_sex);
                        w_objJSonData.put("f_birth", UserManager.getInstance().HealthBaseInfo.get(f_index).member_birth);
                        w_objJSonData.put("f_examinee", UserManager.getInstance().HealthBaseInfo.get(f_index).member_examinee);
                        w_objJSonData.put("f_pregnant", UserManager.getInstance().HealthBaseInfo.get(f_index).member_pregnant);
                        w_objJSonData.put("f_lactating", UserManager.getInstance().HealthBaseInfo.get(f_index).member_lactating);
                        w_objJSonData.put("f_climacterium", UserManager.getInstance().HealthBaseInfo.get(f_index).member_climacterium);

                        w_objJSonData.put("f_res_id", UserManager.getInstance().arr_profile_photo_resID.get(f_index));
                        w_objJSonData.put("f_oldname", UserManager.getInstance().oldname);

                        if(UserManager.getInstance().arr_profile_photo_resID.get(f_index)==-1) {
                            m_uploadImageFile = UserManager.getInstance().arr_profile_photo_file.get(f_index);
                            paramFileNames = new String[1];
                            if (m_uploadImageFile != null && m_uploadImageFile.exists())
                                paramFileNames[0] = m_uploadImageFile.getPath();
                            else
                                paramFileNames[0] = "";
                        }

                        paramValues = new String[]{
                                Net.apis_UPDATE_PROFILE,
                                w_objJSonData.toString()};
                        break;
                    case apis_STORE_INFO_LIVING:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("is_family", String.valueOf(f_index));
                        w_objJSonData.put("f_name", UserManager.getInstance().HealthBaseInfo.get(f_index).member_name);
                        w_objJSonData.put("f_dwelling", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_dwelling));

                        paramValues = new String[]{
                                Net.apis_STORE_INFO_LIVING,
                                w_objJSonData.toString()};
                        break;
                    case apis_STORE_INFO_BODY_ALL:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("is_family", String.valueOf(f_index));
                        w_objJSonData.put("f_name", UserManager.getInstance().HealthBaseInfo.get(f_index).member_name);
                        w_objJSonData.put("f_height", UserManager.getInstance().HealthBaseInfo.get(f_index).member_height);
                        w_objJSonData.put("f_weight", UserManager.getInstance().HealthBaseInfo.get(f_index).member_weight);
                        w_objJSonData.put("f_bmi", UserManager.getInstance().HealthBaseInfo.get(f_index).member_bmi);
                        w_objJSonData.put("f_disease", UserManager.getInstance().HealthBaseInfo.get(f_index).member_disease);
                        w_objJSonData.put("f_in_h", UserManager.getInstance().HealthBaseInfo.get(f_index).member_interest_health);
                        w_objJSonData.put("f_pr_h", UserManager.getInstance().HealthBaseInfo.get(f_index).member_prefer_healthfood);

                        paramValues = new String[]{
                                Net.apis_STORE_INFO_BODY_ALL,
                                w_objJSonData.toString()};
                        break;
                    case apis_UPDATE_BASE:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("is_family", String.valueOf(f_index));
                        w_objJSonData.put("f_nick", UserManager.getInstance().HealthBaseInfo.get(f_index).member_nick_name);
                        w_objJSonData.put("f_name", UserManager.getInstance().HealthBaseInfo.get(f_index).member_name);
                        w_objJSonData.put("f_sex", UserManager.getInstance().HealthBaseInfo.get(f_index).member_sex);
                        w_objJSonData.put("f_birth", UserManager.getInstance().HealthBaseInfo.get(f_index).member_birth);
                        w_objJSonData.put("f_examinee", UserManager.getInstance().HealthBaseInfo.get(f_index).member_examinee);
                        w_objJSonData.put("f_pregnant", UserManager.getInstance().HealthBaseInfo.get(f_index).member_pregnant);
                        w_objJSonData.put("f_lactating", UserManager.getInstance().HealthBaseInfo.get(f_index).member_lactating);
                        w_objJSonData.put("f_climacterium", UserManager.getInstance().HealthBaseInfo.get(f_index).member_climacterium);

                        w_objJSonData.put("f_dwelling", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_dwelling));

                        w_objJSonData.put("f_height", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_height));
                        w_objJSonData.put("f_weight", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_weight));
                        w_objJSonData.put("f_bmi", String.valueOf(UserManager.getInstance().HealthBaseInfo.get(f_index).member_bmi));
                        w_objJSonData.put("f_disease", UserManager.getInstance().HealthBaseInfo.get(f_index).member_disease);
                        w_objJSonData.put("f_in_h", UserManager.getInstance().HealthBaseInfo.get(f_index).member_interest_health);
                        w_objJSonData.put("f_pr_h", UserManager.getInstance().HealthBaseInfo.get(f_index).member_prefer_healthfood);

                        w_objJSonData.put("f_res_id", UserManager.getInstance().arr_profile_photo_resID.get(f_index));
                        w_objJSonData.put("f_oldname", UserManager.getInstance().oldname);

                        if(UserManager.getInstance().arr_profile_photo_resID.get(UserManager.getInstance().currentFamIndex)==-1) {
                            m_uploadImageFile = UserManager.getInstance().arr_profile_photo_file.get(UserManager.getInstance().currentFamIndex);
                            paramFileNames = new String[1];
                            if (m_uploadImageFile != null && m_uploadImageFile.exists())
                                paramFileNames[0] = m_uploadImageFile.getPath();
                            else
                                paramFileNames[0] = "";
                        }

                        paramValues = new String[]{
                                Net.apis_UPDATE_BASE,
                                w_objJSonData.toString()};
                        break;
                    case apis_STORE_INFO_DETAIL_1OF3:
                        params = (String[])_srzb;
                        w_objJSonData.put("f_no", UserManager.getInstance().member_no);

                        w_objJSonData.put("f_name", params[0]);
                        w_objJSonData.put("f_marry", "" + params[1]);
                        w_objJSonData.put("f_sleep", "" + params[2]);
                        w_objJSonData.put("f_drink_am", "" + params[3]);
                        w_objJSonData.put("f_drink_cn", "" + params[4]);
                        w_objJSonData.put("f_smoke", "" + params[5]);
                        w_objJSonData.put("f_exercise", "" + params[6]);

                        paramValues = new String[]{
                                Net.apis_STORE_INFO_DETAIL_1OF3,
                                w_objJSonData.toString()};
                        break;
                    case apis_STORE_INFO_DETAIL_2OF3:
                        params = (String[])_srzb;
                        w_objJSonData.put("f_no", UserManager.getInstance().member_no);

                        w_objJSonData.put("f_name", params[0]);
                        w_objJSonData.put("f_allergy_sm", "" + params[1]);
                        w_objJSonData.put("f_pee_sm", "" + params[2]);
                        w_objJSonData.put("f_dung_sm", "" + params[3]);
                        w_objJSonData.put("f_life_pt", "" + params[4]);
                        w_objJSonData.put("f_eat_pt", "" + params[5]);

                        paramValues = new String[]{
                                Net.apis_STORE_INFO_DETAIL_2OF3,
                                w_objJSonData.toString()};
                        break;
                    case apis_STORE_INFO_DETAIL_3OF3:
                        params = (String[])_srzb;
                        w_objJSonData.put("f_no", UserManager.getInstance().member_no);

                        w_objJSonData.put("f_name", params[0]);
                        w_objJSonData.put("f_allergy", params[1]);
                        w_objJSonData.put("f_eat_drug", params[2]);
                        w_objJSonData.put("f_eat_healthfood", params[3]);
                        w_objJSonData.put("f_health_state", params[4]);

                        paramValues = new String[]{
                                Net.apis_STORE_INFO_DETAIL_3OF3,
                                w_objJSonData.toString()};
                        break;
                    case apis_STORE_INFO_DETAIL_ALL:
                        UserHealthDetail _userHealthDetail = (UserHealthDetail)_srzb;

                        w_objJSonData.put("f_no", UserManager.getInstance().member_no);
                        w_objJSonData.put("f_nick", _userHealthDetail.member_nick_name);
                        w_objJSonData.put("f_name", _userHealthDetail.member_name);

                        w_objJSonData.put("f_marry", "" + _userHealthDetail.member_marry);
                        w_objJSonData.put("f_sleep", "" + _userHealthDetail.member_sleep);
                        w_objJSonData.put("f_drink_am", "" + _userHealthDetail.member_drink_amount);
                        w_objJSonData.put("f_drink_cn", "" + _userHealthDetail.member_drink_count);
                        w_objJSonData.put("f_smoke", "" + _userHealthDetail.member_smoke);
                        w_objJSonData.put("f_exercise", "" + _userHealthDetail.member_exercise);

                        w_objJSonData.put("f_allergy_symptom", _userHealthDetail.member_allergy_symptom);
                        w_objJSonData.put("f_pee_symptom", _userHealthDetail.member_pee_symptom);
                        w_objJSonData.put("f_dung_symptom", _userHealthDetail.member_dung_symptom);
                        w_objJSonData.put("f_life_pattern", _userHealthDetail.member_life_pattern);
                        w_objJSonData.put("f_eat_patternt", _userHealthDetail.member_eat_pattern);

                        w_objJSonData.put("f_allergy", _userHealthDetail.member_allergy);
                        w_objJSonData.put("f_eat_drug", "" + _userHealthDetail.member_eat_drug);
                        w_objJSonData.put("f_eat_health", "" + _userHealthDetail.member_eat_healthfood);
                        w_objJSonData.put("f_health_state", "" + _userHealthDetail.member_health_state);

                        paramValues = new String[]{
                                Net.apis_STORE_INFO_DETAIL_ALL,
                                w_objJSonData.toString()};
                        break;
                    case apis_UPDATE_PW:
                        params = (String[])_srzb;
                        w_objJSonData.put("f_old_password", params[0]);
                        w_objJSonData.put("f_new_password", params[1]);

                        paramValues = new String[]{
                                Net.apis_UPDATE_PW,
                                w_objJSonData.toString()};
                        break;
                    case apis_SIGNOUT:
                        params = (String[])_srzb;
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("part", "" + params[0]);
                        w_objJSonData.put("content", params[1]);

                        paramValues = new String[]{
                                Net.apis_SIGNOUT,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_FAMILY:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("f_name", UserManager.getInstance().member_name);

                        paramValues = new String[]{
                                Net.apis_GET_FAMILY,
                                w_objJSonData.toString()};
                        break;
                    case apis_REGISTER_FAMILY:
                        healthBase = (UserHealthBase)_srzb;

                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("is_family", UserManager.getInstance().currentFamIndex);

                        w_objJSonData.put("f_nick", healthBase.member_nick_name);
                        w_objJSonData.put("f_name", healthBase.member_name);
                        w_objJSonData.put("f_sex", healthBase.member_sex);
                        w_objJSonData.put("f_birth", healthBase.member_birth);
                        w_objJSonData.put("f_dwelling", healthBase.member_dwelling);
                        w_objJSonData.put("f_examinee", "" + String.valueOf(healthBase.member_examinee));
                        w_objJSonData.put("f_pregnant", "" + String.valueOf(healthBase.member_pregnant));
                        w_objJSonData.put("f_lactating", "" + String.valueOf(healthBase.member_lactating));
                        w_objJSonData.put("f_climacterium", String.valueOf(healthBase.member_climacterium));

                        w_objJSonData.put("f_height", String.valueOf(healthBase.member_height));
                        w_objJSonData.put("f_weight", String.valueOf(healthBase.member_weight));
                        w_objJSonData.put("f_bmi", String.valueOf(healthBase.member_bmi));
                        w_objJSonData.put("f_disease", healthBase.member_disease);
                        w_objJSonData.put("f_in_h", healthBase.member_interest_health);
                        w_objJSonData.put("f_pr_h", healthBase.member_prefer_healthfood);

                        w_objJSonData.put("f_res_id", UserManager.getInstance().arr_profile_photo_resID.get(UserManager.getInstance().currentFamIndex));

                        if(UserManager.getInstance().arr_profile_photo_resID.get(UserManager.getInstance().currentFamIndex)==-1) {
                            m_uploadImageFile = UserManager.getInstance().arr_profile_photo_file.get(UserManager.getInstance().currentFamIndex);
                            paramFileNames = new String[1];
                            if (m_uploadImageFile != null && m_uploadImageFile.exists())
                                paramFileNames[0] = m_uploadImageFile.getPath();
                            else
                                paramFileNames[0] = "";
                        }

                        paramValues = new String[]{
                                Net.apis_REGISTER_FAMILY,
                                w_objJSonData.toString()};
                        break;
                    case apis_DELETE_FAMILY:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("f_name", UserManager.getInstance().oldname);

                        paramValues = new String[]{
                                Net.apis_DELETE_FAMILY,
                                w_objJSonData.toString()};
                        break;
                    case GET_REVIEW_BYID:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("review_id", "" + (int) _srzb);

                        paramValues = new String[]{
                                Net.apis_GET_REVIEW_BY_ID,
                                w_objJSonData.toString()};
                        break;
                    case GET_REVIEWEXP_BYID:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("review_id", "" + (int) _srzb);

                        paramValues = new String[]{
                                Net.apis_GET_EXP_BY_ID,
                                w_objJSonData.toString()};
                        break;

                    case GET_PRESET_ACCOUNT:
                        paramValues = new String[]{
                                Net.apis_GET_PRESET_ACCOUNT,
                                w_objJSonData.toString()};
                        break;

                    case GET_POWERCOLUMN_BYID:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("wiki_id", "" + (int) _srzb);

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_GETPOWERCOLUMN_BYID,
                                w_objJSonData.toString()};
                        break;
                    case GET_RECOMMEND_FOOD:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("f_name", UserManager.getInstance().HealthBaseInfo.get((int) _srzb).member_name);

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_RECOMMEND_FOOD,      //50
                                w_objJSonData.toString()};
                        break;
                    case GET_PRODUCT_LIST:
                        params = (String[])_srzb;

                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        if(params[0].equals("productgroup")){
                            //아래 5행은 필터에서 복수 선택이 가능할 경우에 살린다. 현재는 단일 선택 by 동현
//                            for (int i = 0; i < m_selected1.size(); i++) {
//                                strProduct += m_selected1.get(i)._id + ",";
//                            }
//                            if (strProduct.length() > 0)
//                                strProduct = strProduct.substring(0, strProduct.length() - 1);

                            // +1을 하는 것은 서버에서는 제품군 ID의 시작이 1부터 이기 때문에.
                            String strProduct = params[1];
                            w_objJSonData.put("product", strProduct);
                        }
                        if(params[0].equals("functionality")){
//                            for (int i = 0; i < m_selected3.size(); i++) {
//                                strFeature += m_selected3.get(i)._id + ",";
//                            }
//                            if (strFeature.length() > 0)
//                                strFeature = strFeature.substring(0, strFeature.length() - 1);

                            // +1을 하는 것은 서버에서는 복용대상 ID의 시작이 1부터 이기 때문에.
                            String strFeature = String.valueOf(Integer.valueOf(params[1])+1);
                            w_objJSonData.put("feature", strFeature);
                        }
                        if(params[0].equals("person")){
//                            for (int i = 0; i < m_selected4.size(); i++) {
//                                strTaking += m_selected4.get(i)._id + ",";
//                            }
//                            if (strTaking.length() > 0)
//                                strTaking = strTaking.substring(0, strTaking.length() - 1);

                            // +1을 하는 것은 서버에서는 복용대상 ID의 시작이 1부터 이기 때문에.
                            String strTaking = String.valueOf(Integer.valueOf(params[1])+1);
                            //strTaking = getIntent().getStringExtra("value");
                            w_objJSonData.put("taking", strTaking);
                        }
                        if(params[0].equals("keyword")){
                            w_objJSonData.put("keyword", params[1]);
                        }
                        if(!params[2].equals(""))
                            w_objJSonData.put("business", params[2]);
                        if(!params[3].equals(""))
                            w_objJSonData.put("product_group", params[3]);

                        w_objJSonData.put(Net.NET_VALUE_PAGE, Integer.valueOf(params[4]));
                        w_objJSonData.put("order", params[5]);
                        w_objJSonData.put("search", "");

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_GOODLIST,
                                w_objJSonData.toString()};
                        break;
                    case GET_SEARCH_KEYWORD:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_REQUEST_SEARCH_KEYWORD,
                                w_objJSonData.toString()};
                        break;
                    case GET_PRODUCT_DETAIL:
                        w_objJSonData.put("good_id", "" + (int) _srzb);
                        w_objJSonData.put(Net.NET_VALUE_PAGE, 1);

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_DETAILGOOD,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_MORE_REVIEW_IN_DETAIL_GOOD:
                        intP = (int[])_srzb;

                        w_objJSonData.put("good_id", "" +  intP[0]);
                        w_objJSonData.put(Net.NET_VALUE_PAGE, intP[1]);

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_DETAILGOOD,
                                w_objJSonData.toString()};
                        break;
                    case LIKE_PRODUCT:
                        intP = (int[])_srzb;

                        w_objJSonData.put("good_id", "" +  intP[0]);
                        w_objJSonData.put("like", "" + intP[1]);

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_LIKEGOOD,
                                w_objJSonData.toString()};
                        break;
                    case REPORT_PRODUCT:
                        params = (String[])_srzb;

                        w_objJSonData.put("good_id", "" +  params[0]);
                        w_objJSonData.put("report_type", "" + params[1]);
                        w_objJSonData.put("content", params[2]);

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_REPORTGOOD,
                                w_objJSonData.toString()};
                        break;
                    case GET_PRODUCT_BY_ID:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("good_id", "" + (int) _srzb);

                        paramValues = new String[]{
                                Net.POST_FIELD_ACT_GETGOOD_BYID,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_REVIEW_WRITE_CATEGORY:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("good_id", "" + (int) _srzb);

                        paramValues = new String[]{
                                Net.apis_GET_REVIEW_WRITE_CATEGORY,
                                w_objJSonData.toString()};
                        break;
                    case apis_REG_COMMENT_REV_AND_EXP_AND_EVENT:
                        params = (String[])_srzb;

                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("cont_id", params[0]);
                        w_objJSonData.put("content", params[1]);
                        w_objJSonData.put("comment_type", params[2]);

                        paramValues = new String[]{
                                Net.apis_REG_COMMENT_REV_AND_EXP_AND_EVENT,
                                w_objJSonData.toString()};
                        break;
                    case apis_DEL_COMMENT_REV_AND_EXP_AND_EVENT:
                        params = (String[])_srzb;

                        w_objJSonData.put("comment_id", params[0]);
                        w_objJSonData.put("comment_type", params[1]);
                        paramValues = new String[]{
                                Net.apis_DEL_COMMENT_REV_AND_EXP_AND_EVENT,
                                w_objJSonData.toString()};
                        break;
                    case apis_CHANGE_ISOK:
                        intP = (int[])_srzb;

                        w_objJSonData.put("review_id", intP[0]);
                        w_objJSonData.put("isok", intP[1]);

                        paramValues = new String[]{
                                Net.apis_CHANGE_ISOK,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_EVENT_LIST:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put(Net.NET_VALUE_PAGE, _srzb);

                        paramValues = new String[]{
                                Net.apis_GET_EVENT_LIST,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_EVENT_BY_ID:
                        intP = (int[])_srzb;

                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("event_id", "" +  intP[0]);
                        w_objJSonData.put(Net.NET_VALUE_PAGE, intP[1]);

                        paramValues = new String[]{
                                Net.apis_GET_EVENT_BY_ID,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_EVENT_DETAIL:
                        intP = (int[])_srzb;

                        w_objJSonData.put("event_id", intP[0]);
                        w_objJSonData.put(Net.NET_VALUE_PAGE, intP[1]);
                        paramValues = new String[]{
                                Net.apis_GET_EVENT_DETAIL,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_POWER_REVIEW_LIST:
                        w_objJSonData.put(Net.NET_VALUE_PAGE, _srzb);
                        w_objJSonData.put("order", "regdate");

                        paramValues = new String[]{
                                Net.apis_GET_POWER_REVIEW_LIST,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_HEALTH_FOOD_LIST:
                        w_objJSonData.put(Net.NET_VALUE_PAGE, _srzb);

                        paramValues = new String[]{
                                Net.apis_GET_HEALTH_FOOD_LIST,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_EXP_COLUMN_LIST:
                        w_objJSonData.put(Net.NET_VALUE_PAGE, _srzb);
                        w_objJSonData.put("order", "regdate");

                        paramValues = new String[]{
                                Net.apis_GET_EXP_COLUMN_LIST,
                                w_objJSonData.toString()};
                        break;
                    case apis_ADD_VIEWCNT_POWER_REVIEW:
                        w_objJSonData.put("wiki_id", _srzb);
                        paramValues = new String[]{
                                Net.apis_ADD_VIEWCNT_POWER_REVIEW,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_HEALTH_FOOD_DETAIL:
                        w_objJSonData.put("food_id", _srzb);
                        paramValues = new String[]{
                                Net.apis_GET_HEALTH_FOOD_DETAIL,
                                w_objJSonData.toString()};
                        break;
                    case apis_ADD_VIEWCNT_EXP_COLUMN:
                        w_objJSonData.put("wiki_id", _srzb);
                        paramValues = new String[]{
                                Net.apis_ADD_VIEWCNT_EXP_COLUMN,
                                w_objJSonData.toString()};
                        break;
                    case apis_GET_HEALTH_FOOD_BY_ID:
                        w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                        w_objJSonData.put("food_id", "" + (int) _srzb);
                        paramValues = new String[]{
                                Net.apis_GET_HEALTH_FOOD_BY_ID,
                                w_objJSonData.toString()};
                        break;
                    case apis_SET_LIKE_FOOD:
                        intP = (int[])_srzb;

                        w_objJSonData.put("food_id", intP[0]);
                        w_objJSonData.put("like", intP[1]);
                        paramValues = new String[]{
                                Net.apis_SET_LIKE_FOOD,
                                w_objJSonData.toString()};
                        break;
                }
            } catch (JSONException e) {            e.printStackTrace();        }

            String netUrl = Net.URL_SERVER + Net.URL_SERVER_API;
            if (paramFileNames != null && paramFileNames.length > 0) {
                HttpRequester.getInstance().init(m_context, _listener, handler, netUrl,
                        paramFields, paramValues, paramFileNames);
            } else {
                HttpRequester.getInstance().init(m_context, _listener, handler, netUrl,
                        paramFields, paramValues, false);
            }

            HttpRequester.getInstance().setProgressMessage(
                    Net.NET_COMMON_STRING_WAITING);
            HttpRequester.getInstance().startNetThread();
        }
    }

    public boolean LoginInUtil(Context _context, Object _listener, Handler _handler, String m_strUserType, String m_strUseremail, String m_strUserid, String m_strUserPass) {
        SharedPref m_sharedP=new SharedPref();

        m_sharedP.m_strUserType = m_strUserType;
        m_sharedP.m_strUseremail = m_strUseremail;
        m_sharedP.m_strUserid = m_strUserid;
        m_sharedP.m_strUserPass = m_strUserPass;

        if (!m_sharedP.m_strUserType.equals("") || !m_sharedP.m_strUseremail.equals("") || !m_sharedP.m_strUserid.equals("")) {
            new NetUtil.connectAndgetServer(_context).connectServer(_listener, _handler, apis_LOGIN, m_sharedP);
        }
        return true;
    }

    public ArrayList<FoodInfoDeux> PackFood2to1(ArrayList<FoodInfo> arrFoodInfoList) {
        ArrayList<FoodInfoDeux> arrFoodInfoDoubleList = new ArrayList<>();

        for (int i = 0; i < arrFoodInfoList.size(); i=i+2) {
            FoodInfoDeux m_infoDeux = new FoodInfoDeux();

            m_infoDeux._id = arrFoodInfoList.get(i)._id ;
            m_infoDeux._name = arrFoodInfoList.get(i)._name ;
            m_infoDeux._imagePath = arrFoodInfoList.get(i)._imagePath ;
            m_infoDeux._view_cnt = arrFoodInfoList.get(i)._view_cnt ;
            m_infoDeux._like_cnt = arrFoodInfoList.get(i)._like_cnt ;

            if(arrFoodInfoList.size()>i+1){
                m_infoDeux._id2 = arrFoodInfoList.get(i+1)._id;
                m_infoDeux._name2 = arrFoodInfoList.get(i+1)._name;
                m_infoDeux._imagePath2 = arrFoodInfoList.get(i+1)._imagePath;
                m_infoDeux._view_cnt2 = arrFoodInfoList.get(i+1)._view_cnt;
                m_infoDeux._like_cnt2 = arrFoodInfoList.get(i+1)._like_cnt;
            }
            arrFoodInfoDoubleList.add(m_infoDeux);
        }
        return arrFoodInfoDoubleList;
    }

    public FoodInfo parseFood(JSONObject obj_fod) {
        FoodInfo m_info = new FoodInfo();
        try {
            m_info._id = obj_fod.getInt("id");
            m_info._name = obj_fod.getString("name");
            m_info._imagePath = obj_fod.getString("image");
            m_info._view_cnt = obj_fod.getInt("view_cnt");
            m_info._like_cnt = obj_fod.getInt("like_cnt");
        } catch (JSONException e) {e.printStackTrace();}
        return m_info;
    }

    public WikiInfo parseWiki(JSONObject obj_wki) {
        WikiInfo m_info = new WikiInfo();
        try {
            m_info._id = obj_wki.getInt("id");
            m_info._type = obj_wki.getInt("type");
            m_info.title = obj_wki.getString("title");
            m_info._content = obj_wki.getString("content");
            m_info._view_cnt = obj_wki.getInt("view_cnt");
            m_info._comment_cnt = obj_wki.getInt("comment_cnt");
            m_info._like_cnt = obj_wki.getInt("like_cnt");
            m_info._imagePath = obj_wki.getString("image");
        } catch (JSONException e) {e.printStackTrace();}
        return m_info;
    }

    public ReviewInfo parseReview(JSONObject obj_rev, boolean isRv) {
        ReviewInfo m_r_info = new ReviewInfo();
        try {
            m_r_info._id =obj_rev.getInt("id");
            m_r_info._mb_id =obj_rev.getString("mb_id");
            m_r_info.isok = obj_rev.getString("isok");

            if(obj_rev.getString("good_id").length() >0) {
                String[] arrGoodId = obj_rev.getString("good_id").split(",");
                for (String OneGoodId : arrGoodId) {
                    m_r_info._good_id.add(Integer.parseInt(OneGoodId));
                    m_r_info._good_photo_urls.add("/data/file/good/" + OneGoodId + ".png");
                    m_r_info._good_business.add(obj_rev.getString("business"));
                }
            }

            m_r_info.content =obj_rev.getString("content");
            m_r_info.hash_tag =obj_rev.getString("hash_tag");
            m_r_info.regdate =obj_rev.getString("rdate");
            m_r_info.view_cnt =obj_rev.getInt("view_cnt");

            m_r_info.f_photo =obj_rev.getString("f_photo");
            m_r_info.comment_cnt =obj_rev.getInt("comment_cnt");
            m_r_info.like_cnt =obj_rev.getInt("like_cnt");
            m_r_info.category =obj_rev.getInt("category") - 1;      //서버에서의 category는 1부터 이기 때문에
            m_r_info.period =obj_rev.getInt("period");

            m_r_info.title =obj_rev.getString("title");
            m_r_info.business =obj_rev.getString("business");
            m_r_info.good_photo =obj_rev.getString("good_photo");
            m_r_info.photos =new ArrayList<>();
            m_r_info.photos_id =new ArrayList<>();
            m_r_info.photosFile =new ArrayList<>();

            JSONArray photos = obj_rev.getJSONArray("photo_list");
            for(int j = 0; j<photos.length();j++) {
                m_r_info.photos.add(photos.getJSONObject(j).getString("filepath"));
                m_r_info.photos_id.add(photos.getJSONObject(j).getString("no"));
                m_r_info.photosFile.add(null);
            }
            if(isRv) {
                m_r_info.content2 = obj_rev.getString("content2");
                m_r_info.content3 = obj_rev.getString("content3");
                m_r_info.buy_path = obj_rev.getInt("buy_path");
                m_r_info.price = obj_rev.getInt("price");
                m_r_info.retake = obj_rev.getInt("retake");
                m_r_info.rate = obj_rev.getDouble("rate");
                m_r_info.person = obj_rev.getInt("person");
                m_r_info.func_rate = obj_rev.getInt("func_rate");
                m_r_info.price_rate = obj_rev.getInt("price_rate");
                m_r_info.take_rate = obj_rev.getInt("take_rate");
                m_r_info.taste_rate = obj_rev.getInt("taste_rate");
            }else{
                if(obj_rev.getJSONArray("knowhow")!=null){
                    m_r_info.knowhow = new ArrayList<>();
                    JSONArray arrKnowhow = obj_rev.getJSONArray("knowhow");
                    for (int k = 0; k < arrKnowhow.length(); k++) {
                        if(arrKnowhow.get(k).toString().length()!=0)
                            m_r_info.knowhow.add((String)arrKnowhow.get(k));
                    }
                }
            }
            m_r_info._mb_nick = obj_rev.isNull("mb_nick")? "윤지":obj_rev.getString("mb_nick");
            m_r_info._mb_sex = obj_rev.isNull("mb_sex")? 1:obj_rev.getInt("mb_sex");
            m_r_info._mb_age = obj_rev.isNull("mb_age")? 33:(new GregorianCalendar().get(Calendar.YEAR)) -Integer.parseInt(obj_rev.getString("mb_age").substring(0,4))+1;
            m_r_info.Author_pregnant = obj_rev.isNull("f_pregnant")? 0:obj_rev.getInt("f_pregnant");
            m_r_info.Author_examinee = obj_rev.isNull("f_examinee")? 1:obj_rev.getInt("f_examinee");
            m_r_info.Author_lactating = obj_rev.isNull("f_lactating")? 1:obj_rev.getInt("f_lactating");
            m_r_info.Author_climacterium = obj_rev.isNull("f_climacterium")? 1:obj_rev.getInt("f_climacterium");
        } catch (JSONException e) {e.printStackTrace();}
        return m_r_info;
    }

    public GoodInfo parseGood(JSONObject obj) {
        GoodInfo info = new GoodInfo();
        try {
            info._id = obj.getInt("id");
            info._name = obj.getString("name");
            info._rate = obj.getDouble("rate");
            info._business = obj.getString("business");
            info._business_id = obj.getInt("business_id");
            info._report = obj.getString("report");
            info._regdate = obj.getString("regdate").substring(0, 10);
            info._expiredate = obj.getString("expiredate");
            info._ikon = obj.getString("ikon");
            info._amount_per_intake = obj.getString("amount_per_intake");
            info._unit_amount_per_intake = obj.getString("unit_amount_per_intake");
            info._intake = obj.getString("intake");
            info._preserve = obj.getString("preserve");
            info._warning = obj.getString("warning");
            info._kind = obj.getString("kind");
            info._view_cnt = obj.getInt("view_cnt");
            info._review_cnt = obj.getInt("review_cnt");
            info._like_cnt = obj.getInt("like_cnt");
            info._imagePath = obj.getString("f_photo");
        } catch (JSONException e) {e.printStackTrace();}
        return info;
    }

    public Boolean setProfilePhoto(Context _context, int index, String urlProfileImg) {
        int resID;
        String fileName_down;
        String urlImg = urlProfileImg;

        if(urlProfileImg.length()==0)
            urlProfileImg = "";
        else if (urlProfileImg.contains("/data/file")) {
            urlProfileImg = Net.URL_SERVER2 + urlProfileImg;
        }


        while(UserManager.getInstance().arr_profile_photo_file.size() < index+1)
            UserManager.getInstance().arr_profile_photo_file.add(null);

        while(UserManager.getInstance().arr_profile_photo_bitmap.size() < index+1)
            UserManager.getInstance().arr_profile_photo_bitmap.add(null);

        while(UserManager.getInstance().arr_profile_photo_URL.size() < index+1)
            UserManager.getInstance().arr_profile_photo_URL.add(null);

        while(UserManager.getInstance().arr_profile_photo_resID.size() < index+1)
            UserManager.getInstance().arr_profile_photo_resID.add(-1);

        UserManager.getInstance().arr_profile_photo_file.set(index, null);
        if (urlProfileImg.length() > 0) {
            if (urlProfileImg.contains("ic_")) {
                String[] arr_str = urlProfileImg.split("male_");
                resID = Integer.parseInt(String.valueOf(arr_str[arr_str.length - 1].charAt(0))) - 1;
//                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), resArr[resID]);
//                if (Util.saveBitmapToFileCache(bitmap, fileName_down + String.valueOf(resID) + ".png", true))
//                    m_arrPhotoName.set(i, "file://" + new File(fileName_down + String.valueOf(resID) + ".png").getPath());
                if (urlProfileImg.contains("female_"))
                    resID = resID + 4;
            } else {
                resID = -1;
                fileName_down = urlProfileImg.replace("/", "").replace("=","").replace("&","");
                fileName_down = Environment.getExternalStorageDirectory() + "/Vitamin/" +
                        fileName_down.substring(fileName_down.length() - 15, fileName_down.length());
                if (!new File(fileName_down).exists()) {
                    Util.ImgDownTask mImgDownTask = new Util().new ImgDownTask(_context){
                        @Override
                        public void onPostExecute(Integer index)
                        {
                            if(mUpdateCallback!=null)
                                mUpdateCallback.updatePhotoListView(index);
                        }
                    };
                    mImgDownTask.execute(urlProfileImg, fileName_down, String.valueOf(index));//세번째 파라메터가 "0"이면 프로파일 이미지 다운이고, "1"이면 리뷰 콘텐츠 이미지 다운임 by 동현
                } else {
                    UserManager.getInstance().arr_profile_photo_file.set(index, new File(fileName_down));
                    if(mUpdateCallback!=null)
                        mUpdateCallback.updatePhotoListView(index);
                }
            }
        } else {//url이 없을 경우에 대비한 코드
            if (UserManager.getInstance().arr_profile_photo_resID.get(index) == null || UserManager.getInstance().arr_profile_photo_resID.get(index) == -1)
                resID = new Util().getAutoImageResID(index);
            else
                resID = UserManager.getInstance().arr_profile_photo_resID.get(index);
        }
        UserManager.getInstance().arr_profile_photo_resID.set(index, resID);
        UserManager.getInstance().arr_profile_photo_URL.set(index, urlImg);
        UserManager.getInstance().arr_profile_photo_bitmap.set(index, null);
        if(resID != -1) {
            if(resID<4)
                urlProfileImg = "https://admin.vitamiin.co.kr/data/file/family/ic_male_" + String.valueOf(resID+1) + ".png";
            else
                urlProfileImg = "https://admin.vitamiin.co.kr/data/file/family/ic_female_" + String.valueOf(resID-3) + ".png";
            String fileName_preset = Environment.getExternalStorageDirectory() + "/Vitamin/presetIMG_" + String.valueOf(resID) + ".png";
            if (!new File(fileName_preset).exists()) {
                Util.ImgDownTask mImgDownTask = new Util().new ImgDownTask(_context){
                    @Override
                    public void onPostExecute(Integer index)
                    {
                        if(mUpdateCallback!=null)
                            mUpdateCallback.updatePhotoListView(index);
                    }
                };
                mImgDownTask.execute(urlProfileImg, fileName_preset, String.valueOf(index));//세번째 파라메터가 "0"이면 프로파일 이미지 다운이고, "1"이면 리뷰 콘텐츠 이미지 다운임 by 동현
            } else {
                UserManager.getInstance().arr_profile_photo_file.set(index,new File(fileName_preset));
                if(mUpdateCallback!=null)
                    mUpdateCallback.updatePhotoListView(index);
            }
        }
        return true;
//                Bitmap bitmap = BitmapFactory.decodeResource(_context.getResources(), resArr[resID]);
//                if (Util.saveBitmapToFileCache(bitmap, fileName_down + String.valueOf(resID) + ".png", true))
//                    UserManager.getInstance().arr_profile_photo_file.set(famindex, new File(fileName_down + String.valueOf(resID) + ".png"));
//                UserManager.getInstance().arr_profile_photo_URL.set(famindex, null);
    }
    public UserHealthBase transHealthBase_ObjToUM(JSONObject obj){
        UserHealthBase healthBase = new UserHealthBase();
        try {
            healthBase.isset = obj.getInt("f_isset1") == 1;
            healthBase.member_no = obj.getInt("f_no");
            healthBase.member_nick_name = obj.getString("f_nick");
            healthBase.member_name = obj.getString("f_name");
            healthBase.member_sex = obj.getInt("f_sex");
            healthBase.member_birth = obj.getString("f_birth");
            healthBase.member_height = obj.getDouble("f_height");
            healthBase.member_weight = obj.getDouble("f_weight");
            healthBase.member_bmi = obj.getDouble("f_bmi");
            healthBase.member_dwelling = obj.getInt("f_dwelling");
            healthBase.member_examinee = obj.getInt("f_examinee");
            healthBase.member_pregnant = obj.getInt("f_pregnant");
            healthBase.member_lactating = obj.getInt("f_lactating");
            healthBase.member_climacterium = obj.getInt("f_climacterium");
            healthBase.member_disease = obj.getString("f_disease");
            healthBase.member_interest_health = obj.getString("f_interest_health");
            healthBase.member_prefer_healthfood = obj.getString("f_prefer_health");
            healthBase.member_health_base_update = Integer.parseInt(obj.getString("f_health_base_update").replace("-", ""));
        }
        catch (JSONException e) {
            healthBase.releaseUserHealthBase();}

        return healthBase;
    }
    public UserHealthDetail transHealthDetail_ObjToUM(JSONObject obj){
        UserHealthDetail healthDetail = new UserHealthDetail();
        try {
            healthDetail.isset = obj.getInt("f_isset2") == 1;
            healthDetail.member_no = obj.getInt("f_no");
            healthDetail.member_nick_name = "" + obj.getString("f_nick");
            healthDetail.member_name = "" + obj.getString("f_name");
            healthDetail.member_marry = obj.getInt("f_marry");
            healthDetail.member_sleep = obj.getInt("f_sleep");
            healthDetail.member_drink_amount = obj.getInt("f_drink_amount");
            healthDetail.member_drink_count = obj.getInt("f_drink_count");
            healthDetail.member_smoke = obj.getInt("f_smoke");
            healthDetail.member_exercise = obj.getInt("f_exercise");

            healthDetail.member_allergy_symptom = "" + obj.getString("f_allergy_symptom");
            healthDetail.member_pee_symptom = "" + obj.getString("f_pee_symptom");
            healthDetail.member_dung_symptom = "" + obj.getString("f_dung_symptom");
            healthDetail.member_eat_pattern = "" + obj.getString("f_eat_pattern");
            healthDetail.member_life_pattern = "" + obj.getString("f_life_pattern");

            healthDetail.member_allergy = "" + obj.getString("f_allergy");
            healthDetail.member_eat_drug = "" + obj.getString("f_eat_drug");
            healthDetail.member_eat_healthfood = "" + obj.getString("f_eat_healthfood");
            healthDetail.member_health_state = "" + obj.getString("f_health_state");
            healthDetail.member_health_detail_update = Integer.parseInt(obj.getString("f_health_detail_update").replace("-", ""));
        }
        catch (JSONException e) {
            healthDetail.releaseUserHealthDetail();}

        return healthDetail;
    }

    public Boolean transProfile_ObjToUM(Context _context, int index, JSONObject obj) {
        try {
            if(index==0) {
                UserManager.getInstance().member_no = obj.getInt("f_no");
                UserManager.getInstance().member_id = obj.getString("f_id");    // m_edtUser.getText().toString();
                UserManager.getInstance().member_password = obj.getString("f_password");
                UserManager.getInstance().member_fb_id = obj.getString("facebook_id").length() == 0 ? obj.getString("facebook_id") : "";
                UserManager.getInstance().member_kakao_id = obj.getString("kakao_id").length() == 0 ? obj.getString("kakao_id") : "";
                UserManager.getInstance().member_nick_name = obj.getString("f_nick");
                UserManager.getInstance().member_name = obj.getString("f_name");
                UserManager.getInstance().member_sex = obj.getInt("f_sex");
                UserManager.getInstance().member_birth = obj.getString("f_birth");
                UserManager.getInstance().noti_comment = obj.getInt("f_noti_comment");
                UserManager.getInstance().noti_like = obj.getInt("f_noti_like");
                UserManager.getInstance().member_wr_review = obj.getInt("f_write_review");
                UserManager.getInstance().member_wr_comment = obj.getInt("f_write_comment");
                UserManager.getInstance().member_chk_like = obj.getInt("f_check_like");
                UserManager.getInstance().member_grade = obj.getInt("f_user_grade");
                UserManager.getInstance().member_status = obj.getInt("f_status");

                if (obj.getInt("f_status") != 0) {
                    Toast.makeText(_context, "탈퇴한 회원입니다.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            UserHealthBase healthBase = new UserHealthBase();
            healthBase.releaseUserHealthBase();
            healthBase.isset = obj.getInt("f_isset1") == 1;
            if (healthBase.isset)
                healthBase = transHealthBase_ObjToUM(obj);

            UserHealthDetail healthDetail = new UserHealthDetail();
            healthDetail.releaseUserHealthDetail();
            healthDetail.member_nick_name = "" + obj.getString("f_nick");
            healthDetail.member_name = "" + obj.getString("f_name");
            healthDetail.isset = obj.getInt("f_isset2") == 1;
            if (healthDetail.isset)
                healthDetail = transHealthDetail_ObjToUM(obj);

            UserManager.getInstance().updateHealthInfo(index, healthBase, healthDetail);
            setProfilePhoto(_context, index, obj.getString("photo"));
        }
        catch (JSONException e) {
            UserManager.getInstance().releaseUserData();
            new NoticeDialog(_context, "자동 로그인 실패 알림", message + " 자동 로그인이 실패하였습니다. 다시 시도해주세요..","잠시 후에 자동으로 닫힙니다.", true).show();
            return false;
        }
        return true;
    }
}
