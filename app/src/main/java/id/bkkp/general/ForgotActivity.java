package id.bkkp.general;

/**
 * Created by Joko on 16/05/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import mehdi.sakout.fancybuttons.FancyButton;

public class ForgotActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    SmsBroadcastReceiver mSmsBroadcastReceiver;
    private int RESOLVE_HINT = 2;
    EditText inputMobileNumber, inputOtp;
    Button btnGetOtp, btnVerifyOtp;
    ConstraintLayout layoutInput, layoutVerify;
    LinearLayout layoutPass;

    private static final String TAG = "ForgotActivity";
    private ProgressDialog mProgressDialog;

    private Toolbar toolbar;
    private EditText inputPhone2, inputPhone, inputPasswd, inputPasswd2;
    private TextInputLayout inputLayoutBST, inputLayoutPasswd, inputLayoutPasswd2;
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
    Calendar myCalendar;
    private String serverDate;
    int dateNo;
    Boolean reqStatus = false;

    public static String URL_LOGIN = "https://bkkp.dephub.go.id/bkkpapi/loginapp_dev.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        // Session manager
        session = new SessionManager(getApplicationContext());

        //new isOnline().execute("");
        // Check if user is already logged in or not

        toolbar = (Toolbar) findViewById(R.id.toolbar_reg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        //title.setText(getResources().getString(R.string.labelloginuser));

        inputLayoutBST = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputLayoutPasswd = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutPasswd2 = (TextInputLayout) findViewById(R.id.input_layout_password2);
        inputPhone2 = (EditText) findViewById(R.id.input_phone);
        inputPasswd = (EditText) findViewById(R.id.input_password);
        inputPasswd2 = (EditText) findViewById(R.id.input_password2);
        inputPhone = (EditText) findViewById(R.id.editTextInputMobile);
        //btnSignIn = (Button) findViewById(R.id.btn_signin);

        inputPhone2.addTextChangedListener(new MyTextWatcher(inputPhone2));
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
        //getHintPhoneNumber();
        btnGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(reqStatus){
                    Toast.makeText(ForgotActivity.this, R.string.loading2, Toast.LENGTH_LONG).show();
                } else {
                    if (!validatePhone2()) {
                        return;
                    }
                /*if (!validatePhone()) {
                    return;
                }*/
                    // Call server API for requesting OTP and when you got success start
                    // SMS Listener for listing auto read message lsitner
                    new isOnline().execute("");
                    showProgressDialog();
                    handler7.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (internetAvailable) {
                                hideProgressDialog();
                                startSMSListener();
                                //String phone = inputMobileNumber.getText().toString().trim();
                                String phone = inputPhone2.getText().toString().trim();
                                session.setPhone(phone);
                                //session.setUserBST(phone);
                                sendHash(session.sHash());
                                reqStatus = true;
                            } else {
                                hideProgressDialog();
                                Toast.makeText(ForgotActivity.this, R.string.no_internet2, Toast.LENGTH_LONG).show();
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
                showProgressDialog();
                handler7.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(internetAvailable){
                            String otp = inputOtp.getText().toString().trim();
                            confirmOTP(otp);
                        } else {
                            hideProgressDialog();
                            Toast.makeText(ForgotActivity.this, R.string.no_internet2, Toast.LENGTH_LONG).show();
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
                showProgressDialog();
                handler7.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(internetAvailable){
                            hideProgressDialog();
                            String passwd = inputPasswd.getText().toString().trim();
                            sendPass(passwd);
                        } else {
                            hideProgressDialog();
                            Toast.makeText(ForgotActivity.this, R.string.no_internet2, Toast.LENGTH_LONG).show();
                        }
                    }
                }, 2000);
            }
        });
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
        Toast.makeText(this, "Time out, please resend", Toast.LENGTH_LONG).show();
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                //Toast.makeText(ForgotActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotActivity.this, "Error", Toast.LENGTH_LONG).show();
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

    private void sendHash(String hashString) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        //Cache.Entry entry2 = cache2.get(URL_LOGIN+"?reset=" + hashString + "&bst=" + session.sUserBST() + "&phone=" + session.phone());
        Cache.Entry entry2 = cache2.get(URL_LOGIN+"?reset=" + hashString + "&phone=" + session.phone());

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
                    URL_LOGIN+"?reset=" + hashString + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

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
                Toast.makeText(ForgotActivity.this, returnmessage, Toast.LENGTH_LONG).show();
                Log.d("BKKP", "BKKP success register: " + feedArray.toString());
                layoutInput.setVisibility(View.GONE);
                layoutVerify.setVisibility(View.VISIBLE);
            } else {
                String errorMsg = response.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
                session.setLogin(true);
                Toast.makeText(getApplicationContext(),
                        response.getString("msg"), Toast.LENGTH_LONG).show();
                layoutVerify.setVisibility(View.GONE);
                layoutPass.setVisibility(View.VISIBLE);
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
                session.setLogin(true);
                Toast.makeText(getApplicationContext(),
                        response.getString("msg"), Toast.LENGTH_LONG).show();
                session.clearAllData();
                handler7.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(new ContextThemeWrapper(ForgotActivity.this, android.R.style.Theme_Holo_Light_Dialog));
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
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

    private boolean validatePhone2() {
        if (inputPhone2.getText().toString().trim().isEmpty()) {
            requestFocus(inputPhone2);
            Toast.makeText(getApplicationContext(), "Nomor telepon harus diisi", Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone2.getText().toString().trim().length() < 10) {
            requestFocus(inputPhone2);
            Toast.makeText(getApplicationContext(), "Inputkan nomor telepon yang valid", Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone2.getText().toString().trim().substring(0,3).equals("+62")) {
            inputPhone2.setText(inputPhone2.getText().toString().trim().replace("+62","0"));
            //return true;
        } else if (!inputPhone2.getText().toString().trim().substring(0,2).equals("08")) {
            requestFocus(inputPhone2);
            Toast.makeText(getApplicationContext(), "Inputkan nomor telepon yang valid", Toast.LENGTH_LONG).show();
            return false;
        } else {
            //inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        if (inputPhone.getText().toString().trim().isEmpty()) {
            requestFocus(inputPhone);
            Toast.makeText(getApplicationContext(), "Nomor telepon harus diisi", Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone.getText().toString().trim().length() < 10) {
            requestFocus(inputPhone);
            Toast.makeText(getApplicationContext(), "Inputkan nomor telepon yang valid", Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone.getText().toString().trim().substring(0,3).equals("+62")) {
            inputPhone.setText(inputPhone.getText().toString().trim().replace("+62","0"));
            //return true;
        } else if (!inputPhone.getText().toString().trim().substring(0,2).equals("08")) {
            requestFocus(inputPhone);
            Toast.makeText(getApplicationContext(), "Inputkan nomor telepon yang valid", Toast.LENGTH_LONG).show();
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

    public void showMessage(String title, String message)
    {
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
                            LoginActivity.class);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}

