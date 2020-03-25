package app.vitamiin.com.common;

public class Const {
    public static boolean IS_TEST_CODE = true;

    /*Confirm Type*/
    public static int CONFIRM_YES_NO = 1; //예, 아니
    public static int CONFIRM_LOGIN_FINDPWD = 2; //로그인, 비밀번호 찾기
    public static int CONFIRM_CHECK_CANCEL = 3; //확인, 취소
    public static int CONFIRM_REG_CANCEL = 4; //등록, 취소

    /*Alert Type*/
    public static int ALERT_YES = 1; //확인
    public static int ALERT_LOGIN = 2; //로그인

    public static String g_gcmRegId = null;
    public static int g_nDeviceNo = 0;
    public static String g_fcmToken = null;
    public static final String GCM_SENDERID = "654230625417";

    public static float MAPZOOM_DEFAULT = 14.0f/* 2 */;
    public static float MAPZOOM_MAX = 25.0f;
    public static float MAPZOOM_MIN = 10.0f;
}
