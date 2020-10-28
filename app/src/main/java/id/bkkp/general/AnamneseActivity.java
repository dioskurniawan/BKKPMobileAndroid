package id.bkkp.general;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import mehdi.sakout.fancybuttons.FancyButton;

public class AnamneseActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SessionManager session;
    private EditText bst;
    CheckBox q1,q2,q3,q4,q5,q6,q7,q8,q9,q10,q11,q12,q13,q14,q15,q16,q17,q18,q19,q20,q21,q22,q23,q24,q25,q26,q27,q28,q29,q30,q31,q32,q33,q34;
    int q1_val,q2_val,q3_val,q4_val,q5_val,q6_val,q7_val,q8_val,q9_val,q10_val,q11_val,q12_val,q13_val,q14_val,q15_val,q16_val,q17_val,q18_val,q19_val,q20_val,q21_val,q22_val,q23_val,q24_val,q25_val,q26_val,q27_val,q28_val,q29_val,q30_val,q31_val,q32_val,q33_val,q34_val;
    Calendar myCalendar;
    private String serverDate;
    int seafarer_id, seafarer_status, dateNo;
    private ProgressDialog mProgressDialog;
    AlertDialog alertDialog1;
    final Handler handler7 = new Handler();
    FancyButton btnSave, btnBack;
    Typeface font1, font2;
    public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
    SwipeRefreshLayout swipeRefreshLayout;
    boolean internetAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anamnese);

        session = new SessionManager(AnamneseActivity.this);
        font1 = Typeface.createFromAsset(getResources().getAssets(), "bahnschrift.ttf");
        font2 = Typeface.createFromAsset(getResources().getAssets(), "OpenSansBI.ttf");
        TextView toolbar = findViewById(R.id.toolbar_title);
        toolbar.setTypeface(font1);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        q1 = findViewById(R.id.q1);
        q2 = findViewById(R.id.q2);
        q3 = findViewById(R.id.q3);
        q4 = findViewById(R.id.q4);
        q5 = findViewById(R.id.q5);
        q6 = findViewById(R.id.q6);
        q7 = findViewById(R.id.q7);
        q8 = findViewById(R.id.q8);
        q9 = findViewById(R.id.q9);
        q10 = findViewById(R.id.q10);
        q11 = findViewById(R.id.q11);
        q12 = findViewById(R.id.q12);
        q13 = findViewById(R.id.q13);
        q14 = findViewById(R.id.q14);
        q15 = findViewById(R.id.q15);
        q16 = findViewById(R.id.q16);
        q17 = findViewById(R.id.q17);
        q18 = findViewById(R.id.q18);
        q19 = findViewById(R.id.q19);
        q20 = findViewById(R.id.q20);
        q21 = findViewById(R.id.q21);
        q22 = findViewById(R.id.q22);
        q23 = findViewById(R.id.q23);
        q24 = findViewById(R.id.q24);
        q25 = findViewById(R.id.q25);
        q26 = findViewById(R.id.q26);
        q27 = findViewById(R.id.q27);
        q28 = findViewById(R.id.q28);
        q29 = findViewById(R.id.q29);
        q30 = findViewById(R.id.q30);
        q31 = findViewById(R.id.q31);
        q32 = findViewById(R.id.q32);
        q33 = findViewById(R.id.q33);
        q34 = findViewById(R.id.q34);

        q1.setTypeface(font1);
        q2.setTypeface(font1);
        q3.setTypeface(font1);
        q4.setTypeface(font1);
        q5.setTypeface(font1);
        q6.setTypeface(font1);
        q7.setTypeface(font1);
        q8.setTypeface(font1);
        q9.setTypeface(font1);
        q10.setTypeface(font1);
        q11.setTypeface(font1);
        q12.setTypeface(font1);
        q13.setTypeface(font1);
        q14.setTypeface(font1);
        q15.setTypeface(font1);
        q16.setTypeface(font1);
        q17.setTypeface(font1);
        q18.setTypeface(font1);
        q19.setTypeface(font1);
        q20.setTypeface(font1);
        q21.setTypeface(font1);
        q22.setTypeface(font1);
        q23.setTypeface(font1);
        q24.setTypeface(font1);
        q25.setTypeface(font1);
        q26.setTypeface(font1);
        q27.setTypeface(font1);
        q28.setTypeface(font1);
        q29.setTypeface(font1);
        q30.setTypeface(font1);
        q31.setTypeface(font1);
        q32.setTypeface(font1);
        q33.setTypeface(font1);
        q34.setTypeface(font1);

        Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        Date currentLocalTimes = cald.getTime();
        DateFormat dateF = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat dateF2 = new SimpleDateFormat("yyyy-MM-dd");
        dateF.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        serverDate = dateF2.format(currentLocalTimes);

        myCalendar = Calendar.getInstance();

        btnSave = (FancyButton) findViewById(R.id.btn_save);
        /*if(!session.sUserBST16().equals("62")){
            btnSave.setVisibility(View.INVISIBLE);
        }*/
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AnamneseActivity.this);
                builder.setTitle(R.string.confirm);
                builder.setMessage(R.string.confirm_txt);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitForm();
                                /*new isOnline().execute("");
                                handler7.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(internetAvailable){
                                            submitForm();
                                        } else {
                                            Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, 2000);*/
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
        });

        btnBack = (FancyButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AnamneseActivity.this,
                        ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

        new isOnline().execute("");
        handler7.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(internetAvailable){
                    showProgressDialog(false);
                    loadDetail(session.sUserBST());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet1, Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);
        //showMessage("ok",session.sDob());
        Log.d("jwt",session.sJWT());
    }

    private void showProgressDialog(boolean cancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(AnamneseActivity.this);
            mProgressDialog.setMessage(AnamneseActivity.this.getString(R.string.loading));
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

    @Override
    public void onRefresh() {
        new isOnline().execute("");

        handler7.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(internetAvailable){
                    showProgressDialog(false);
                    loadDetail(session.sUserBST());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet1, Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);
    }

    private void loadDetail(String sbst) {
        // We first check for cached request
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?view_anamnese=" + sbst + "&jwt=" + session.sJWT() + "&phone=" + session.phone());

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
                    URL_INPUT+"?view_anamnese=" + sbst + "&jwt=" + session.sJWT() + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("BKKP debug", "Response: " + response.toString());
                    parseJsonFeed(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("BKKP debug", "Error: " + error.getMessage());
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
            String uid = response.getString("uid");
            String returnmessage = response.getString("msg");
            if (!error) {
                hideProgressDialog();
                btnSave.setVisibility(View.INVISIBLE);
                JSONObject feedObj = response.getJSONObject("anamnese");
                //for (int i = 0; i < 34; i++)
                q1.setChecked(feedObj.getBoolean("q1"));
                q2.setChecked(feedObj.getBoolean("q2"));
                q3.setChecked(feedObj.getBoolean("q3"));
                q4.setChecked(feedObj.getBoolean("q4"));
                q5.setChecked(feedObj.getBoolean("q5"));
                q6.setChecked(feedObj.getBoolean("q6"));
                q7.setChecked(feedObj.getBoolean("q7"));
                q8.setChecked(feedObj.getBoolean("q8"));
                q9.setChecked(feedObj.getBoolean("q9"));
                q10.setChecked(feedObj.getBoolean("q10"));
                q11.setChecked(feedObj.getBoolean("q11"));
                q12.setChecked(feedObj.getBoolean("q12"));
                q13.setChecked(feedObj.getBoolean("q13"));
                q14.setChecked(feedObj.getBoolean("q14"));
                q15.setChecked(feedObj.getBoolean("q15"));
                q16.setChecked(feedObj.getBoolean("q16"));
                q17.setChecked(feedObj.getBoolean("q17"));
                q18.setChecked(feedObj.getBoolean("q18"));
                q19.setChecked(feedObj.getBoolean("q19"));
                q20.setChecked(feedObj.getBoolean("q20"));
                q21.setChecked(feedObj.getBoolean("q21"));
                q22.setChecked(feedObj.getBoolean("q22"));
                q23.setChecked(feedObj.getBoolean("q23"));
                q24.setChecked(feedObj.getBoolean("q24"));
                q25.setChecked(feedObj.getBoolean("q25"));
                q26.setChecked(feedObj.getBoolean("q26"));
                q27.setChecked(feedObj.getBoolean("q27"));
                q28.setChecked(feedObj.getBoolean("q28"));
                q29.setChecked(feedObj.getBoolean("q29"));
                q30.setChecked(feedObj.getBoolean("q30"));
                q31.setChecked(feedObj.getBoolean("q31"));
                q32.setChecked(feedObj.getBoolean("q32"));
                q33.setChecked(feedObj.getBoolean("q33"));
                q34.setChecked(feedObj.getBoolean("q34"));
                //}
                swipeRefreshLayout.setRefreshing(false);
            } else {
                if(!session.sDob().equals("")){
                    //dob.setText(session.sDob());
                }
                swipeRefreshLayout.setRefreshing(false);
                hideProgressDialog();
                //no_agenda.setText(feedObj.getString("no_agenda"));
            }


        } catch (JSONException e) {
            swipeRefreshLayout.setRefreshing(false);
            hideProgressDialog();
            e.printStackTrace();
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateDob() {
        Calendar calendar  = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -15);
        Date d1 = calendar.getTime();
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), "Usia "+ serverDate + d1.toString(), Toast.LENGTH_LONG).show();
        if (date1.after(d1)) {
            Toast.makeText(getApplicationContext(), R.string.age, Toast.LENGTH_LONG).show();
            return false;
        } else {
        }
        return true;
    }

    private void submitForm() {

        new isOnline().execute("");
        handler7.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(internetAvailable){
                        //showMessage("bst",last6_bst);
                    showProgressDialog(false);
                    new saveData().execute();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);

    }
    private class saveData extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            q1_val = (q1.isChecked()) ? 1 : 0;
            q2_val = (q2.isChecked()) ? 1 : 0;
            q3_val = (q3.isChecked()) ? 1 : 0;
            q4_val = (q4.isChecked()) ? 1 : 0;
            q5_val = (q5.isChecked()) ? 1 : 0;
            q6_val = (q6.isChecked()) ? 1 : 0;
            q7_val = (q7.isChecked()) ? 1 : 0;
            q8_val = (q8.isChecked()) ? 1 : 0;
            q9_val = (q9.isChecked()) ? 1 : 0;
            q10_val = (q10.isChecked()) ? 1 : 0;
            q11_val = (q11.isChecked()) ? 1 : 0;
            q12_val = (q12.isChecked()) ? 1 : 0;
            q13_val = (q13.isChecked()) ? 1 : 0;
            q14_val = (q14.isChecked()) ? 1 : 0;
            q15_val = (q15.isChecked()) ? 1 : 0;
            q16_val = (q16.isChecked()) ? 1 : 0;
            q17_val = (q17.isChecked()) ? 1 : 0;
            q18_val = (q18.isChecked()) ? 1 : 0;
            q19_val = (q19.isChecked()) ? 1 : 0;
            q20_val = (q20.isChecked()) ? 1 : 0;
            q21_val = (q21.isChecked()) ? 1 : 0;
            q22_val = (q22.isChecked()) ? 1 : 0;
            q23_val = (q23.isChecked()) ? 1 : 0;
            q24_val = (q24.isChecked()) ? 1 : 0;
            q25_val = (q25.isChecked()) ? 1 : 0;
            q26_val = (q26.isChecked()) ? 1 : 0;
            q27_val = (q27.isChecked()) ? 1 : 0;
            q28_val = (q28.isChecked()) ? 1 : 0;
            q29_val = (q29.isChecked()) ? 1 : 0;
            q30_val = (q30.isChecked()) ? 1 : 0;
            q31_val = (q31.isChecked()) ? 1 : 0;
            q32_val = (q32.isChecked()) ? 1 : 0;
            q33_val = (q33.isChecked()) ? 1 : 0;
            q34_val = (q34.isChecked()) ? 1 : 0;
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            /*String values = arg0[0];
            String extractString = values;
            String[] result = extractString.split("!");*/

            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("anamnese", session.sUserBST());
                jsonObject.put("q_1", q1_val);
                jsonObject.put("q_2", q2_val);
                jsonObject.put("q_3", q3_val);
                jsonObject.put("q_4", q4_val);
                jsonObject.put("q_5", q5_val);
                jsonObject.put("q_6", q6_val);
                jsonObject.put("q_7", q7_val);
                jsonObject.put("q_8", q8_val);
                jsonObject.put("q_9", q9_val);
                jsonObject.put("q_10", q10_val);
                jsonObject.put("q_11", q11_val);
                jsonObject.put("q_12", q12_val);
                jsonObject.put("q_13", q13_val);
                jsonObject.put("q_14", q14_val);
                jsonObject.put("q_15", q15_val);
                jsonObject.put("q_16", q16_val);
                jsonObject.put("q_17", q17_val);
                jsonObject.put("q_18", q18_val);
                jsonObject.put("q_19", q19_val);
                jsonObject.put("q_20", q20_val);
                jsonObject.put("q_21", q21_val);
                jsonObject.put("q_22", q22_val);
                jsonObject.put("q_23", q23_val);
                jsonObject.put("q_24", q24_val);
                jsonObject.put("q_25", q25_val);
                jsonObject.put("q_26", q26_val);
                jsonObject.put("q_27", q27_val);
                jsonObject.put("q_28", q28_val);
                jsonObject.put("q_29", q29_val);
                jsonObject.put("q_30", q30_val);
                jsonObject.put("q_31", q31_val);
                jsonObject.put("q_32", q32_val);
                jsonObject.put("q_33", q33_val);
                jsonObject.put("q_34", q34_val);

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(URL_INPUT, params);
                Log.d("bkkp post", "bkkp debug : jsonObject=" + jsonObject.toString());

                //Log.d("fao", "fao debug : saveData=" + session.nUserID() + "!" + house_id + "!" + num_of_chick + "!" + result[0] + "!" + snap_chick_afkir + "!" + snap_chick_death + "!" + result[4] + "!" + result[5] + "!" + result[6]);
                return json;
            } catch (Exception e) {
                Log.d("bkkp post err", "bkkp debug err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            hideProgressDialog();
            try {
                boolean error = json.getBoolean("error");

                if (!error) {

                    //String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    Toast.makeText(getApplicationContext(), returnmessage, Toast.LENGTH_SHORT).show();

                    Log.d("bkkp", returnmessage);
                    //new selectSummaryReport().execute("select");

                    /*Intent i = new Intent(getApplicationContext(), AnamneseActivity.class);
                    startActivity(i);
                    finish();*/

                } else {
                    // Error in login. Get the error message
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                // JSON error
                hideProgressDialog();
                e.printStackTrace();
                //Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }
    /*private void saveData(String name, String no_ktp, String tmp_lahir, String jk, String agama, String marital, String addr, String last6_bst) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?save_anamnese=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&q1=" + name + "&q2=" + no_ktp + "&tmp_lahir=" + tmp_lahir + "&tgl_lahir=" + serverDate + "&jk=" + jk + "&agama=" + agama + "&marital=" + marital + "&addr=" + addr + "&last6_bst=" + last6_bst);

        if (entry2 != null) {
            // fetch the data from cache
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonFeedSend(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_INPUT+"?save_anamnese=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&name=" + name + "&no_ktp=" + no_ktp + "&tmp_lahir=" + tmp_lahir + "&tgl_lahir=" + serverDate + "&jk=" + jk + "&agama=" + agama + "&marital=" + marital + "&addr=" + addr + "&last6_bst=" + last6_bst, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("BKKP debug1", "Response: " + response.toString());
                    parseJsonFeedSend(response);
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

    private void parseJsonFeedSend(JSONObject response) {
        try {

            boolean error = response.getBoolean("error");
            if (!error) {
                //String uid = response.getString("uid");
                String returnmessage = response.getString("msg");
                JSONArray feedArray = response.getJSONArray("profile");
                int rows = feedArray.length();
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    /-*if(imgState) {
                        uploadImage(feedObj.getString("id"));
                    } else {
                        loadDetail(session.sUserBST());
                        handler7.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), PendaftaranActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);
                    }*-/
                }
                hideProgressDialog();

                //showMessage("ok",returnmessage);
                //Toast.makeText(ProfileActivity.this, returnmessage, Toast.LENGTH_LONG).show();

            } else {
                hideProgressDialog();
                String errmessage = response.getString("error_msg");
                showMessage("Info", errmessage);
            }
        } catch (JSONException e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }*/

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
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
}
