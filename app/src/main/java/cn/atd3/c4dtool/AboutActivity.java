package cn.atd3.c4dtool;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
            actionBar.setTitle("关于我们");
        }
        TextView get=(TextView)findViewById(R.id.alpay);
        if (get != null) {
            get.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(pay_url);
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
}
