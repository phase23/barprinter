package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.szzcs.quickpayaipos.utils.DialogUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextStyle;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Linkdevice extends AppCompatActivity {
    Button blinkback;
    Button doreprint;



    Button blinkupdate;
    TextView setdeviceid;
    TextView setstatus;
    String postaction;
    EditText pin;
    Handler handler2;


    private DriverManager mDriverManager = MyApp.sDriverManager;
    private Printer mPrinter;
    private boolean mPrintStatus = false;
    private Bitmap mBitmapDef;
    String orderdetails;
    public static final String PRINT_TEXT = "本智能POS机带打印机，基于android 平台应用，整合昂贵的ECR、收银系统，伴随新型扫码支付的需求也日益突出，大屏智能安卓打印机设备，内置商户的营销管理APP，在商品管理的同时，受理客户订单支付，很好的满足了以上需求；同时便携式的要求，随着快递实名制的推行，运用在快递行业快速扫条码进件。做工精良，品质优良，是市场的最佳选择。";
    public static final String QR_TEXT = "https://www.baidu.com";
    public static final String BAR_TEXT = "50001";
    public static final String BAR_TEXT2 = "50002";
    public static final String BAR_TEXT3 = "50003";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkdevice);


        mDriverManager = MyApp.sDriverManager;
        mPrinter = mDriverManager.getPrinter();
        int printerStatus = mPrinter.getPrinterStatus();
        // Log.d(TAG, "getPrinterStatus: " + printerStatus);
        // Log.i("getPrinterStatus: " + printerStatus);

        if (printerStatus != SdkResult.SDK_OK) {
            mPrintStatus = true;
        } else {
            mPrintStatus = false;
        }




        String thismydevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        handler2 = new Handler(Looper.getMainLooper());

        blinkback = (Button)findViewById(R.id.linkback);
        doreprint = (Button)findViewById(R.id.reprint);

        blinkupdate = (Button)findViewById(R.id.linkupdate);
        setdeviceid = (TextView)findViewById(R.id.deviceid);
        setstatus= (TextView)findViewById(R.id.status);
       // pin = (EditText)findViewById(R.id.linkcode);
       // String thispin = pin.getText().toString();

        try {
        Log.i("[print]","https://quickpay.ai/api_checkdevice.php?deviceid=" + thismydevice );
        doGetRequest("https://quickpay.ai/api_checkdevice.php?deviceid=" + thismydevice);
    } catch (IOException e) {
        e.printStackTrace();
    }



        setdeviceid.setText(thismydevice);

        blinkback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Linkdevice.this, MainActivity.class);

                startActivity(intent);

            }

        });


        doreprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    Log.i("[print]","https://quickpay.ai/api_printlast.php?deviceid=" + thismydevice );
                    doprintRequest("https://quickpay.ai/api_printlast.php?deviceid=" + thismydevice);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }

        });






    }


    void doprintRequest(String url) throws IOException {
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
                        printswipe(postaction);



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


    private void printswipe(String orders) {



        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int printStatus = mPrinter.getPrinterStatus();
                        if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                            Linkdevice.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                                }
                            });
                        } else {

                            String[] pieces = orders.split(Pattern.quote("~"));
                            String company = pieces[1].trim();
                            String country = pieces[3].trim();
                            String dater = pieces[5].trim();
                            String tel = pieces[4].trim();
                            String trans  = pieces[8].trim();
                            String lastdigits  = pieces[7].trim();
                            String totalp  = pieces[6].trim();
                            String addr  = pieces[2].trim();


                            PrnStrFormat format = new PrnStrFormat();
                            format.setTextSize(50);
                            format.setAli(Layout.Alignment.ALIGN_CENTER);
                            format.setStyle(PrnTextStyle.BOLD);
                            mPrinter.setPrintAppendString(" ", format);
                            mPrinter.setPrintAppendString(company, format);
                            format.setTextSize(30);
                            format.setStyle(PrnTextStyle.NORMAL);
                            mPrinter.setPrintAppendString(tel, format);
                            format.setTextSize(25);
                            mPrinter.setPrintAppendString(addr, format);
                            mPrinter.setPrintAppendString(country, format);
                            format.setAli(Layout.Alignment.ALIGN_NORMAL);
                            mPrinter.setPrintAppendString("All prices in USD ", format);
                            mPrinter.setPrintAppendString("Transaction: " + trans, format);
                            // mPrinter.setPrintAppendString("Some Items may vary due to in-store availability", format);
                            mPrinter.setPrintAppendString("_________________________", format);


                            mPrinter.setPrintAppendString("Entry Method: Swiped", format);
                            mPrinter.setPrintAppendString("Card " + lastdigits, format);
                            mPrinter.setPrintAppendString("__", format);



                                /*
                                  public String thistotalgroceries;
                                 public String thisservicecharge;
                                    public String  thisdelivery;
                                    public String alltotal;
                                 */

                            mPrinter.setPrintAppendString("Total : $" + totalp , format);
                            mPrinter.setPrintAppendString("Date : " + dater , format);
                            mPrinter.setPrintAppendString(" ", format);
                            format.setStyle(PrnTextStyle.NORMAL);
                            mPrinter.setPrintAppendString("Thank you for being our customer! ", format);
                            mPrinter.setPrintAppendString("_________________________", format);
                            mPrinter.setPrintAppendString("Accept credit cards anywhere ", format);
                            mPrinter.setPrintAppendString("visit:quickpay.ai ", format);
                            mPrinter.setPrintAppendString(" ", format);
                            mPrinter.setPrintAppendString(" ", format);
                            mPrinter.setPrintAppendString(" ", format);
                    /*
                    mPrinter.setPrintAppendString(getString(R.string.show_barcode_status2), format);
                    mPrinter.setPrintAppendBarCode(getApplicationContext(), BAR_TEXT, 300, 80, false, Layout.Alignment.ALIGN_CENTER, BarcodeFormat.CODE_128);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(getString(R.string.show_barcode_status3), format);
                    mPrinter.setPrintAppendBarCode(getApplicationContext(), BAR_TEXT, 300, 100, false, Layout.Alignment.ALIGN_OPPOSITE, BarcodeFormat.CODE_128);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);

                     */
                            printStatus = mPrinter.setPrintStart();
                            if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                                Linkdevice.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                                    }
                                });
                            }

                        }
                    }
                }).start();



            }
        });

    }


    private void printPaperOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    Linkdevice.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                        }
                    });
                } else {
                    mPrinter.setPrintLine(10);
                }


                //  mPrinter.setPrintStart();
            }
        }).start();
    }

    private String getSn() {
        // Config the SDK base info
        Sys sys = MyApp.sDriverManager.getBaseSysDevice();
        String[] pid = new String[1];
        int status = sys.getPid(pid);
        int count = 0;
        while (status != SdkResult.SDK_OK && count < 3) {
            count++;
            int sysPowerOn = sys.sysPowerOn();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final int i = sys.sdkInit();
        }
        return pid[0];
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
                                setstatus.setText(postaction);


                            }
                        });


                    }
                });
    }



}