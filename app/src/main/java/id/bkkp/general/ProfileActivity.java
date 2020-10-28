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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SessionManager session;
    private EditText bst, nama, alamat, ktp, pob, dob, company;
    Calendar myCalendar;
    private String serverDate;
    int seafarer_id, seafarer_status, dateNo;
    private ProgressDialog mProgressDialog;
    AlertDialog alertDialog1;
    final Handler handler7 = new Handler();
    FancyButton btnSave, btnBack;
    TextView txt_seafarer, txt_nama, txt_ktp, txt_alamat, txt_pob, txt_dob, txt_jk, txt_marital, txt_agama, txt_photo, txt_company;
    Typeface font1, font2;
    Spinner s1, s2, s3;
    ArrayAdapter<CharSequence> spinnerAdapter1, spinnerAdapter2, spinnerAdapter3;
    ImageView photo;
    public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
    SwipeRefreshLayout swipeRefreshLayout;
    boolean internetAvailable = false;

    Button buttonChoose, btnAnamnese;
    FloatingActionButton buttonUpload;
    Bitmap bitmap, decoded;
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100
    boolean imgState = false;

    private static final String TAG = MainActivity.class.getSimpleName();
    //private String UPLOAD_URL = "http://10.0.2.2/android/upload_image/upload.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "photo";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new SessionManager(ProfileActivity.this);
        font1 = Typeface.createFromAsset(getResources().getAssets(), "bahnschrift.ttf");
        font2 = Typeface.createFromAsset(getResources().getAssets(), "OpenSansBI.ttf");
        TextView toolbar = findViewById(R.id.toolbar_title);
        toolbar.setTypeface(font1);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        s1 = (Spinner) findViewById(R.id.jk);
        s2 = (Spinner) findViewById(R.id.agama);
        s3 = (Spinner) findViewById(R.id.marital);
        txt_seafarer = findViewById(R.id.seafarer);
        txt_nama = findViewById(R.id.txt_nama);
        txt_ktp = findViewById(R.id.txt_ktp);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_pob = findViewById(R.id.txt_pob);
        txt_dob = findViewById(R.id.txt_dob);
        txt_jk = findViewById(R.id.txt_jk);
        txt_marital = findViewById(R.id.txt_marital);
        txt_agama = findViewById(R.id.txt_agama);
        txt_photo = findViewById(R.id.txt_photo);
        txt_company = findViewById(R.id.txt_company);

        txt_seafarer.setTypeface(font2);
        txt_nama.setTypeface(font1);
        txt_ktp.setTypeface(font1);
        txt_alamat.setTypeface(font1);
        txt_pob.setTypeface(font1);
        txt_dob.setTypeface(font1);
        txt_jk.setTypeface(font1);
        txt_marital.setTypeface(font1);
        txt_agama.setTypeface(font1);
        txt_photo.setTypeface(font1);
        txt_company.setTypeface(font1);

        bst = findViewById(R.id.input_phone);
        nama = findViewById(R.id.input_nama);
        alamat = findViewById(R.id.input_alamat);
        ktp = findViewById(R.id.input_ktp);
        pob = findViewById(R.id.input_pob);
        dob = findViewById(R.id.input_dob);
        photo = findViewById(R.id.photo);
        company = findViewById(R.id.input_company);
        Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        Date currentLocalTimes = cald.getTime();
        DateFormat dateF = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat dateF2 = new SimpleDateFormat("yyyy-MM-dd");
        dateF.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        if(!session.sUserBST().equals(String.valueOf(-999))) {
            txt_seafarer.setText("BST : " + session.sUserBST());
        } else {
            txt_seafarer.setText("BST : - ");
        }
        dob.setText(dateF.format(currentLocalTimes));
        serverDate = dateF2.format(currentLocalTimes);

        spinnerAdapter1 = ArrayAdapter.createFromResource(this, R.array.jk_array, android.R.layout.simple_spinner_item);
        spinnerAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(spinnerAdapter1);
        spinnerAdapter2 = ArrayAdapter.createFromResource(this, R.array.agama_array, android.R.layout.simple_spinner_item);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(spinnerAdapter2);
        spinnerAdapter3 = ArrayAdapter.createFromResource(this, R.array.marital_array, android.R.layout.simple_spinner_item);
        spinnerAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s3.setAdapter(spinnerAdapter3);

        //int spinnerPosition2 = spinnerAdapter1.getPosition("Gunung Sahari");
        //s1.setSelection(spinnerPosition2);

        myCalendar = Calendar.getInstance();

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

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, /*android.R.style.Theme_Holo_Light_Dialog,*/ date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                dateNo = 1;
            }
        });

        btnSave = (FancyButton) findViewById(R.id.btn_save);
        if(!session.sUserBST16().equals("62")){
            btnSave.setVisibility(View.INVISIBLE);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Anda yakin data yang dimasukkan sudah benar ?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

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
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

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
                Intent i = new Intent(ProfileActivity.this,
                        MainActivity.class);
                i.putExtra("fragment", 3);
                startActivity(i);
                finish();
            }
        });

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        btnAnamnese = (Button) findViewById(R.id.btn_anamnese);
        btnAnamnese.setTypeface(font2);
        //buttonUpload = (FloatingActionButton) findViewById(R.id.buttonUpload);

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btnAnamnese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AnamneseActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //buttonUpload.setVisibility(View.GONE);

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
    }

    private void showProgressDialog(boolean cancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ProfileActivity.this);
            mProgressDialog.setMessage(ProfileActivity.this.getString(R.string.loading));
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
            dob.setText(sdf.format(myCalendar.getTime()));
            serverDate = newFormat.format(myCalendar.getTime());
            startdate = newFormat.format(myCalendar.getTime());
        } else {
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
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?account_profile=" + sbst + "&jwt=" + session.sJWT() + "&phone=" + session.phone());

        Log.d("loadDetail debug",URL_INPUT+"?account_profile=" + sbst + "&jwt=" + session.sJWT() + "&phone=" + session.phone());
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
                    URL_INPUT+"?account_profile=" + sbst + "&jwt=" + session.sJWT() + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

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
            //showMessage("msg",response.toString());
            photo.setVisibility(View.VISIBLE);
            boolean error = response.getBoolean("error");
            if (!error) {
                hideProgressDialog();
                //String uid = response.getString("uid");
                String returnmessage = response.getString("msg");
                //JSONArray feedArray = response.getJSONArray("user");
                //int rows = feedArray.length();
                //if (rows > 0) {
                    //for (int i = 0; i < feedArray.length(); i++) {
                    //    JSONObject feedObj = (JSONObject) feedArray.get(i);

                        JSONObject feedObj = response.getJSONObject("user");

                        seafarer_status = feedObj.getInt("status");
                        seafarer_id = feedObj.getInt("seafarer_id");

                        txt_seafarer.setText("BST : " + feedObj.getString("seafarer_bst").substring(0,10));
                        bst.setText(feedObj.getString("seafarer_bst").substring(10));

                        nama.setText(feedObj.getString("seafarer_name"));
                        dob.setText(feedObj.getString("seafarer_dob"));
                        pob.setText(feedObj.getString("seafarer_pob"));
                        ktp.setText(feedObj.getString("seafarer_ktp"));
                        alamat.setText(feedObj.getString("seafarer_address"));
                        company.setText(feedObj.getString("company"));
                        String spinner_religion = "Islam";
                        String spinner_sex;
                        String spinner_marital = getResources().getString(R.string.marital2);
                        String sex = feedObj.getString("seafarer_sex");

                        serverDate = feedObj.getString("seafarer_dob");
                        switch (sex) {
                            case "female":
                                spinner_sex = getResources().getString(R.string.female);
                                break;
                            default:
                                spinner_sex = getResources().getString(R.string.male);
                                break;
                        }

                        String religion = feedObj.getString("seafarer_religion");
                        switch (religion) {
                            /*case "islam":
                                spinner_religion = "Islam";
                                break;*/
                            case "protestan":
                                spinner_religion = "Kristen Protestan";
                                break;
                            case "katolik":
                                spinner_religion = "Kristen Katolik";
                                break;
                            case "hindu":
                                spinner_religion = "Hindu";
                                break;
                            case "budha":
                                spinner_religion = "Budha";
                                break;
                            case "alirankepercayaan":
                                spinner_religion = "Aliran Kepercayaan";
                                break;
                            default:
                                spinner_religion = "Islam";
                                break;
                        }

                        String marital = feedObj.getString("seafarer_marital");
                        switch (marital) {
                            case "kawin":
                                spinner_marital = getResources().getString(R.string.marital1);
                                break;
                            default:
                                spinner_religion = getResources().getString(R.string.marital2);
                                break;
                        }

                        session.setUserBST16(feedObj.getString("seafarer_bst"));
                        session.setJWT(feedObj.getString("token"));
                        session.setSeafarerName(feedObj.getString("seafarer_name"));

                        if (spinnerAdapter1 != null) {
                            int spinnerPosition1 = spinnerAdapter1.getPosition(spinner_sex);
                            s1.setSelection(spinnerPosition1);
                        }
                        if (spinnerAdapter2 != null) {
                            int spinnerPosition2 = spinnerAdapter2.getPosition(spinner_religion);
                            s2.setSelection(spinnerPosition2);
                        }
                        if (spinnerAdapter3 != null) {
                            int spinnerPosition3 = spinnerAdapter3.getPosition(spinner_marital);
                            s3.setSelection(spinnerPosition3);
                        }

                        /*if (spinnerAdapter1 == null) {
                            isLoading = true;
                            handlerTask.run();
                        }*/
                        Glide.with(getApplicationContext()).load("http://bkkp.dephub.go.id/image.php?file=" + feedObj.getString("seafarer_photo")).into(photo);
                //showMessage("Info","Jika data pelaut yang ditampilkan tidak sesuai, mohon menghubungi BKKP dengan menginfokan nomor BST Anda dan agar tidak mengupdate data pelaut ini.");

                    //}
                    //} else {}
                //}
                swipeRefreshLayout.setRefreshing(false);
            } else {
                //String uid = response.getString("uid");
                String returnmessage = response.getString("error_msg");
                Toast.makeText(getApplicationContext(), returnmessage, Toast.LENGTH_LONG).show();
                if(!session.sDob().equals("")){
                    dob.setText(session.sDob());
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

    private boolean validateKTP() {
        if (ktp.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.id_mandatory, Toast.LENGTH_LONG).show();
            requestFocus(ktp);
            return false;
        } else if (ktp.getText().toString().trim().length() < 16) {
            Toast.makeText(getApplicationContext(), R.string.id_16digit, Toast.LENGTH_LONG).show();
            requestFocus(ktp);
            return false;
        } else {
        }
        return true;
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
        if (!validateKTP()) {
            return;
        }
        if (!validateDob()) {
            return;
        }

        final String name = nama.getText().toString();
        final String no_ktp = ktp.getText().toString().trim();
        final String tmp_lahir = pob.getText().toString();
        final String jk = s1.getSelectedItem().toString();
        final String agama = s2.getSelectedItem().toString();
        final String marital = s3.getSelectedItem().toString();
        final String addr = alamat.getText().toString();
        final String last6_bst = bst.getText().toString().trim();
        final String comp = company.getText().toString();

        // Check for empty data in the form
        if (!name.isEmpty() && !tmp_lahir.isEmpty() && !addr.isEmpty()) {
            new isOnline().execute("");
            handler7.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(internetAvailable){
                        //showMessage("bst",last6_bst);
                        showProgressDialog(false);
                        saveData(name, no_ktp, tmp_lahir, jk, agama, marital, addr, last6_bst, comp);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                    }
                }
            }, 2000);
            //}
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    R.string.profile_mandatory, Toast.LENGTH_LONG)
                    .show();
        }
        //Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private void saveData(String name, String no_ktp, String tmp_lahir, String jk, String agama, String marital, String addr, String last6_bst, String comp) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?save_profile=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&name=" + name + "&no_ktp=" + no_ktp + "&tmp_lahir=" + tmp_lahir + "&tgl_lahir=" + serverDate + "&jk=" + jk + "&agama=" + agama + "&marital=" + marital + "&addr=" + addr + "&last6_bst=" + last6_bst + "&company=" + comp + "&phone=" + session.phone());

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
                    URL_INPUT+"?save_profile=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&name=" + name + "&no_ktp=" + no_ktp + "&tmp_lahir=" + tmp_lahir + "&tgl_lahir=" + serverDate + "&jk=" + jk + "&agama=" + agama + "&marital=" + marital + "&addr=" + addr + "&last6_bst=" + last6_bst + "&company=" + comp + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

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
                    if(imgState) {
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
                    }
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
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(final String id_seafarer) {
        //menampilkan progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_INPUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Add", jObj.toString());
                                //Toast.makeText(ProfileActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                kosong();
                                loadDetail(session.sUserBST());

                                handler7.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), PendaftaranActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 2000);
                            } else {
                                Toast.makeText(ProfileActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();

                        //menampilkan toast
                        Toast.makeText(ProfileActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put(KEY_IMAGE, getStringImage(decoded));
                params.put("seafarer_id", id_seafarer);

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
                imgState = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void kosong() {
        photo.setImageResource(0);
        photo.setVisibility(View.GONE);
        imgState = false;
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        photo.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

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
                int timeoutMs = 5000;
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
