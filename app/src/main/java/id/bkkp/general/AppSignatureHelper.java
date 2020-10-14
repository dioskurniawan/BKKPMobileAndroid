package id.bkkp.general;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;

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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created on : May 21, 2019
 * Author     : AndroidWave
 */
public class AppSignatureHelper extends ContextWrapper {

  private static final String TAG = "AppSignatureHelper";
  private static final String HASH_TYPE = "SHA-256";
  public static final int NUM_HASHED_BYTES = 9;
  public static final int NUM_BASE64_CHAR = 11;
  public static String URL_LOGIN = "https://bkkp.dephub.go.id/bkkpapi/loginapp_dev.php";
  private SessionManager session;

  public AppSignatureHelper(Context context) {
    super(context);
    session = new SessionManager(getApplicationContext());
  }

  /**
   * Get all the app signatures for the current package
   */
  public ArrayList<String> getAppSignatures() {
    ArrayList<String> appCodes = new ArrayList<>();

    try {
      // Get all package signatures for the current package
      String packageName = getPackageName();
      PackageManager packageManager = getPackageManager();
      Signature[] signatures = packageManager.getPackageInfo(packageName,
          PackageManager.GET_SIGNATURES).signatures;

      // For each signature create a compatible hash
      for (Signature signature : signatures) {
        String hash = hash(packageName, signature.toCharsString());
        if (hash != null) {
          appCodes.add(String.format("%s", hash));
          session.setHash(hash);
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      Log.e(TAG, "Unable to find package to obtain hash.", e);
    }
    return appCodes;
  }

  private static String hash(String packageName, String signature) {
    String appInfo = packageName + " " + signature;
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));
      }
      byte[] hashSignature = messageDigest.digest();

      // truncated into NUM_HASHED_BYTES
      hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
      // encode into Base64
      String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
      base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR);

      Log.d(TAG, String.format("pkg: %s -- hash: %s", packageName, base64Hash));
      return base64Hash;
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "hash:NoSuchAlgorithm", e);
    }
    return null;
  }

}
