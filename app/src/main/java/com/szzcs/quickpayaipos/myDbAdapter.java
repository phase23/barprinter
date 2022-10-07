package com.szzcs.quickpayaipos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;


public class myDbAdapter {
    myDbHelper myhelper;
    public myDbAdapter(Context context)
    {
        myhelper = new myDbHelper(context);
    }



    public long insertData(String transactionid, int timestamp, String company, String dateTime, double tip, double total,int lastdigit  )
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.COMPANY, company);
        contentValues.put(myDbHelper.TRANSACTIONID, transactionid);
        contentValues.put(myDbHelper.TIMESTAMP, timestamp);
        contentValues.put(myDbHelper.DEDATE, dateTime);
        contentValues.put(myDbHelper.TIP, tip);
        contentValues.put(myDbHelper.TOTAL, total);
        contentValues.put(myDbHelper.LASTDIGIT, lastdigit);
        long id = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return id;
    }

    public String getData()
    {

        String output;
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID,myDbHelper.COMPANY, myDbHelper.TRANSACTIONID,myDbHelper.TIMESTAMP, myDbHelper.DEDATE,myDbHelper.TIP,myDbHelper.TOTAL,myDbHelper.LASTDIGIT };
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null, myDbHelper.TIMESTAMP + " DESC");
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int cid =cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
            String company =cursor.getString(cursor.getColumnIndex(myDbHelper.COMPANY));
            String transactionid =cursor.getString(cursor.getColumnIndex(myDbHelper.TRANSACTIONID));
            String  nicedate =cursor.getString(cursor.getColumnIndex(myDbHelper.DEDATE));
            double  tip  =cursor.getDouble(cursor.getColumnIndex(myDbHelper.TIP));
            double  total  =cursor.getDouble(cursor.getColumnIndex(myDbHelper.TOTAL));
            int  lastdigit  =cursor.getInt(cursor.getColumnIndex(myDbHelper.LASTDIGIT));

            String COUNTRY = "US";
            String LANGUAGE = "en";
            String nicecharge = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(total);
            String nicetip = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(tip);


            buffer.append("\nTransaction:" +transactionid + "\nDate:" + nicedate   + "\n" + "Tip: " + nicetip + "\nTotal: " + nicecharge + "\n**** **** **** "+ lastdigit +  "@");


        }
        return buffer.toString();
    }


    public String retrieveData(String tranx)
    {
        Log.i("print ", tranx);

        tranx = tranx.trim();

        tranx = tranx.substring(1);//ello Worl

        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID,myDbHelper.COMPANY, myDbHelper.TRANSACTIONID,myDbHelper.TIMESTAMP, myDbHelper.DEDATE,myDbHelper.TIP,myDbHelper.TOTAL,myDbHelper.LASTDIGIT };
       Cursor cursor =db.query(myDbHelper.TABLE_NAME + " where transactionid = '" + tranx + "'",columns,null,null,null,null, myDbHelper.TIMESTAMP + " DESC");


        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int cid =cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
            String company =cursor.getString(cursor.getColumnIndex(myDbHelper.COMPANY));
            String transactionid =cursor.getString(cursor.getColumnIndex(myDbHelper.TRANSACTIONID));
            String  nicedate =cursor.getString(cursor.getColumnIndex(myDbHelper.DEDATE));
            double  tip  =cursor.getDouble(cursor.getColumnIndex(myDbHelper.TIP));
            double  total  =cursor.getDouble(cursor.getColumnIndex(myDbHelper.TOTAL));
            int  lastdigit  =cursor.getInt(cursor.getColumnIndex(myDbHelper.LASTDIGIT));

            String COUNTRY = "US";
            String LANGUAGE = "en";
            String nicecharge = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(total);
            String nicetip = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(tip);


            buffer.append(transactionid + "~" + nicedate   + "~" + nicetip + "~" + nicecharge + "~**** **** **** "+ lastdigit +  "~" + company);


        }
        return buffer.toString();
    }


    public  int delete(String uname)
    {
        /*
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={uname};

        int count =db.delete(myDbHelper.TABLE_NAME ,myDbHelper.NAME+" = ?",whereArgs);
        */
        int count = 0;
        return  count;

    }

    public int updateName(String oldName , String newName)
    {
        /*
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME,newName);
        String[] whereArgs= {oldName};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.NAME+" = ?",whereArgs );
        return count;

         */
        int count = 0;
        return count;
    }

    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "quickpay.db";    // Database Name
        private static final String TABLE_NAME = "transactionhistory";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String UID="_id";     // Column I (Primary Key)
        private static final String COMPANY   = "company";    // Column III
        private static final String TRANSACTIONID   = "transactionid";    // Column III
        private static final String TIMESTAMP = "timestamp";    // Column III
        private static final String DEDATE = "dateTime";    //Column II
        private static final String LASTDIGIT = "lastdigit";    //Column II
        private static final String TIP = "tip";    //
        private static final String TOTAL = "total";    //

        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COMPANY+" VARCHAR(255) , "+TRANSACTIONID+" VARCHAR(255) ,"+ TIMESTAMP+" INT(15),"+ DEDATE +" VARCHAR(225),"+ TIP +" DECIMAL(10,2),"+ TOTAL +" DECIMAL(10,2),"+ LASTDIGIT +" INT(5) );";


        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                Message.message(context,""+e);
            }
        }
    }
}