package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Addpoints extends AppCompatActivity {

    Handler handler2;
    TextView getoldresult;
    TextView getnewresult;
    TextView getaddresult;
    Button blinkback;
    String postaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpoints);

        String thismydevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        handler2 = new Handler(Looper.getMainLooper());

        String cardcode = getIntent().getExtras().getString("UID");
        blinkback = (Button)findViewById(R.id.linkback);

        getaddresult = (TextView)findViewById(R.id.addresult);
        getoldresult = (TextView)findViewById(R.id.putoldresult);
        getnewresult = (TextView)findViewById(R.id.putnewresult);

        try {
            Log.i("[print]","https://quickpay.ai/api_addpoints.php?deviceid=" + thismydevice + "&uid=" + cardcode );
            doGetRequest("https://quickpay.ai/api_addpoints.php?deviceid=" + thismydevice + "&uid=" + cardcode);
        } catch (IOException e) {
            e.printStackTrace();
        }


        blinkback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Addpoints.this, MainActivity.class);

                startActivity(intent);

            }

        });


    }



    void doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                Log.i("[print]","error" + e);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        postaction = response.body().string();
                        Log.i("assyn url",postaction);
                        // Do something with the response


                        Log.i("[print]",postaction);
                        postaction = postaction.trim();
                        String[] separated = postaction.split("~");
                        String thisresult = separated[0].trim();

                        handler2.post(new Runnable() {
                            @Override
                            public void run() {

                                if (thisresult.equals("noregister")) {
                                    getaddresult.setText("This card is Not registered");
                                    getoldresult.setText("0");
                                    getnewresult.setText("0");


                                }

                                if (thisresult.equals("addpoints")) {
                                    String oldp = separated[1].trim();
                                    String newp = separated[2].trim();

                                    getaddresult.setText("New points added");
                                    getoldresult.setText(oldp);
                                    getnewresult.setText(newp);


                                }

                            }
                        });


                    }
                });
    }





}