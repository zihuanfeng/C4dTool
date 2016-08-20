package cn.atd3.c4dtool;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //  包名信息
    static  String pkgs[]={"com.n0n3m4.droidc","com.n0n3m4.gcc4droid","com.n0n3m4.droidsdl"};
    static int C4droid_ID=0;
    static int GCC_ID=1;
    static int SDL_ID=2;
    ClickListener cl=null;
    RelativeLayout layout=null;
    View dlview=null;
    TextView infotext=null;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layout=(RelativeLayout) findViewById(R.id.main_layout);
        dlview=findViewById(R.id.dl_buttons);
        if (null!=dlview)
        {
           dlview.setVisibility(View.GONE);
        }
        cl=new ClickListener();
        Button check=(Button)findViewById(R.id.check_system);
        Button x32_en=(Button)findViewById(R.id.c432_en);
        Button x32_zh=(Button)findViewById(R.id.c432_zh);
        Button x64_en=(Button)findViewById(R.id.c464_en);
        Button x64_zh=(Button)findViewById(R.id.c464_zh);
        if (x32_en != null) {
            x32_en.setOnClickListener(cl);
        }
        if (x32_zh != null) {
            x32_zh.setOnClickListener(cl);
        }
        if (x64_zh != null) {
            x64_zh.setOnClickListener(cl);
        }
        if (x64_en != null) {
            x64_en.setOnClickListener(cl);
        }
        if (null!=check)
        {
            check.setOnClickListener(cl);
        }
        infotext = (TextView)findViewById(R.id.info_output);
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if (msg.arg1==0)
                    Toast.makeText(getApplicationContext(),getString(R.string.refresh_ok),Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(),getString(R.string.refresh_error),Toast.LENGTH_SHORT).show();
            }
        };
    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId())
            {
                case R.id.check_system:
                    v.setVisibility(View.GONE);
                    checkSystem();
                    break;
                default:
                    Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
                    intent.setData(Uri.parse(getURl(v.getId()))); //为Intent设置DATA属性
                    startActivity(intent);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_uninstall_c4:
                uninstall_pkg(pkgs[C4droid_ID]);
                break;
            case R.id.action_uninstall_gcc:
                uninstall_pkg(pkgs[GCC_ID]);
                break;
            case R.id.action_uninstall_sdl:
                uninstall_pkg(pkgs[SDL_ID]);
                break;
            case  R.id.action_refresh:
                refreash();
                break;
            case R.id.action_about:
                //跳转到关于
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            default:
                Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
                intent.setData(Uri.parse(getURl(id))); //为Intent设置DATA属性
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // 卸载APK
    private  void uninstall_pkg(String pkgname)
    {
        if (checkInstalled(pkgname))
        {
            uninstall(pkgname);
        }
        else
        {
            Snackbar.make(layout,getString(R.string.no_install),Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    }
    // 卸载APK
    private void uninstall(String pkgname)
    {
        Intent localIntent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
        localIntent.setData(Uri.parse("package:" + pkgname));
        startActivity(localIntent);
    }
    // 检查APK是否安装
    private boolean checkInstalled(String pkgname)
    {
        try
        {
            PackageInfo localPackageInfo = getPackageManager().getPackageInfo(pkgname, 0);
            if (localPackageInfo != null) {
                return  true;
            }
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
        return false;
    }
    private  void checkSystem()
    {

        if (Build.CPU_ABI.equalsIgnoreCase("x86"))
        {
            infotext.setText(getString(R.string.system_x86));
        }
        else
        {
            String sys=getCpuString();
            int type=32;
            if (sys.contains("Arch64"))
            {
                type=64;
            }
            infotext.setText(String.format(getString(R.string.system_info_format),type));
            dlview.setVisibility(View.VISIBLE);
        }

        if (checkInstalled(pkgs[C4droid_ID]) && checkInstalled(pkgs[GCC_ID]) && checkInstalled(pkgs[SDL_ID]) )
        {
            Toast.makeText(getApplicationContext(),getString(R.string.system_check),Toast.LENGTH_SHORT).show();
        }

    }
    static public String getCpuString(){
        String strInfo = "";
        try
        {
            byte[] bs = new byte[1024];
            RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
            reader.read(bs);
            String ret = new String(bs);
            int index = ret.indexOf(0);
            if(index != -1) {
                strInfo = ret.substring(0, index);
            } else {
                strInfo = ret;
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return strInfo;
    }

    public String getURl(int id)
    {
        // 默认的信息
        String default_json="{\"c432_en\":\"http:\\/\\/file.atd3.cn\\/c4dtool\\/download\\/32\\/en\\/latest\"," +
                "\"c432_zh\":\"http:\\/\\/file.atd3.cn\\/c4dtool\\/download\\/32\\/zh\\/latest\"," +
                "\"c464_en\":\"http:\\/\\/file.atd3.cn\\/c4dtool\\/download\\/64\\/en\\/latest\"," +
                "\"c464_zh\":\"http:\\/\\/file.atd3.cn\\/c4dtool\\/download\\/64\\/zh\\/latest\"," +
                "\"get_codebook\":\"http:\\/\\/file.atd3.cn\\/c4dtool\\/codebook\\/latest\"," +
                "\"get_CIDE\":\"http:\\/\\/file.atd3.cn\\/c4dtool\\/cide\\/latest\"}";
        String read=null;
        JSONObject obj=null;
        // 读取的信息
        try {
            read=readFile(getString(R.string.url_info));
            obj = new JSONObject(read);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                obj=new JSONObject(default_json);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        switch (id)
        {
            case R.id.c432_en:
                try {
                    if (obj != null) {
                        return obj.getString("c432_en");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.c432_zh:
                try {
                    if (obj != null) {
                        return obj.getString("c432_zh");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.c464_en:
                try {
                    if (obj != null) {
                        return obj.getString("c464_en");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.c464_zh:
                try {
                    if (obj != null) {
                        return obj.getString("c464_zh");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_get_book:
                try {
                    if (obj != null) {
                        return obj.getString("get_codebook");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_getCIDE:
                try {
                    if (obj != null) {
                        return obj.getString("get_CIDE");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return  null;
    }

    public String readFile(String filename) throws Exception {
        FileInputStream fis =getApplicationContext().openFileInput(filename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = fis.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        fis.close();
        baos.close();
        return baos.toString();
    }
    public boolean refreash()
    {
        Toast.makeText(getApplicationContext(),getString(R.string.refresh_url),Toast.LENGTH_SHORT).show();
        new Thread(new Runnable(){
            @Override
            public void run() {
                String url="http://test.atd3.org/c4tool/check_info";
                JSONObject obj=new JSONObject();
                FileOutputStream outputStream;
                Message msg=handler.obtainMessage();
                try {
                    obj.put("UUID",UUID.randomUUID().toString());
                    obj.put("MobileInfo",getMobileInfo());
                    obj.put("CPUInfo",getCpuString());
                    JSONObject get=new JSONObject(post_json(url,obj.toString()));
                    Log.i("dxkite_getJson",get.toString());
                    outputStream = openFileOutput(getString(R.string.url_info), Context.MODE_PRIVATE);
                    outputStream.write(get.toString().getBytes());
                    outputStream.close();
                    msg.arg1=0;
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    msg.arg1=1;
                }
                handler.sendMessage(msg);
            }
        }).start();
        return  false;
    }

    public static JSONObject  getMobileInfo() {
        JSONObject mbInfo = new JSONObject();
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                mbInfo.put(name, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mbInfo;
    }

    public static  String post_json(String address,String data)
    {
        String get_json="";
        try {
            URLConnection rulConnection=new URL(address).openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");
            httpUrlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.connect();
            // 不读取404
            //if (httpUrlConnection.getResponseCode()!=404)
           // {
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
                InputStream inputStream = httpUrlConnection.getInputStream();
                BufferedReader r=new BufferedReader(new InputStreamReader(inputStream));
                String l,out="";
                while (null!=(l=r.readLine())){
                    out+=l+'\n';
                }
                get_json=out;
           // }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return get_json;
    }
}
