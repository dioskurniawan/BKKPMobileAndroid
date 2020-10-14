package id.bkkp.general;

/**
 * Created by Joko on 16/05/2018.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ProgressDialog mProgressDialog;

    private Toolbar toolbar;
    private EditText inputPhone, inputPassword;
    private TextInputLayout inputLayoutPhone, inputLayoutPassword;
    private Button btnSignIn, btnReg, btnForgot;
    private SessionManager session;
    private static final int MY_PERMISSIONS_REQUEST = 0;
    final Handler handler7 = new Handler();
    final Handler handler4 = new Handler();
    int n4 = 0;
    boolean isLoading4 = false;
    //private NetworkLocator netLoc1;
    private String personName, personEmail, personId;
    String name;
    String password;
    boolean internetAvailable = false;

    public static String URL_LOGIN = "https://bkkp.dephub.go.id/bkkpapi/loginapp_dev.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session manager
        session = new SessionManager(getApplicationContext());
        session.setLang(session.sLang());
        //session.setLang(Locale.getDefault().getLanguage());
        //Toast.makeText(getApplicationContext(), session.sLang(), Toast.LENGTH_LONG).show();
        new isOnline().execute("");
        // Check if user is already logged in or not

        toolbar = (Toolbar) findViewById(R.id.toolbar_sign);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        //title.setText(getResources().getString(R.string.labelloginuser));

        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignIn = (Button) findViewById(R.id.btn_signin);
        btnReg = (Button) findViewById(R.id.btn_register);
        btnForgot = (Button) findViewById(R.id.btn_forgetpass);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            inputLayoutPhone.setVisibility(View.GONE);
            inputLayoutPassword.setVisibility(View.GONE);
            inputPhone.setVisibility(View.GONE);
            inputPassword.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.GONE);
            btnReg.setVisibility(View.GONE);
            btnForgot.setVisibility(View.GONE);
            new isOnline().execute("");
            handler7.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /*if(internetAvailable){
                        new reValidatePassword().execute("pass");
                    } else {*/
                        //finish();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("fragment", 0);
                        startActivity(intent);
                        finish();
                    //}
                }
            }, 2000);
            //}
            Log.d("bst = "+session.sUserBST()+"jwttokenlogin2 =",session.sJWT());
        }
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();*/
                showProgressDialog();
                submitForm();
                /*runOnUiThread(new Runnable(){
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
                });*/

            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);

        MenuItem client = menu.findItem(R.id.action_client);
        client.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public void onStart() {
        super.onStart();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(new ContextThemeWrapper(LoginActivity.this, android.R.style.Theme_Holo_Light_Dialog));
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

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validatePhone()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        name = inputPhone.getText().toString().trim();
        name = name.replaceAll(" ","");
        password = inputPassword.getText().toString().trim();

        // Check for empty data in the form
        if (!name.isEmpty() && !password.isEmpty()) {
            // login user
            /*if(!isInternetOn()) {
                Toast.makeText(getApplicationContext(), R.string.no_internet1, Toast.LENGTH_LONG).show();
            } else {*/
            new isOnline().execute("");
            handler7.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(internetAvailable){
                        checkLogin(name, password);
                    } else {
                        hideProgressDialog();
                        Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                    }
                }
            }, 2000);
            //}
        } /*else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(), R.string.msg_txt, Toast.LENGTH_LONG).show();
        }*/
        //Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private boolean validatePhone() {
        if (inputPhone.getText().toString().trim().isEmpty()) {
            hideProgressDialog();
            inputLayoutPhone.setError(getString(R.string.phone_required1));
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            hideProgressDialog();
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
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

    private void checkLogin(final String sPhone, final String sPasswd) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        //mProgressDialog.setMessage("Logging in ...");
        //showProgressDialog();
        //Toast.makeText(getApplicationContext(), email + "!" + password, Toast.LENGTH_LONG).show();
        new sendData().execute(sPhone + "!" + sPasswd);
    }

    /*final Runnable handlerTask4 = new Runnable() {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            n4++;
            //Log.d("debug","n4="+n4);
            if (n4 >= 5) {
                if (isLoading4) {
                    checkLogin(name, password);
                    isLoading4 = false;
                    n4 = 0;
                    Thread.currentThread().interrupt();
                }
            }
            handler4.postDelayed(handlerTask4, 1000);
        }
    };*/

    private class sendData extends AsyncTask<String, Integer, JSONObject> {
        //private NetworkLocator netLoc1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //netLoc1 = new NetworkLocator(getApplicationContext());
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String userpass = arg0[0];
            String extractString = userpass;
            String[] result = extractString.split("!");

            try {
                //sImei = netLoc1.findDeviceID();
                //send to server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("phone", result[0]);
                jsonObject.put("password", result[1]);
                jsonObject.put("lang", session.sLang());

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
                hideProgressDialog();
                //Log.d("errordebug",json.toString());
                //JSONObject jObj = new JSONObject(json);
                boolean error = json.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
                    hideProgressDialog();

                    //String uid = json.getString("uid");
                    String returnmessage = json.getString("msg");
                    JSONObject account = json.getJSONObject("user");
                    String user_last_login = account.getString("last_login");
                    String user_passwd = account.getString("password");
                    String user_phone = account.getString("phone");
                    String token = account.getString("token");
                    //String bst16 = account.getString("seafarer_bst");

                    session.setLogin(true);
                    //session.setUserBST(uid);
                    session.setPasswd(user_passwd);
                    session.setLastLogin(user_last_login);
                    session.setPhone(user_phone);
                    session.setJWT(token);
                    Log.d("bst = "+session.sUserBST()+"jwttokenlogin1 =",session.sJWT());

                    //session.setUserBST16(bst16);

                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("fragment", 3);
                    startActivity(intent);
                    finish();
                } else {
                    // Error in login. Get the error message
                    hideProgressDialog();
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
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

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("fragment", 3);
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
                            LoginActivity.class);
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
                int timeoutMs = 1500;
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
                            MainActivity.class);
                    i.putExtra("fragment", 3);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}

