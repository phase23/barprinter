package com.szzcs.smartpos;

import static android.graphics.Color.RED;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.szzcs.scan.OnActivateListener;
import com.szzcs.scan.SDKUtils;
import com.szzcs.smartpos.scan.ScanActivity;
import com.szzcs.smartpos.utils.DialogUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextStyle;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;



import java.io.IOException;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActionBar actionBar;
    TextView deviceset;
    TextView errorcode;
    Button printbarcode;
    Button printvillareciepts;
    EditText pin;
    AlertDialog dialog;
    String postaction;
    String name;

    private DriverManager mDriverManager = MyApp.sDriverManager;
    private Printer mPrinter;
    private boolean mPrintStatus = false;
    private Bitmap mBitmapDef;
    String orderdetails;
    public static final String PRINT_TEXT = "本智能POS机带打印机，基于android 平台应用，整合昂贵的ECR、收银系统，伴随新型扫码支付的需求也日益突出，大屏智能安卓打印机设备，内置商户的营销管理APP，在商品管理的同时，受理客户订单支付，很好的满足了以上需求；同时便携式的要求，随着快递实名制的推行，运用在快递行业快速扫条码进件。做工精良，品质优良，是市场的最佳选择。";
    public static final String QR_TEXT = "https://www.baidu.com";
    public static final String BAR_TEXT = "6922711079066";
    public String thistotalgroceries;
    public String thisservicecharge;
    public String  thisdelivery;
    public String alltotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);



        Intent i = new Intent(this, Myservice.class);
        this.startService(i);


        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        deviceset = (TextView) findViewById(R.id.device);
        errorcode = (TextView) findViewById(R.id.errormsg);
        deviceset.setText(deviceId);

        mDriverManager = MyApp.sDriverManager;
        mPrinter = mDriverManager.getPrinter();
        int printerStatus = mPrinter.getPrinterStatus();
        Log.d(TAG, "getPrinterStatus: " + printerStatus);
        if (printerStatus != SdkResult.SDK_OK) {
            mPrintStatus = true;
        } else {
            mPrintStatus = false;
        }


        printbarcode = (Button) findViewById(R.id.sbbtn);
        printvillareciepts = (Button) findViewById(R.id.villeats);
        pin = (EditText) findViewById(R.id.orderno);



        printvillareciepts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thispin = pin.getText().toString();

                if (thispin.matches("")) {
                    Toast.makeText(getApplicationContext(), "Enter an order number", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(MainActivity.this)
                        .build();
                dialog.show();

                try {

                    setlpost(thispin);
                    Log.i("[print]", "https://axcess.ai/barapp/shopper_sendvillaeats.php?ordernum=" + thispin + "&deviceid=" + deviceId);
                    doGetvillaeats("https://axcess.ai/barapp/shopper_sendvillaeats.php?ordernum=" + thispin + "&deviceid=" + deviceId);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });


        printbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thispin = pin.getText().toString();

                if (thispin.matches("")) {
                    Toast.makeText(getApplicationContext(), "Enter an order number", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(MainActivity.this)
                        .build();
                dialog.show();

                try {

                    setlpost(thispin);
                    Log.i("[print]", "https://axcess.ai/barapp/shopper_sendbarcodes.php?ordernum=" + thispin + "&deviceid=" + deviceId);
                    doGetbarcodes("https://axcess.ai/barapp/shopper_sendbarcodes.php?ordernum=" + thispin + "&deviceid=" + deviceId);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });


    }




    void doGetbarcodes(String url) throws IOException {
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


                        Log.i("[print]",postaction);
                        postaction = postaction.trim();
                        dialog.dismiss();

                       printBar(postaction);



                    }
                });
    }


    void doGetvillaeats(String url) throws IOException {
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


                        Log.i("[print]",postaction);
                        postaction = postaction.trim();
                        dialog.dismiss();

                        printVillaeats(postaction);



                    }
                });
    }



    public String getlpost() {
        return name;
    }

    public void setlpost(String newName) {
        this.name = newName;
    }


    @Override
    public void onResume() {
        super.onResume();
        // This registers messageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(messageReceiver, new IntentFilter("my-message"));


    }



    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String myout = intent.getStringExtra("send"); // -1 is going to be used as the default value
            Log.i("url out",myout);

            if(myout.equals("")){
             //do nothing   
            }else {
                printorder(myout);
            }

        }
    };



    private void printorder(String orders) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int printStatus = mPrinter.getPrinterStatus();
                            if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                                    }
                                });
                            } else {


                                String eachline;
                                PrnStrFormat format = new PrnStrFormat();
                                format.setTextSize(30);
                                format.setStyle(PrnTextStyle.BOLD);
                                mPrinter.setPrintAppendString(orders, format);
                                format.setAli(Layout.Alignment.ALIGN_NORMAL);
                                format.setStyle(PrnTextStyle.NORMAL);
                                format.setTextSize(18);




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
                                    MainActivity.this.runOnUiThread(new Runnable() {
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


    private void printBar(String orders) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


        if(orders.equals("")){


                        errorcode.setText("Order number not found");
                        errorcode.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Order not found", Toast.LENGTH_SHORT).show();





            }else {


            errorcode.setVisibility(View.INVISIBLE);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    int printStatus = mPrinter.getPrinterStatus();
                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                            }
                        });
                    } else {

                        String[] allorders = orders.split(Pattern.quote("@"));
                        int itemcount = allorders.length;
                        String eachline;
                        String thisorder = getlpost();
                        PrnStrFormat format = new PrnStrFormat();
                        format.setTextSize(30);
                        format.setStyle(PrnTextStyle.BOLD);
                        mPrinter.setPrintAppendString("Order No: " + thisorder + " - " + itemcount + " Items listed", format);
                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.NORMAL);
                        format.setTextSize(18);

                        for (int i = 0; i < itemcount; i++) {
                            eachline = allorders[i];

                            String[] eachitem = eachline.split("~");
                            String upc = eachitem[0];
                            String itemscanned = eachitem[1];

                            mPrinter.setPrintAppendString(itemscanned, format);
                            mPrinter.setPrintAppendBarCode(getApplicationContext(), upc, 360, 100, true, Layout.Alignment.ALIGN_NORMAL, BarcodeFormat.CODE_128);
                        }


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
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                                         }
                                    });
                                }

                            }
                         }
                    }).start();

                 }//end if


            }
        });

    }


    private void printVillaeats(String orders) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if(orders.equals("")){


                    errorcode.setText("Order number not found");
                    errorcode.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Order not found", Toast.LENGTH_SHORT).show();





                }else {


                    errorcode.setVisibility(View.INVISIBLE);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int printStatus = mPrinter.getPrinterStatus();
                            if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                                    }
                                });
                            } else {

                                String[] allorders = orders.split(Pattern.quote("@"));
                                String lner = allorders[1];
                                String[] linez = lner.split("~");
                                String itemsc = linez[4];
                                String driverfee = linez[5];
                                String servicecharge = linez[6];
                                String thistotalprice = linez[7];

                                int itemcount = allorders.length;
                                String eachline;
                                String thisorder = getlpost();
                                PrnStrFormat format = new PrnStrFormat();
                                format.setTextSize(50);
                                format.setAli(Layout.Alignment.ALIGN_CENTER);
                                format.setStyle(PrnTextStyle.BOLD);
                                mPrinter.setPrintAppendString(" ", format);
                                mPrinter.setPrintAppendString(" VillaEats ", format);
                                format.setTextSize(30);
                                format.setStyle(PrnTextStyle.NORMAL);
                                mPrinter.setPrintAppendString(" villaeats.ai ", format);
                                mPrinter.setPrintAppendString(" 1(264)4760608 ", format);
                                format.setTextSize(25);
                                format.setAli(Layout.Alignment.ALIGN_NORMAL);
                                mPrinter.setPrintAppendString("All prices in USD ", format);
                                mPrinter.setPrintAppendString("Order No: " + thisorder + " - " + itemsc + " Items listed", format);
                                mPrinter.setPrintAppendString("_________________________", format);
                                for (int i = 0; i < itemcount; i++) {
                                    eachline = allorders[i];

                                    String[] eachitem = eachline.split("~");
                                    String upc = eachitem[0];
                                    String itemsold = eachitem[1];
                                    String itemprice = eachitem[2];
                                    thistotalgroceries = eachitem[3];

                                    mPrinter.setPrintAppendString(itemsold +"\n " + itemprice, format);
                                    mPrinter.setPrintAppendString("__", format);

                                }

                                /*
                                  public String thistotalgroceries;
                                 public String thisservicecharge;
                                    public String  thisdelivery;
                                    public String alltotal;
                                 */

                                mPrinter.setPrintAppendString("Subtotal : " + thistotalgroceries , format);
                                mPrinter.setPrintAppendString("Delivery Charge: " + driverfee, format);
                                mPrinter.setPrintAppendString("Service Charge: " + servicecharge, format);
                                format.setStyle(PrnTextStyle.BOLD);
                                mPrinter.setPrintAppendString("Total Price: " + thistotalprice, format);
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
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                                        }
                                    });
                                }

                            }
                        }
                    }).start();

                }//end if


            }
        });

    }



    private void printPaperOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    MainActivity.this.runOnUiThread(new Runnable() {
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



}
