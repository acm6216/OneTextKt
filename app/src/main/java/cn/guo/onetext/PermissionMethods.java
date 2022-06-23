package cn.guo.onetext;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionMethods {

    private static final String TAG = "PermissionMethods";
    private static final String DIALOG_TITLE = "权限请求";
    private static final String DISENABLE = "拒绝";
    private static final String ENABLE = "授权";
    public static final int REQUEST_PERMISSION_CODE = 0;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] StoragePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public List<delayOverlayPermissionCheckListener> listener;
    private boolean delayOverlayPermissionCheckIsRun = false;
    public PermissionMethods(){
        this.listener = new ArrayList<>();
    }

    public static void askPermission(Activity mActivity, String[] permissions, int requestCode) {
        if (!checkPermission(mActivity, permissions)) {
            ActivityCompat.requestPermissions(mActivity, permissions, requestCode);
        }
    }

    public static boolean checkPermission(Context mContext, String[] permissions) {
        boolean result = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean checkStoragePermission(Activity mActivity){
        return checkPermission(mActivity,PERMISSIONS_STORAGE);
    }

    public static void storagePermissionDialog(final Activity mActivity){
        AlertDialog.Builder storagePermissionDialog = new AlertDialog.Builder(mActivity);
        storagePermissionDialog.setTitle(DIALOG_TITLE);
        storagePermissionDialog.setMessage("从手机读写文件需要读写权限，请授予。");
        storagePermissionDialog.setNegativeButton(DISENABLE,null);//中
        storagePermissionDialog.setNeutralButton("",null);//左
        storagePermissionDialog.setPositiveButton(ENABLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askStoragePermission(mActivity);
            }
        });//右
        storagePermissionDialog.show();
    }
    public static void storagePermission(Activity activity){
        if(!checkPermission(activity,PERMISSIONS_STORAGE))
            storagePermissionDialog(activity);
    }
    private static void askStoragePermission(Activity mActivity){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            askPermission(mActivity,PERMISSIONS_STORAGE,REQUEST_PERMISSION_CODE);
        }
    }

    private static void overlayPermissionDialog(final Activity mActivity){
        AlertDialog.Builder overlayPermission = new AlertDialog.Builder(mActivity);
        overlayPermission.setTitle(DIALOG_TITLE);
        overlayPermission.setMessage("需要悬浮穿权限才能正常显示");
        overlayPermission.setPositiveButton(ENABLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                mActivity.startActivityForResult(intent, REQUEST_PERMISSION_CODE);
            }
        });
        overlayPermission.setNegativeButton(DISENABLE, null);
        overlayPermission.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("SameParameterValue")
    public static void askOverlayPermission(final Activity mActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mActivity)) {
                overlayPermissionDialog(mActivity);
            }
        }
    }

    public interface delayOverlayPermissionCheckListener{
        void delayOverlayPermissionChecked(boolean enable);
    }
    public void addListener(delayOverlayPermissionCheckListener listener){
        this.listener.add(listener);
    }
    public void useInterface(boolean enable){
        for(delayOverlayPermissionCheckListener list:listener)
            if(list!=null)
                list.delayOverlayPermissionChecked(enable);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void delayOverlayPermissionCheck(final Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                if(delayOverlayPermissionCheckIsRun)
                    return;
                delayOverlayPermissionCheckIsRun=true;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        delayOverlayPermissionCheckIsRun=false;
                        if (!Settings.canDrawOverlays(mContext)) {
                            useInterface(false);
                        }
                    }
                }, 1000);
            }else {
                useInterface(true);
            }
        }
    }
}
