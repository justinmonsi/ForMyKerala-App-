package in.co.iodev.formykerala.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.iodev.formykerala.HTTPPostGet;
import in.co.iodev.formykerala.Models.DataModel;
import in.co.iodev.formykerala.OTPTextEditor;
import in.co.iodev.formykerala.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static in.co.iodev.formykerala.Constants.Constants.Pin_Selection;
import static in.co.iodev.formykerala.Constants.Constants.Verify_OTP;
import static java.lang.Boolean.TRUE;

public class PinSelection extends AppCompatActivity {
    SharedPreferences sharedPref;
    EditText otp1,otp2,otp3,otp4;
    Button verify;
    Gson gson = new Gson();
    ImageView back;


    String StringData,request_post_url=Pin_Selection,TimeIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinselection);
        sharedPref=getDefaultSharedPreferences(getApplicationContext());
        otp1=findViewById(R.id.otp1);
        otp2=findViewById(R.id.otp2);
        otp3=findViewById(R.id.otp3);
        otp4=findViewById(R.id.otp4);
        back=findViewById(R.id.back_button);
        verify=findViewById(R.id.otp_verify);
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
            Toast.makeText(PinSelection.this,"Please Enter Valid OTP",Toast.LENGTH_LONG).show();
        }
        else {
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

        @Override
        protected String doInBackground(String... urls) {
            String response;
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
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            JSONObject responseObject = null;
            try {
                responseObject = new JSONObject(result);
                Toast.makeText(getApplicationContext(), responseObject.getString("Message"), Toast.LENGTH_LONG).show();
                if (responseObject.getString("Message").equals("Success")) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("TimeIndex", responseObject.getString("TimeIndex"));
                    editor.putBoolean("Login", TRUE);
                    editor.apply();
                    startActivity(new Intent(PinSelection.this, ReceiverDetails.class));
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


