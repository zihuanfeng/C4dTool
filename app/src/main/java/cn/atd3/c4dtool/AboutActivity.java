package cn.atd3.c4dtool;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AboutActivity extends AppCompatActivity {
    // OK url-1
    // public  static  final  String pay_url="alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https://qr.alipay.com/apl21r0lc1j3fps776?_s=web-other";
    // OK url-2
    // public  static  final  String pay_url="alipayqr://platformapi/startapp?saId=10000007&qrcode=https://qr.alipay.com/apl21r0lc1j3fps776?_s=web-other";
    // OK url-3 last
    public  static  final  String pay_url="alipayqr://platformapi/startapp?saId=10000007&qrcode=https://qr.alipay.com/apl21r0lc1j3fps776";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // 显示返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        View get= findViewById(R.id.alpay);
        if (get != null) {
            get.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(getQrUrl());
                    intent.setData(content_url);
                    intent.setClassName("com.eg.android.AlipayGphone","com.alipay.mobile.quinox.LauncherActivity");
                    startActivity(intent);
                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String getQrUrl()  {

        FileInputStream fis = null;
        try {
            fis = getApplicationContext().openFileInput(getString(R.string.url_info));
            ByteArrayOutputStream json_code = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = fis.read(buf)) != -1) {
                json_code.write(buf, 0, len);
            }
            fis.close();
            json_code.close();
            JSONObject json=new JSONObject(json_code.toString());
            String code=json.getString("qrcode_url");
            if (code!=null && !code.isEmpty())
            {
                Log.i("dxkite-qrcode",code);
                return code;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return  pay_url;
    }
}
