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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import java.util.HashMap;
import java.util.Map;

import net.saoshyant.wave.R;
import net.saoshyant.wave.app.DatabaseHandler;
import net.saoshyant.wave.app.MyApplication;
import net.saoshyant.wave.app.cropper.CropImageView;


public class Support extends Activity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 3;
    EditText inputsubject, inputfeedback;
    ProgressBar nprog;
    ImageButton btnsend, btnback, btnpicsetdel;
    String imagename = "";
    int serverResponseCode = 0;
    ProgressDialog dialogProgress = null;
    public static final String URL_REQUEST = "http://saoshyant.net/wave/support.php";
    String upLoadServerUri = "http://saoshyant.net/uploadsupportimage.php";
    Bitmap croppedImage;
    CropImageView cropImageView;
    File f;
    private ImageView mImageView;
    private Uri mImageCaptureUri;
    private Dialog cropperdialog;
    private static CoordinatorLayout coordinatorLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id .coordinatorLayout);
        btnsend = (ImageButton) findViewById(R.id.send);
        btnback = (ImageButton) findViewById(R.id.back);
        btnpicsetdel = (ImageButton) findViewById(R.id.btnpicsetdel);
        nprog = (ProgressBar) findViewById(R.id.progressBar1);
        inputsubject = (EditText) findViewById(R.id.subject);
        inputfeedback = (EditText) findViewById(R.id.feedback);
        mImageView = (ImageView) findViewById(R.id.uploadimage);
        btnsend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                btnsend.setVisibility(View.INVISIBLE);
                nprog.setVisibility(View.VISIBLE);
                if (inputfeedback.getText().toString().length() < 1) {
                    Snackbar.make(coordinatorLayout, R.string.insertfeedback, Snackbar.LENGTH_LONG).show();
                    btnsend.setVisibility(View.VISIBLE);
                    nprog.setVisibility(View.INVISIBLE);

                } else {
                    Processsupport();
                }
            }

        });

        btnback.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                finish();

            }

        });
        //111111111

        final String[] items = new String[]{getText(R.string.takecamera).toString(), getText(R.string.takegallery).toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.selectpic);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_chichi_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (item == 1) { //pick from file
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "completeaction"), PICK_FROM_FILE);
                } else if (item == 2) {
                }
            }
        });

        final AlertDialog dialog = builder.create();
        btnpicsetdel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (imagename.equals("")) {
                    dialog.show();
                } else {
                    imagename = "";
                    mImageView.setImageResource(R.drawable.camera_icon);

                    btnpicsetdel.setImageResource(R.drawable.add_icon);
                }

            }

        });
        cropperdialog = new Dialog(Support.this);
        cropperdialog.setContentView(R.layout.cropper);
        cropperdialog.setTitle("Crop image");
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
                mImageView.setImageBitmap(croppedImage);
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
                dialogProgress = ProgressDialog.show(Support.this, "", getText(R.string.uploadingstart).toString(), true);
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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {

            case PICK_FROM_CAMERA:
                Bitmap PICK_FROM_CAMERA_bitmap = null;
                try {
                    PICK_FROM_CAMERA_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
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
                Bitmap PICK_FROM_FILE_bitmap = null;
                try {
                    PICK_FROM_FILE_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cropImageView.setImageBitmap(PICK_FROM_FILE_bitmap);
                cropImageView.setInitialAttributeValues(2, true, 10, 10);
                cropImageView.setGuidelines(2);
                cropperdialog.show();


                break;


        }


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
                    mImageView.setImageResource(R.drawable.camera_icon);
                 //   Toast.makeText(getApplicationContext(), "sourcefilenotexist", Toast.LENGTH_SHORT).show();
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
                            Snackbar.make(coordinatorLayout, R.string.succes, Snackbar.LENGTH_LONG).show();
                            imagename = fileName;
                            btnpicsetdel.setImageResource(R.drawable.delete_icon);

                            if (f.exists())
                                f.delete();
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
                        mImageView.setImageResource(R.drawable.camera_icon);
                      //  Toast.makeText(Support.this, "MalformedURLException Exception : check script url.", Toast.LENGTH_SHORT).show();
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
                        mImageView.setImageResource(R.drawable.camera_icon);
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        Snackbar.make(coordinatorLayout, R.string.errorconection, Snackbar.LENGTH_LONG).show();
                    }
                });
                //    Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
            dialogProgress.dismiss();
            return serverResponseCode;

        } // End else block 
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    public  void Processsupport() {
        String idno, code,profilepic,name,subject,feedback;

        subject = inputsubject.getText().toString();
        feedback = inputfeedback.getText().toString();
        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

        HashMap<String, String> user;
        user = db1.getUserDetails();
        idno = user.get("idno");
        profilepic = user.get("profileimage");
        name = user.get("realname");
        code = user.get("code");
        final Map<String, String> params = new HashMap<>();
        params.put("tag", "support");
        params.put("id", idno);
        params.put("code", code);
        params.put("profilepic", profilepic);
        params.put("name", name);
        params.put("subject", subject);
        params.put("feedback", feedback);
        params.put("imagename", imagename);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_REQUEST, new Response.Listener<String>() {
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
                            Snackbar.make(coordinatorLayout, R.string.succes, Snackbar.LENGTH_LONG).show();
                            finish();
                        } else {
                            Snackbar.make(coordinatorLayout, R.string.errorproblem, Snackbar.LENGTH_LONG).show();
                        }


                    }

                } catch (JSONException e) {
                 //   Toast.makeText(getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        Snackbar.make(coordinatorLayout, R.string.errorconection, Snackbar.LENGTH_LONG).show();
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



}
