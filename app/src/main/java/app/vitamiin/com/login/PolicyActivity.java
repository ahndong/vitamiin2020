package app.vitamiin.com.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.R;


public class PolicyActivity extends AppCompatActivity implements View.OnClickListener {

    Context m_context;
    ImageView m_imvAgree1, m_imvAgree2, m_imvAgree3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        m_context = this;

        initView();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);
        findViewById(R.id.tv_agree_1).setOnClickListener(this);
        findViewById(R.id.tv_agree_2).setOnClickListener(this);
        findViewById(R.id.tv_agree_3).setOnClickListener(this);
        m_imvAgree1 = (ImageView) findViewById(R.id.imv_agree_1);
        m_imvAgree2 = (ImageView) findViewById(R.id.imv_agree_2);
        m_imvAgree3 = (ImageView) findViewById(R.id.imv_agree_3);

        m_imvAgree1.setOnClickListener(this);
        m_imvAgree2.setOnClickListener(this);
        m_imvAgree3.setOnClickListener(this);

        if (getIntent().getBooleanExtra("back", false)) {
            m_imvAgree1.setSelected(true);
            m_imvAgree2.setSelected(true);
            m_imvAgree3.setSelected(true);
        }

        if (getIntent().getBooleanExtra("fromSetting", false)) {
            m_imvAgree1.setSelected(true);
            m_imvAgree2.setSelected(true);
            m_imvAgree3.setSelected(true);

            m_imvAgree1.setEnabled(false);
            m_imvAgree2.setEnabled(false);
            m_imvAgree3.setEnabled(false);

            findViewById(R.id.tv_next).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_back:
                if (getIntent().getBooleanExtra("fromSetting", false)) {
                    finish();
                } else {
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.tv_next:
                if (!m_imvAgree1.isSelected() || !m_imvAgree2.isSelected() || !m_imvAgree3.isSelected()) {
                    Toast.makeText(this, "약관 내용에 동의해 주십시오.", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent = new Intent(this, RegisterIDActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_agree_1:
                intent = new Intent(this, PolicyViewActivity.class);
                intent.putExtra("fromSetting", true);
                intent.putExtra("type", 0);
                intent.putExtra("title", "이용약관 동의");
                break;
            case R.id.tv_agree_2:
                intent = new Intent(this, PolicyViewActivity.class);
                intent.putExtra("fromSetting", true);
                intent.putExtra("type", 1);
                intent.putExtra("title", "개인정보 취급 방침 동의");
                break;
            case R.id.tv_agree_3:
                intent = new Intent(this, PolicyViewActivity.class);
                intent.putExtra("fromSetting", true);
                intent.putExtra("type", 2);
                intent.putExtra("title", "개인정보 제3자 제공에 대한 방침 동의");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
