package id.bkkp.general;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import mehdi.sakout.fancybuttons.FancyButton;

public class HistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SessionManager session;
    Typeface font1, font2;

    private ListView listView;
    private static final int MY_PERMISSIONS_1 = 0;

    private Context mContext;
    private ProgressDialog pDialog;
    public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
    private HistoryListAdapter listAdapter;
    private List<GetItem> getItems;
    TextView txtEmpty;
    boolean internetAvailable;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout searchLayout;
    EditText txtSearch;
    Button clearText;
    final Handler handler2 = new Handler();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        session = new SessionManager(getApplicationContext());

        font1 = Typeface.createFromAsset(getResources().getAssets(), "bahnschrift.ttf");
        font2 = Typeface.createFromAsset(getResources().getAssets(), "OpenSansBI.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setTypeface(font1);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(HistoryActivity.this);

        txtEmpty = (TextView) findViewById(R.id.empty);

        listView = (ListView) findViewById(R.id.listAntrian);
        getItems = new ArrayList<GetItem>();
        listAdapter = new HistoryListAdapter(HistoryActivity.this, getItems);

        if(listView.getFooterViewsCount() > 0){
            listView.removeAllViews();
        }

        if(listView.getFooterViewsCount() > 0){
            listView.removeAllViews();
        }
        listView.setAdapter(listAdapter);

        new isOnline().execute("");
        showProgressDialog(false);
        loadData(session.sUserBST());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        showProgressDialog(false);
        loadData(session.sUserBST());
    }

    private void loadData(String bst) {
        getItems.clear();
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?history=" + bst + "&jwt=" + session.sJWT() + "&phone=" + session.phone());

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
                    URL_INPUT+"?history=" + bst + "&jwt=" + session.sJWT() + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

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
            hideProgressDialog();
            //Log.d("token",session.sJWT());
            JSONArray feedArray = response.getJSONArray("queue");
            int rows = feedArray.length();
            if(rows > 0){
                txtEmpty.setVisibility(View.GONE);
            } else {
                txtEmpty.setVisibility(View.VISIBLE);
            }

            //Log.d("logd",feedArray.toString());
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                GetItem item = new GetItem();

                item.setId(feedObj.getInt("bst"));
                item.setName(feedObj.getString("no_antrian"));
                item.setContent(feedObj.getString("clinic"));
                item.setContent2(feedObj.getString("tgl_pesan_server"));
                item.setContent3(feedObj.getString("tgl_batal"));
                item.setCode(feedObj.getString("tgl_server"));
                item.setTimeStamp(feedObj.getString("tgl_pesan"));
                item.setStatus(feedObj.getString("status"));

                try {
                } catch (Exception e) {
                    Log.d("BKKP", "BKKP debug err3: " + e.getMessage());
                }


                getItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("fragment", 3);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

  /*@Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
          Intent i = new Intent(mContext,
                  MainActivity.class);
          //i.putExtra("id_main", 1);
          startActivity(i);
          getActivity().finish();
          return true;
      }

    }
    return super.onKeyDown(keyCode, event);
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
                internetAvailable = true;

                return true;
            } catch (IOException e) {
                internetAvailable = false;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean online) {
            super.onPostExecute(online);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private void showProgressDialog(boolean cancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(HistoryActivity.this);
            mProgressDialog.setMessage(HistoryActivity.this.getString(R.string.loading));
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
                    i.putExtra("fragment", 3);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
