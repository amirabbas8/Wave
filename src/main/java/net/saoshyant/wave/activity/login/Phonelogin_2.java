package net.saoshyant.wave.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import net.saoshyant.wave.R;
import net.saoshyant.wave.activity.Main;
import net.saoshyant.wave.app.DatabaseHandler;
import net.saoshyant.wave.app.MyApplication;


public class Phonelogin_2 extends Activity {

    ImageButton btnLogin;

    EditText inputrealname,inputfrequency;

    ProgressBar vprog;
    public static final String URL_REQUEST = "http://saoshyant.net/wave/phonelogin_2.php";
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

        setContentView(R.layout.phonelogin_2);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id .coordinatorLayout);
        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
       String realname = user.get("realname");

        inputrealname = (EditText) findViewById(R.id.inputrealname);
        inputfrequency= (EditText) findViewById(R.id.inputfrequency);
        btnLogin = (ImageButton) findViewById(R.id.btnlogin);
        vprog = (ProgressBar) findViewById(R.id.progressBar1);

        inputrealname.setText(realname);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (inputrealname.getText().toString().length() > 4 & inputrealname.getText().toString().length() < 21) {

                    if (inputfrequency.getText().toString().length() > 4 & inputfrequency.getText().toString().length() < 21) {
                        btnLogin.setVisibility(View.INVISIBLE);
                        vprog.setVisibility(View.VISIBLE);
                        ProcessLogin();
                    }else {
                        Snackbar.make(coordinatorLayout, R.string.errorfrequency, Snackbar.LENGTH_LONG).show();
                        btnLogin.setVisibility(View.VISIBLE);
                        vprog.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Snackbar.make(coordinatorLayout, R.string.errorrname, Snackbar.LENGTH_LONG).show();
                    btnLogin.setVisibility(View.VISIBLE);
                    vprog.setVisibility(View.INVISIBLE);
                }
            }
        });

    }





    private void ProcessLogin()
    {
        String idno, realname,frequency,code;
        realname = inputrealname.getText().toString();
        frequency=  inputfrequency.getText().toString();

        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
        idno = user.get("idno");
        code = user.get("code");

        final Map<String, String> params = new HashMap<>();
        params.put("tag", "phonelogin");
        params.put("id", idno);
        params.put("code", code);
        params.put("realname", realname);
        params.put("frequency", frequency);
    //    Log.d(Phonelogin_2.class.getSimpleName(), params.toString());
        StringRequest strReq = new StringRequest( Request.Method.POST,URL_REQUEST, new Response.Listener<String>()
        {
            //response from the server
            @Override
            public void onResponse (String response)
            {
             //   Log.d(Phonelogin_2.class.getSimpleName(), response);
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
                            /**
                             * Clear all previous data in SQlite database.
                             **/
                            db.resetTables();
                            db.addUser(feedObj.getString(KEY_ID),feedObj.getString(KEY_code), feedObj.getString(KEY_REALNAME),
                                   "home", feedObj.getString(KEY_BIO),
                                    feedObj.getString(KEY_CONTRYCODE),
                                    feedObj.getString(KEY_profileimage), feedObj.getString(KEY_username)
                                    , feedObj.getString(KEY_frequency));

                            Intent upanel = new Intent(getApplicationContext(), Main.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(upanel);
                            finish();
                        }else if(Integer.parseInt(res) == 2) {
                            Snackbar.make(coordinatorLayout, "این فرکانس قبلا انتخاب شده", Snackbar.LENGTH_LONG).show();
                            btnLogin.setVisibility(View.VISIBLE);
                            vprog.setVisibility(View.INVISIBLE);
                        }  else if(Integer.parseInt(res) == 3) {
                            Snackbar.make(coordinatorLayout, R.string.frequencyalreadytaken, Snackbar.LENGTH_LONG).show();
                            btnLogin.setVisibility(View.VISIBLE);
                            vprog.setVisibility(View.INVISIBLE);
                        }else {

                            btnLogin.setVisibility(View.VISIBLE);
                            vprog.setVisibility(View.INVISIBLE);
                            Snackbar.make(coordinatorLayout, R.string.errorproblem, Snackbar.LENGTH_LONG).show();
                        }


                    }

                }
                catch(JSONException e)
                {
                    btnLogin.setVisibility(View.VISIBLE);
                    vprog.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse (VolleyError error)
                    {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        btnLogin.setVisibility(View.VISIBLE);
                        vprog.setVisibility(View.INVISIBLE);
                        //Toast.makeText(getApplicationContext(), "Error1: connection error", Toast.LENGTH_SHORT).show();

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