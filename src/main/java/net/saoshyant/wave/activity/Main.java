package net.saoshyant.wave.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.saoshyant.wave.R;
import net.saoshyant.wave.activity.login.Phonelogin_2;
import net.saoshyant.wave.activity.login.selectcountry;
import net.saoshyant.wave.app.DatabaseHandler;

import java.util.Calendar;
import java.util.HashMap;

import ir.adad.client.Adad;

public class Main extends Activity {
int y,m,d;
    int year,month,day;
    Intent home,PhoneLogin,realnamelogin;
    String firstpage,realname;
    TextView hi;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Adad.initialize(getApplicationContext());
        setContentView(R.layout.main);

            final RelativeLayout exdatela = (RelativeLayout) findViewById(R.id.exdatela);
            Button download = (Button) findViewById(R.id.download);
            download.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Uri uri = Uri.parse("http://saoshyant.net/wave/appdownload/Wave.apk");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            y = 2017;
            m = 1;
            d = 15;

            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hi = (TextView) findViewById(R.id.hi);


            home = new Intent(getApplicationContext(), MainActivity.class);
            home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PhoneLogin = new Intent(getApplicationContext(), selectcountry.class);
            PhoneLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            realnamelogin = new Intent(getApplicationContext(), Phonelogin_2.class);
            realnamelogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
            HashMap<String, String> user;
            if (db1.getRowCount() > 0) {
                user = db1.getUserDetails();

                firstpage = user.get("firstpage");
                realname = user.get("realname");
                String hi_1 = "Salam ";
                hi.setText(hi_1  + realname);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    public void run() {


                        if (year >= y) {
                            if (year > y) {

                                exdatela.setVisibility(View.VISIBLE);

                            } else if (year == y) {
                                if (month > m) {
                                    exdatela.setVisibility(View.VISIBLE);

                                } else if (month == m) {

                                    if (day >= d) {
                                        exdatela.setVisibility(View.VISIBLE);

                                    } else {
                                        switch (firstpage) {
                                            case "home":

                                                startActivity(home);

                                                finish();
                                                break;
                                            case "realnamelogin":

                                                startActivity(realnamelogin);

                                                finish();
                                                break;
                                            default:

                                                startActivity(PhoneLogin);

                                                finish();
                                                break;
                                        }
                                    }
                                } else {

                                    switch (firstpage) {
                                        case "home":

                                            startActivity(home);

                                            finish();
                                            break;
                                        case "realnamelogin":

                                            startActivity(realnamelogin);

                                            finish();
                                            break;
                                        default:

                                            startActivity(PhoneLogin);

                                            finish();
                                            break;
                                    }
                                }

                            }

                        } else if ("home".equals(firstpage)) {

                            startActivity(home);

                            finish();
                        } else if ("realnamelogin".equals(firstpage)) {

                            startActivity(realnamelogin);

                            finish();
                        } else {

                            startActivity(PhoneLogin);

                            finish();
                        }
                    }
                }, 2000);
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    public void run() {
                        startActivity(PhoneLogin);

                        finish();
                    }
                }, 2000);

            }
        }





}
