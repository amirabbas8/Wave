package net.saoshyant.wave.fragments;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xingliuhua.lib_refreshlayout.RefreshLayout;

import net.saoshyant.wave.R;
import net.saoshyant.wave.app.DatabaseHandler;
import net.saoshyant.wave.app.Feedlistadapter.FeedListAdapter_my_wave;
import net.saoshyant.wave.app.MyApplication;
import net.saoshyant.wave.app.data.FeedItem_my_wave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.saoshyant.wave.activity.MainActivity.snackbar;


public class My_Wave_Fragment extends Fragment {

    public My_Wave_Fragment() {
        // Required empty public constructor
    }
    private ListView listView_my_wave;
    private FeedListAdapter_my_wave listAdapter_my_wave;
    private List<FeedItem_my_wave> FeedItem_my_wave;
    private String pid = "0";
    private int pidint = 0;
    ImageButton record_play, delete,send ;
    MediaRecorder recorder ;
    String n;
    MediaPlayer mediaPlayer;
    FileInputStream fis;
    final String filename = Environment.getExternalStorageDirectory().getAbsolutePath() +"/upload/wave_" + String.valueOf(System.currentTimeMillis()) + ".mp3";
  // File f;
    private int serverResponseCode = 0;
    DatabaseHandler db1;
    String upLoadServerUri = "http://saoshyant.net/uploadvoice.php";

