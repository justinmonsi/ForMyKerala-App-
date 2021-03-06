package in.co.iodev.formykerala.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.iodev.formykerala.Controllers.CheckInternet;
import in.co.iodev.formykerala.Controllers.HTTPPostGet;
import in.co.iodev.formykerala.Controllers.ProgressBarHider;
import in.co.iodev.formykerala.Models.DataModel;
import in.co.iodev.formykerala.Controllers.OTPTextEditor;
import in.co.iodev.formykerala.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static in.co.iodev.formykerala.Constants.Constants.DForgot_Reset_PIN;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DPinReset extends AppCompatActivity {
    SharedPreferences sharedPref;
    EditText otp1,otp2,otp3,otp4;
    Button verify;
    Gson gson = new Gson();
    ImageView back;
    Context context;
    ProgressBarHider hider;


    String StringData,request_post_url=DForgot_Reset_PIN,TimeIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppLocale(MainActivity.languagePreferences.getString("LOCALE_CODE", null), getResources());
        setContentView(R.layout.activity_pinselection);
        sharedPref=getDefaultSharedPreferences(getApplicationContext());
        otp1=findViewById(R.id.otp1);
        otp2=findViewById(R.id.otp2);
        otp3=findViewById(R.id.otp3);
        otp4=findViewById(R.id.otp4);
        back=findViewById(R.id.back_button);
        final String localeCode=MainActivity.languagePreferences.getString("LOCALE_CODE", null);
        final ImageView voice=findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voice.setClickable(false);
                MediaPlayer mp = new MediaPlayer();

                try {
                    if(localeCode.equals("ml"))
                    { mp=MediaPlayer.create(getApplicationContext(),R.raw.selectpin_mal);
                        mp.start();}
                    else if (localeCode.equals("en"))
                    {
                        mp=MediaPlayer.create(getApplicationContext(),R.raw.selectpin_eng);
                        mp.start();
                    }
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            voice.setClickable(true);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        context=this;
        verify=findViewById(R.id.otp_verify);
        hider=new ProgressBarHider(verify.getRootView(),verify);

        otp1.addTextChangedListener(new OTPTextEditor(otp1,otp1.getRootView()));
        otp2.addTextChangedListener(new OTPTextEditor(otp2,otp2.getRootView()));
        otp3.addTextChangedListener(new OTPTextEditor(otp3,otp3.getRootView()));
        otp4.addTextChangedListener(new OTPTextEditor(otp4,otp4.getRootView()));
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });
        TimeIndex=sharedPref.getString("TimeIndex","");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    public void verify() {
        if(otp1.getText().toString().equals("")||otp2.getText().toString().equals("")||otp3.getText().toString().equals("")||otp4.getText().toString().equals("")){
            Toast.makeText(DPinReset.this,"Please Enter Valid PIN",Toast.LENGTH_LONG).show();
        }
        else {
            hider.show();
            StringData = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
            DataModel d = new DataModel();
            d.setPIN(StringData);
            d.setPhoneNumber(sharedPref.getString("PhoneNumber", ""));
            d.setTimeIndex(TimeIndex);
            StringData = gson.toJson(d);
            Log.i("jisjoe", StringData);

            new HTTPAsyncTask2().execute(request_post_url);
        }
    }
    private class HTTPAsyncTask2 extends AsyncTask<String, Void, String> {
        String response="Network Error";

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    response = HTTPPostGet.getJsonResponse(urls[0], StringData);
                    Log.i("jisjoe", response.toString());
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error!";
                }
                finally {
                    hider.hide();
                }
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPreExecute() {
            CheckInternet CI=new CheckInternet();
            CI.isOnline(context);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            hider.hide();
            JSONObject responseObject = null;
            try {
                responseObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), responseObject.getString("Message"), Toast.LENGTH_LONG).show();
                if (responseObject.getString("Message").equals("Success")) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("TimeIndex", responseObject.getString("TimeIndex"));
                    editor.apply();
                    TimeIndex=sharedPref.getString("TimeIndex","");
                    editor.putBoolean(TimeIndex+"DLogin",FALSE);
                    editor.apply();
                    Intent intent = new Intent(DPinReset.this, DonorLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), responseObject.getString("Message"), Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
        boolean doubleBackToExitPressedOnce = false;

        @Override
        public void onBackPressed() {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

}


