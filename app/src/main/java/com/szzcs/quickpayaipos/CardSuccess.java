package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.szzcs.quickpayaipos.utils.DialogUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextStyle;

import java.util.regex.Pattern;

public class CardSuccess extends AppCompatActivity {
    TextView gettransaction;
    Button printr;
    String getall;
    Button blinkback;
    myDbAdapter helper;

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
        setContentView(R.layout.activity_card_success);
        String gettransid = getIntent().getExtras().getString("transid");
         getall = getIntent().getExtras().getString("puttall");

        helper = new myDbAdapter(this);

        String[] pieces = getall.split(Pattern.quote("~"));
        String icompany = pieces[13].trim();
        String icountry = pieces[15].trim();
        String idater = pieces[17].trim();
        String itel = pieces[16].trim();
        String itrans  = pieces[6].trim();
        String ilastdigits  = pieces[9].trim();
        String itotalp  = pieces[18].trim();
        String  itimestamp  = pieces[20].trim();
        String  tip  = pieces[21].trim();

        double getTip = 0.00;
        try {
            getTip = Float.parseFloat(tip);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        double chargeprice = 0.00;
        try {
            chargeprice = Float.parseFloat(itotalp);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }


        int inttimestamp = 0;
        try {
            inttimestamp = Integer.parseInt(itimestamp);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        int intlastdigits = 0;
        try {
            intlastdigits = Integer.parseInt(ilastdigits);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }



    long id  = helper.insertData(itrans, inttimestamp, icompany, idater, getTip, chargeprice, intlastdigits );

    if(id<=0)
    {
        //Message.message(getApplicationContext(),"Insertion Unsuccessful");
        //Toast.makeText(getApplicationContext(), "Insertion Failed", Toast.LENGTH_LONG).show();


    } else
    {
        Message.message(getApplicationContext(),"Transaction Completed");
        //Toast.makeText(getApplicationContext(), "Transaction Completed", Toast.LENGTH_LONG).show();

    }




        gettransaction = (TextView)findViewById(R.id.transid);
        gettransaction.setText(gettransid);



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


        Log.i("[print] xx",getall);

        printr = (Button)findViewById(R.id.print);
        blinkback = (Button)findViewById(R.id.linkback);



        blinkback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CardSuccess.this, MainActivity.class);

                startActivity(intent);

            }

        });



        printr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                printswipe(getall);

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
                                CardSuccess.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtils.show(getApplicationContext(), getString(R.string.printer_out_of_paper));

                                    }
                                });
                            } else {

                                String[] pieces = orders.split(Pattern.quote("~"));
                                String company = pieces[13].trim();
                                String country = pieces[15].trim();
                                String dater = pieces[17].trim();
                                String tel = pieces[16].trim();
                                String trans  = pieces[3].trim();
                                String lastdigits  = pieces[19].trim();
                                String totalp  = pieces[18].trim();
                                String  tip  = pieces[21].trim();

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

                                mPrinter.setPrintAppendString("Tip : $" + tip , format);
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
                                    CardSuccess.this.runOnUiThread(new Runnable() {
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
                    CardSuccess.this.runOnUiThread(new Runnable() {
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