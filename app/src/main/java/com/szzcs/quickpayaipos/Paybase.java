package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class Paybase extends AppCompatActivity {
    EditText isubtotal;
    EditText istoretip;
    EditText istorefinal;
    SharedPreferences sharedpreferences;
    Button itipper;
    Button tocontine;
    Button tocancel;
    String thissubtotal;
    String thisstoretip;
    TextView thistotaltxt;
    TextView thistiptxt;
    myDbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paybase);



        isubtotal = (EditText)findViewById(R.id.subtotal);
        istoretip = (EditText)findViewById(R.id.storetip);
        istorefinal = (EditText)findViewById(R.id.storefinal);

        thistotaltxt = (TextView)findViewById(R.id.totalnumber);
        thistiptxt = (TextView)findViewById(R.id.tipnumber);
        itipper = (Button)findViewById(R.id.tipper);
        tocontine = (Button)findViewById(R.id.activatebtn);
        tocancel = (Button)findViewById(R.id.cancelbtn);
        //database stuff


        isubtotal.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);



        String thismydevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        isubtotal.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setCharge();
            }
        });



          tocancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Paybase.this, MainActivity.class);

                startActivity(intent);

            }

        });


        isubtotal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {


                }else {



                }
            }
        });




            tocontine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent nointernet = new Intent(Nointernet.this, MainActivity.class);
                //startActivity(nointernet);
                thissubtotal = isubtotal.getText().toString();
                if(thissubtotal.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter a charge", Toast.LENGTH_SHORT).show();
                    return;
                }


                float myNum = 0;
                try {
                    myNum = Float.parseFloat(thissubtotal);
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }

                if(myNum < 1) {
                    Toast.makeText(getApplicationContext(), "The charge amount cannot be less than $1.00", Toast.LENGTH_SHORT).show();
                    return;

                }

                String getfinal = istorefinal.getText().toString();
                Log.i("total",getfinal);


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("chargetotal", getfinal);
                editor.putString("incltip", thisstoretip);
                editor.apply();


                Fragment fragment = new CardFragment();
                if (savedInstanceState == null)
                    getFragmentManager().beginTransaction().add(R.id.charge_frame, fragment).commit();


            }

        });




        itipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder alert = new AlertDialog.Builder(Paybase.this);
                final EditText edittext = new EditText(getApplicationContext());
                alert.setMessage("Tips");
                alert.setTitle("Add Tip below");
                edittext.setHint("0.00");
                edittext.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                //edittext.setText("pruce");
                // edittext.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED); //for positive or negative values
                edittext.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                alert.setView(edittext);
                edittext.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);



                alert.setPositiveButton("Add Tip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        //Editable YouEditTextValue = edittext.getText();
                        //OR
                        String newprice = edittext.getText().toString();
                        System.out.println("print : "+ newprice );
                        istoretip.setText(newprice);

                        setCharge();



                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();









            }

        });



    }


public void setCharge(){

    thissubtotal = isubtotal.getText().toString();
    thisstoretip = istoretip.getText().toString();

    double getTotal = 0;
    try {
        getTotal = Float.parseFloat(thissubtotal);
    } catch(NumberFormatException nfe) {
        System.out.println("Could not parse " + nfe);
    }

    double getTip = 0;
    try {
        getTip = Float.parseFloat(thisstoretip);
    } catch(NumberFormatException nfe) {
        System.out.println("Could not parse " + nfe);
    }

    double totalcharge = getTip + getTotal;
            String strtotal = String.valueOf(totalcharge);
            istorefinal.setText(strtotal);

    String COUNTRY = "US";
    String LANGUAGE = "en";
    String nicecharge = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(totalcharge);
    String nicetip = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(getTip);

    //thistotaltxt.setText(String.format("$%.2f", "" + totalcharge));
    thistotaltxt.setText(String.valueOf(nicecharge));
    thistiptxt.setText(String.valueOf(nicetip));




    /*
    long id  = helper.insertData("sring","party");

    if(id<=0)
    {
        Message.message(getApplicationContext(),"Insertion Unsuccessful");

    } else
    {
        Message.message(getApplicationContext(),"Insertion Successful");

    }
*/
}





    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();

        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm2.hideSoftInputFromWindow(isubtotal.getWindowToken(), 0);
        }

        return true;
    }

}