    public static final String URL_REQUEST_addwave = "http://saoshyant.net/wave/addwave.php";
    public static final String URL_REQUEST_get_feedlist_my_wave = "http://saoshyant.net/wave/get_feedlist_my_wave.php";
    TextView timer;
    CountDownTimer countDownTimer;
    long timelong;
    EditText input_text;
    String text;
    File file;
    ProgressBar progressBar2;
    RelativeLayout relativeLayout4;
    RefreshLayout refreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_wave, container, false);
        db1 = new DatabaseHandler(getActivity().getApplicationContext());
        record_play = (ImageButton)rootView.findViewById(R.id.rec_play);
        delete = (ImageButton)rootView.findViewById(R.id.delete);
        send = (ImageButton)rootView.findViewById(R.id.send);
        progressBar2 = (ProgressBar)rootView.findViewById(R.id.progressBar2);
        timer = (TextView)rootView.findViewById(R.id.timer);
        relativeLayout4 = (RelativeLayout)rootView.findViewById(R.id.relativeLayout4);
        input_text= (EditText) rootView.findViewById(R.id.text);


        record_play.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                relativeLayout4.setVisibility(View.VISIBLE);
                recorder = new MediaRecorder();
                try {
                    synchronized (this) {
                        String state = android.os.Environment.getExternalStorageState();
                        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                            throw new IOException("SD Card is not mounted.  It is " + state + ".");
                        }

                        // make sure the directory we plan to store the recording in exists
                        File directory = new File(filename).getParentFile();
                        if (!directory.exists() && !directory.mkdirs()) {
                            throw new IOException("Path to file could not be created.");
                        }

                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(filename);
                        recorder.prepare();
                        recorder.start();
                        record_play.setImageResource(R.drawable.stop_record);
                         countDownTimer    =      new CountDownTimer(10000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                timer.setText( "" + millisUntilFinished / 1000);
                                timelong=10-(millisUntilFinished / 1000);
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                timer.setText( "10S");
                                send.setVisibility(View.VISIBLE);
                                delete.setVisibility(View.VISIBLE);
                                recorder.stop();
                                recorder.release();
                                n="play";
                                record_play.setImageResource(R.drawable.play);



                                mediaPlayer = new MediaPlayer();
                                try {

                                    file = new File(filename);
                                    fis = new FileInputStream(file);
                                    mediaPlayer.setDataSource(fis.getFD());
                                } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                    public void onCompletion(MediaPlayer mp) {
                                        mediaPlayer.stop();
                                        n="play";
                                        record_play.setImageResource(R.drawable.play);
                                    }
                                });
                                send.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        send.setVisibility(View.INVISIBLE);
                                        progressBar2.setVisibility(View.VISIBLE);
                                        uploadFile(file);

                                    }
                                });

                                record_play.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        if(Objects.equals(n, "play")){

                                            try {

                                                mediaPlayer.prepare();
                                                mediaPlayer.start();
                                                n="stop";
                                                record_play.setImageResource(R.drawable.stop);
                                            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else {
                                            if (Objects.equals(n, "stop")) try {
                                                mediaPlayer.reset();
                                                mediaPlayer.setDataSource(fis.getFD());
                                                n = "play";
                                                record_play.setImageResource(R.drawable.play);


                                            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                            }

                        }.start();
                        record_play.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                countDownTimer.cancel();
                                timer.setText(timelong+"s");
                                send.setVisibility(View.VISIBLE);
                                delete.setVisibility(View.VISIBLE);
                                recorder.stop();
                                recorder.release();
                                v.setPressed(false);
                                n="play";
                                record_play.setImageResource(R.drawable.play);



                                 mediaPlayer = new MediaPlayer();
                                try {

                                     file = new File(filename);
                                     fis = new FileInputStream(file);
                                    mediaPlayer.setDataSource(fis.getFD());
                                } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                    public void onCompletion(MediaPlayer mp) {
                                        mediaPlayer.stop();
                                        n="play";
                                        record_play.setImageResource(R.drawable.play);
                                    }
                                    });
                                send.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                  send.setVisibility(View.INVISIBLE);
                                  progressBar2.setVisibility(View.VISIBLE);
                                    uploadFile(file);

                                    }
                                });

                                record_play.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                if(Objects.equals(n, "play")){

                                    try {

                                        mediaPlayer.prepare();
                                        mediaPlayer.start();
                                        n="stop";
                                        record_play.setImageResource(R.drawable.stop);
                                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    if (Objects.equals(n, "stop")) try {
                                        mediaPlayer.reset();
                                        mediaPlayer.setDataSource(fis.getFD());
                                        n = "play";
                                        record_play.setImageResource(R.drawable.play);


                                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                relativeLayout4.setVisibility(View.INVISIBLE);
                send.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
                timer.setText("10");
                mediaPlayer.reset();
                record_play.setImageResource(R.drawable.record);
                recorder = new MediaRecorder();
                record_play.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        relativeLayout4.setVisibility(View.VISIBLE);
                        recorder = new MediaRecorder();
                        try {
                            synchronized (this) {
                                String state = android.os.Environment.getExternalStorageState();
                                if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                                    throw new IOException("SD Card is not mounted.  It is " + state + ".");
                                }

                                // make sure the directory we plan to store the recording in exists
                                File directory = new File(filename).getParentFile();
                                if (!directory.exists() && !directory.mkdirs()) {
                                    throw new IOException("Path to file could not be created.");
                                }

                                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                                recorder.setOutputFile(filename);
                                recorder.prepare();
                                recorder.start();
                                record_play.setImageResource(R.drawable.stop_record);
                                countDownTimer    =      new CountDownTimer(10000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        timer.setText( "" + millisUntilFinished / 1000);
                                        timelong=10-(millisUntilFinished / 1000);
                                        //here you can have your logic to set text to edittext
                                    }

                                    public void onFinish() {
                                        timer.setText( "10S");
                                        send.setVisibility(View.VISIBLE);
                                        delete.setVisibility(View.VISIBLE);
                                        recorder.stop();
                                        recorder.release();
                                        n="play";
                                        record_play.setImageResource(R.drawable.play);



                                        mediaPlayer = new MediaPlayer();
                                        try {

                                            file = new File(filename);
                                            fis = new FileInputStream(file);
                                            mediaPlayer.setDataSource(fis.getFD());
                                        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                            e.printStackTrace();
                                        }
                                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                            public void onCompletion(MediaPlayer mp) {
                                                mediaPlayer.stop();
                                                n="play";
                                                record_play.setImageResource(R.drawable.play);
                                            }
                                        });
                                        send.setOnClickListener(new View.OnClickListener() {

                                            public void onClick(View v) {
                                                send.setVisibility(View.INVISIBLE);
                                                progressBar2.setVisibility(View.VISIBLE);
                                                uploadFile(file);

                                            }
                                        });

                                        record_play.setOnClickListener(new View.OnClickListener() {

                                            public void onClick(View v) {
                                                if(Objects.equals(n, "play")){

                                                    try {

                                                        mediaPlayer.prepare();
                                                        mediaPlayer.start();
                                                        n="stop";
                                                        record_play.setImageResource(R.drawable.stop);
                                                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else {
                                                    if (Objects.equals(n, "stop")) try {
                                                        mediaPlayer.reset();
                                                        mediaPlayer.setDataSource(fis.getFD());
                                                        n = "play";
                                                        record_play.setImageResource(R.drawable.play);


                                                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });

                                    }

                                }.start();
                                record_play.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View v) {
                                        countDownTimer.cancel();
                                        timer.setText(timelong+"s");
                                        send.setVisibility(View.VISIBLE);
                                        delete.setVisibility(View.VISIBLE);
                                        recorder.stop();
                                        recorder.release();
                                        v.setPressed(false);
                                        n="play";
                                        record_play.setImageResource(R.drawable.play);



                                        mediaPlayer = new MediaPlayer();
                                        try {

                                            file = new File(filename);
                                            fis = new FileInputStream(file);
                                            mediaPlayer.setDataSource(fis.getFD());
                                        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                            e.printStackTrace();
                                        }
                                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                            public void onCompletion(MediaPlayer mp) {
                                                mediaPlayer.stop();
                                                n="play";
                                                record_play.setImageResource(R.drawable.play);
                                            }
                                        });
                                        send.setOnClickListener(new View.OnClickListener() {

                                            public void onClick(View v) {
                                                send.setVisibility(View.INVISIBLE);
                                                progressBar2.setVisibility(View.VISIBLE);
                                                uploadFile(file);

                                            }
                                        });

                                        record_play.setOnClickListener(new View.OnClickListener() {

                                            public void onClick(View v) {
                                                if(Objects.equals(n, "play")){

                                                    try {

                                                        mediaPlayer.prepare();
                                                        mediaPlayer.start();
                                                        n="stop";
                                                        record_play.setImageResource(R.drawable.stop);
                                                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else {
                                                    if (Objects.equals(n, "stop")) try {
                                                        mediaPlayer.reset();
                                                        mediaPlayer.setDataSource(fis.getFD());
                                                        n = "play";
                                                        record_play.setImageResource(R.drawable.play);


                                                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        listView_my_wave = (ListView) rootView.findViewById(R.id.list);
       // listView_my_wave.setVisibility(View.INVISIBLE);
        FeedItem_my_wave = new ArrayList<>();

        listAdapter_my_wave = new FeedListAdapter_my_wave(getActivity(), FeedItem_my_wave);
        listView_my_wave.setAdapter(listAdapter_my_wave);




          refreshLayout = (RefreshLayout) rootView. findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedItem_my_wave.clear();
                listAdapter_my_wave.notifyDataSetChanged();
                get_feedlist_my_wave("get_feedlist_my_wave");



            }

            @Override
            public void onLoadmore() {
                if (9 < FeedItem_my_wave.size()) {
                    //listView_my_wave.setVisibility(View.VISIBLE);
                    get_feedlist_my_wave("get_feedlist_my_wave_loadmore");

                }
                           // refreshLayout.setNeedLoadMore(false);

                      //  refreshLayout.setLoadMoreing(false);
            }
        });
        get_feedlist_my_wave("get_feedlist_my_wave");
   return rootView;
    }



    public void uploadFile(final File finalFile) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... arg0) {

                final String fileName2 = finalFile.getName();
                HttpURLConnection conn;
                DataOutputStream dos;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;

                if (!finalFile.isFile()) {

                    //   dialogProgress.dismiss();

                    // Log.e("uploadFile", "Source File not exist ");

                    getActivity().   runOnUiThread(new Runnable() {

                        public void run() {
                            //     if (f.exists())
                            //      f.delete();
                            send.setVisibility(View.VISIBLE);
                            progressBar2.setVisibility(View.INVISIBLE);
                        //    snackbar(getText(R.string.sourcefilenotexist).toString());
                        }
                    });

                    return null;

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
                        conn.setRequestProperty("uploaded_file", fileName2);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                                + fileName2 + "\"" + lineEnd);

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

                            getActivity().     runOnUiThread(new Runnable() {

                                public void run() {
                                    //dialogProgress.dismiss();

                                    HashMap<String, String> user;

                                    user = db1.getUserDetails();
                                    String idno = user.get("idno");
                                    String code = user.get("code");
                                    String realname = user.get("realname");
                                    String profileimage = user.get("profileimage");
                                    String contrycode = user.get("contrycode");
                                    text=input_text.getText().toString();
                                    final Map<String, String> params = new HashMap<>();
                                    params.put("tag", "addwave");
                                    params.put("id", idno);
                                    params.put("code", code);
                                    params.put("wave", fileName2);
                                    params.put("realname", realname);
                                    params.put("profileimage", profileimage);
                                    params.put("contrycode", contrycode);
                                    params.put("text", text);
                                    //  Log.d(Phonelogin_1.class.getSimpleName(), params.toString());
                                    StringRequest strReq = new StringRequest(Request.Method.POST, URL_REQUEST_addwave, new Response.Listener<String>() {
                                        //response from the server
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d("", response);
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

                                                        DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
                                                        /**
                                                         * Clear all previous data in SQlite database.
                                                         **/
                                                        db.resetTables();
                                                        db.addUser(feedObj.getString(KEY_ID), feedObj.getString(KEY_code), feedObj.getString(KEY_REALNAME),
                                                                "home", feedObj.getString(KEY_BIO),
                                                                feedObj.getString(KEY_CONTRYCODE),
                                                                feedObj.getString(KEY_profileimage), feedObj.getString(KEY_username)
                                                                , feedObj.getString(KEY_frequency));
                                                        snackbar(getText(R.string.succes).toString());
                                                          //     if (f.exists())
                                                        //        f.delete();
                                                        relativeLayout4.setVisibility(View.INVISIBLE);
                                                        send.setVisibility(View.INVISIBLE);
                                                        progressBar2.setVisibility(View.INVISIBLE);
                                                        delete.setVisibility(View.INVISIBLE);
                                                        timer.setText("10");
                                                        mediaPlayer.reset();
                                                        record_play.setImageResource(R.drawable.record);
                                                        recorder = new MediaRecorder();
                                                        record_play.setOnClickListener(new View.OnClickListener() {

                                                            public void onClick(View v) {
                                                                relativeLayout4.setVisibility(View.VISIBLE);
                                                                recorder = new MediaRecorder();
                                                                try {
                                                                    synchronized (this) {
                                                                        String state = android.os.Environment.getExternalStorageState();
                                                                        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                                                                            throw new IOException("SD Card is not mounted.  It is " + state + ".");
                                                                        }

                                                                        // make sure the directory we plan to store the recording in exists
                                                                        File directory = new File(filename).getParentFile();
                                                                        if (!directory.exists() && !directory.mkdirs()) {
                                                                            throw new IOException("Path to file could not be created.");
                                                                        }

                                                                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                                                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                                                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                                                                        recorder.setOutputFile(filename);
                                                                        recorder.prepare();
                                                                        recorder.start();
                                                                        record_play.setImageResource(R.drawable.stop_record);
                                                                        countDownTimer    =      new CountDownTimer(10000, 1000) {

                                                                            public void onTick(long millisUntilFinished) {
                                                                                timer.setText( "" + millisUntilFinished / 1000);
                                                                                timelong=10-(millisUntilFinished / 1000);
                                                                                //here you can have your logic to set text to edittext
                                                                            }

                                                                            public void onFinish() {
                                                                                timer.setText( "10S");
                                                                                send.setVisibility(View.VISIBLE);
                                                                                delete.setVisibility(View.VISIBLE);
                                                                                recorder.stop();
                                                                                recorder.release();
                                                                                n="play";
                                                                                record_play.setImageResource(R.drawable.play);



                                                                                mediaPlayer = new MediaPlayer();
                                                                                try {

                                                                                    file = new File(filename);
                                                                                    fis = new FileInputStream(file);
                                                                                    mediaPlayer.setDataSource(fis.getFD());
                                                                                } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                                                                    public void onCompletion(MediaPlayer mp) {
                                                                                        mediaPlayer.stop();
                                                                                        n="play";
                                                                                        record_play.setImageResource(R.drawable.play);
                                                                                    }
                                                                                });
                                                                                send.setOnClickListener(new View.OnClickListener() {

                                                                                    public void onClick(View v) {
                                                                                        send.setVisibility(View.INVISIBLE);
                                                                                        progressBar2.setVisibility(View.VISIBLE);
                                                                                        uploadFile(file);

                                                                                    }
                                                                                });

                                                                                record_play.setOnClickListener(new View.OnClickListener() {

                                                                                    public void onClick(View v) {
                                                                                        if(Objects.equals(n, "play")){

                                                                                            try {

                                                                                                mediaPlayer.prepare();
                                                                                                mediaPlayer.start();
                                                                                                n="stop";
                                                                                                record_play.setImageResource(R.drawable.stop);
                                                                                            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                        }
                                                                                        else {
                                                                                            if (Objects.equals(n, "stop")) try {
                                                                                                mediaPlayer.reset();
                                                                                                mediaPlayer.setDataSource(fis.getFD());
                                                                                                n = "play";
                                                                                                record_play.setImageResource(R.drawable.play);


                                                                                            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });

                                                                            }

                                                                        }.start();
                                                                        record_play.setOnClickListener(new View.OnClickListener() {

                                                                            public void onClick(View v) {
                                                                                countDownTimer.cancel();
                                                                                timer.setText(timelong+"s");
                                                                                send.setVisibility(View.VISIBLE);
                                                                                delete.setVisibility(View.VISIBLE);
                                                                                recorder.stop();
                                                                                recorder.release();
                                                                                v.setPressed(false);
                                                                                n="play";
                                                                                record_play.setImageResource(R.drawable.play);



                                                                                mediaPlayer = new MediaPlayer();
                                                                                try {

                                                                                    file = new File(filename);
                                                                                    fis = new FileInputStream(file);
                                                                                    mediaPlayer.setDataSource(fis.getFD());
                                                                                } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                                                                    public void onCompletion(MediaPlayer mp) {
                                                                                        mediaPlayer.stop();
                                                                                        n="play";
                                                                                        record_play.setImageResource(R.drawable.play);
                                                                                    }
                                                                                });
                                                                                send.setOnClickListener(new View.OnClickListener() {

                                                                                    public void onClick(View v) {
                                                                                        send.setVisibility(View.INVISIBLE);
                                                                                        progressBar2.setVisibility(View.VISIBLE);
                                                                                        uploadFile(file);

                                                                                    }
                                                                                });

                                                                                record_play.setOnClickListener(new View.OnClickListener() {

                                                                                    public void onClick(View v) {
                                                                                        if(Objects.equals(n, "play")){

                                                                                            try {

                                                                                                mediaPlayer.prepare();
                                                                                                mediaPlayer.start();
                                                                                                n="stop";
                                                                                                record_play.setImageResource(R.drawable.stop);
                                                                                            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                        }
                                                                                        else {
                                                                                            if (Objects.equals(n, "stop")) try {
                                                                                                mediaPlayer.reset();
                                                                                                mediaPlayer.setDataSource(fis.getFD());
                                                                                                n = "play";
                                                                                                record_play.setImageResource(R.drawable.play);


                                                                                            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                      //  Toast.makeText(getActivity().getApplicationContext(), "errorproblem", Toast.LENGTH_SHORT).show();
                                                    }


                                                }

                                            } catch (JSONException e) {
                                              //  Toast.makeText(getActivity().getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    //  Log.e(TAG, "Error1: " + error.getMessage());
                                                    snackbar(getText(R.string.errorconection).toString());
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

                            });
                        }

                        //close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (MalformedURLException ex) {

                        //dialogProgress.dismiss();
                        ex.printStackTrace();

                        getActivity().    runOnUiThread(new Runnable() {

                            public void run() {
                                send.setVisibility(View.VISIBLE);
                                progressBar2.setVisibility(View.INVISIBLE);
                             //   "MalformedURLException Exception : check script url.";
                                //  if (f.exists())
                                //     f.delete();
                            }
                        });

                        //    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                    } catch (Exception e) {

                        // dialogProgress.dismiss();
                        e.printStackTrace();
                        getActivity(). runOnUiThread(new Runnable() {

                            public void run() {
                                //      if (f.exists())
                                //        f.delete();
                                send.setVisibility(View.VISIBLE);
                                progressBar2.setVisibility(View.INVISIBLE);
                                snackbar(getText(R.string.errorconection).toString());
                            }
                        });
                        //    Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
                    }
                    //  dialogProgress.dismiss();
                    return null;

                } // End else block
            }
        } .execute();

}
    public  void get_feedlist_my_wave(final String tag) {
        String idno, code;

        DatabaseHandler db1 = new DatabaseHandler(getActivity().getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
        idno = user.get("idno");
        code = user.get("code");

        final Map<String, String> params = new HashMap<>();
        params.put("tag", tag);
        params.put("id", idno);
        params.put("code", code);
        params.put("pid", pid);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_REQUEST_get_feedlist_my_wave, new Response.Listener<String>() {
            //response from the server
            @Override
            public void onResponse(String response) {
                Log.d("responce", response);
                try {
                    JSONObject responseObj = new JSONObject(response);

                    if (responseObj.isNull("post")) {
                      //  new newNetCheck().execute();
                    } else {
                        JSONArray feedArray = responseObj.getJSONArray("post");
                        for (int i = 0; i < feedArray.length(); i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                            FeedItem_my_wave item = new FeedItem_my_wave();
                            //postid
                            item.setId(feedObj.getInt("id"));
                            item.setUserId(feedObj.getString("userid"));
                            item.setName(feedObj.getString("name"));
                            item.setText(feedObj.getString("text"));
                            // Image might be null sometimes
                            String image = "http://saoshyant.net/profileimages/" + feedObj.getString("profilePic");
                            if (("http://saoshyant.net/profileimages/").equals(image)) {image = "http://saoshyant.net/profileimages/ic_profile.png";}
                            item.setProfilePic(image);
                            item.setVoice(feedObj.getString("voice"));
                            item.setTimeStamp(feedObj.getString("timeStamp"));
                            int nlike= Integer.parseInt(feedObj.getString("nlike"));
                            if(nlike<100){item.setNLike(feedObj.getString("nlike"));}
                            else if(100<=nlike & nlike<500){item.setNLike("+100");}
                            else if(500<=nlike & nlike<1000){item.setNLike("+500");}
                            else if(1000<=nlike & nlike<2000){item.setNLike("+1k");}
                            else if(2000<=nlike & nlike<3000){item.setNLike("+2k");}

                            item.setMylike(feedObj.getString("mylike"));
                            item.setoprog("2");
                            item.setlprog("2");
                            item.setPprog("2");
                            pid = String.valueOf(feedObj.getInt("id"));
                            pidint = pidint + 1;
                            FeedItem_my_wave.add(item);
                        }

                        // Adding Load More button to lisview at bottom
                        // notify data changes to list adapater
                        listAdapter_my_wave.notifyDataSetChanged();
                        if (tag.equals("get_feedlist_my_wave")) {
                            //listView_my_wave.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                        } else if (tag.equals("get_feedlist_my_wave_loadmore")) {
                              refreshLayout.setLoadMoreing(false);
                        }


                        // Getting adapter
                     //   listAdapter_my_wave = new FeedListAdapter_my_wave(getActivity(), FeedItem_my_wave);
                     //   listView_my_wave.setAdapter(listAdapter_my_wave);

                        if (pidint >= 10) {
                             refreshLayout.setNeedLoadMore(true);
                            pidint = 0;
                        } else {
                            refreshLayout.setNeedLoadMore(false);
                            pidint = 0;
                        }



                    }



                } catch (JSONException e) {
                    refreshLayout.setRefreshing(false);
                    refreshLayout.setLoadMoreing(false);
                   //Toast.makeText(getActivity().getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        refreshLayout.setRefreshing(false);
                        refreshLayout.setLoadMoreing(false);
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        snackbar(getText(R.string.errorconection).toString());
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
    public  void playreset(){
        int FGS= FeedItem_my_wave.size();
        if(!(FGS ==0)){
            for (int i = 0; i <FGS; i++) {
                FeedItem_my_wave item = FeedItem_my_wave.get(i);

                item.setPprog("2");

                listAdapter_my_wave.notifyDataSetChanged();
            }
        }
    }
}

