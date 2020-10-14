package id.bkkp.general;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class PelautListAdapter extends BaseAdapter {
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

	public PelautListAdapter(Activity activity, List<GetItem> getItems) {
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
			convertView = inflater.inflate(R.layout.list_pelaut, null);

		session = new SessionManager(activity.getApplicationContext());

		/*if(mContext instanceof NewsFragment){
			((YourActivityName)mContext).yourDesiredMethod();
		}*/

		internetAvailable = session.isInternetAvailable();
		/*if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();*/

		final View finalConvertView = convertView;

		TextView name = (TextView) convertView.findViewById(R.id.title);
		ImageView photo = (ImageView) convertView.findViewById(R.id.list_image);
		TextView bst = (TextView) convertView.findViewById(R.id.bst);
		TextView content = (TextView) convertView.findViewById(R.id.certificate);
		TextView content2 = (TextView) convertView.findViewById(R.id.role);
		TextView timestamp = (TextView) convertView.findViewById(R.id.expdate);
		name.setTypeface(font1);

		final GetItem item = getItems.get(position);

		name.setText(item.getName());
		String first7bst = item.getCode().substring(0,7);
		bst.setText("BST #"+item.getCode().replace(first7bst, "*******"));
		content.setText(mContext.getResources().getString(R.string.certnum)+": "+item.getContent3());
		content2.setText(mContext.getResources().getString(R.string.position)+": "+item.getContent2());
		timestamp.setText(mContext.getResources().getString(R.string.expdate)+": "+item.getTimeStamp());

		if (item.getImage() != null) {
			/*try {
				File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
						File.separator + "kasandra" + File.separator + "product" + File.separator + item.getImage()); //+ "." + mFormat.name().toLowerCase());
				Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
				pImageView.setImageBitmap(image);
				pImageView.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
			} catch (Exception e) {*/
				//photo.setImageUrl("https://kasandra.biz/images/"+item.getImage(), imageLoader);
				Glide.with(convertView.getContext()).load(item.getImage()).into(photo);
				photo.setVisibility(View.VISIBLE);
			//}
			/*pImageView
					.setResponseObserver(new PImageView.ResponseObserver() {
						@Override
						public void onError() {
						}

						@Override
						public void onSuccess() {
						}
					});*/
		} else {
			photo.setVisibility(View.GONE);
		}
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
