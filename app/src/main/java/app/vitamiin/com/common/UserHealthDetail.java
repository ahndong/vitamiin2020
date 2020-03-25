package app.vitamiin.com.common;

import java.io.Serializable;
/**
 * Created by dong8 on 2016-10-07.
 */

public class UserHealthDetail implements Serializable {
    private UserHealthDetail userHealthDetail;

    /**
     * 객체 얻기
     *
     * @return
     */
    public UserHealthDetail getInstance() {
        if (userHealthDetail == null) {
            userHealthDetail = new UserHealthDetail();
        }
        return userHealthDetail;
    }
    public UserHealthDetail() {
        releaseUserHealthDetail();
    }
    ///////////////////////////////////////////////////////////////////////////////////
//t_health_detail
    public boolean isset;
    public int member_no;               /* 유저 아이디 * ex:2348923 */
    public String member_nick_name = null;        /* 유저 닉네임 */
    public String member_name = null;
    public int member_health_detail_update;      //최근 health base 입력 날짜

    public int member_marry;
    public int member_sleep;
    public int member_drink_amount;
    public int member_drink_count;
    public int member_smoke;
    public int member_exercise;

    public String member_allergy_symptom = null;
    public String member_pee_symptom = null;
    public String member_dung_symptom = null;
    public String member_eat_pattern = null;
    public String member_life_pattern = null;

    public String member_allergy = null;
    public String member_eat_drug = null;
    public String member_eat_healthfood = null;
    public String member_health_state = null;
    ///////////////////////////////////////////////////////////////////////////////////
/* 유저정보 초기화 */
    public void releaseUserHealthDetail() {
        isset = false;
        member_no = -1;               /* 유저 아이디 * ex:2348923 */
        member_nick_name = "";        /* 유저 닉네임 */
        member_name = "";
        member_health_detail_update = -1;    //최근	health	base	입력	날짜

        member_marry = -1;
        member_sleep = -1;
        member_drink_amount = -1;
        member_drink_count = -1;
        member_smoke = -1;
        member_exercise = -1;

        member_allergy_symptom = "";
        member_pee_symptom = "";
        member_dung_symptom = "";
        member_eat_pattern = "";
        member_life_pattern = "";

        member_allergy = "";
        member_eat_drug = "";
        member_eat_healthfood = "";
        member_health_state = "";
    }
    public void setUserHealthDetail(UserHealthDetail data) {
        isset = data.isset;
        member_no = data.member_no;
        member_nick_name = data.member_nick_name;
        member_name = data.member_name;
        member_health_detail_update = data.member_health_detail_update;

        member_marry	=	data.member_marry;
        member_sleep	=	data.member_sleep;
        member_drink_amount	=	data.member_drink_amount;
        member_drink_count	=	data.member_drink_count;
        member_smoke	=	data.member_smoke;
        member_exercise	=	data.member_exercise;

        member_allergy_symptom	=	data.member_allergy_symptom;
        member_pee_symptom	=	data.member_pee_symptom;
        member_dung_symptom	=	data.member_dung_symptom;
        member_eat_pattern	=	data.member_eat_pattern;
        member_life_pattern	=	data.member_life_pattern;

        member_allergy	=	data.member_allergy;
        member_eat_drug	=	data.member_eat_drug;
        member_eat_healthfood	=	data.member_eat_healthfood;
        member_health_state	=	data.member_health_state;
    }
}
