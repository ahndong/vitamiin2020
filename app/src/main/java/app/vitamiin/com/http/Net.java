package app.vitamiin.com.http;

/**
 * 서버통신을 위한 상수변수들
 *
 * @author 534
 */
public final class Net {

    // --------------------------- thread for network process
    public final static int THREAD_REQUEST_END = 100;

    // ---------------------------Network connected Type
    /**
     * 연동 대기 중
     */
    public final static int CONNECTION_NONE = 10;
    /**
     * 연동 성공
     */
    public final static int CONNECTION_SUCCSES = 1;
    /**
     * 연동 실패 -> 서버에서 내려오는 값(result_code)이 200 이 아닌경우
     */
    public final static int CONNECTION_FAILD = 0;
    /**
     * 연동 오류
     */
    public final static int CONNECTION_ERROR = 2;
    /**
     * JSON 형식 오류
     */
    public final static int JSON_PARSER_FAILD = 3;
    /**
     * 스트림 오류
     */
    public final static int BAD_STREAM = 4;
    /**
     * 요청파라메터 오류
     */
    public final static int BAD_REQUEST_PARAMS = 5;
    /**
     * 서버 URL
     */
    //public final static String URL_SERVER1 = "http://vitamin.kakaoapps.co.kr/";
    //public final static String URL_SERVER = "http://vitamin.kakaoapps.co.kr/api";
    public final static String URL_SERVER1 = "https://admin.vitamiin.co.kr/";
    public final static String URL_SERVER2 = "https://admin.vitamiin.co.kr";
    public final static String URL_SERVER = "https://admin.vitamiin.co.kr/api";
    /**
     * 서버 API
     */
    public final static String URL_SERVER_API = "/api.php";
    public static final String NET_COMMON_STRING_WAITING = "Please Wait..";
    public static final String NET_POST_FIELD_REQUEST = "DATA";
    public static final String NET_POST_FIELD_ACT = "ACT";

    /**
     * 서버 접속 ACT
     */
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////  사용자 정보 관련 API///////////////////////////////////////////
    public final static String apis_REG_FCMTOKEN_TO_SERVER = "069" ;
    public final static String apis_CHECK_ID = "003";
    public final static String apis_CHECK_NICK = "004";
//    public final static String POST_FIELD_ACT_FINDID = "005";
    public final static String apis_FIND_PW = "006";
    public final static String apis_UPDATE_PW = "060";
    public final static String apis_REGISTER_USER = "002"; //회원 가입
    public final static String apis_SIGNOUT = "040";
    public final static String apis_LOGIN = "001"; // 로그인
    public final static String apis_UPDATE_PROFILE = "059";
    public final static String apis_CHANGE_PHOTO = "049";
    public final static String apis_REGISTER_FAMILY = "030";
    public final static String apis_UPDATE_BASE = "065";
    public final static String apis_GET_FAMILY = "031";
    public final static String apis_DELETE_FAMILY = "066";
    public final static String apis_STORE_INFO_LIVING = "057";
    public final static String apis_STORE_INFO_BODY_ALL = "058";
    public final static String apis_STORE_INFO_DETAIL_1OF3 = "056";  //가족 및 본인 상세 수정 1번화면: RegisterLifeActivity의 수정모드 진입Activity의 수정모드 진입
    public final static String apis_STORE_INFO_DETAIL_2OF3 = "072"; //가족 및 본인 상세 수정 2번화면: RegisterDetailLifeActivity의 수정모드 진입
//    public final static String apis_STORE_INFO_DETAIL_3OF3_1_Allergy = "053";  //가족 및 본인 상세 수정 3번화면: RegisterAllergyActivity의 수정모드 진입
//    public final static String apis_STORE_INFO_DETAIL_3OF3_2_Drug = "070"; //가족 및 본인 상세 수정 4번화면: RegisterDetailDrugActivity의 수정모드 진입
//    public final static String apis_STORE_INFO_DETAIL_3OF3_3_DrugHealthFood = "071"; //가족 및 본인 상세 수정 5번화면: RegisterDetailHealthfood
//    public final static String apis_STORE_INFO_DETAIL_3OF3_4_Healthstate = "073"; //가족 및 본인 상세 수정 6번화면: RegisterDetailHealthstateActivity의 수정모드 진입
    public final static String apis_STORE_INFO_DETAIL_3OF3 = "081" ;
    public final static String apis_STORE_INFO_DETAIL_ALL = "074"; //가족 및 본인 상세 작성 모드(추천화면에서 진입): 상세정보 전체 한번에 저장

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////      리뷰 관련 API   ///////////////////////////////////////////
    public final static String apis_GET_REVIEW_LIST = "021";
    public final static String apis_GET_MY_REVIEW_LIST = "045";
    public final static String apis_GET_REVIEW_BY_ID = "077" ;
//    public final static String apis_GET_MORE_REVIEW_IN_DETAIL_GOOD = "082" ;
    public final static String apis_WRITE_REVIEW = "011";
    public final static String apis_UPDATE_REVIEW = "064";

