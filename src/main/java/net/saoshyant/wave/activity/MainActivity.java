package net.saoshyant.wave.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.Tracker;

import net.saoshyant.wave.R;
import net.saoshyant.wave.activity.login.Phonelogin_1;
import net.saoshyant.wave.app.DatabaseHandler;
import net.saoshyant.wave.app.MyApplication;
import net.saoshyant.wave.app.cropper.CropImageView;
import net.saoshyant.wave.fragments.Geyhan_Fragment;
import net.saoshyant.wave.fragments.My_Wave_Fragment;
import net.saoshyant.wave.fragments.Station_Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import co.ronash.pushe.Pushe;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    ProgressDialog dialogProgress = null;
    Bitmap croppedImage;
    CropImageView cropImageView;
    File f;
    private int serverResponseCode = 0;
    private Uri mImageCaptureUri;
    public static final String URL_REQUEST_editname = "http://saoshyant.net/wave/editname.php";
    public static final String URL_REQUEST_logout = "http://saoshyant.net/wave/logout.php";
    private Dialog cropperdialog;
    private TabLayout tabLayout;
    private NetworkImageView profilePic;
    private ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    EditText inputName;
    TextInputLayout   inputLayoutName;
    ImageButton ok;
    DatabaseHandler db1;
    String upLoadServerUri = "http://saoshyant.net/upload.php";
  static   MediaPlayer player;
    private static long back_pressed;
    private static CoordinatorLayout coordinatorLayout;
      Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pushe.initialize(getApplicationContext(),true);
        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id .coordinatorLayout);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View view = mInflater.inflate(R.layout.toolbar, null);
        getSupportActionBar().setCustomView(view);
        //profile
         db1 = new DatabaseHandler(getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
        String realname = user.get("realname");
        String profileimage = user.get("profileimage");
        inputName = (EditText)view. findViewById(R.id.input_name);
        inputName.setText(realname);
        inputLayoutName = (TextInputLayout)view. findViewById(R.id.input_layout_name);
        final ProgressBar progressBar =(ProgressBar)view. findViewById(R.id.progressBar);


        profilePic = (NetworkImageView) view.findViewById(R.id.profile);
        if (Objects.equals(profileimage, "")) {
            profilePic.setImageUrl("http://saoshyant.net/profileimages/ic_profile.png", imageLoader);
        } else {
            profilePic.setImageUrl("http://saoshyant.net/profileimages/" + profileimage, imageLoader);
        }



        profilePic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Processimage();
            }
        });


         ok = (ImageButton) view.findViewById(R.id.search);

        ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                    if (inputName.getText().toString().length() > 4 & inputName.getText().toString().length() < 21) {
                        inputLayoutName.setErrorEnabled(false);
                        requestFocus(profilePic);
                        ok.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        String idno, realname, code;
                        realname = inputName.getText().toString();
                        HashMap<String, String> user;
                        user = db1.getUserDetails();
                        idno = user.get("idno");
                        code = user.get("code");
                        final Map<String, String> params = new HashMap<>();
                        params.put("tag", "editname");
                        params.put("id", idno);
                        params.put("code", code);
                        params.put("realname", realname);
                        Log.d(Phonelogin_1.class.getSimpleName(), params.toString());
                        StringRequest strReq = new StringRequest(Request.Method.POST, URL_REQUEST_editname, new Response.Listener<String>() {
                            //response from the server
                            @Override
                            public void onResponse(String response) {
                                Log.d(Phonelogin_1.class.getSimpleName(), response);
                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    JSONArray feedArray = responseObj.getJSONArray("user");
                                    JSONObject feedObj = (JSONObject) feedArray.get(0);

                                    String KEY_SUCCESS = "success";
                                    String KEY_REALNAME = "realname";
                                    String KEY_code = "code";
                                    String KEY_BIO = "bio";
                                    String KEY_CONTRYCODE = "contrycode";
                                    String KEY_profileimage = "profileimages";
                                    String KEY_username = "username";
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
                                            db.addUser(feedObj.getString(KEY_ID), feedObj.getString(KEY_code), feedObj.getString(KEY_REALNAME),
                                                    "home", feedObj.getString(KEY_BIO),
                                                    feedObj.getString(KEY_CONTRYCODE),
                                                    feedObj.getString(KEY_profileimage), feedObj.getString(KEY_username)
                                                    , feedObj.getString(KEY_frequency));

                                            ok.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.INVISIBLE);
                                        } else {
                                            ok.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Snackbar.make(coordinatorLayout, R.string.errorproblem, Snackbar.LENGTH_LONG).show();
                                        }


                                    }

                                } catch (JSONException e) {
                                    ok.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    //  Toast.makeText(getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //  Log.e(TAG, "Error1: " + error.getMessage());
                                        Snackbar.make(coordinatorLayout, R.string.errorconection, Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                        ) {

                            /* Passing user parameters to our server
                             * @return*/
                            @Override
                            protected Map<String, String> getParams() {
                                // Log.e(TAG, "Posting params: " + params.toString());
                                return params;
                            }
                        };
                        // Adding request to request queue
                        MyApplication.getInstance().addToRequestQueue(strReq);
                    }else {
                        Snackbar.make(coordinatorLayout, R.string.errorrname, Snackbar.LENGTH_LONG).show();
                    }


            }
        });
        inputName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0)
                   ok.setVisibility(View.VISIBLE);
            }
        });


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    // Adding custom view to tab

    private void setupTabIcons() {

        TextView tabGeyhan = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabGeyhan.setText(R.string.geyhan);
        tabGeyhan.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.geyhan, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabGeyhan);

        TextView tabMy_Wave = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabMy_Wave.setText(R.string.my_wave);
        tabMy_Wave.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.my_wave, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabMy_Wave);

        TextView tabStation = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabStation.setText(R.string.station);
        tabStation.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.station, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabStation);
    }

