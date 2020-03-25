package app.vitamiin.com.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.SelectPhotoManager;
import app.vitamiin.com.home.ManageProfileActivity;
import app.vitamiin.com.setting.MyPageActivity;

public class PhotoSelectDialog extends Dialog implements View.OnClickListener, SelectPhotoManager.PhotoSelectCallback {
    private Context _context;
    int m_type = 0;

    public PhotoSelectDialog(Context context, int type) {
        super(context);
        _context = context;
        m_type = type;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_photo);

        findViewById(R.id.rly_camera).setOnClickListener(this);
        findViewById(R.id.rly_gallery).setOnClickListener(this);
        findViewById(R.id.imv_male_1).setOnClickListener(this);
        findViewById(R.id.imv_male_2).setOnClickListener(this);
        findViewById(R.id.imv_male_3).setOnClickListener(this);
        findViewById(R.id.imv_male_4).setOnClickListener(this);
        findViewById(R.id.imv_female_1).setOnClickListener(this);
        findViewById(R.id.imv_female_2).setOnClickListener(this);
        findViewById(R.id.imv_female_3).setOnClickListener(this);
        findViewById(R.id.imv_female_4).setOnClickListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rly_camera:
                doTakePhotoAction();
                break;
            case R.id.rly_gallery:
                goToPhotoAlbum();
                break;
            case R.id.imv_male_1:
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setSelectedImage(0);
                else if (m_type == 2)
                    ((MyPageActivity) _context).setSelectedImage(0);
                dismiss();
                break;
            case R.id.imv_male_2:
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setSelectedImage(1);
                else if (m_type == 2)
                    ((MyPageActivity) _context).setSelectedImage(1);
                dismiss();
                break;
            case R.id.imv_male_3:
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setSelectedImage(2);
                else if (m_type == 2)
                    ((MyPageActivity) _context).setSelectedImage(2);
                dismiss();
                break;
            case R.id.imv_male_4:
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setSelectedImage(3);
                else if (m_type == 2)
                    ((MyPageActivity) _context).setSelectedImage(3);
                dismiss();
                break;
            case R.id.imv_female_1:
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setSelectedImage(4);
                else if (m_type == 2)
                    ((MyPageActivity) _context).setSelectedImage(4);
                dismiss();
                break;
            case R.id.imv_female_2:
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setSelectedImage(5);
                else if (m_type == 2)
                    ((MyPageActivity) _context).setSelectedImage(5);
                dismiss();
                break;
            case R.id.imv_female_3:
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setSelectedImage(6);
                else if (m_type == 2)
                    ((MyPageActivity) _context).setSelectedImage(6);
                dismiss();
                break;
            case R.id.imv_female_4:
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setSelectedImage(7);
                else if (m_type == 2)
                    ((MyPageActivity) _context).setSelectedImage(7);
                dismiss();
                break;
            case R.id.tv_close:
                dismiss();
                break;
        }
    }

    private SelectPhotoManager m_photoManger = null;

    /**
     * 표준카메라 호출 하기
     */
    private void doTakePhotoAction() {

        if (m_photoManger == null) {
            m_photoManger = new SelectPhotoManager((AppCompatActivity) _context);
            m_photoManger.setPhotoSelectCallback(this);
        }
        m_photoManger.doTakePicture();
        dismiss();
    }

    /**
     * 사진앨범 액티비티로 이동
     */
    private void goToPhotoAlbum() {
        if (m_photoManger == null) {
            m_photoManger = new SelectPhotoManager((AppCompatActivity) _context);
            m_photoManger.setPhotoSelectCallback(this);
        }
        m_photoManger.doPickFromGallery();
        dismiss();
    }

    @Override
    public void onSelectImageDone(Bitmap image, File file) {
        if (image != null && file != null) {
            if (m_type == 1)
                ((ManageProfileActivity) _context).setSelectedImage(image, file);
            else if (m_type == 2)
                ((MyPageActivity) _context).setSelectedImage(image, file);
        }
    }

    @Override
    public void onFailedSelectImage(int errorCode, String err) {
        Boolean temp = true;
    }

    @Override
    public void onDeleteImage() {}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        m_photoManger.onActivityResult(requestCode, resultCode, data);
    }
}
