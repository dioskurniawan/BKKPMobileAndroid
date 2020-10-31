package id.bkkp.general;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "BKKP";
	
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setDob(String sDob) {
		editor.putString("dob", sDob);
		editor.commit();

		//Log.d(TAG, "Date modified!");
	}

	public String sDob(){
		return pref.getString("dob", "");
	}

	public void setWiFi(String WiFi) {
		editor.putString("wifi", WiFi);
		// commit changes
		editor.commit();

		//Log.d(TAG, "WiFi modified!");
	}

    public void setWiFiInfo(String WiFiUser, String WiFiPasswd) {
        editor.putString("wifiuser", WiFiUser);
        editor.putString("wifipass", WiFiPasswd);
        // commit changes
        editor.commit();

        //Log.d(TAG, "WiFiInfo modified!");
    }

    public void setURL(String URL) {
        editor.putString("url", URL);
        editor.commit();

        //Log.d(TAG, "URL modified!");
    }

	public void setSavedTrue(boolean isSaved) {
		editor.putBoolean("is_saved", isSaved);
		editor.commit();

		//Log.d(TAG, "saved true modified!");
	}

	public String sWiFi(){
		return pref.getString("wifi", "WiFi disconnected");
	}

    public String sURL(){
        return pref.getString("url", "https://bkkp.dephub.go.id");
    }

	public void setLogin(boolean isLoggedIn) {
		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
		editor.commit();

		//Log.d(TAG, "User login session modified!");
	}
	public void setToken(String token) {
		editor.putString("token", token);
		editor.commit();
		//Log.d(TAG, "Token modified!");
	}
	public void setJWT(String jwt) {
		editor.putString("jwt", jwt);
		editor.commit();
	}
	public void setIMEI(String IMEI) {
		editor.putString("imei", IMEI);
		editor.commit();
		//Log.d(TAG, "IMEI modified!");
	}
	public void setPhone(String phone) {
		editor.putString("phone", phone);
		editor.commit();
	}
	public void setTempPhone(String tempPhone) {
		editor.putString("phone_temp", tempPhone);
		editor.commit();
	}
	public void setEmail(String email) {
		editor.putString("email", email);
		editor.commit();
	}
	public void setUserEmail(String sUserEmail) {
		editor.putString("user_email", sUserEmail);
		editor.commit();
		//Log.d(TAG, "user email modified!");
	}
	public void setUserBST(String sUSerBST) {
		editor.putString("bst", sUSerBST);
		editor.commit();
		//Log.d(TAG, "user ID modified!");
	}
	public void setUserBST16(String sUSerBST16) {
		editor.putString("bst16", sUSerBST16);
		editor.commit();
		//Log.d(TAG, "user ID modified!");
	}
	public void setUserName(String sUserName) {
		editor.putString("user_name", sUserName);
		editor.commit();
		//Log.d(TAG, "user name modified!");
	}
	public void setSeafarerName(String seafarerName) {
		editor.putString("seafarer_name", seafarerName);
		editor.commit();
		//Log.d(TAG, "user name modified!");
	}
	public void setPasswd(String sPasswd) {
		editor.putString("user_pass", sPasswd);
		editor.commit();
		//Log.d(TAG, "user name modified!");
	}
	public void setLang(String sLang) {
		editor.putString("lang", sLang);
		editor.commit();
	}
	public void setJabatan(String sJabatan) {
		editor.putString("user_jabatan", sJabatan);
		editor.commit();
	}
	public void setLastLogin(String sLastLogin) {
		editor.putString("user_last_login", sLastLogin);
		editor.commit();
	}
	public void setProfileID(int nProfileID) {
		editor.putInt("user_profile_id", nProfileID);
		editor.commit();
	}
	public void setProfile(String sProfile) {
		editor.putString("user_profile", sProfile);
		editor.commit();
	}
	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}

	public String sUserBST(){
		return pref.getString("bst", "-999");
	}

	public String sUserBST16(){
		return pref.getString("bst16", "62");
	}

	public String sToken(){
		return pref.getString("token", "Token belum didefinisikan");
	}

	public String sJWT(){
		return pref.getString("jwt", "");
	}

	public String sIMEI(){
		return pref.getString("imei", "IMEI not Found");
	}

	public String sUserName(){
		return pref.getString("user_name", "Username");
	}

	public String seafarerName(){
		return pref.getString("seafarer_name", "Pelaut");
	}

	public String sPasswd(){
		return pref.getString("user_pass", "Password");
	}

	public String sLang(){
		return pref.getString("lang", "in");
	}

	public String sJabatan(){
		return pref.getString("user_jabatan", "Jabatan");
	}

	public String sLastLogin(){
		return pref.getString("user_last_login", "0000-00-00 00:00:00");
	}

	public int nProfileID(){
		return pref.getInt("user_profile_id", -0);
	}

	public String sProfile(){
		return pref.getString("user_profile", "Profile");
	}

	public String sUserEmail(){
		return pref.getString("user_email", "Email");
	}

	public String phone() {
		return pref.getString("phone", "021");
	}

	public String tempPhone() {
		return pref.getString("phone_temp", "021");
	}

	public String email() {
		return pref.getString("email", "@");
	}

	public String message() {
		return pref.getString("message", "");
	}

	public void setLatLng(double latitude, double longitude) {

		editor.putLong("latitude", Double.doubleToLongBits(latitude));
		editor.putLong("longitude", Double.doubleToLongBits(longitude));
		// commit changes
		editor.commit();

		Log.d(TAG, "Store last position");
	}

	public double longitude(){
		return Double.longBitsToDouble(pref.getLong("longitude", (long) 0.0));
	}

	public double latitude(){
		return Double.longBitsToDouble(pref.getLong("latitude", (long) 0.0));
	}

	public String sHash(){
		return pref.getString("hash", "hash");
	}

	public void setHash(String sHash) {
		editor.putString("hash", sHash);
		editor.commit();
	}

	public boolean isInternetAvailable(){
		return pref.getBoolean("online", false);
	}
	public void setInternetAvailable(boolean isOnline) {
		editor.putBoolean("online", isOnline);
		editor.commit();
	}

	public boolean isBstExist(){
		return pref.getBoolean("bstexist", false);
	}
	public void setBstExist(boolean isBstExist) {
		editor.putBoolean("bstexist", isBstExist);
		editor.commit();
	}

	public void clearAllData() {
		editor.clear();
		editor.commit();
	}
}
