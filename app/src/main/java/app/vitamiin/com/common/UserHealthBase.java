package app.vitamiin.com.common;

import java.io.Serializable;

/**
 * Created by dong8 on 2016-10-07.
 */

public class UserHealthBase implements Serializable {
    private UserHealthBase userHealthBase;

    /**
     * 객체 얻기
     *
     * @return
     */
    public  UserHealthBase getInstance() {
        if (userHealthBase == null) {
            userHealthBase = new UserHealthBase();
        }
        return userHealthBase;
    }

    public UserHealthBase() {
        releaseUserHealthBase();
    }

    ///////////////////////////////////////////////////////////////////////////////////
//t_health_base
    public boolean isset;
    public int member_no;               /* 유저 아이디 * ex:2348923 */
    public String member_nick_name = "";        /* 유저 닉네임 */
    public String member_name = "";
    public int member_sex; //0.male, 1:female
    public String member_birth = "";

    public int member_health_base_update = 0;      //최근 health base 입력 날짜
    //health_base table
    public double member_height;
    public double member_weight;
    public double member_bmi;
    public int member_dwelling = 0;
    public int member_examinee = 1; //0:yes 1:no
    public int member_pregnant = 1; //0:yes 1:no
    public int member_lactating = 1; //0:yes 1:no
    public int member_climacterium = 1; //0:yes 1:no
    public String member_disease = ""; //0~22, ex:0,1,2,3,4, Max:5
    public String member_interest_health = ""; //0~27, ex:0,1,2,3,4, Max:5
    public String member_prefer_healthfood = ""; //0~35, ex:0,1,2,3,4, Max:5
    ///////////////////////////////////////////////////////////////////////////////////
/* 유저정보 초기화 */
    public void releaseUserHealthBase() {
        isset = false;
        member_no = -1;
        member_nick_name = "";
        member_name = "";
        member_sex = -1;     //0.male,	1:female
        member_birth = null;

        member_health_base_update = -1;    //최근	health	base	입력	날짜

        member_height = -1;
        member_weight = -1;
        member_bmi = -1;
        member_dwelling = -1;
        member_examinee = -1;	        //0:수험생yes 1:수험생no
        member_pregnant = -1;	        //0:임신yes 1:임신no
        member_lactating = -1;	        //0:수유yes 1:수유no
        member_climacterium = -1;	    //0:갱년yes 1:갱년no
        member_disease = "";	//0~22,	ex:0,1,2,3,4,	Max:5
        member_interest_health = "";	//0~27,	ex:0,1,2,3,4,	Max:5
        member_prefer_healthfood = "";	//0~35,	ex:0,1,2,3,4,	Max:5
    }

    public void setUserHealthBase(UserHealthBase data) {
        isset = data.isset;
        member_no = data.member_no;
        member_nick_name = data.member_nick_name;
        member_name = data.member_name;
        member_sex = data.member_sex;
        member_birth = data.member_birth;

        member_health_base_update = data.member_health_base_update;

        member_height = data.member_height;
        member_weight = data.member_weight;
        member_bmi = data.member_bmi;
        member_dwelling = data.member_dwelling;
        member_examinee = data.member_examinee;
        member_pregnant = data.member_pregnant;
        member_lactating = data.member_lactating;
        member_climacterium = data.member_climacterium;
        member_disease = data.member_disease;
        member_interest_health = data.member_interest_health;
        member_prefer_healthfood = data.member_prefer_healthfood;
    }
}
