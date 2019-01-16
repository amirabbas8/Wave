package net.saoshyant.wave.activity.login;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.saoshyant.wave.R;
import net.saoshyant.wave.activity.Main;
import net.saoshyant.wave.app.DatabaseHandler;
import net.saoshyant.wave.app.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Phonelogin_1 extends FragmentActivity {


    EditText inputPhone;
    ProgressBar vprog;
    ImageButton btnlogin;
    int randcod,nlogin;
    String sms, phone;
    String countrycode="0";
    public static final String URL_REQUEST = "http://saoshyant.net/wave/phonelogin_1.php";
    private CoordinatorLayout coordinatorLayout;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonelogin_1);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id .coordinatorLayout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             countrycode = extras.getString("countrycode");

        }
        // creating connection detector class instance
        Random r = new Random();
        randcod = r.nextInt((9999 - 1000) + 1) + 1000;
        sms = String.valueOf(randcod);
        btnlogin = (ImageButton) findViewById(R.id.btnlogin);
        vprog = (ProgressBar) findViewById(R.id.progressBar1);
        inputPhone = (EditText) findViewById(R.id.phone);

        Button changecountry = (Button) findViewById(R.id.changecountry);

        changecountry.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent upanel5 = new Intent(Phonelogin_1.this, selectcountry.class);
                upanel5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel5);
                finish();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (inputPhone.getText().toString().length() > 9 & inputPhone.getText().toString().length() < 11) {
                    if (Objects.equals("0", countrycode)) {
                        Snackbar.make(coordinatorLayout, R.string.cyc, Snackbar.LENGTH_LONG).show();
                    } else {

                        btnlogin.setVisibility(View.INVISIBLE);
                        vprog.setVisibility(View.VISIBLE);

                        String phoneNo = "30004746575666";
                     //   Toast.makeText(getApplicationContext(), sms, Toast.LENGTH_SHORT).show();
                          try {
                             SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNo, null, sms, null, null);

                              Snackbar.make(coordinatorLayout, R.string.onemin, Snackbar.LENGTH_LONG).show();
                        // Make sure the device has the proper dependencies.



                        nlogin = 1;
                        ProcessLogin();


                          } catch (Exception e) {
                              Snackbar.make(coordinatorLayout, R.string.errorsendsms, Snackbar.LENGTH_LONG).show();
                           //   nlogin = 1;
                            //  ProcessLogin();
                            e.printStackTrace();
                           btnlogin.setVisibility(View.VISIBLE);
                           vprog.setVisibility(View.INVISIBLE);
                         }

                    }
                } else {
                    Snackbar.make(coordinatorLayout, R.string.errorphone, Snackbar.LENGTH_LONG).show();
                }
            }

        });


    }







    private void ProcessLogin()
    {
        phone = inputPhone.getText().toString();
       phone= inputPhone.getText().toString();
        final Map<String, String> params = new HashMap<>();
        params.put("tag", "phonelogin");
        params.put("countrycode", countrycode);
        params.put("phone", phone);
        params.put("sms", sms);//5067
        StringRequest strReq = new StringRequest( Request.Method.POST,URL_REQUEST, new Response.Listener<String>()
        {
            //response from the server
            @Override
            public void onResponse (String response)
            {
                Log.d("a", response);
                try
                {
                    JSONObject responseObj = new JSONObject(response);
                    JSONArray feedArray = responseObj.getJSONArray("user");
                    JSONObject feedObj = (JSONObject) feedArray.get(0);

                    String KEY_SUCCESS = "success";
                    String KEY_REALNAME = "realname";
                    String KEY_code = "code";
                    String KEY_BIO = "bio";
                    String KEY_CONTRYCODE = "contrycode";
                    String KEY_profileimage = "profileimages";
                    String KEY_username= "username";
                    String KEY_frequency = "frequency";
                    String KEY_ID = "id";
                        if (feedObj.getString(KEY_SUCCESS) != null) {
                            String res = feedObj.getString(KEY_SUCCESS);


                            if (Integer.parseInt(res) == 1) {
                                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                                db.resetTables();

                                if ("".equals(feedObj.getString(KEY_frequency))) {
                                    db.addUser(feedObj.getString(KEY_ID),feedObj.getString(KEY_code), feedObj.getString(KEY_REALNAME), "realnamelogin", feedObj.getString(KEY_BIO), feedObj.getString(KEY_CONTRYCODE), feedObj.getString(KEY_profileimage), feedObj.getString(KEY_username), feedObj.getString(KEY_frequency));
                                } else {
                                    db.addUser(feedObj.getString(KEY_ID),feedObj.getString(KEY_code), feedObj.getString(KEY_REALNAME), "home", feedObj.getString(KEY_BIO), feedObj.getString(KEY_CONTRYCODE), feedObj.getString(KEY_profileimage), feedObj.getString(KEY_username), feedObj.getString(KEY_frequency));
                                }
                                /**
                                 * If JSON array details are stored in SQlite it launches the User Panel.
                                 **/
                                Intent upanel = new Intent(getApplicationContext(), Main.class);
                                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                //  pDialog.dismiss();

                                startActivity(upanel);
                                /**
                                 * Close Login Screen
                                 **/

                                finish();
                            } else if (Integer.parseInt(res) == 2) {
                                if (nlogin==1)
                                {
                                    nlogin=2;
                                    ProcessLogin();
                                }else if(nlogin==2){
                                    nlogin=3;
                                    ProcessLogin();
                                }else if(nlogin==3){
                                    btnlogin.setVisibility(View.VISIBLE);
                                    vprog.setVisibility(View.INVISIBLE);
                                    Snackbar.make(coordinatorLayout, R.string.errorresivesms, Snackbar.LENGTH_LONG).show();
                                }

                            } else {
                                if (nlogin==1)
                                {
                                    nlogin=2;
                                    ProcessLogin();
                                }else if(nlogin==2){
                                    nlogin=3;
                                    ProcessLogin();
                                }else if(nlogin==3){
                                    btnlogin.setVisibility(View.VISIBLE);
                                    vprog.setVisibility(View.INVISIBLE);
                                    Snackbar.make(coordinatorLayout, R.string.errorresivesms, Snackbar.LENGTH_LONG).show();
                                }

                            }
                        }



                }
                catch(JSONException e)
                {
                    btnlogin.setVisibility(View.VISIBLE);
                    vprog.setVisibility(View.INVISIBLE);
                    Snackbar.make(coordinatorLayout, R.string.errorproblem, Snackbar.LENGTH_LONG).show();
                  //  Toast.makeText(getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse (VolleyError error)
                    {
                        btnlogin.setVisibility(View.VISIBLE);
                        vprog.setVisibility(View.INVISIBLE);
                    //   Log.e(TAG, "Error1: " + error.getMessage());
                      //  Toast.makeText(getApplicationContext(), "Error1: connection error", Toast.LENGTH_SHORT).show();
                        Snackbar.make(coordinatorLayout, R.string.errorproblem, Snackbar.LENGTH_LONG).show();
                    }
                }
        )
        {

            /* Passing user parameters to our server
             * @return*/
            @Override
            protected Map<String, String> getParams ()
            {
                // Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }




}
