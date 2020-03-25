package app.vitamiin.com.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class UserManager{
    private static UserManager userManager;

    public static UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }
    private UserManager() {
        releaseUserData();
    }

    public String onlineVesion="";
    public static final int USER_LEVEL_MEMBER = 1;
    public static final int USER_LEVEL_MASTER = 2;
    public int event_num = 1;

    public int currentFamIndex = 0;
    public String oldname = "";

    public boolean is_familyinfo_loaded=false;
    public String member_type = ""; //sns type:facebook, kakao, emailaccount
    public String fb_token = null;
    public String fcm_token = null;

    //가족추가시 이용
    //public String family_photo = null;//서버 url
    //public File family_photo_file = null;
    //public Bitmap family_photo_bitmap = null;
    //public int family_photo_resID = -1;
    public List<String> arr_profile_photo_URL = null;
    public List<File> arr_profile_photo_file = null;
    public List<Bitmap> arr_profile_photo_bitmap = null;
    public List<Integer> arr_profile_photo_resID = null;

    //member table
    public int member_no;           /* 유저 번호*자료기지 index  */
    public String member_id = "";       /* 유저 아이디 * ex:2348923   */
    public String member_password = "";
    public String member_fb_id = "";
    public String member_kakao_id = "";
    public String member_nick_name = "";
    public String member_name = "";
    public int member_sex; //0.male, 1:female
    public String member_birth = "";
    public int noti_comment;
    public int noti_like;
    public int member_wr_review = 0;
    public int member_wr_comment = 0;
    public int member_chk_like = 0;
    public int member_grade = 0;
    public int member_status = 0;
    /*UserManager에서 base와 detail에 해당하는 정보를 포함하지 않기로 하면서 아래 코드를 주석화 by 동현
    //health_base table
    public double member_height;
    public double member_weight;
    public double member_bmi;
    public int member_dwelling = 0;
    public int member_pregnant = -1; //0~
    public String member_disease = ""; //0~22, ex:0,1,2,3,4, Max:5
    public String member_interest_health = ""; //0~27, ex:0,1,2,3,4, Max:5
    public String member_prefer_healthfood = ""; //0~35, ex:0,1,2,3,4, Max:5
    //health_detail table
    public int member_marriage = 0; //0:미혼, 1:기혼
    public int member_sleep = -1; //0~
    public int member_drink_am = -1; //0~
    public int member_drink_cn = -1; //0~
    public int member_smoke = -1; //0~
    public String member_allergy = ""; //0~9, ex:0,1,2,3,4, Max:5
    public int member_exercise; //0~
    public String member_allergy_sm = "";
    public String member_pee_sm = "";
    public String member_dung_sm = "";
    public String member_eat_pt = "";
    public String member_life_pt = "";
    public String member_eat_drug = "";
    public String member_eat_healthfood = "";
    public String member_health_state = "";
    */

    //public UserHealthBase myHealthBase = new UserHealthBase();
    //public UserHealthDetail myHealthDetail = new UserHealthDetail();
    public List<UserHealthBase> HealthBaseInfo = null;
    public List<UserHealthDetail> HealthDetailInfo = null;

    /* 유저정보 초기화  */
    public void releaseUserData() {
        is_familyinfo_loaded = false;
        member_type = "";	//sns	type:facebook,	kakao, emailaccount
        fb_token = "";
        fcm_token = "";

        arr_profile_photo_URL = new ArrayList<>();
        arr_profile_photo_file = new ArrayList<>();
        arr_profile_photo_bitmap = new ArrayList<>();
        arr_profile_photo_resID = new ArrayList<>();

        member_no = -1;	/*	유저	번호*자료기지	index	*/
        member_id = null;	/*	유저	아이디 */
        member_password = null;
        member_fb_id = null;
        member_kakao_id = null;
        member_nick_name = null;
        member_name = null;
        member_sex = -1;     //0.male,	1:female
        member_birth = null;
        noti_comment = -1;
        noti_like = -1;
        member_wr_review = -1;
        member_wr_comment	 = -1;
        member_chk_like = -1;
        member_grade = -1;
        member_status = -1;

//        UserHealthBase _userHealthBase = new UserHealthBase();
//        _userHealthBase.releaseUserHealthBase();
        HealthBaseInfo = new ArrayList<>();

//        UserHealthDetail _userHealthDetail = new UserHealthDetail();
//        _userHealthDetail.releaseUserHealthDetail();
        HealthDetailInfo = new ArrayList<>();
    }

    public void saveData(Context context, String type, String email_id, String id, String password) {
        //
        SharedPreferences pref = context.getSharedPreferences("Vitamiin", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("type", type);
        edit.putString("email", email_id);
        edit.putString("id", id);
        edit.putString("pass", password);
        edit.apply();
    }

    public void setUser(UserManager user) {
        is_familyinfo_loaded= user.is_familyinfo_loaded;
        member_type = user.member_type;
        fb_token = user.fb_token;
        fcm_token = user.fcm_token;

        arr_profile_photo_URL.clear();
        arr_profile_photo_URL.addAll(user.arr_profile_photo_URL);
        arr_profile_photo_file.clear();
        arr_profile_photo_file.addAll(user.arr_profile_photo_file);
        arr_profile_photo_bitmap.clear();
        arr_profile_photo_bitmap.addAll(user.arr_profile_photo_bitmap);
        arr_profile_photo_resID.clear();
        arr_profile_photo_resID.addAll(user.arr_profile_photo_resID);

        member_no = user.member_no;
        member_id = user.member_id;
        member_password = user.member_password;
        member_fb_id = user.member_fb_id;
        member_kakao_id = user.member_kakao_id;
        member_nick_name = user.member_nick_name;
        member_name = user.member_name;
        member_sex = user.member_sex;
        member_birth = user.member_birth;
        noti_comment = user.noti_comment;
        noti_like = user.noti_like;
        member_wr_review = user.member_wr_review;
        member_wr_comment = user.member_wr_comment;
        member_chk_like = user.member_chk_like;
        member_grade = user.member_grade;
        member_status = user.member_status;

        HealthBaseInfo.clear();
        HealthBaseInfo.addAll(user.HealthBaseInfo);
        HealthDetailInfo.clear();
        HealthDetailInfo.addAll(user.HealthDetailInfo);
    }
    public boolean changeHealthBaseInfo(UserHealthBase data){
        int family_index = 100;
        //find index of family member
        for (int i = 0; i< HealthBaseInfo.size(); i++) {
            if(HealthBaseInfo.get(i).member_name.equals(data.member_name)){
                family_index = i;
                break;
            }
            //입력하려는 data.member_name 가 familyHeal...에 없다
            //오류를 던지게 하거나 family_index를 100일 때 오류를 스스로 catch하도록 나중에 작업하는 것이 좋을 듯
            return false;
        }
        //change family health information
        HealthBaseInfo.set(family_index, data);
        return true;
    }
    public boolean changeHealthDetailInfo(UserHealthDetail data){
        int family_index = 100;
        //find index of family member
        for (int i = 0; i< HealthDetailInfo.size(); i++) {
            if(HealthDetailInfo.get(i).member_name.equals(data.member_name)){
                family_index = i;
                break;
            }
            //입력하려는 data.member_name 가 familyHeal...에 없다
            //오류를 던지게 하거나 family_index를 100일 때 오류를 스스로 catch하도록 나중에 작업하는 것이 좋을 듯
            return false;
        }
        //change family health information
        HealthDetailInfo.set(family_index, data);
        return true;
    }

    public void updateHealthInfo(Integer index, UserHealthBase fam_h_b, UserHealthDetail fam_h_d){
        if(HealthBaseInfo.size() > index){
            HealthBaseInfo.set(index, fam_h_b);
        }else {
            while(HealthBaseInfo.size() < index){
                HealthBaseInfo.add(null);
            }
            HealthBaseInfo.add(fam_h_b);
        }

        if(HealthDetailInfo.size() > index){
            HealthDetailInfo.set(index, fam_h_d);
        }else {
            while(HealthDetailInfo.size() < index){
                HealthDetailInfo.add(null);
            }
            HealthDetailInfo.add(fam_h_d);
        }
    }
}
