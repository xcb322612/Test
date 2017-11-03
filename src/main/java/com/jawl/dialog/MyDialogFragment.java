package com.jawl.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;

import rx.functions.Action1;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by SKT on 2017/10/12.
 */

public class MyDialogFragment extends DialogFragment implements View.OnClickListener {

    /**
     * 获取照片底部弹出对话框
     */

    View mView;
    TextView mTvHead;
    TextView mTvPhoto;
    TextView mTvCancel;
    Uri cameraProvider;
    File file;
    private final static int CAME_CODE = 0x0;
    private final static int PIC_CODE = 0x1;

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), R.layout.dialog_head, null);
        Dialog dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        dialog.setContentView(mView);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animtion);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = 30;
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        lp.width = windowManager.getDefaultDisplay().getWidth() - dp2px(20);
        window.setAttributes(lp);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dialog_head, null);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();

    }

    private void initListener() {
        mTvHead.setOnClickListener(this);
        mTvPhoto.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }

    private void initView(View view) {
        mTvHead = (TextView) view.findViewById(R.id.tv_camera);
        mTvPhoto = (TextView) view.findViewById(R.id.tv_photo);
        mTvCancel = (TextView) view.findViewById(R.id.tv_cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera:
                //检查权限(6.0以上做权限判断)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    RxPermissions.getInstance(getActivity()).request(PERMISSIONS)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        openCamera();
                                    } else {
                                        ToastUtils.showToast(getActivity(), "缺少必要权限>应用信息>权限>中授予！");
//                                        openAppDetails();
                                    }
                                }
                            });
                } else {
                    openCamera();
                }
                break;
            case R.id.tv_photo:
                selectFromAlbum();
                break;
            case R.id.tv_cancel:
                this.dismiss();
                break;
        }
    }

    private void openAppDetails() {
        /**
         * 指引申请权限详情对话框
         */

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("拍照需要访问必要权限 “相机” 和 “读写手机存储”，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }


    private void openCamera() {
        /**
         * 相机获取照片
         */
        Intent intent = new Intent();
        file = new FileStorage().createCropFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraProvider = FileProvider.getUriForFile(getActivity(), "com.jawl.dialog.fileprovider", file);//通过FileProvider创建一个content类型的Uri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加对目标应用临时授权
        } else {
            cameraProvider = Uri.fromFile(file);
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraProvider);//将拍取的照片保存到指定URI
        startActivityForResult(intent, CAME_CODE);

    }

    private void selectFromAlbum() {
        /**
         * 相册获取照片
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PIC_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAME_CODE && resultCode == Activity.RESULT_OK) {
            Log.e("TAG", "onActivityResult: " + FormatUtils.getFileSize(getActivity(), file.length()));

            /**
             * 使用鲁班图片压缩
             */
            Luban.with(getActivity())
                    .load(file.getAbsolutePath())
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(final File file) {
                            if (onFinishListener!=null){
                                onFinishListener.onFile(file);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    })
                    .launch();
            if (onFinishListener != null) {
                onFinishListener.onFinish(cameraProvider);
            }
        } else if (requestCode == PIC_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (onFinishListener != null) {
                onFinishListener.onFinish(uri);
            }
        } else {
            this.dismiss();
        }
    }

    private static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param sp sp值
     * @return 转换后的px值
     */
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }


    /**
     * 获取照片回调接口
     */

    public OnFinishListener onFinishListener;

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    protected interface OnFinishListener {
        void onFinish(Uri uri);
        void onFile(File file);
    }
}
