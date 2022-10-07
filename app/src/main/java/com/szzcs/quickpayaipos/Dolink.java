package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Dolink extends AppCompatActivity {
    EditText linkcode;
    Button doactivate;
    TextView error;
    String postaction;
    AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dolink);
        linkcode = (EditText)findViewById(R.id.activationcode);
        doactivate = (Button)findViewById(R.id.activatebtn);
        //error = (TextView)findViewById(R.id.errortext);


        String globaldevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        doactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = linkcode.getText().toString();

                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(Dolink.this)
                        .build();

                if (code.matches("")) {
                    Toast.makeText(getApplicationContext(), "Enter a code to continue", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }


                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(Dolink.this)
                        .build();
                dialog.show();


                try {

                    Log.i("[print]","https://quickpay.ai/linknewdevice.php?&code=" + code + "&token=" + globaldevice);
                    doGetRequest("https://quickpay.ai/linknewdevice.php?&code=" + code + "&token=" + globaldevice);
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



                        if(postaction.equals("noluck")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // For the example, you can show an error dialog or a toast
                                    // on the main UI thread
                                    Toast.makeText(getApplicationContext(), "Your code is incorrect", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });

                            return;

                        }

                        if(postaction.equals("success")){

                            // Toast.makeText(getApplicationContext(), "Success "+ cunq, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Dolink.this, Linkdevice.class);

                            startActivity(intent);

                            dialog.dismiss();

                        }




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