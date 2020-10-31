package id.bkkp.general;

/**
 * Created by Joko on 16/05/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import mehdi.sakout.fancybuttons.FancyButton;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    SmsBroadcastReceiver mSmsBroadcastReceiver;
    private int RESOLVE_HINT = 2;
    EditText inputMobileNumber, inputOtp;
    Button btnGetOtp, btnVerifyOtp;
    ConstraintLayout layoutInput, layoutVerify;
    LinearLayout layoutPass;

    private static final String TAG = "RegisterActivity";
    private ProgressDialog mProgressDialog;

    private Toolbar toolbar;
    //private EditText inputBST;
    private EditText inputEmail, inputPhone, inputPasswd, inputPasswd2;
    //private TextInputLayout inputLayoutBST
    private TextView txtEmail, textView3;
    private TextInputLayout inputLayoutEmail, inputLayoutDob, inputLayoutPasswd, inputLayoutPasswd2;
    private FancyButton btnOk;
    private SessionManager session;
    private static final int MY_PERMISSIONS_REQUEST = 0;
    final Handler handler7 = new Handler();
    final Handler handler4 = new Handler();
    int n4 = 0;
    boolean isLoading4 = false;
    String name;
    String phone;
    boolean internetAvailable = false;
    private EditText inputDate;
    Calendar myCalendar;
    private String serverDate;
    int dateNo;
    Boolean reqStatus = false;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    public static String URL_LOGIN = "https://bkkp.dephub.go.id/bkkpapi/loginapp_dev.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Session manager
        session = new SessionManager(getApplicationContext());

        builder=new AlertDialog.Builder(new ContextThemeWrapper(RegisterActivity.this, android.R.style.Theme_Black_NoTitleBar));

        new isOnline().execute("");
        // Check if user is already logged in or not

        toolbar = (Toolbar) findViewById(R.id.toolbar_reg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        //title.setText(getResources().getString(R.string.labelloginuser));

        txtEmail = (TextView) findViewById(R.id.txtEmail);
        textView3 = (TextView) findViewById(R.id.textView3);
        //inputLayoutBST = (TextInputLayout) findViewById(R.id.input_layout_bst);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutDob = (TextInputLayout) findViewById(R.id.input_layout_datetime1);
        inputLayoutPasswd = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutPasswd2 = (TextInputLayout) findViewById(R.id.input_layout_password2);
        //inputBST = (EditText) findViewById(R.id.input_bst);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPasswd = (EditText) findViewById(R.id.input_password);
        inputPasswd2 = (EditText) findViewById(R.id.input_password2);
        inputPhone = (EditText) findViewById(R.id.editTextInputMobile);
        //btnSignIn = (Button) findViewById(R.id.btn_signin);

        Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        Date currentLocalTimes = cald.getTime();
        DateFormat dateF = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat dateF2 = new SimpleDateFormat("yyyy-MM-dd");
        dateF.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));

        inputDate = (EditText) findViewById(R.id.seafarer_dob);
        inputDate.setText(dateF.format(currentLocalTimes));
        serverDate = dateF2.format(currentLocalTimes);
        session.setDob(serverDate);

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

        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, /*android.R.style.Theme_Holo_Light_Dialog,*/ date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                dateNo = 1;
            }
        });
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            //inputLayoutBST.setVisibility(View.GONE);
            inputLayoutEmail.setVisibility(View.GONE);
            //inputBST.setVisibility(View.GONE);
            inputEmail.setVisibility(View.GONE);
            inputPhone.setVisibility(View.GONE);
            //btnSignIn.setVisibility(View.GONE);
            new isOnline().execute("");
            handler7.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("fragment", 0);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
        //inputBST.addTextChangedListener(new MyTextWatcher(inputBST));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));

        initViews();
        // init broadcast receiver
        mSmsBroadcastReceiver = new SmsBroadcastReceiver();

        //set google api client for hint request
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        mSmsBroadcastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);

        // get mobile number from phone
        getHintPhoneNumber();
        btnGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                /*if (!validateBST()) {
                    return;
                }*/
                if(reqStatus){
                    Toast.makeText(RegisterActivity.this, R.string.loading2, Toast.LENGTH_LONG).show();
                } else {
                    if (!validateEmail()) {
                        return;
                    }
                    /*if (!validateDob()) {
                        return;
                    }*/
                    if (!validatePhone()) {
                        return;
                    }
                    // Call server API for requesting OTP and when you got success start
                    // SMS Listener for listing auto read message lsitner
                    new isOnline().execute("");
                    showProgressDialog(false);
                    handler7.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (internetAvailable) {
                                hideProgressDialog();
                                startSMSListener();
                                String phone = inputMobileNumber.getText().toString().trim();
                                //String bst = inputBST.getText().toString().trim();
                                String email = inputEmail.getText().toString().trim();
                                session.setPhone(phone);
                                //session.setUserBST(bst);
                                session.setEmail(email);
                                sendHash(session.sHash());
                                reqStatus = true;
                            } else {
                                hideProgressDialog();
                                Toast.makeText(RegisterActivity.this, R.string.no_internet2, Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 2000);
                }
            }
        });
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // Call server API for requesting OTP and when you got success start
                // SMS Listener for listing auto read message lsitner
                new isOnline().execute("");
                showProgressDialog(false);
                handler7.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(internetAvailable){
                            String otp = inputOtp.getText().toString().trim();
                            confirmOTP(otp);
                        } else {
                            hideProgressDialog();
                            Toast.makeText(RegisterActivity.this, R.string.no_internet2, Toast.LENGTH_LONG).show();
                        }
                    }
                }, 2000);
            }
        });

        btnOk = (FancyButton) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePass1()) {
                    return;
                }
                if (!validatePass2()) {
                    return;
                }
                new isOnline().execute("");
                showProgressDialog(false);
                handler7.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(internetAvailable){
                            hideProgressDialog();
                            String passwd = inputPasswd.getText().toString().trim();
                            sendPass(passwd);
                        } else {
                            hideProgressDialog();
                            Toast.makeText(RegisterActivity.this, R.string.no_internet2, Toast.LENGTH_LONG).show();
                        }
                    }
                }, 2000);
            }
        });
        /*btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runOnUiThread(new Runnable(){
                    public void run() {

                        new isOnline().execute("");

                        handler7.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(internetAvailable){
                                    submitForm();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                                }
                            }
                        }, 2000);
                    }
                });

            }
        });*/
    }

    private void initViews() {
        inputMobileNumber = findViewById(R.id.editTextInputMobile);
        inputOtp = findViewById(R.id.editTextOTP);
        btnGetOtp = findViewById(R.id.buttonGetOTP);
        btnVerifyOtp = findViewById(R.id.buttonVerify);
        layoutInput = findViewById(R.id.getOTPLayout);
        layoutVerify = findViewById(R.id.verifyOTPLayout);
        layoutPass = findViewById(R.id.createPassLayout);
    }

    private void updateLabel(final int numDate) {
        String startdate = null;
        String enddate = null;
        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        //if(numDate == 1) {
            inputDate.setText(sdf.format(myCalendar.getTime()));
            serverDate = newFormat.format(myCalendar.getTime());
            startdate = newFormat.format(myCalendar.getTime());
            session.setDob(serverDate);
        //} else {
        //}
    }

    @Override public void onConnected(@Nullable Bundle bundle) {

    }

    @Override public void onConnectionSuspended(int i) {

    }

    @Override public void onOtpReceived(String otp) {
        //Toast.makeText(this, "Otp Received " + otp, Toast.LENGTH_LONG).show();
        String otpCode = otp.replace("<#> ", "");
        inputOtp.setText(otpCode.substring(0, 4));
    }

    @Override public void onOtpTimeout() {
        Toast.makeText(this, "OTP Timed out", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();

    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                //Toast.makeText(RegisterActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId();  <-- will need to process phone number string
                    inputMobileNumber.setText(credential.getId());
                }

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /*void POSTVOLLEY (){

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("hash", hashString);
            jsonBody.put("bst", session.sUserBST());
            jsonBody.put("phone", session.phone());
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY response : "+response, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    /*private void checkOTPExptime() {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_LOGIN+"?change_bst=" + bst10 + "&jwt=" + session.sJWT() + "&phone=" + session.phone());

        //Toast.makeText(mContext, "page: " + URL_INPUT+"?pelaut=", Toast.LENGTH_SHORT).show();
        if (entry2 != null) {
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
                    URL_LOGIN+"?change_bst=" + bst10 + "&jwt=" + session.sJWT() + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

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
            hideProgressDialog();
            boolean error = response.getBoolean("error");
            if (!error) {
                //JSONArray feedArray = response.getJSONArray("bst");
                //String returnmessage = response.getString("msg");
                //Toast.makeText(RegisterActivity.this, returnmessage, Toast.LENGTH_LONG).show();
                Log.d("BKKP", "BST changed successfully: ");// + feedArray.toString());

                session.setUserBST(bst10);
                finish();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            } else {
                String errorMsg = response.getString("error_msg");
                Toast.makeText(RegisterActivity.this,
                        errorMsg, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }*/

    private void sendHash(String hashString) {
        showMessage("",getResources().getString(R.string.loading2));
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_LOGIN+"?hash=" + hashString + "&phone=" + session.phone() + "&email=" + session.email());

        //Toast.makeText(mContext, "page: " + URL_INPUT+"?pelaut=", Toast.LENGTH_SHORT).show();
        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonFeedHash(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_LOGIN+"?hash=" + hashString + "&phone=" + session.phone() + "&email=" + session.email(), null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("BKKP debug1", "Response: " + response.toString());
                    parseJsonFeedHash(response);
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

    private void parseJsonFeedHash(JSONObject response) {
        try {
            boolean error = response.getBoolean("error");
            if (!error) {
                JSONArray feedArray = response.getJSONArray("hash");
                String returnmessage = response.getString("msg");
                //Toast.makeText(RegisterActivity.this, returnmessage, Toast.LENGTH_LONG).show();
                Log.d("BKKP", "BKKP success register: " + feedArray.toString());
                //layoutInput.setVisibility(View.GONE);
                inputPhone.setEnabled(false);
                textView3.setVisibility(View.GONE);
                txtEmail.setVisibility(View.GONE);
                inputLayoutEmail.setVisibility(View.GONE);
                btnGetOtp.setVisibility(View.GONE);
                layoutVerify.setVisibility(View.VISIBLE);
                btnVerifyOtp.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            } else {
                alertDialog.dismiss();
                reqStatus = false;
                String errorMsg = response.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }

      /*int rows = feedArray.length();
      if(rows > 0){
        txtEmpty.setVisibility(View.GONE);
      } else {
        txtEmpty.setVisibility(View.VISIBLE);
      }

      for (int i = 0; i < feedArray.length(); i++) {
        JSONObject feedObj = (JSONObject) feedArray.get(i);

        GetItem item = new GetItem();
        item.setId(feedObj.getInt("disposisi_id"));
        item.setMasterId(feedObj.getInt("sm_id"));
        item.setName(feedObj.getString("no_agenda"));
        item.setContent(feedObj.getString("ditujukan_kepada"));

        item.setTimeStamp(feedObj.getString("disposisi_date"));

        try {
        } catch (Exception e) {
          Log.d("BKKP", "BKKP debug insert disposisi err3: " + e.getMessage());
        }
      }*/
        } catch (JSONException e) {
            e.printStackTrace();
            alertDialog.dismiss();
        }
    }

    private void confirmOTP(String otp) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_LOGIN+"?otp=" + otp + "&phone=" + session.phone());

        //Toast.makeText(mContext, "page: " + URL_INPUT+"?pelaut=", Toast.LENGTH_SHORT).show();
        if (entry2 != null) {
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
                    URL_LOGIN+"?otp=" + otp + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

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
            hideProgressDialog();
            boolean error = response.getBoolean("error");

            if (!error) {
                hideProgressDialog();

                /*String uid = json.getString("uid");

                session.setUserBST(uid);*/
                //session.setLogin(true);
                Toast.makeText(getApplicationContext(),
                        response.getString("msg"), Toast.LENGTH_LONG).show();
                layoutInput.setVisibility(View.GONE);
                layoutVerify.setVisibility(View.GONE);
                btnVerifyOtp.setVisibility(View.GONE);
                layoutPass.setVisibility(View.VISIBLE);
                /*handler7.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);*/
            } else {
                // Error in login. Get the error message
                hideProgressDialog();
                String errorMsg = response.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendPass(String passwd) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_LOGIN+"?createpass=" + passwd + "&phone=" + session.phone());

        //Toast.makeText(mContext, "page: " + URL_INPUT+"?pelaut=", Toast.LENGTH_SHORT).show();
        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonFeedPass(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_LOGIN+"?createpass=" + passwd + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("BKKP debug1", "Response: " + response.toString());
                    parseJsonFeedPass(response);
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

    private void parseJsonFeedPass(JSONObject response) {

        try {
            hideProgressDialog();
            boolean error = response.getBoolean("error");

            if (!error) {
                hideProgressDialog();
                //session.setLogin(true);
                Toast.makeText(getApplicationContext(),
                        response.getString("msg"), Toast.LENGTH_LONG).show();
                session.clearAllData();
                handler7.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            } else {
                // Error in login. Get the error message
                hideProgressDialog();
                String errorMsg = response.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showProgressDialog(Boolean stat) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(new ContextThemeWrapper(RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog));
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(stat);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                /*case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;*/
            }
        }
    }

    /*private boolean validateBST() {
        if (inputBST.getText().toString().trim().isEmpty()) {
            inputLayoutBST.setError(getString(R.string.err_msg_bst));
            requestFocus(inputBST);
            return false;
        } else if (inputBST.getText().toString().trim().length() < 10) {
            inputLayoutBST.setError("Inputkan 10 digit awal nomor BST");
            requestFocus(inputBST);
            return false;
        } else if (!inputBST.getText().toString().trim().substring(0,2).equals("62")) {
            inputLayoutBST.setError("Nomor BST tidak valid");
            requestFocus(inputBST);
            return false;
        } else {
            inputLayoutBST.setErrorEnabled(false);
        }
        return true;
    }*/

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateEmail() {
        if (inputEmail.getText().toString().trim().isEmpty() || !isValidEmail(inputEmail.getText().toString().trim())) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
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
            requestFocus(inputDate);
            Toast.makeText(getApplicationContext(), R.string.age, Toast.LENGTH_LONG).show();
            return false;
        } else {
        }
        return true;
    }

    private boolean validatePhone() {
        if (inputPhone.getText().toString().trim().isEmpty()) {
            requestFocus(inputPhone);
            Toast.makeText(getApplicationContext(), R.string.phone_mandatory, Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone.getText().toString().trim().length() < 10) {
            requestFocus(inputPhone);
            Toast.makeText(getApplicationContext(), R.string.phone_valid, Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone.getText().toString().trim().substring(0,3).equals("+62")) {
            inputPhone.setText(inputPhone.getText().toString().trim().replace("+62","0"));
            //return true;
        } else if (!inputPhone.getText().toString().trim().substring(0,2).equals("08")) {
            requestFocus(inputPhone);
            Toast.makeText(getApplicationContext(), R.string.phone_valid, Toast.LENGTH_LONG).show();
            return false;
        } else {
            //inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePass1() {
        if (inputPasswd.getText().toString().trim().isEmpty()) {
            inputLayoutPasswd.setError(getString(R.string.err_msg_password));
            requestFocus(inputPasswd);
            return false;
        } else if (inputPasswd.getText().toString().trim().length() < 5) {
            inputLayoutPasswd.setError("Inputkan password minimal 5 digit");
            requestFocus(inputPasswd);
            return false;
        } else {
            inputLayoutPasswd.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePass2() {
        if (inputPasswd2.getText().toString().trim().isEmpty()) {
            inputLayoutPasswd2.setError(getString(R.string.err_msg_password_retype));
            requestFocus(inputPasswd2);
            return false;
        } else if (inputPasswd2.getText().toString().trim().length() < 5) {
            inputLayoutPasswd2.setError("Inputkan password minimal 5 digit");
            requestFocus(inputPasswd2);
            return false;
        } else if (!inputPasswd2.getText().toString().trim().equals(inputPasswd.getText().toString().trim())) {
            inputLayoutPasswd2.setError("Konfirmasi password harus sama");
            requestFocus(inputPasswd2);
            return false;
        } else {
            inputLayoutPasswd2.setErrorEnabled(false);
        }
        return true;
    }

    /*private class reValidatePassword extends AsyncTask<String, Integer, JSONObject> {
        //private NetworkLocator netLoc1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //netLoc1 = new NetworkLocator(getApplicationContext());
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String value = arg0[0];
            //String extractString = userpass;
            //String[] result = extractString.split("!");

            try {
                //sImei = netLoc1.findDeviceID();
                //send to server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("recheck_client", session.sUserBST());
                jsonObject.put("recheck_pass", session.sPasswd());

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(URL_LOGIN, params);
                Log.d("BKKP", "BKKP debug : jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("BKKP", "BKKP debug err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                //JSONObject jObj = new JSONObject(json);
                boolean error = json.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
                    hideProgressDialog();

                    String uid = json.getString("uid");
                    String returnmessage = json.getString("msg");
                    JSONObject grp = json.getJSONObject("client");
                    //String grp_id = grp.getString("grp_id");
                    String client_name = grp.getString("name");
                    String client_passwd = grp.getString("password");

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    //}

                } else {
                    // Error in login. Get the error message
                    hideProgressDialog();
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                    session.clearAllData();
                    Intent i = new Intent(getApplicationContext(),
                            RegisterActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }*/

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

    public void showMessage(String title, String message) {
        //AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(RegisterActivity.this, android.R.style.Theme_Black_NoTitleBar));
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent i = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}

