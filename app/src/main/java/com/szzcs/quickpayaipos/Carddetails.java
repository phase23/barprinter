package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
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
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Carddetails extends AppCompatActivity {
String getcarddetails;
String getcardno;
String getexpiry;
TextView  cardname;
TextView  cardno;
    TextView  thisfinal;
    TextView  cardresulttxt;
    Button trybtn;
Button dochargecard;
    Button blinkback;
    EditText scvv;
    EditText samount;
    String thisamount;
    String thiscvv;
    String postaction;
    Handler handler2;
    String fname;
    String lname;
    String chargethis;
String tip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carddetails);

        getcarddetails = getIntent().getExtras().getString("carddetails");
        getcardno = getIntent().getExtras().getString("cardno");
        getexpiry = getIntent().getExtras().getString("expiry");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        chargethis = preferences.getString("chargetotal", "");
        tip = preferences.getString("incltip", "");


        double createTotal = 0;
        try {
            createTotal = Float.parseFloat(chargethis);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }


        cardname = (TextView)findViewById(R.id.trck1);
        cardno = (TextView)findViewById(R.id.trck2);
        cardresulttxt = (TextView)findViewById(R.id.cardresult);
        thisfinal = (TextView)findViewById(R.id.finalcharge);


        String thismydevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        handler2 = new Handler(Looper.getMainLooper());

        samount = (EditText)findViewById(R.id.amount);
        scvv = (EditText)findViewById(R.id.cvv);

        String COUNTRY = "US";
        String LANGUAGE = "en";
        String nicecharge = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(createTotal);
        samount.setText(chargethis);
        thisfinal.setText(nicecharge);
         //Toast.makeText(getApplicationContext(), "Over here:" + chargethis, Toast.LENGTH_LONG).show();


        dochargecard = (Button)findViewById(R.id.chargecard);
        blinkback = (Button)findViewById(R.id.linkback);
        trybtn = (Button)findViewById(R.id.tryagainbtn);

       // B4658596116066014^JNO-BAPTISTE       /P W.MR^2206221000000000000000218000000
       // %B4658596116066014^JNO-BAPTISTE       /P W.MR^2206221000000000000000218000000?;4658596116066014=22062212180000000001?
       // %B4100750001509794^JNO-BAPTISTE/PHILMON.^2402101926200010000000598000000?;4100750001509794=24021019262059800100?

        String[] pieces = getcarddetails.split(Pattern.quote("^"));
        String namepiece = pieces[1].trim();
        String[] nameoutput = namepiece.split(Pattern.quote("/"));
        fname = nameoutput[0].trim();
         lname = nameoutput[1];



        cardname.setText(fname + ' ' + lname );

        String newcardno = getcardno.substring(0,8);
        cardno.setText(newcardno + "**** ****");






        trybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Carddetails.this,MainActivity.class);
                startActivity(i);

            }

        });




        blinkback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Carddetails.this, MainActivity.class);

                startActivity(intent);

            }

        });


        dochargecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    thisamount = samount.getText().toString();
                thiscvv = scvv.getText().toString();

                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                } else {
                    connected = false;
                }


                 if(thiscvv.length() != 3  ) {
                     Toast.makeText(getApplicationContext(), "The CVV must be three characters", Toast.LENGTH_SHORT).show();
                     return;

                 }


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

                getcarddetails = getIntent().getExtras().getString("carddetails");
                getcardno = getIntent().getExtras().getString("cardno");
                getexpiry = getIntent().getExtras().getString("expiry");


                if(!connected) {
                    Toast.makeText(getApplicationContext(), "Check Internet & Restart App", Toast.LENGTH_LONG).show();
                    Intent nointernet = new Intent(Carddetails.this, Nointernet.class);
                    startActivity(nointernet);

                }else {

                    try {

                        dochargecard.setText("Please wait...");
                        dochargecard.setEnabled(false);

                        Log.i("[print]", "https://quickpay.ai/api_chargecard.php?deviceid=" + thismydevice + "&ccnumber=" + getcardno + "&cvv=" + thiscvv + "&charge=" + thisamount + "&expr=" + getexpiry + "&ccname=" + fname + ' ' + lname + "&tip=" + tip);
                        doGetRequest("https://quickpay.ai/api_chargecard.php?deviceid=" + thismydevice + "&ccnumber=" + getcardno + "&cvv=" + thiscvv + "&charge=" + thisamount + "&expr=" + getexpiry + "&ccname=" + fname + ' ' + lname  + "&tip=" + tip);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                        String[] separated = postaction.split("~");
                        String thisresult = separated[0].trim();
                        Log.i("[print]",thisresult);

                        if(thisresult.equals("nocharge")) {
                            String thisdescipt = separated[3];


                            handler2.post(new Runnable() {
                                @Override
                                public void run() {
                                    //setstatus.setText(postaction);
                                    dochargecard.setVisibility(View.INVISIBLE);
                                    samount.setVisibility(View.INVISIBLE);
                                    scvv.setVisibility(View.INVISIBLE);

                                    blinkback.setText("Try again");
                                    cardresulttxt.setVisibility(View.VISIBLE);
                                    cardresulttxt.setText(thisdescipt);

                                    dochargecard.setEnabled(true);
                                    dochargecard.setText("Charge Card");

                                }
                            });

                        }else if(thisresult.equals("charge")) {
                            String thisdescipt = separated[3];

                            Intent intent = new Intent(Carddetails.this, CardSuccess.class);
                            intent.putExtra("transid",thisdescipt);
                            intent.putExtra("puttall",postaction);
                            startActivity(intent);



                        }else {

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