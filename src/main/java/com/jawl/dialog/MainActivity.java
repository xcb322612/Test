package com.jawl.dialog;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    MyDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        mImageView = (ImageView) findViewById(R.id.iv_head);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment = new MyDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "");
                dialogFragment.setOnFinishListener(new MyDialogFragment.OnFinishListener() {
                    @Override
                    public void onFinish(Uri uri) {
                        Log.e("TAG", "onFinish: " + uri.toString());
                        Glide.with(MainActivity.this).load(uri).apply(bitmapTransform(new CropCircleTransformation()))
                                .into(mImageView);
                        dialogFragment.dismiss();
                    }
                    @Override
                    public void onFile(File file) {
                        Toast.makeText(MainActivity.this,FormatUtils.getFileSize(MainActivity.this,file.length()), Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "onFile: "+file.getAbsolutePath() );
                    }
                });
            }
        });
    }
}