    public final static String apis_GET_EXP_LIST = "023";
    public final static String apis_GET_MY_EXP_LIST = "047";
    public final static String apis_GET_EXP_BY_ID = "078" ;
    public final static String apis_WRITE_EXP = "032";
    public final static String apis_UPDATE_EXP = "067";

    public final static String apis_DETAIL_REVIEW = "015";
    public final static String apis_GET_REVIEW_WRITE_CATEGORY = "062";
    public final static String apis_DELETE_REVIEW = "063";
    public final static String apis_REG_COMMENT_REV_AND_EXP_AND_EVENT = "085" ;
    public final static String apis_DEL_COMMENT_REV_AND_EXP_AND_EVENT = "086" ;
    public final static String apis_LIKE_REVIEW = "017";
    public final static String apis_REPORT_REVIEW = "020";
    public final static String apis_CHANGE_ISOK = "088" ;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////    이벤트 관련 API   ///////////////////////////////////////////
    public final static String apis_GET_EVENT_LIST = "083" ;
    public final static String apis_GET_EVENT_BY_ID = "084" ;
    public final static String apis_GET_EVENT_DETAIL = "087" ;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////      비타위키 관련 API   ///////////////////////////////////////////
    public final static String apis_GET_POWER_REVIEW_LIST = "026";
    public final static String apis_GET_HEALTH_FOOD_LIST = "024";
    public final static String apis_GET_EXP_COLUMN_LIST = "089";
    public final static String apis_ADD_VIEWCNT_POWER_REVIEW = "090";
    public final static String apis_GET_HEALTH_FOOD_DETAIL = "025";
    public final static String apis_ADD_VIEWCNT_EXP_COLUMN = "091";
    public final static String apis_GET_HEALTH_FOOD_BY_ID = "080";
    public final static String apis_SET_LIKE_FOOD = "022";

    public final static String no_more_use_apis_GET_POWER_REVIEW_AND_EXP_COLUMN_DETAIL = "027";

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////      기타 관련 API   ///////////////////////////////////////////
    public final static String apis_GET_PRESET_ACCOUNT = "092";

    public final static String POST_FIELD_ACT_MAINGOOD = "007";
    public final static String POST_FIELD_ACT_DETAILLIST = "008";

    public final static String POST_FIELD_ACT_GOODLIST = "009";

    public final static String POST_FIELD_ACT_BUSINESSLIST = "010";
    public final static String POST_FIELD_ACT_DETAILGOOD = "012";
    public final static String POST_FIELD_ACT_LIKEGOOD = "013";
    public final static String POST_FIELD_ACT_REPORTGOOD = "014";
    public final static String POST_FIELD_ACT_VIEWGOOD_FOR_CHART = "016";
    public final static String POST_FIELD_ACT_FALLOW_USER = "018";


    public final static String POST_FIELD_ACT_LIKEWIKI = "028";


