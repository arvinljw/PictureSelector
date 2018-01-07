package net.arvin.selectordemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * created by arvin on 16/10/24 14:55
 * email：1035407623@qq.com
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    /**
     * 权限回调接口
     */
    private CheckPermListener mListener;
    protected static final int RC_PERM = 123;

    public void checkPermission(CheckPermListener listener, String resString, String... mPerms) {
        mListener = listener;
        if (EasyPermissions.hasPermissions(this, mPerms)) {
            if (mListener != null)
                mListener.agreeAllPermission();
        } else {
            EasyPermissions.requestPermissions(this, resString, RC_PERM, mPerms);
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EasyPermissions.SETTINGS_REQ_CODE) {
            backFromSetting();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // 只同意了部分权限
    }

    @Override
    public void onPermissionsAllGranted() {
        if (mListener != null)
            mListener.agreeAllPermission();//同意了全部权限的回调
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
                "当前应用缺少必要权限。\n请点击\"设置\"-\"权限\"-打开所需权限。",
                "设置", "取消", null, perms);
    }

    protected void backFromSetting() {
    }

    public interface CheckPermListener {
        //权限通过后的回调方法
        void agreeAllPermission();
    }
}
