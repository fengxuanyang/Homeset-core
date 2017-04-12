package com.ragentek.homeset.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ragentek.homeset.core.HomesetService;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.utils.DeviceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;


/**
 * Created by wei.zhao1 on 2017/4/7.
 */

public class SettingActivity extends Activity {
    private static final String TAG = "SettingActivity";

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;

    private static final String QRCODE_FILE = "qrcode.txt";
    private static final String QRCODE_KEY = "http://we.qq.com/d/AQAVT3PAnA5SGjGRj2ZRsH02kZIRaZ6pot25Toe6";

    private TextView mQRCodeKeyView;
    private ImageView mQRImageView = null;
    private Button mSkipBtn;
    private Button mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mQRImageView = (ImageView) findViewById(R.id.qrcode);
        mQRCodeKeyView = (TextView) findViewById(R.id.qrcode_key);

        mSkipBtn = (Button) findViewById(R.id.skip);
        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startCoreService();
                finish();
            }
        });

        mSetting = (Button) findViewById(R.id.system_setting);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSystemSetting();
            }
        });

        showQrcode();
    }

    private void showQrcode() {
        try {
            DeviceUtils deviceUtils = new DeviceUtils(getApplicationContext());
            String qrCode = deviceUtils.getQrticket();
            if (qrCode == null) {
                qrCode = readQRCodeKey();
            } else {
                LogUtils.d(TAG, "read qrcode from SharedPreferences, qrCode=" + qrCode);
            }

            mQRCodeKeyView.setText(qrCode);
            mQRImageView.setImageBitmap(createQRImage(qrCode));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap createQRImage(String text) throws WriterException {
        Bitmap bitmap = null;

        int matrixSize = 256;

        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, matrixSize, matrixSize, hints);

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private String readQRCodeKey() {
        String qrCode = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), QRCODE_FILE);
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            qrCode = new String(b);
            LogUtils.d(TAG, "read qrcode from file, qrCode=" + qrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (qrCode == null) {
            qrCode = QRCODE_KEY;
            LogUtils.d(TAG, "read qrcode from default, qrCode=" + qrCode);
        }

        return qrCode;
    }

    private void startCoreService() {
        Intent intent = new Intent(SettingActivity.this, HomesetService.class);
        intent.setAction(HomesetService.ACTION_START);
        startService(intent);
    }

    private void startSystemSetting() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings");
        startActivity(intent);
    }
}
