package id.bkkp.general;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class PelautFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


	private ListView listView;
	private static final int MY_PERMISSIONS_1 = 0;

	private Context mContext;
	private int nProfileID = 0;
	//private SessionManager session;
	private ProgressDialog pDialog;
	final Handler handler = new Handler();
	int n = 0;
	public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
	private PelautListAdapter listAdapter;
	private List<GetItem> getItems;
	TextView toolbar_title;
	TextView txtEmpty;
	boolean internetAvailable;
	SwipeRefreshLayout swipeRefreshLayout;
	LinearLayout searchLayout;
	EditText txtSearch;
	Button clearText;
	final Handler handler2 = new Handler();
	private ProgressDialog mProgressDialog;

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new isOnline().execute("");

		// Session manager
		/*session = new SessionManager(mContext);

		internetAvailable = session.isInternetAvailable();

		if (nProfileID == 0) {
			nProfileID = session.nProfileID();
		}
		// check ProfileID from persistence store
		if (session.nProfileID() != -0) {
			nProfileID = session.nProfileID();
		}*/
		//loadData();
		//new syncCategory().execute("select");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onRefresh() {
		showProgressDialog(true);
		loadData("");
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pelaut, container, false);

		toolbar_title = (TextView) view.findViewById(R.id.toolbar_title);
		toolbar_title.setText(R.string.cari_pelaut);

		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
		swipeRefreshLayout.setOnRefreshListener(this);

		searchLayout = (LinearLayout) view.findViewById(R.id.search);
		txtSearch = (EditText) view.findViewById(R.id.txtSearch);
		clearText = (Button) view.findViewById(R.id.cleartext);
		final FancyButton btnSearch = (FancyButton) view.findViewById(R.id.search_button);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String text = txtSearch.getText().toString();
				if (text.equals("")) {
					Toast.makeText(mContext, R.string.keyword, Toast.LENGTH_SHORT).show();
				} else {
					handler2.postDelayed(new Runnable() {
						@Override
						public void run() {
							showProgressDialog(true);
							loadData(text);
						}
					}, 2000);
				}
			}
		});
		clearText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				txtSearch.setText("");
				showProgressDialog(true);
				loadData("");
			}
		});

		txtEmpty = (TextView) view.findViewById(R.id.empty);
		pDialog = new ProgressDialog(new ContextThemeWrapper(mContext, android.R.style.Theme_Holo_Light_Dialog));

		listView = (ListView) view.findViewById(R.id.listPelaut);
		getItems = new ArrayList<GetItem>();
		listAdapter = new PelautListAdapter(getActivity(), getItems);

		if(listView.getFooterViewsCount() > 0){
			listView.removeAllViews();
		}

		if(listView.getFooterViewsCount() > 0){
			listView.removeAllViews();
		}
		listView.setAdapter(listAdapter);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(mContext,
					android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {

				if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
						android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				} else {
					ActivityCompat.requestPermissions(getActivity(),
							new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
							MY_PERMISSIONS_1);
				}
			} else {
				new isOnline().execute("");
				showProgressDialog(true);
				loadData("");
                /*if(internetAvailable){
          loadCategory();
        } else {
          loadCategorySQLite();
        }*/
			}
		} else {
			new isOnline().execute("");
			showProgressDialog(true);
			loadData("");
            /*if(internetAvailable){
        loadCategory();
      } else {
        loadCategorySQLite();
      }*/
		}
		return view;
	}

	private class MyTextWatcher implements TextWatcher {

		private View view;

		private MyTextWatcher(View view) {
			this.view = view;
		}

		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			final String text = txtSearch.getText().toString();
			if(text.equals("")){
				//Toast.makeText(getApplicationContext(), "Silakan inputkan SKU/PLU atau Nama Produk", Toast.LENGTH_SHORT).show();
			} else {
				handler2.postDelayed(new Runnable() {
					@Override
					public void run() {
						showProgressDialog(true);
						loadData(text);
					}
				}, 2000);
			}
		}

		public void afterTextChanged(Editable editable) {
		}
	}

	final Runnable handlerTask = new Runnable() {

		@Override
		public void run() {

			n++;
			if (n >= 15) { // upload data every 30 seconds
				Toast.makeText(mContext, "Failed to display data, please check your internet connection", Toast.LENGTH_SHORT).show();
				if(pDialog != null) {
					pDialog.dismiss();
					n = 0;
					handler.removeCallbacks(handlerTask);
				}
			}
			handler.postDelayed(handlerTask, 1000);
		}
	};

	private void loadData(String keyword) {
		getItems.clear();
		listView.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
		// We first check for cached request
		Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
		//Cache cache = AppController.getInstance().getRequestQueue().getCache().get(URL_NEWS).serverDate;
		//AppController.getInstance().getRequestQueue().getCache().invalidate(URL_NEWS+String.valueOf(page), true);
		cache2.clear();
		Cache.Entry entry2 = cache2.get(URL_INPUT+"?pelaut=" + "&keyword=" + keyword);

		Log.d("log_url",URL_INPUT+"?pelaut=" + "&keyword=" + keyword);
		//Toast.makeText(mContext, "page: " + URL_INPUT+"?pelaut=", Toast.LENGTH_SHORT).show();
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
					URL_INPUT+"?pelaut=" + "&keyword=" + keyword, null, new Response.Listener<JSONObject>() {

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
			/*jsonReq.setRetryPolicy(new DefaultRetryPolicy(5000,
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
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
		hideProgressDialog();
		try {

			JSONArray feedArray = response.getJSONArray("seafarer");
			int rows = feedArray.length();
			if(rows > 0){
				txtEmpty.setVisibility(View.GONE);
			} else {
				txtEmpty.setVisibility(View.VISIBLE);
			}

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				GetItem item = new GetItem();
				item.setCode(feedObj.getString("bst"));
				item.setName(feedObj.getString("fullname"));
				item.setContent(feedObj.getString("examdate"));
				item.setContent2(feedObj.getString("role"));
				item.setContent3(feedObj.getString("certnum"));
				// Image might be null sometimes
                String image = feedObj.isNull("picture_path") ? null : feedObj
                        .getString("picture_path");

				item.setImage(image);
				item.setTimeStamp(feedObj.getString("expdate"));

				try {
					//write.execSQL("INSERT INTO m_category VALUES(" + feedObj.getInt("cat_id") + ", '" + feedObj.getString("name") + "', '" + feedObj.getString("updated_date") + "', '" + feedObj.getString("picture_path") + "', '" + feedObj.getString("discount") + "');");
					//Log.d("BKKP", "BKKP debug insert category: INSERT INTO m_category VALUES(" + feedObj.getInt("cat_id") + ", '" + feedObj.getString("name") + "', '" + feedObj.getString("updated_date") + "', '" + feedObj.getString("picture_path") + "');");

                    /*Cursor c = read.rawQuery("select category_id from m_category where category_name='Makanan'", null);
                    int rows = c.getCount();
                    c.moveToFirst();
                    Log.d("BKKP", "BKKP debug insert category: "+c.getString(0));*/
				} catch (Exception e) {
					Log.d("BKKP", "BKKP debug insert surat_masuk err3: " + e.getMessage());
				}


				getItems.add(item);
			}

			// notify data changes to list adapater
			listAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_1: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//new populateProductList().execute();
					loadData("");
				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
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

	private void showProgressDialog(boolean cancel) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext.getString(R.string.loading));
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

	public void showMessage(String title, String message) {
		AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.show();
	}

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
}