// Adding fragments to ViewPager
static ViewPagerAdapter adapter;
    private void setupViewPager(ViewPager viewPager) {
         adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Geyhan_Fragment(), getText(R.string.geyhan).toString());
        adapter.addFrag(new My_Wave_Fragment(),getText(R.string.my_wave).toString());
        adapter.addFrag(new Station_Fragment(), getText(R.string.station).toString());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_about) {
            Intent upanel = new Intent(MainActivity.this, About.class);
            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(upanel);

            return true;
        }else if (id == R.id.action_support)
        {
            Intent upanel = new Intent(MainActivity.this, Support.class);
            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(upanel);

            return true;
        }else if (id == R.id.action_donate)
        {
            Intent upanel = new Intent(MainActivity.this, Donate.class);
            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(upanel);
            return true;
        }else if (id == R.id.action_policies)
        {
            Intent upanel = new Intent(MainActivity.this, Policies.class);
            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(upanel);
            return true;
        }else if (id == R.id.action_logout)
        {

            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog);
            TextView textview = (TextView) dialog.findViewById(R.id.text);
            textview.setText(R.string.logouterror);
            dialog.setTitle(R.string.logout);
            Button yes = (Button) dialog.findViewById(R.id.yes);
            Button no = (Button) dialog.findViewById(R.id.no);

            yes.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Processlogout();
                }
            });
            no.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
    private void Processimage() {
        //انتخاب عکس جدید برای پروفایل
        final String[] items = new String[]{getText(R.string.takecamera).toString(),getText(R.string.takegallery).toString() };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.selectpic);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { //pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "completeaction"), PICK_FROM_FILE);
                }
            }
        });

        final AlertDialog dialog = builder.create();


                dialog.show();


        cropperdialog = new Dialog(this);
        cropperdialog.setContentView(R.layout.cropper);
        // Initialize components of the app
        cropImageView = (CropImageView) cropperdialog.findViewById(R.id.CropImageView);
        //mImageCaptureUri
        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setInitialAttributeValues(2, true, 10, 10);
        cropImageView.setGuidelines(2);
        ImageButton cropButton = (ImageButton) cropperdialog.findViewById(R.id.Button_crop);

        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedImage();
                profilePic.setImageBitmap(croppedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                croppedImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

//you can create a new file name "test.jpg" in sdcard folder.
                f = new File(Environment.getExternalStorageDirectory(), "chichi_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//write the bytes in file
                FileOutputStream fo = null;
                try {
                    fo = new FileOutputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    assert fo != null;
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }

// remember close de FileOutput
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //   final File finalFile1 = new File(mImageCaptureUri.getPath());
                dialogProgress = ProgressDialog.show(MainActivity.this, "", "uploadingfile", true);
                new Thread(new Runnable() {

                    public void run() {
                       runOnUiThread(new Runnable() {

                           public void run() {
                           }
                       });

                        uploadFile(f);

                    }
                }).start();

                cropperdialog.dismiss();
            }
        });
        ImageButton Buttoncancell = (ImageButton) cropperdialog.findViewById(R.id.Button_cancell);

        Buttoncancell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mImageCaptureUri = null;
                cropperdialog.dismiss();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {

            case PICK_FROM_CAMERA:
                Bitmap PICK_FROM_CAMERA_bitmap = null;
                try {
                    PICK_FROM_CAMERA_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cropImageView.setImageBitmap(PICK_FROM_CAMERA_bitmap);
                cropImageView.setInitialAttributeValues(2, true, 10, 10);
                cropImageView.setGuidelines(2);
                cropperdialog.show();

                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();

                mImageCaptureUri = data.getData();
                Bitmap PICK_FROM_FILE_bitmap = null;
                try {
                    PICK_FROM_FILE_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cropImageView.setImageBitmap(PICK_FROM_FILE_bitmap);
                cropImageView.setInitialAttributeValues(2, true, 10, 10);
                cropImageView.setGuidelines(2);
                cropperdialog.show();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();

                if (extras != null) {
                    final File finalFile1 = new File(mImageCaptureUri.getPath());

                    System.out.println(mImageCaptureUri);

                  //   dialog = ProgressDialog.show(MainActivity.this, "", "uploadingfile", true);
                    new Thread(new Runnable() {

                        public void run() {
                            runOnUiThread(new Runnable() {

                                public void run() {
                                Snackbar.make(coordinatorLayout, R.string.uploadingstart, Snackbar.LENGTH_LONG).show();
                                }
                            });

                            uploadFile(finalFile1);

                        }
                    }).start();
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) {
                    f.delete();
                }

                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //uploadFile

    public int uploadFile(File finalFile) {

        final String fileName = finalFile.getName();
        HttpURLConnection conn;
        DataOutputStream dos;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        if (!finalFile.isFile()) {

            dialogProgress.dismiss();

            // Log.e("uploadFile", "Source File not exist ");

            runOnUiThread(new Runnable() {

                public void run() {
                    if (f.exists())
                        f.delete();
                    Snackbar.make(coordinatorLayout, "sourcefilenotexist", Snackbar.LENGTH_LONG).show();
                }
            });

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(finalFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {

                        public void run() {
                            dialogProgress.dismiss();

                            HashMap<String, String> user;
                            user = db1.getUserDetails();
                            String idno = user.get("idno");
                            String code = user.get("code");
                            String realname = user.get("realname");
                            String firstpage = user.get("firstpage");
                            String bio = user.get("bio");
                            String contrycode = user.get("contrycode");
                            String username = user.get("username");
                            String frequency = user.get("frequency");
                            profilePic.setImageUrl("http://saoshyant.net/profileimages/" + fileName, imageLoader);
                            db1.resetTables();
                            db1.addUser(idno,code, realname, firstpage, bio, contrycode, fileName, username, frequency);

                            final Map<String, String> params = new HashMap<>();
                            params.put("tag", "editprofileimage");
                            params.put("id", idno);
                            params.put("code", code);
                            params.put("profileimage", fileName);
                            Log.d(Phonelogin_1.class.getSimpleName(), params.toString());
                            StringRequest strReq = new StringRequest( Request.Method.POST, "http://saoshyant.net/wave/editprofileimage.php", new Response.Listener<String>()
                            {
                                //response from the server
                                @Override
                                public void onResponse (String response)
                                {
                                    Log.d(Phonelogin_1.class.getSimpleName(), response);
                                    try
                                    {
                                        JSONObject responseObj = new JSONObject(response);
                                        JSONArray feedArray = responseObj.getJSONArray("user");
                                        JSONObject feedObj = (JSONObject) feedArray.get(0);
                                        Snackbar.make(coordinatorLayout, R.string.succes, Snackbar.LENGTH_LONG).show();
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
                                                db.addUser(feedObj.getString(KEY_ID), feedObj.getString(KEY_code), feedObj.getString(KEY_REALNAME),
                                                        "home", feedObj.getString(KEY_BIO),
                                                        feedObj.getString(KEY_CONTRYCODE),
                                                        feedObj.getString(KEY_profileimage), feedObj.getString(KEY_username)
                                                        , feedObj.getString(KEY_frequency));

                                                if (f.exists())
                                                    f.delete();
                                            } else {
                                                Snackbar.make(coordinatorLayout,  R.string.errorproblem, Snackbar.LENGTH_LONG).show();
                                            }


                                        }

                                    }
                                    catch(JSONException e)
                                    {
                                       // Toast.makeText(getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse (VolleyError error)
                                        {
                                            //  Log.e(TAG, "Error1: " + error.getMessage());

                                            Snackbar.make(coordinatorLayout, R.string.errorconection, Snackbar.LENGTH_LONG).show();
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

                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialogProgress.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {

                      //  Toast.makeText(MainActivity.this, "MalformedURLException Exception : check script url.", Toast.LENGTH_SHORT).show();
                        if (f.exists())
                            f.delete();
                    }
                });

                //    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialogProgress.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        if (f.exists())
                            f.delete();

                     //   btnsend.setVisibility(View.VISIBLE);
                     //   nprog.setVisibility(View.INVISIBLE);

                         Snackbar.make(coordinatorLayout,  R.string.errorconection, Snackbar.LENGTH_LONG).show();
                    }
                });
                //    Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
            dialogProgress.dismiss();
            return serverResponseCode;

        } // End else block
    }
    public static void mediaplay(String file,String n, final String fragment, final String pid, final String userid)
    {
if(!(player ==null)) {
    if (player.isPlaying()) {
        player.stop();
        player.release();
        player=null;
        Fragment StationFragment = adapter.getItem(2);
        ((net.saoshyant.wave.fragments.Station_Fragment) StationFragment).playreset();
        Fragment My_Wave_Fragment = adapter.getItem(1);
        ((net.saoshyant.wave.fragments.My_Wave_Fragment) My_Wave_Fragment).playreset();
        Fragment GeyhanFragment = adapter.getItem(0);
        ((net.saoshyant.wave.fragments.Geyhan_Fragment) GeyhanFragment).playreset();
    }else{
        player=null;
    }
}
                if(Objects.equals(n, "play")){
                    Log.d("a:", "1");
                    player = new MediaPlayer();
                    try {
                        Log.d("a:", "2");
                        player.setDataSource(file);
                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("a:", "3-");
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        public void onPrepared(MediaPlayer mp) {
                            Log.d("a:", "3");
                            if (!(player == null)) {
                                player.start();
                                Log.d("a:", "4");
                                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                    public void onCompletion(MediaPlayer mp) {
                                        Log.d("a:", "5");
                                        if (Objects.equals(fragment, "station")) {

                                            Fragment StationFragment = adapter.getItem(2);


                                            ((net.saoshyant.wave.fragments.Station_Fragment) StationFragment).playnext();

                                        } else if (Objects.equals(fragment, "geyhan")) {
                                            play_geyhan(pid, userid, "play");
                                        } else if (Objects.equals(fragment, "fla_my_wave")) {
                                            Fragment My_Wave_Fragment = adapter.getItem(1);
                                            ((net.saoshyant.wave.fragments.My_Wave_Fragment) My_Wave_Fragment).playreset();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
                else if (Objects.equals(n, "stop"))
                    {
player=null;
                    }

            }
    public static void play_geyhan(String pid, final String id,final String whatdo) {

        final Map<String, String> params = new HashMap<>();
        params.put("tag", "play_geyhan");
        params.put("id", id);
        params.put("pid", pid);
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://saoshyant.net/wave/geyhan.php", new Response.Listener<String>() {
            //response from the server
            @Override
            public void onResponse(String response) {
                Log.d("", response);
                try {
                    JSONObject responseObj = new JSONObject(response);
                    JSONArray feedArray = responseObj.getJSONArray("post");
                    JSONObject feedObj = (JSONObject) feedArray.get(0);

                    if (feedObj.getString("success") != null) {
                        String res = feedObj.getString("success");

                        if (Integer.parseInt(res) == 1) {
                            if(Objects.equals(whatdo, "play")) {
                             //   play.setImageResource(R.drawable.stop);
                            }else if(Objects.equals(whatdo, "stop"))
                            {
                              //  play.setImageResource(R.drawable.play);
                            }
                            String pid = feedObj.getString("id");
                            //  String userid = feedObj.getString("userid");
                            String wave = feedObj.getString("wave");
                            String   fileURL="http://saoshyant.net/voice/"+wave;
                            mediaplay(fileURL, whatdo, "geyhan", pid,id);
                            //sendto mainactivity
                            //play
                        }else if(Integer.parseInt(res) == 2){
                             Fragment GeyhanFragment = adapter.getItem(1);

                             ((net.saoshyant.wave.fragments.Geyhan_Fragment) GeyhanFragment).playreset();
                        }
                        else {
                            Snackbar.make(coordinatorLayout,  R.string.errorproblem, Snackbar.LENGTH_LONG).show();
                        }


                    }

                } catch (JSONException e) {
                 //   Toast.makeText(activity.getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        Snackbar.make(coordinatorLayout,  R.string.errorconection, Snackbar.LENGTH_LONG).show();
                    }
                }
        ) {

            /* Passing user parameters to our server
             * @return*/
            @Override
            protected Map<String, String> getParams() {
                // Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

    }
    public  void Processlogout() {
        String idno, code;

        final DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

        HashMap<String, String> user;
        user = db1.getUserDetails();
        idno = user.get("idno");
        code = user.get("code");
        final Map<String, String> params = new HashMap<>();
        params.put("tag", "logout");
        params.put("id", idno);
        params.put("code", code);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_REQUEST_logout, new Response.Listener<String>() {
            //response from the server
            @Override
            public void onResponse(String response) {
                Log.d("a:", response);
                try {
                    JSONObject responseObj = new JSONObject(response);
                    JSONArray feedArray = responseObj.getJSONArray("user");
                    JSONObject feedObj = (JSONObject) feedArray.get(0);

                    if (feedObj.getString("success") != null) {
                        String res = feedObj.getString("success");

                        if (Integer.parseInt(res) == 1) {
                            db1.resetTables();
                            Intent upanel = new Intent(getApplicationContext(), Main.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                            finish();
                        } else {
                            Snackbar.make(coordinatorLayout, "errorproblem", Snackbar.LENGTH_LONG).show();
                        }


                    }

                } catch (JSONException e) {
                   // Toast.makeText(getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        Snackbar.make(coordinatorLayout,  R.string.errorconection, Snackbar.LENGTH_LONG).show();
                    }
                }
        ) {

            /* Passing user parameters to our server
             * @return*/
            @Override
            protected Map<String, String> getParams() {
                // Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

    }
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
mediaplay("","stop","","","");
            super.onBackPressed();
        } else {
            Snackbar.make(coordinatorLayout, R.string.onback, Snackbar.LENGTH_LONG).show();
            back_pressed = System.currentTimeMillis();
        }
    }
public static void snackbar(String text)
{

    Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).show();
}

}














































