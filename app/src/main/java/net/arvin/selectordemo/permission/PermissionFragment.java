package net.arvin.selectordemo.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 2018/9/17 16:32
 * Function：
 * Desc：6.0权限动态申请实现类
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PermissionFragment extends Fragment {
    private static final int REQUEST_CODE_PERMISSION = 0x1001;
    private static final int REQUEST_CODE_SETTING = 0x1002;
    private static final int REQUEST_CODE_INSTALL_APP = 0x1003;

    private Context context;
    private PermissionUtil permissionUtil;

    private String requestMsg;
    private String[] requestPermissions;
    private boolean requestInstall;

    private AlertDialog requestPermissionDialog;
    private AlertDialog openSettingDialog;
    private AlertDialog openInstallAppDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();
        if (savedInstanceState != null) {
            requestMsg = savedInstanceState.getString("requestMsg");
            requestPermissions = savedInstanceState.getStringArray("requestPermissions");
        }

        if (requestMsg != null) {
            request(requestMsg, requestPermissions);
        }
        if (requestInstall) {
            requestInstallApp();
        }
    }

    public void setPermissionUtil(PermissionUtil permissionUtil) {
        this.permissionUtil = permissionUtil;
        requestPermissionDialog = null;
        openSettingDialog = null;
        openInstallAppDialog = null;
    }

    public void request(String msg, String[] permissions) {
        this.requestMsg = msg;
        this.requestPermissions = permissions;
        if (context == null) {
            return;
        }
        if (checkIsGranted(permissions)) {
            requestBack(true);
        } else {
            requestPermissions(msg);
        }
    }

    private boolean checkIsGranted(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String perm : permissions) {
            boolean hasPerm = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
                    || PermissionChecker.checkSelfPermission(context, perm) == PermissionChecker.PERMISSION_GRANTED;
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions(String msg) {
        boolean shouldShowRationale = false;
        for (String perm : requestPermissions) {
            shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(perm);
        }

        if (shouldShowRationale) {
            Activity activity = getActivity();
            if (null == activity || permissionUtil == null) {
                Log.d("PermissionFragment", "permissionUtil is null");
                return;
            }
            showRequestPermissionDialog(msg, activity);
        } else {
            requestPermissions(requestPermissions, REQUEST_CODE_PERMISSION);
        }
    }

    private void showRequestPermissionDialog(String msg, Activity activity) {
        PermissionUtil.Builder resBuilder = permissionUtil.getBuilder();
        if (!resBuilder.isShowRequest()) {
            requestPermissions(requestPermissions, REQUEST_CODE_PERMISSION);
            return;
        }
        if (requestPermissionDialog == null) {
            requestPermissionDialog = new CustomAlertDialogBuilder(activity)
                    .initRequestPermission(resBuilder, msg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(requestPermissions, REQUEST_CODE_PERMISSION);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestBack(false);
                        }
                    }).create();
            requestPermissionDialog.setCancelable(resBuilder.isRequestCancelable());
            requestPermissionDialog.setCanceledOnTouchOutside(resBuilder.isRequestCancelable());
            requestPermissionDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    requestBack(false);
                }
            });
        }
        requestPermissionDialog.setMessage(msg);
        requestPermissionDialog.show();
        setAlertDialogColor(requestPermissionDialog, resBuilder);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE_PERMISSION) {
            return;
        }

        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        if (!denied.isEmpty()) {
            onPermissionsDenied(denied);
        }

        if (!granted.isEmpty() && denied.isEmpty()) {
            requestBack(true);
        }
    }

    private void onPermissionsDenied(List<String> deniedPerms) {
        boolean shouldShowRationale = true;
        for (String perm : deniedPerms) {
            shouldShowRationale = shouldShowRequestPermissionRationale(perm);
            if (!shouldShowRationale) {
                break;
            }
        }
        if (!shouldShowRationale) {
            final Activity activity = getActivity();
            if (null == activity || permissionUtil == null) {
                return;
            }
            PermissionUtil.Builder resBuilder = permissionUtil.getBuilder();
            if (!resBuilder.isShowSetting()) {
                requestBack(false, true);
                return;
            }
            if (openSettingDialog == null) {
                openSettingDialog = new CustomAlertDialogBuilder(activity)
                        .initSetting(resBuilder, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openSetting(activity);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestBack(false);
                            }
                        }).create();
                openSettingDialog.setCancelable(resBuilder.isSettingCancelable());
                openSettingDialog.setCanceledOnTouchOutside(resBuilder.isSettingCancelable());
                openSettingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        requestBack(false);
                    }
                });
            }
            openSettingDialog.show();
            setAlertDialogColor(openSettingDialog, resBuilder);
        } else {
            requestBack(false);
        }
    }

    private void openSetting(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CODE_SETTING);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING) {
            request(requestMsg, requestPermissions);
        }
        if (requestCode == REQUEST_CODE_INSTALL_APP) {
            requestInstallApp(true);
        }
    }

    public void requestInstallApp() {
        requestInstallApp(false);
    }

    private void requestInstallApp(boolean fromResult) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context == null) {
                requestInstall = true;
                return;
            }
            boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                showOpenInstallAppPermissionDialog(fromResult);
                return;
            }
        }
        callCanInstallApp(true);
    }

    private void showOpenInstallAppPermissionDialog(boolean fromResult) {
        final Activity activity = getActivity();
        if (null == activity || permissionUtil == null) {
            return;
        }
        PermissionUtil.Builder resBuilder = permissionUtil.getBuilder();
        if (!resBuilder.isShowInstall()) {
            if (fromResult) {
                callCanInstallApp(false);
            } else {
                openInstallAppSetting();
            }
            return;
        }
        if (openInstallAppDialog == null) {
            openInstallAppDialog = new CustomAlertDialogBuilder(activity)
                    .initInstallApp(resBuilder, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openInstallAppSetting();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callCanInstallApp(false);
                        }
                    }).create();
            openInstallAppDialog.setCancelable(resBuilder.isSettingCancelable());
            openInstallAppDialog.setCanceledOnTouchOutside(resBuilder.isSettingCancelable());
            openInstallAppDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    callCanInstallApp(false);
                }
            });
        }
        openInstallAppDialog.show();
        setAlertDialogColor(openInstallAppDialog, resBuilder);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void openInstallAppSetting() {
        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, REQUEST_CODE_INSTALL_APP);
    }

    private void requestBack(boolean granted) {
        requestBack(granted, false);
    }

    private void requestBack(boolean granted, boolean isAlwaysDenied) {
        if (permissionUtil != null) {
            permissionUtil.requestBack(granted, isAlwaysDenied);
        }
    }

    private void callCanInstallApp(boolean canInstall) {
        if (permissionUtil != null) {
            permissionUtil.callCanInstallApp(canInstall);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("requestMsg", requestMsg);
        outState.putStringArray("requestPermissions", requestPermissions);
    }

    @Override
    public void onDestroyView() {
        context = null;
        permissionUtil = null;
        super.onDestroyView();
    }

    private void setAlertDialogColor(AlertDialog alertDialogColor, PermissionUtil.Builder resBuilder) {
        if (isValidateRes(resBuilder.getEnsureBtnColor())) {
            alertDialogColor.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resBuilder.getEnsureBtnColor());
        }
        if (isValidateRes(resBuilder.getCancelBtnColor())) {
            alertDialogColor.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resBuilder.getCancelBtnColor());
        }
        if (isValidateRes(resBuilder.getTitleColor())) {
            setReflectTextColor(alertDialogColor, "mTitleView", resBuilder.getTitleColor());
        }
        if (isValidateRes(resBuilder.getMsgColor())) {
            setReflectTextColor(alertDialogColor, "mMessageView", resBuilder.getMsgColor());
        }
    }

    private boolean isValidateRes(int resColor) {
        return resColor != 0;
    }

    private void setReflectTextColor(AlertDialog alertDialog, String fieldName, @ColorInt int color) {
        try {
            Class<AlertDialog> dialogClass = AlertDialog.class;
            Field mAlertField = dialogClass.getDeclaredField("mAlert");
            mAlertField.setAccessible(true);
            Object mAlert = mAlertField.get(alertDialog);

            Field field = mAlert.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            TextView textView = (TextView) field.get(mAlert);
            textView.setTextColor(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class CustomAlertDialogBuilder extends AlertDialog.Builder {
        CustomAlertDialogBuilder(@NonNull Context context) {
            super(context);
        }

        CustomAlertDialogBuilder initRequestPermission(PermissionUtil.Builder resBuilder, String msg,
                                                       DialogInterface.OnClickListener ensureListener, DialogInterface.OnClickListener cancelListener) {
            setMessage(msg);
            if (!TextUtils.isEmpty(resBuilder.getTitleText())) {
                setTitle(resBuilder.getTitleText());
            }
            if (!TextUtils.isEmpty(resBuilder.getEnsureBtnText())) {
                setPositiveButton(resBuilder.getEnsureBtnText(), ensureListener);
            }
            if (!TextUtils.isEmpty(resBuilder.getCancelBtnText())) {
                setNegativeButton(resBuilder.getCancelBtnText(), cancelListener);
            }
            return this;
        }

        CustomAlertDialogBuilder initSetting(PermissionUtil.Builder resBuilder, DialogInterface.OnClickListener ensureListener,
                                             DialogInterface.OnClickListener cancelListener) {
            setMessage(resBuilder.getSettingMsg());
            if (!TextUtils.isEmpty(resBuilder.getTitleText())) {
                setTitle(resBuilder.getTitleText());
            }
            if (!TextUtils.isEmpty(resBuilder.getSettingEnsureText())) {
                setPositiveButton(resBuilder.getSettingEnsureText(), ensureListener);
            }
            if (!TextUtils.isEmpty(resBuilder.getSettingCancelText())) {
                setNegativeButton(resBuilder.getSettingCancelText(), cancelListener);
            }
            return this;
        }

        CustomAlertDialogBuilder initInstallApp(PermissionUtil.Builder resBuilder, DialogInterface.OnClickListener ensureListener,
                                                DialogInterface.OnClickListener cancelListener) {
            setMessage(resBuilder.getInstallAppMsg());
            if (!TextUtils.isEmpty(resBuilder.getTitleText())) {
                setTitle(resBuilder.getTitleText());
            }
            if (!TextUtils.isEmpty(resBuilder.getEnsureBtnText())) {
                setPositiveButton(resBuilder.getEnsureBtnText(), ensureListener);
            }
            if (!TextUtils.isEmpty(resBuilder.getCancelBtnText())) {
                setNegativeButton(resBuilder.getCancelBtnText(), cancelListener);
            }
            return this;
        }
    }
}