    public final static String POST_FIELD_ACT_VIEW_NOTICE = "033";
    public final static String POST_FIELD_ACT_VIEW_FAQ = "034";
    public final static String POST_FIELD_ACT_APP_SETTING = "037";
    public final static String POST_FIELD_ACT_ADD_QUESTION = "038";
    public final static String POST_FIELD_ACT_UPDATE_NOTI = "039";
    public final static String POST_FIELD_ACT_FALLOWING = "042";
    public final static String POST_FIELD_ACT_FALLOWER = "043";

    public final static String POST_FIELD_ACT_DISABLE_FALLOWING = "044";

    public final static String POST_FIELD_ACT_MY_REVIEWSIKDAN = "046";
    public final static String POST_FIELD_ACT_MY_LIKE = "048";

    public final static String POST_FIELD_ACT_RECOMMEND_FOOD = "050";
    public final static String POST_FIELD_ACT_RECOMMEND_GOOD = "051";
    public final static String POST_FIELD_ACT_UPDATE_DISEASE = "054";
    public final static String POST_FIELD_ACT_UPDATE_PREFER = "055";

    public final static String POST_FIELD_ACT_GETGOOD_BYID = "061";
    public final static String POST_FIELD_ACT_REQUEST_GOOD_INFOS = "075" ;
    public final static String POST_FIELD_ACT_REQUEST_SEARCH_KEYWORD = "076" ;
    public final static String POST_FIELD_ACT_GETPOWERCOLUMN_BYID = "079" ;


    public final static String NET_VALUE_CODE = "statuscode";
    public final static String NET_VALUE_MSG = "msg";
    public final static String NET_VALUE_RESULT = "result";
    public final static String NET_VALUE_LIST = "list";
    public final static String NET_VALUE_UPLOAD_FILES = "files[]";

    public final static String NET_VALUE_TYPE = "type";
    public final static String NET_VALUE_FB_ID = "fb_id";
    public final static String NET_VALUE_FB_NAME = "fb_name";
    public final static String NET_VALUE_FB_PICTURE = "fb_picture";
    public final static String NET_VALUE_KAKAO_ID = "kakao_id";
    public final static String NET_VALUE_KAKAO_NAME = "kakao_name";
    public final static String NET_VALUE_KAKAO_PICTURE = "kakao_picture";
    public final static String NET_VALUE_NAVER_ID = "naver_id";
    public final static String NET_VALUE_NAVER_NAME = "naver_name";
    public final static String NET_VALUE_NAVER_PICTURE = "naver_picture";
    public final static String NET_VALUE_MEMBER_NO = "f_no";
    public final static String NET_VALUE_MEMBER_ID = "f_id";
    public final static String NET_VALUE_MEMBER_INTRO = "intro";
    public final static String NET_VALUE_MEMBER_ID_TO = "f_id_to";
    public final static String NET_VALUE_MEMBER_PHOTO = "f_photo";
    public final static String NET_VALUE_MEMBER_PWD = "f_password";
    public final static String NET_VALUE_MEMBER_NICK = "f_nick";
    public final static String NET_VALUE_MEMBER_EMAIL = "f_email";
    public final static String NET_VALUE_MEMBER_BIRTH = "f_birth";
    public final static String NET_VALUE_MEMBER_SEX = "f_sex";
    public final static String NET_VALUE_REGDATE = "regdate";
    public final static String NET_VALUE_PHOTO = "photo";
    public final static String NET_VALUE_PAGE = "page";
    public final static String NET_VALUE_TOTAL_PAGE = "total_page";
    public final static String NET_VALUE_TOTAL_COUNT = "total_count";
    public final static String NET_VALUE_DEVICE_NO = "device_no";

    public final static String NET_VALUE_DEVICE_TYPE = "device_type";
    public final static String NET_VALUE_DEVICE_ID = "device_id";
    public final static String NET_VALUE_DEVICE_OS_VER = "device_os_ver";
    public final static String NET_VALUE_DEVICE_MODEL = "device_model";
    public final static String NET_VALUE_PUSH_TOKEN = "push_token";
    public final static String NET_VALUE_FCM_TOKEN = "fcm_token";
    public final static String NET_VALUE_DELETE_PHOTO_IDS = "delete_photo_ids";
}
