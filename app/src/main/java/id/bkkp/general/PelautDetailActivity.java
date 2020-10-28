package id.bkkp.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class PelautDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String intentFragment;
    private ImageView photo;
    private TextView nama_pelaut, no_bst, profile1, profile2, profile3, profile4, profile5, profile6, profile7, cert1, cert2, cert3, cert4, cert5, cert6, cert7;
    static final int MY_PERMISSIONS_1 = 0;
    public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
    boolean internetAvailable;
    final Handler handler7 = new Handler();
    private ProgressDialog mProgressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelaut_detail);
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {// to avoid the NullPointerException
            intentFragment = bundle.getString("cert");
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        nama_pelaut = findViewById(R.id.nama_pelaut);
        no_bst = findViewById(R.id.no_bst);
        photo = findViewById(R.id.imgUser);
        profile1 = findViewById(R.id.profile1);
        profile2 = findViewById(R.id.profile2);
        profile3 = findViewById(R.id.profile3);
        profile4 = findViewById(R.id.profile4);
        profile5 = findViewById(R.id.profile5);
        profile6 = findViewById(R.id.profile6);
        profile7 = findViewById(R.id.profile7);
        cert1 = findViewById(R.id.cert1);
        cert2 = findViewById(R.id.cert2);
        cert3 = findViewById(R.id.cert3);
        cert4 = findViewById(R.id.cert4);
        cert5 = findViewById(R.id.cert5);
        cert6 = findViewById(R.id.cert6);
        cert7 = findViewById(R.id.cert7);
        new isOnline().execute("");
        handler7.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (internetAvailable) {
                    showProgressDialog(false);
                    viewDetail(intentFragment);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                }
            }
        }, 1200);
    }

    private void viewDetail(String cert) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?cert_num=" + cert);

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
            swipeRefreshLayout.setRefreshing(false);
        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_INPUT+"?cert_num=" + cert, null, new Response.Listener<JSONObject>() {

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
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void parseJsonFeed(JSONObject response) {
        try {

            boolean error = response.getBoolean("error");
            if (!error) {
                //String uid = response.getString("uid");
                String returnmessage = response.getString("msg");
                JSONArray feedArray = response.getJSONArray("seafarer");
                int rows = feedArray.length();

                hideProgressDialog();
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    //JSONObject feedObj = response.getJSONObject("queue");

                    no_bst.setText(feedObj.getString("bst"));
                    nama_pelaut.setText(feedObj.getString("profile1"));
                    profile2.setText(feedObj.getString("profile2"));
                    profile3.setText(feedObj.getString("profile3"));
                    profile4.setText(feedObj.getString("profile4"));
                    profile5.setText(feedObj.getString("profile5"));
                    profile6.setText(feedObj.getString("profile6"));
                    profile7.setText(feedObj.getString("profile7"));
                    cert1.setText(feedObj.getString("cert1"));
                    cert2.setText(feedObj.getString("cert2"));
                    cert3.setText(feedObj.getString("cert3"));
                    cert4.setText(feedObj.getString("cert4"));
                    cert5.setText(feedObj.getString("cert5"));
                    cert6.setText(feedObj.getString("cert6"));
                    cert7.setText(feedObj.getString("cert7"));

                    Glide.with(getApplicationContext()).load("https:bkkp.dephub.go.id/image.php?file="+feedObj.getString("photo")).into(photo);
                    //photo.setVisibility(View.VISIBLE);
                }
            } else {
                hideProgressDialog();
                String errmessage = response.getString("error_msg");
                Toast.makeText(getApplicationContext(), errmessage, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        viewDetail(intentFragment);
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

    private void showProgressDialog(boolean cancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(PelautDetailActivity.this);
            mProgressDialog.setMessage(PelautDetailActivity.this.getString(R.string.loading));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent i = new Intent(getApplicationContext(),
                            MainActivity.class);
                    i.putExtra("fragment", 1);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}