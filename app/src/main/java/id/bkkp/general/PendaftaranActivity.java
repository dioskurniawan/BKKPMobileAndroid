package id.bkkp.general;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mehdi.sakout.fancybuttons.FancyButton;

public class PendaftaranActivity extends AppCompatActivity {

    SessionManager session;
    private EditText tglMcu, phone;
    Calendar myCalendar;
    private String serverDate;
    int dateNo;
    private ProgressDialog mProgressDialog;
    AlertDialog alertDialog1;
    final Handler handler7 = new Handler();
    FancyButton btnNext, btnBack;
    TextView seafarer, tgl, klinik, txtphone, txtphone1;
    Typeface font1, font2;
    Spinner s1, s2;
    ArrayAdapter<CharSequence> spinnerAdapter1;
    public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
    boolean internetAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran);

        session = new SessionManager(PendaftaranActivity.this);
        font1 = Typeface.createFromAsset(getResources().getAssets(), "bahnschrift.ttf");
        font2 = Typeface.createFromAsset(getResources().getAssets(), "OpenSansBI.ttf");
        TextView toolbar = findViewById(R.id.toolbar_title);
        toolbar.setTypeface(font1);

        Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        cald.add(Calendar.DATE, 1);
        final Date currentLocalTimes = cald.getTime();
        DateFormat dateF = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat dateF2 = new SimpleDateFormat("yyyy-MM-dd");
        dateF.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));

        s1 = (Spinner) findViewById(R.id.klinik);
        seafarer = findViewById(R.id.seafarer);
        tgl = findViewById(R.id.txt_tgl);
        klinik = findViewById(R.id.txt_klinik);
        //txtphone = findViewById(R.id.txt_phone);
        //txtphone1 = findViewById(R.id.txt_phone1);
        seafarer.setTypeface(font1);
        tgl.setTypeface(font1);
        klinik.setTypeface(font1);
        //txtphone.setTypeface(font1);
        //txtphone1.setTypeface(font1);
        //phone = findViewById(R.id.input_hp);
        tglMcu = findViewById(R.id.tgl_mcu);
        tglMcu.setText(dateF.format(currentLocalTimes));
        serverDate = dateF2.format(currentLocalTimes);
        spinnerAdapter1 = ArrayAdapter.createFromResource(this, R.array.klinik_array, android.R.layout.simple_spinner_item);
        spinnerAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(spinnerAdapter1);

        int spinnerPosition2 = spinnerAdapter1.getPosition("KU BKKP Gunung Sahari");
        s1.setSelection(spinnerPosition2);

        myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.DATE, 1);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateNo);
            }
        };

        tglMcu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(PendaftaranActivity.this, /*android.R.style.Theme_Holo_Light_Dialog,*/ date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                dateNo = 1;
            }
        });

        seafarer.setText("BST : " + session.sUserBST16());
        //phone.setText(session.phone());

        btnNext = (FancyButton) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final String newphone = phone.getText().toString().trim();
                final String newphone = session.phone();
                final String tgl = tglMcu.getText().toString().trim();
                final String klinik = s1.getSelectedItem().toString();
                if(klinik.equals("KU BKKP Ancol")){
                    Toast.makeText(getApplicationContext(), R.string.ancol, Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PendaftaranActivity.this);
                    builder.setTitle(R.string.confirm);
                    builder.setMessage(getResources().getString(R.string.confirm_txt1) + tgl + getResources().getString(R.string.di) + klinik);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new isOnline().execute("");
                                    handler7.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (internetAvailable) {
                                                showProgressDialog(false);
                                                reqQueue(serverDate, newphone, klinik);
                                            } else {
                                                Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }, 2000);
                                }
                            }
                    );
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog1.dismiss();
                                }
                            }
                    );
                    alertDialog1 = builder.create();
                    alertDialog1.setCanceledOnTouchOutside(false);
                    alertDialog1.show();
                }
                //getActivity().finish();
            }
        });

        btnBack = (FancyButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PendaftaranActivity.this, MainActivity.class);
                i.putExtra("fragment", 3);
                startActivity(i);
                finish();
            }
        });
    }

    private void showProgressDialog(boolean cancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(PendaftaranActivity.this);
            mProgressDialog.setMessage(PendaftaranActivity.this.getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(cancel);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateLabel(final int numDate) {
        String startdate = null;
        String enddate = null;
        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(numDate == 1) {
            tglMcu.setText(sdf.format(myCalendar.getTime()));
            serverDate = newFormat.format(myCalendar.getTime());
            startdate = newFormat.format(myCalendar.getTime());
        } else {
        }
    }

    private void reqQueue(String tgl, String newphone, String clinic) {
        if(clinic.equals("KU BKKP Gunung Sahari")){
            clinic = "1";
        } else {
            clinic = "2";
        }
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?reserve=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&phone=" + newphone + "&tgl_pesan=" + tgl + "&clinic=" + clinic);

        Log.d("debug reqQueue",URL_INPUT+"?reserve=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&phone=" + newphone + "&tgl_pesan=" + tgl + "&clinic=" + clinic);
        if (entry2 != null) {
            // fetch the data from cache
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_INPUT+"?reserve=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&phone=" + newphone + "&tgl_pesan=" + tgl + "&clinic=" + clinic, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("BKKP debug1", "Response: " + response.toString());
                    parseJsonFeed(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("BKKP debug2", "Error: " + error.getMessage());
                }
            });
            jsonReq2.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq2);
        }
    }

    private void parseJsonFeed(JSONObject response) {
        try {

            boolean error = response.getBoolean("error");
            if (!error) {
                //String uid = response.getString("uid");
                String returnmessage = response.getString("msg");
                JSONArray feedArray = response.getJSONArray("queue");
                int rows = feedArray.length();

                hideProgressDialog();
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    //JSONObject feedObj = response.getJSONObject("queue");

                    String tgl = feedObj.getString("tgl");
                    String klinik = feedObj.getString("klinik");
                    int no_antrian = feedObj.getInt("no_antrian");
                    String no_hp = feedObj.getString("phone");
                    String tgl_server = feedObj.getString("tgl_server");
                    //seafarer_id = feedObj.getInt("seafarer_id");
                    //showMessage("ok",returnmessage);
                    Intent intent = new Intent(PendaftaranActivity.this,
                            PendaftaranResultActivity.class);
                    intent.putExtra("msg","MCU tanggal "+tgl+" di klinik "+klinik+" dengan nomer antrian "+no_antrian+". SMS notifikasi dikirim ke "+no_hp+".");
                    intent.putExtra("val1",no_antrian);
                    intent.putExtra("val2",tgl);
                    intent.putExtra("val3",klinik);
                    intent.putExtra("val4", tgl_server+"#"+String.valueOf(no_antrian));
                    startActivity(intent);
                    finish();
                }
            } else {
                hideProgressDialog();
                String errmessage = response.getString("error_msg");
                char msg = errmessage.charAt(0);
                if(msg == 'A'){
                    errmessage = errmessage;
                } else {
                    errmessage = "Kuota antrian untuk MCU tanggal "+tglMcu.getText().toString().trim()+" di klinik "+s1.getSelectedItem().toString()+" sudah terpenuhi. Silakan pilih tanggal dan/atau klinik lain.";
                }
                Intent i = new Intent(PendaftaranActivity.this,
                        PendaftaranResultActivity.class);
                i.putExtra("msg",errmessage);
                i.putExtra("val1",0);
                i.putExtra("val2","");
                i.putExtra("val3","");
                i.putExtra("val4","");

                startActivity(i);
                finish();
            }
        } catch (JSONException e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }

    private class isOnline extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
<<<<<<< HEAD
                int timeoutMs = 5000;
=======
                int timeoutMs = 1500;
>>>>>>> bf25cb751f4cc992e6de58b8b17607974d00cca2
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(sockaddr, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) { return false; }
        }

        @Override
        protected void onPostExecute(Boolean online) {
            super.onPostExecute(online);
            internetAvailable = online;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent i = new Intent(getApplicationContext(),
                            MainActivity.class);
                    i.putExtra("fragment", 3);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
