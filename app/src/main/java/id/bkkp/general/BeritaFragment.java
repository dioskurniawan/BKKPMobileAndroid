package id.bkkp.general;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

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

//import ss.com.bannerslider.Slider;

public class BeritaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


	private ListView listView;
	private static final int MY_PERMISSIONS_1 = 0;

	private Context mContext;
	private int nProfileID = 0;
	private SessionManager session;
	private ProgressDialog pDialog;
	final Handler handler = new Handler();
	int n = 0;
	public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
	private BeritaListAdapter listAdapter;
	private List<GetItem> getItems;
	TextView toolbar_title;
	TextView txtEmpty;
	boolean internetAvailable;
	SwipeRefreshLayout swipeRefreshLayout;
	LinearLayout searchLayout;
	EditText txtSearch;
	Button clearText;
	final Handler handler2 = new Handler();
	SliderView sliderView;
	private SliderAdapterExample adapter;

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
		session = new SessionManager(mContext);

		/*internetAvailable = session.isInternetAvailable();

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
		loadData("");
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_berita, container, false);

		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
		swipeRefreshLayout.setOnRefreshListener(this);

		txtEmpty = (TextView) view.findViewById(R.id.empty);
		pDialog = new ProgressDialog(new ContextThemeWrapper(mContext, android.R.style.Theme_Holo_Light_Dialog));

		listView = (ListView) view.findViewById(R.id.listBerita);
		getItems = new ArrayList<GetItem>();
		listAdapter = new BeritaListAdapter(getActivity(), getItems);

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
				loadData("");
                /*if(internetAvailable){
          loadCategory();
        } else {
          loadCategorySQLite();
        }*/
			}
		} else {
			new isOnline().execute("");
			loadData("");
            /*if(internetAvailable){
        loadCategory();
      } else {
        loadCategorySQLite();
      }*/
		}

		sliderView = view.findViewById(R.id.imageSlider);

		adapter = new SliderAdapterExample(mContext);

		sliderView.setSliderAdapter(adapter);

		sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
		sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
		sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
		sliderView.setIndicatorSelectedColor(Color.WHITE);
		sliderView.setIndicatorUnselectedColor(Color.GRAY);
		sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
		sliderView.startAutoCycle();

		renewItems(sliderView);

		sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
			@Override
			public void onIndicatorClicked(int position) {
				sliderView.setCurrentPagePosition(position);
			}
		});

		return view;
	}

	public void renewItems(View view) {
		List<SliderItem> sliderItemList = new ArrayList<>();
		//dummy data
		for (int i = 0; i < 5; i++) {
			SliderItem sliderItem = new SliderItem();
			//sliderItem.setDescription("Slider Item " + i);
			if (i == 0) {
				sliderItem.setImageUrl("https://bkkp.dephub.go.id/bkkpapi/slide1.php?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
			} else if (i == 1) {
				sliderItem.setImageUrl("https://bkkp.dephub.go.id/bkkpapi/slide2.php?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
			} else if (i == 2) {
				sliderItem.setImageUrl("https://bkkp.dephub.go.id/bkkpapi/slide3.php?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
			} else if (i == 3) {
				sliderItem.setImageUrl("https://bkkp.dephub.go.id/bkkpapi/slide4.php?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
			} else {
				sliderItem.setImageUrl("https://bkkp.dephub.go.id/bkkpapi/slide5.php?auto=compress&cs=tinysrgb&h=750&w=1260");
			}
			sliderItemList.add(sliderItem);
		}
		adapter.renewItems(sliderItemList);
	}

	public void removeLastItem(View view) {
		if (adapter.getCount() - 1 >= 0)
			adapter.deleteItem(adapter.getCount() - 1);
	}

	public void addNewItem(View view) {
		SliderItem sliderItem = new SliderItem();
		sliderItem.setDescription("Slider Item Added Manually");
		sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
		adapter.addItem(sliderItem);
	}

	/*final Runnable handlerTask = new Runnable() {

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
	};*/

	private void loadData(String keyword) {
		getItems.clear();
		listView.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
		// We first check for cached request
		Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
		//Cache cache = AppController.getInstance().getRequestQueue().getCache().get(URL_NEWS).serverDate;
		//AppController.getInstance().getRequestQueue().getCache().invalidate(URL_NEWS+String.valueOf(page), true);
		cache2.clear();
		Cache.Entry entry2 = cache2.get(URL_INPUT+"?berita=" + "&keyword=" + keyword);

		//Toast.makeText(mContext, "page: " + URL_INPUT+"?berita=", Toast.LENGTH_SHORT).show();
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
					URL_INPUT+"?berita=" + "&keyword=" + keyword, null, new Response.Listener<JSONObject>() {

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
		try {
			//Toast.makeText(mContext, "STATUS "+response.getBoolean("error"), Toast.LENGTH_SHORT).show();
			JSONArray feedArray = response.getJSONArray("story");
			int rows = feedArray.length();
			if(rows > 0){
				txtEmpty.setVisibility(View.GONE);
			} else {
				txtEmpty.setVisibility(View.VISIBLE);
			}

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				GetItem item = new GetItem();
				item.setId(feedObj.getInt("id"));
				item.setName(feedObj.getString("title"));
				item.setContent2(feedObj.getString("excerpt"));
				item.setContent3(feedObj.getString("uri"));

				item.setTimeStamp(feedObj.getString("postdate"));

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