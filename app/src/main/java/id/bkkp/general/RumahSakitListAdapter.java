package id.bkkp.general;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class RumahSakitListAdapter extends BaseAdapter {
	private Activity activity;
	private Context mContext;
	private LayoutInflater inflater;
	private List<GetItem> getItems;
	//ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	private SessionManager session;
	Typeface font1;
	FancyButton delete;
	boolean internetAvailable;
	final Handler handler7 = new Handler();
	private ProgressDialog mProgressDialog;

	public RumahSakitListAdapter(Activity activity, List<GetItem> getItems) {
		this.activity = activity;
		this.getItems = getItems;

		font1 = Typeface.createFromAsset(activity.getAssets(), "quattrocentobold.ttf");
		mContext=activity;
	}

	@Override
	public int getCount() {
		return getItems.size();
	}

	@Override
	public Object getItem(int location) {
		return getItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.list_rumahsakit, null);

		session = new SessionManager(activity.getApplicationContext());

		/*if(mContext instanceof NewsFragment){
			((YourActivityName)mContext).yourDesiredMethod();
		}*/

		internetAvailable = session.isInternetAvailable();
		/*if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();*/

		final View finalConvertView = convertView;

		TextView name = (TextView) convertView.findViewById(R.id.title);
		//TextView content = (TextView) convertView.findViewById(R.id.excerpt);
		final TextView content2 = (TextView) convertView.findViewById(R.id.address);
		TextView content3 = (TextView) convertView.findViewById(R.id.city);
		name.setTypeface(font1);

		final GetItem item = getItems.get(position);

		name.setText(item.getName());
		content2.setText(item.getContent2());
		content3.setText(item.getContent3());

		final View finalConvertView1 = convertView;

		return convertView;
	}

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

}
