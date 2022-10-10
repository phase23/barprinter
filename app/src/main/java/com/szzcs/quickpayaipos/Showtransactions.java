package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.szzcs.quickpayaipos.utils.DialogUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextStyle;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Showtransactions extends AppCompatActivity {
    ListView listView;
    myDbAdapter helper;
    private DriverManager mDriverManager = MyApp.sDriverManager;
    private Printer mPrinter;
    private boolean mPrintStatus = false;
    private Bitmap mBitmapDef;
    public static final String PRINT_TEXT = "本智能POS机带打印机，基于android 平台应用，整合昂贵的ECR、收银系统，伴随新型扫码支付的需求也日益突出，大屏智能安卓打印机设备，内置商户的营销管理APP，在商品管理的同时，受理客户订单支付，很好的满足了以上需求；同时便携式的要求，随着快递实名制的推行，运用在快递行业快速扫条码进件。做工精良，品质优良，是市场的最佳选择。";
    public static final String QR_TEXT = "https://www.baidu.com";
    public static final String BAR_TEXT = "50001";
    public static final String BAR_TEXT2 = "50002";
    public static final String BAR_TEXT3 = "50003";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtransactions);

        helper = new myDbAdapter(this);
        String data = helper.getData();
        String[] trx = data.split(Pattern.quote("@"));


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





        listView=(ListView)findViewById(R.id.listout);
        ArrayList<String> arrayList=new ArrayList<>();

        for (int i = 0; i < trx.length; i++) {

            arrayList.add(trx[i]);
        }



        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String cliked = arrayList.get(position);
                String requiredString = cliked.substring(cliked.indexOf("n:") + 1, cliked.indexOf("Da"));
                //Toast.makeText(getApplicationContext(), "Click item " + position + " " + requiredString, Toast.LENGTH_LONG).show();


                AlertDialog.Builder builder = new AlertDialog.Builder(Showtransactions.this);
                builder.setTitle("Confirm Print");

                builder.setMessage(Html.fromHtml("Confirm Print Receipt " ));

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("accept tag", requiredString);

                        String getdata = helper.retrieveData(requiredString);

                        printswipe(getdata);


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
                            Showtransactions.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                                }
                            });
                        } else {

                            Log.i("print", orders);
                            String[] pieces = orders.split(Pattern.quote("~"));
                            String company = pieces[5].trim();
                            String dater = pieces[1].trim();
                            String lastdigits  = pieces[4].trim();
                            String totalp  = pieces[3].trim();
                            String tips  = pieces[2].trim();
                            String trans  = pieces[0].trim();





                            PrnStrFormat format = new PrnStrFormat();
                            format.setTextSize(50);
                            format.setAli(Layout.Alignment.ALIGN_CENTER);
                            format.setStyle(PrnTextStyle.BOLD);
                            mPrinter.setPrintAppendString(" ", format);
                            mPrinter.setPrintAppendString(company, format);
                            format.setTextSize(30);
                            format.setStyle(PrnTextStyle.NORMAL);
                            mPrinter.setPrintAppendString("copy", format);
                            format.setTextSize(25);

                            format.setAli(Layout.Alignment.ALIGN_NORMAL);
                            mPrinter.setPrintAppendString("All prices in USD ", format);
                            mPrinter.setPrintAppendString("Traceid: " + trans, format);
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
                            mPrinter.setPrintAppendString("Tip : " + tips , format);
                            mPrinter.setPrintAppendString("Total : " + totalp , format);
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
                                Showtransactions.this.runOnUiThread(new Runnable() {
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
                    Showtransactions.this.runOnUiThread(new Runnable() {
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





}