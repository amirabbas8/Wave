package net.saoshyant.wave.activity.login;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import net.saoshyant.wave.R;
import net.saoshyant.wave.activity.Policies;
import net.saoshyant.wave.app.CountryPicker;
import net.saoshyant.wave.app.CountryPickerListener;

import java.util.Objects;

public class selectcountry extends FragmentActivity {



    String countrycode="0";
    TextView  countryname,login,signup;
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
        setContentView(R.layout.selectcountry);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id .coordinatorLayout);
        // creating connection detector class instance

        countryname = (TextView) findViewById(R.id.countryname);
        login = (TextView) findViewById(R.id.login);
        signup = (TextView) findViewById(R.id.signup);
        countryname.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final CountryPicker picker = CountryPicker.newInstance(getText(R.string.cyc).toString());
                picker.setListener(new CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code, String dialCode) {
                        countrycode = dialCode;
                        countryname.setText(name);
                        //     Toast.makeText(selectcountry.this, "Country Name: " + name + " - Code: " + code  + " - Currency: "    + CountryPicker.getCurrencyCode(code) + " - Dial Code: " + dialCode, Toast.LENGTH_SHORT).show();

                        if (Objects.equals("0", countrycode)) {
                            Snackbar.make(coordinatorLayout, R.string.cyc, Snackbar.LENGTH_LONG).show();
                        } else {

                            if(Objects.equals(countrycode, "+98")){
                                login.setVisibility(View.INVISIBLE);
                                signup.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(getBaseContext(), Phonelogin_1.class);
                                intent.putExtra("countrycode", "+98");
                                startActivity(intent);
                                finish();
                            }else{
                                login.setVisibility(View.VISIBLE);
                                signup.setVisibility(View.VISIBLE);
                                signup.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        Intent intent = new Intent(getBaseContext(), selectpfn.class);
                                        intent.putExtra("countrycode", countrycode);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                login.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        Intent intent = new Intent(getBaseContext(), loginpfn.class);
                                        intent.putExtra("countrycode", countrycode);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                        picker.dismiss();
                    }
                });

                picker.show(getSupportFragmentManager(), "COUNTRY_CODE_PICKER");
            }
        });
        TextView policiesaccept = (TextView) findViewById(R.id.policiesaccept);

        policiesaccept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent upanel5 = new Intent(selectcountry.this, Policies.class);
                upanel5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel5);
            }
        });



    }












}
