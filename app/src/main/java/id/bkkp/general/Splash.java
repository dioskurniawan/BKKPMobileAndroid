package id.bkkp.general;
 

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.Locale;


public class Splash extends Activity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private SessionManager session;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        session = new SessionManager(getApplicationContext());
        session.setLang(Locale.getDefault().getLanguage());
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
            	if(isConnected()) {
	                Intent i = new Intent(Splash.this, MainActivity.class);
	                startActivity(i);
	                finish();
            	} else { 
            		Toast.makeText(Splash.this, R.string.unreachable, Toast.LENGTH_LONG).show();
            	}
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }       
 
}