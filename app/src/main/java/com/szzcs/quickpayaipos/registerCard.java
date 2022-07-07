package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class registerCard extends AppCompatActivity {
    TextView getuid;
    TextView getresult;
    Button blinkback;
    Button tryregister;
    Handler handler2;
    String postaction;
    EditText suid;
    EditText sname;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);


        String thismydevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        handler2 = new Handler(Looper.getMainLooper());


        String cardcode = getIntent().getExtras().getString("UID");
        getuid = (TextView)findViewById(R.id.uid);
        getresult = (TextView)findViewById(R.id.lresult);


        String newcardno = cardcode.substring(0,4);
        getuid.setText(newcardno + "******");

        tryregister = (Button)findViewById(R.id.registerbtn);
        blinkback = (Button)findViewById(R.id.linkback);


        suid = (EditText)findViewById(R.id.uidfull);
        suid.setText(cardcode);

        sname = (EditText)findViewById(R.id.name);


        blinkback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(registerCard.this, MainActivity.class);

                startActivity(intent);

            }

        });



        tryregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getresult.setVisibility(View.INVISIBLE);
                String thisuid = suid.getText().toString();
                String thisname = sname.getText().toString();

                if (thisname.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter name ", Toast.LENGTH_SHORT).show();
                    return;
                }



                try {
                    Log.i("[print]","https://quickpay.ai/api_registercard.php?deviceid=" + thismydevice + "&name="+ thisname + "&uid="+ thisuid);
                    doGetRequest("https://quickpay.ai/api_registercard.php?deviceid=" + thismydevice + "&name="+ thisname + "&uid="+ thisuid);
                } catch (IOException e) {
                    e.printStackTrace();
                }


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


                        handler2.post(new Runnable() {
                            @Override
                            public void run() {

                                if(postaction.equals("duplicate")) {
                                    getresult.setVisibility(View.VISIBLE);
                                    getresult.setText("This card is already registered");


                                }else{

                                    getresult.setVisibility(View.VISIBLE);
                                    tryregister.setVisibility(View.INVISIBLE);
                                    getresult.setText("Card Registerd Successfully");
                                    blinkback.setText("Finish");
                                        getresult.setTextColor(Color.parseColor("#006400"));

                                }



                            }
                        });


                    }
                });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();

        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        return true;
    }


}