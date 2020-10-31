package id.bkkp.general;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import mehdi.sakout.fancybuttons.FancyButton;

public class PopActivity extends Activity {

    int intentFragment;
    private SessionManager session;
    private EditText inputBST;
    String bst10;
    private ProgressDialog mProgressDialog;
    final Handler handler7 = new Handler();
    public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
    boolean internetAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        session = new SessionManager(getApplicationContext());
        session.setBstExist(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {// to avoid the NullPointerException
            intentFragment = bundle.getInt("num");
        }
        LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout1);
        inputBST = (EditText) findViewById(R.id.bst10);
        if(!session.sUserBST().equals(String.valueOf(-999))) {
            inputBST.setText(session.sUserBST());
        }

        FancyButton btnCancel = (FancyButton) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                MainActivity.activityMain.finish();
                Intent intent = new Intent(PopActivity.this, MainActivity.class);
                intent.putExtra("fragment", 3);
                startActivity(intent);
            }
        });
        FancyButton btnSave = (FancyButton) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateBST()) {
                    return;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(PopActivity.this);
                builder.setMessage(R.string.confirm_txt);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new isOnline().execute("");
                        handler7.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(internetAvailable){
                                    bst10 = inputBST.getText().toString().trim();
                                    showProgressDialog(false);
                                    changeBst();
                                } else {
                                    Toast.makeText(PopActivity.this, R.string.no_internet2, Toast.LENGTH_LONG).show();
                                }
                            }
                        }, 2000);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        if(intentFragment == 1) {
            layout1.setVisibility(View.VISIBLE);
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.4));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -17;

        getWindow().setAttributes(params);
    }

    private boolean validateBST() {
        if (inputBST.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.err_msg_bst, Toast.LENGTH_LONG).show();
            //inputLayoutBST.setError(getString(R.string.err_msg_bst));
            requestFocus(inputBST);
            return false;
        } else if (inputBST.getText().toString().trim().length() < 10) {
            Toast.makeText(getApplicationContext(), R.string.enter_bst10, Toast.LENGTH_LONG).show();
            //inputLayoutBST.setError(R.string.enter_bst10);
            requestFocus(inputBST);
            return false;
        } else if (!inputBST.getText().toString().trim().substring(0,2).equals("62")) {
            Toast.makeText(getApplicationContext(), R.string.err_msg_bst2, Toast.LENGTH_LONG).show();
            //inputLayoutBST.setError(R.string.err_msg_bst2);
            requestFocus(inputBST);
            return false;
        } else {
            //inputLayoutBST.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void changeBst() {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?change_bst=" + bst10 + "&jwt=" + session.sJWT() + "&phone=" + session.phone());

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
                    URL_INPUT+"?change_bst=" + bst10 + "&jwt=" + session.sJWT() + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

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
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            } else {
                String errorMsg = response.getString("error_msg");
                Toast.makeText(PopActivity.this,
                        errorMsg, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }

    private void showProgressDialog(boolean cancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(PopActivity.this);
            mProgressDialog.setMessage(getResources().getString(R.string.loading2));
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
