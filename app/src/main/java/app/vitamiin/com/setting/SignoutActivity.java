package app.vitamiin.com.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.ConfirmDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class SignoutActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context m_context=this;
    int m_nResultType = 200;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    public int m_nConnectType = 0;

    int m_nPart = -1;
    EditText m_edtContent;
    TextView m_tvReason0, m_tvReason1, m_tvReason2, m_tvReason3, m_tvReason4, m_tvReason5, m_tvReason6, m_tvReason7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetUtilConnetServer = new NetUtil().new connectAndgetServer(this);
        setContentView(R.layout.activity_signout);

        initView();
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.tv_signout).setOnClickListener(this);
        m_edtContent = (EditText) findViewById(R.id.edt_content);
        m_tvReason0 = (TextView) findViewById(R.id.tv_reason_0);
        m_tvReason1 = (TextView) findViewById(R.id.tv_reason_1);
        m_tvReason2 = (TextView) findViewById(R.id.tv_reason_2);
        m_tvReason3 = (TextView) findViewById(R.id.tv_reason_3);
        m_tvReason4 = (TextView) findViewById(R.id.tv_reason_4);
        m_tvReason5 = (TextView) findViewById(R.id.tv_reason_5);
        m_tvReason6 = (TextView) findViewById(R.id.tv_reason_6);
        m_tvReason7 = (TextView) findViewById(R.id.tv_reason_7);
        m_tvReason0.setOnClickListener(this);
        m_tvReason1.setOnClickListener(this);
        m_tvReason2.setOnClickListener(this);
        m_tvReason3.setOnClickListener(this);
        m_tvReason4.setOnClickListener(this);
        m_tvReason5.setOnClickListener(this);
        m_tvReason6.setOnClickListener(this);
        m_tvReason7.setOnClickListener(this);
    }

    private void clearReason() {
        m_tvReason0.setSelected(false);
        m_tvReason1.setSelected(false);
        m_tvReason2.setSelected(false);
        m_tvReason3.setSelected(false);
        m_tvReason4.setSelected(false);
        m_tvReason5.setSelected(false);
        m_tvReason6.setSelected(false);
        m_tvReason7.setSelected(false);
        m_edtContent.setText("");
        m_edtContent.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
            case R.id.tv_signout:
                if (m_nPart == -1) {
                    Toast.makeText(this, "탈퇴 사유를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_nPart == 7 && m_edtContent.getText().toString().length() == 0) {
                    Toast.makeText(this, "기타 사유를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                new ConfirmDialog(this, "회원 탈퇴", "탈퇴 시 본 계정으로는 재가입이 불가능합니다.", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_nConnectType = NetUtil.apis_SIGNOUT;
                        mNetUtilConnetServer.connectServer(m_context, handler, m_nConnectType,
                                new String[] {String.valueOf(m_nPart), m_edtContent.getText().toString()});
                    }
                }).show();
                break;
            case R.id.tv_reason_0:
                clearReason();
                m_tvReason0.setSelected(true);
                m_nPart = 0;
                break;
            case R.id.tv_reason_1:
                clearReason();
                m_tvReason1.setSelected(true);
                m_nPart = 1;
                break;
            case R.id.tv_reason_2:
                clearReason();
                m_tvReason2.setSelected(true);
                m_nPart = 2;
                break;
            case R.id.tv_reason_3:
                clearReason();
                m_tvReason3.setSelected(true);
                m_nPart = 3;
                break;
            case R.id.tv_reason_4:
                clearReason();
                m_tvReason4.setSelected(true);
                m_nPart = 4;
                break;
            case R.id.tv_reason_5:
                clearReason();
                m_tvReason5.setSelected(true);
                m_nPart = 5;
                break;
            case R.id.tv_reason_6:
                clearReason();
                m_tvReason6.setSelected(true);
                m_nPart = 6;
                break;
            case R.id.tv_reason_7:
                clearReason();
                m_tvReason7.setSelected(true);
                m_edtContent.setEnabled(true);
                m_nPart = 7;
                break;
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {processForNetEnd();}
        }
    };

    private void processForNetEnd() {
        parseJSON();
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                UserManager.getInstance().releaseUserData();
                UserManager.getInstance().saveData(this,"","","","");
                if(UserManager.getInstance().member_type.equals("facebook")) {
                    disconnectFromFacebook();
                } else if(UserManager.getInstance().member_type.equals("kakao")){
                    disconnectFromKakao();
                } else {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
        } catch (JSONException e) {e.printStackTrace();}
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }).executeAsync();
    }

    private void disconnectFromKakao() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this).setMessage(appendMessage)
            .setPositiveButton(getString(R.string.com_kakao_ok_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManagement.requestUnlink(new UnLinkResponseCallback() {
                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Logger.e(errorResult.toString());
                        }

                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Logger.e(errorResult.toString());
                        }

                        @Override
                        public void onNotSignedUp() {
                            Logger.e("nono");
                        }

                        @Override
                        public void onSuccess(Long userId) {
                            Logger.e("yesyes");

                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                    dialog.dismiss();
                }
            }).setNegativeButton(getString(R.string.com_kakao_cancel_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
    }

    JSONObject _jResult;

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }
}
