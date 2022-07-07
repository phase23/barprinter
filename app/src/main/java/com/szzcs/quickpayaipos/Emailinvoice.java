package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Emailinvoice extends AppCompatActivity {
    Button elinkback;
    EditText samount;
    EditText semail;
    EditText sref;
    EditText sinvname;
    Button dosendinvoice;
    String postaction;
    Handler handler2;
    String thisamount;
    String thisemail;
    String thisref;
    String thisname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailinvoice);

        elinkback = (Button)findViewById(R.id.emailfinish);
        dosendinvoice = (Button)findViewById(R.id.sendinvoice);

        samount = (EditText)findViewById(R.id.amount);
        semail = (EditText)findViewById(R.id.email);
        sref = (EditText)findViewById(R.id.ref);
        sinvname = (EditText)findViewById(R.id.invname);



        String thismydevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);



        handler2 = new Handler(Looper.getMainLooper());


        dosendinvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 thisamount = samount.getText().toString();
                 thisemail = semail.getText().toString();
                thisref = sref.getText().toString();
                thisname = sinvname.getText().toString();

                float myNum = 0;
                try {
                    myNum = Float.parseFloat(thisamount);
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }

                if(myNum < 1) {
                    Toast.makeText(getApplicationContext(), "The charge amount cannot be less than $1.00", Toast.LENGTH_SHORT).show();
                    return;


                }

                if (thisname.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please a name to send to", Toast.LENGTH_SHORT).show();
                    return;
                }



                if (thisamount.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (thisref.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a reference", Toast.LENGTH_SHORT).show();
                    return;
                }



                if(!isValidEmailId(thisemail.trim())){
                    Toast.makeText(getApplicationContext(), "Please a valid email", Toast.LENGTH_SHORT).show();
                    return;

                }



                AlertDialog.Builder builder = new AlertDialog.Builder(Emailinvoice.this);
                builder.setTitle("Confirm");

                builder.setMessage(Html.fromHtml("Confirm  invoice to " +  thisemail + "<br> " + thisname + "<br> for $" + thisamount ));

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                       // sendorderid = tagname.trim();

                        try {
                            Log.i("[print]","https://quickpay.ai/api_emailinvoice.php?deviceid=" + thismydevice + "&amount=" + thisamount  +"&remail=" + thisemail + "&invoicerec=" + thisref + "&rname=" +thisname );
                            doGetRequest("https://quickpay.ai/api_emailinvoice.php?deviceid=" + thismydevice + "&amount=" + thisamount  +"&remail=" + thisemail + "&invoicerec=" + thisref + "&rname=" + thisname );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                        Intent intent = new Intent(Emailinvoice.this, Emailsent.class);
                        startActivity(intent);




                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();




            }



        });


        elinkback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Emailinvoice.this, MainActivity.class);

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


                        handler2.post(new Runnable() {
                            @Override
                            public void run() {
                               // setstatus.setText(postaction);


                            }
                        });


                    }
                });
    }



    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
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