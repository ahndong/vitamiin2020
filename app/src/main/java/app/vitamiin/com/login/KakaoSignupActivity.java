package app.vitamiin.com.login;

import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.home.MainActivity;
/**
 * Created by dong8 on 2017-02-25.
 */

/**
 * 유효한 세션이 있다는 검증 후
 * me를 호출하여 가입 여부에 따라 가입 페이지를 그리던지 Main 페이지로 이동 시킨다.
 */
public class KakaoSignupActivity extends AppCompatActivity {
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                Boolean tf = true;
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                redirectMainActivity();
            }
        });
    }

    private void redirectMainActivity() {
        //추후 프로필 설정 으로. by 동현
        final Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }

    protected void redirectLoginActivity() {
        final Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }
//
//    protected void showSignup() {
//        setContentView(R.layout.layout_usermgmt_signup);
//        final ExtraUserPropertyLayout extraUserPropertyLayout = (ExtraUserPropertyLayout) findViewById(R.id.extra_user_property);
//        Button signupButton = (Button) findViewById(R.id.buttonSignup);
//        signupButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                requestSignUp(extraUserPropertyLayout.getProperties());
//            }
//        });
//    }
//
//    private void requestSignUp(final Map<String, String> properties) {
//        UserManagement.requestSignup(new ApiResponseCallback<Long>() {
//            @Override
//            public void onNotSignedUp() {
//            }
//
//            @Override
//            public void onSuccess(Long result) {
//                requestMe();
//            }
//
//            @Override
//            public void onFailure(ErrorResult errorResult) {
//                final String message = "UsermgmtResponseCallback : failure : " + errorResult;
//                com.kakao.util.helper.log.Logger.w(message);
//                KakaoToast.makeToast(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                finish();
//            }
//
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//            }
//        }, properties);
//    }
}
