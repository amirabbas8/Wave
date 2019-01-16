package net.saoshyant.wave.app.Feedlistadapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import net.saoshyant.wave.R;
import net.saoshyant.wave.activity.MainActivity;
import net.saoshyant.wave.app.DatabaseHandler;
import net.saoshyant.wave.app.MyApplication;
import net.saoshyant.wave.app.data.FeedItem_my_wave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.saoshyant.wave.activity.MainActivity.snackbar;


public class FeedListAdapter_my_wave extends BaseAdapter {

    private static
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    ImageButton btnlike,delete,play;
    Button report;
    FeedItem_my_wave item;
    String userid,postid,fprofilepic;
    String id;
    int pos;
    ProgressBar lprog,oprog;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem_my_wave> feedItemMywaves;

    public FeedListAdapter_my_wave(Activity activity, List<FeedItem_my_wave> feedItemMywaves) {
        this.activity = activity;
        this.feedItemMywaves = feedItemMywaves;
    }


    public int getCount() {
        return feedItemMywaves.size();
    }


    public Object getItem(int location) {
        return feedItemMywaves.get(location);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item_my_wave, null);

        if (imageLoader == null)
            imageLoader = MyApplication.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView nlike = (TextView) convertView.findViewById(R.id.nlike);
        TextView text = (TextView) convertView.findViewById(R.id.textView4);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profileimage);
        btnlike = (ImageButton) convertView.findViewById(R.id.like);
        delete = (ImageButton) convertView.findViewById(R.id.delete);
        report = (Button) convertView.findViewById(R.id.report);
        play = (ImageButton) convertView.findViewById(R.id.play);
        oprog = (ProgressBar) convertView.findViewById(R.id.oprog);
        lprog = (ProgressBar) convertView.findViewById(R.id.lprog);
     //   String fontPath = "fonts/BYekan.ttf";
     //   Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
       // name.setTypeface(tf);
    //    timestamp.setTypeface(tf);
     //   statusMsg.setTypeface(tf);
    //    nlike.setTypeface(tf);
        item = feedItemMywaves.get(position);
        DatabaseHandler db1 = new DatabaseHandler(activity);
        HashMap<String, String> user;
        user = db1.getUserDetails();
        id = user.get("idno");



        name.setText(item.getName());

        text.setText(item.getText());
        nlike.setText(item.getNLike());


        // user profile pic
        profilePic.setImageUrl( item.getProfilePic(), imageLoader);

        if ("1".equals(item.getoprog())) {

            oprog.setVisibility(View.VISIBLE);
            btnlike.setVisibility(View.INVISIBLE);

        } else if ("2".equals(item.getoprog())) {

            oprog.setVisibility(View.INVISIBLE);
            btnlike.setVisibility(View.VISIBLE);

        }
        if ("1".equals(item.getPprog())) {

      play.setImageResource(R.drawable.stop);
            play.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    pos = position;
                    postid = String.valueOf(feedItemMywaves.get(position).getId());
                    userid = String.valueOf(feedItemMywaves.get(position).getUserId());
                    String voice = String.valueOf(feedItemMywaves.get(position).getVoice());

                    for (int i = 0; i < feedItemMywaves.size(); i++) {
                        feedItemMywaves.get(i).setPprog("2");
                    }
                    String fileURL = "http://saoshyant.net/voice/" + voice;
                    MainActivity.mediaplay(fileURL, "stop", "fla_my_wave","","");
                    feedItemMywaves.get(position).setPprog("2");
                    notifyDataSetChanged();


                }
            });

        } else if ("2".equals(item.getPprog())) {

            play.setImageResource(R.drawable.play);
            play.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    pos = position;
                    postid = String.valueOf(feedItemMywaves.get(position).getId());
                    userid = String.valueOf(feedItemMywaves.get(position).getUserId());
                    String voice = String.valueOf(feedItemMywaves.get(position).getVoice());

                    for (int i = 0; i < feedItemMywaves.size(); i++) {
                        feedItemMywaves.get(i).setPprog("2");
                    }

                    String fileURL = "http://saoshyant.net/voice/" + voice;
                    MainActivity.mediaplay(fileURL, "play", "fla_my_wave","","");
                    feedItemMywaves.get(position).setPprog("1");
                    notifyDataSetChanged();

                }
            });

        }

        if ("1".equals(item.getlprog())) {

            lprog.setVisibility(View.VISIBLE);
            btnlike.setVisibility(View.INVISIBLE);

        } else if ("2".equals(item.getlprog())) {

            lprog.setVisibility(View.INVISIBLE);
            btnlike.setVisibility(View.VISIBLE);

        }

        if ("1".equals(item.getMylike())) {
            btnlike.setImageResource(R.drawable.feed_button_like_active);

            btnlike.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    pos = position;
                    postid = String.valueOf(feedItemMywaves.get(position).getId());
                    userid = String.valueOf(feedItemMywaves.get(position).getUserId());
                    fprofilepic=String.valueOf(feedItemMywaves.get(position).getProfilePic());
                    feedItemMywaves.get(position).setlprog("1");
                    notifyDataSetChanged();
                    Processlike(postid,userid,fprofilepic);

                }
            });

        } else if ("2".equals(item.getMylike())) {

            btnlike.setImageResource(R.drawable.feed_button_like);

            btnlike.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    pos = position;
                    postid = String.valueOf(feedItemMywaves.get(position).getId());
                    userid = String.valueOf(feedItemMywaves.get(position).getUserId());
                    fprofilepic=String.valueOf(feedItemMywaves.get(position).getProfilePic());
                    feedItemMywaves.get(position).setlprog("1");
                    notifyDataSetChanged();
                    Processlike(postid,userid,fprofilepic);

                }
            });

        }
        delete.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                pos = position;
                postid = String.valueOf(feedItemMywaves.get(position).getId());

                feedItemMywaves.get(position).setoprog("1");
                notifyDataSetChanged();
                Processdelete(postid);

            }
        });

        report.setText(new String(Character.toChars(0x1F621)));
        report.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {


                pos = position;
                postid = String.valueOf(feedItemMywaves.get(position).getId());
                Processreport(postid);

            }
        });
        return convertView;

    }





  void    Processlike(String postid,String userid,String fprofilepic) {



        String idno, code,name,profilepic;

        DatabaseHandler db1 = new DatabaseHandler(activity.getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
      idno = user.get("idno");
        code = user.get("code");
      name = user.get("realname");
      profilepic = user.get("profileimage");
        final Map<String, String> params = new HashMap<>();
        params.put("tag", "like_dislike");
        params.put("id", idno);
        params.put("code", code);
      params.put("postid", postid);
      params.put("userid", userid);
      params.put("name", name);
      params.put("myprofilepic", profilepic);
      params.put("fprofilepic", fprofilepic);
      StringRequest strReq = new StringRequest(Request.Method.POST, "http://saoshyant.net/wave/like_dislike.php", new Response.Listener<String>() {
            //response from the server
            @Override
            public void onResponse(String response) {
                Log.d("a:", response);
                try {
                    JSONObject responseObj = new JSONObject(response);
                    JSONArray feedArray = responseObj.getJSONArray("post");
                    JSONObject feedObj = (JSONObject) feedArray.get(0);
                    String KEY_REALNAME = "realname";
                    String KEY_code = "code";
                    String KEY_BIO = "bio";
                    String KEY_CONTRYCODE = "contrycode";
                    String KEY_profileimage = "profileimages";
                    String KEY_username= "username";
                    String KEY_frequency = "frequency";
                    String KEY_ID = "id";
                    if (feedObj.getString("success") != null) {
                        String res = feedObj.getString("success");

                        if (Integer.parseInt(res) == 51) {
                        feedItemMywaves.get(pos).setlprog("2");
                        notifyDataSetChanged();
                            snackbar(activity.getText(R.string.errorfunblock).toString());

                    } else if (Integer.parseInt(res) == 52) {
                        feedItemMywaves.get(pos).setlprog("2");
                        notifyDataSetChanged();
                            snackbar(activity.getText(R.string.errorblock).toString());

                    } else if (Integer.parseInt(res) == 1) {

                            DatabaseHandler db = new DatabaseHandler(activity.getApplicationContext());
                            /**
                             * Clear all previous data in SQlite database.
                             **/
                            db.resetTables();
                            db.addUser(feedObj.getString(KEY_ID), feedObj.getString(KEY_code), feedObj.getString(KEY_REALNAME),
                                    "home", feedObj.getString(KEY_BIO),
                                    feedObj.getString(KEY_CONTRYCODE),
                                    feedObj.getString(KEY_profileimage), feedObj.getString(KEY_username)
                                    , feedObj.getString(KEY_frequency));
                            //hh
                        feedItemMywaves.get(pos).setMylike("1");
                        int b = Integer.parseInt(feedItemMywaves.get(pos).getNLike());
                        int c = b + 1;
                        String d = String.valueOf(c);
                        feedItemMywaves.get(pos).setNLike(d);

                        btnlike.setImageResource(R.drawable.feed_button_like_active);
                        feedItemMywaves.get(pos).setlprog("2");
                        notifyDataSetChanged();

                    } else if (Integer.parseInt(res) == 2) {
                            feedItemMywaves.get(pos).setMylike("2");
                            int b = Integer.parseInt(feedItemMywaves.get(pos).getNLike());
                            int c = b - 1;
                            String d = String.valueOf(c);
                            feedItemMywaves.get(pos).setNLike(d);

                            btnlike.setImageResource(R.drawable.feed_button_like_active);
                        feedItemMywaves.get(pos).setlprog("2");
                        notifyDataSetChanged();
                    }else {
                            feedItemMywaves.get(pos).setlprog("2");
                            notifyDataSetChanged();
                            snackbar(activity.getText(R.string.errorproblem).toString());
                        }
                }   } catch (JSONException e) {
                   // Toast.makeText(activity.getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        snackbar(activity.getText(R.string.errorconection).toString());
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


    void    Processdelete(String postid) {



        String idno, code;

        DatabaseHandler db1 = new DatabaseHandler(activity.getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
        idno = user.get("idno");
        code = user.get("code");

        final Map<String, String> params = new HashMap<>();
        params.put("tag", "delete");
        params.put("id", idno);
        params.put("code", code);
        params.put("postid", postid);
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://saoshyant.net/wave/deletepost.php", new Response.Listener<String>() {
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
                            feedItemMywaves.get(pos).setoprog("2");
                            feedItemMywaves.remove(pos);
                            notifyDataSetChanged();

                        } else if (Integer.parseInt(res) == 2) {
                            feedItemMywaves.get(pos).setoprog("2");
                            snackbar(activity.getText(R.string.errorproblem).toString());

                            notifyDataSetChanged();
                        } else {
                            feedItemMywaves.get(pos).setoprog("2");
                            notifyDataSetChanged();
                            snackbar(activity.getText(R.string.errorproblem).toString());

                        }
                    }   } catch (JSONException e) {
                    feedItemMywaves.get(pos).setoprog("2");
                    notifyDataSetChanged();
                   // Toast.makeText(activity.getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());

                        feedItemMywaves.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        snackbar(activity.getText(R.string.errorconection).toString());
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


    void    Processreport(String postid) {


        String idno, code;

        DatabaseHandler db1 = new DatabaseHandler(activity.getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
        idno = user.get("idno");
        code = user.get("code");

        final Map<String, String> params = new HashMap<>();
        params.put("tag", "report");
        params.put("id", idno);
        params.put("code", code);
        params.put("postid", postid);
        params.put("kind", "wave_voice");
        params.put("comment", "comment");
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://saoshyant.net/wave/report.php", new Response.Listener<String>() {
            //response from the server
            @Override
            public void onResponse(String response) {
                Log.d("", response);
                try {
                    JSONObject responseObj = new JSONObject(response);
                    JSONArray feedArray = responseObj.getJSONArray("user");
                    JSONObject feedObj = (JSONObject) feedArray.get(0);
                    if (feedObj.getString("success") != null) {
                        String res = feedObj.getString("success");

                        if (Integer.parseInt(res) == 1) {
                            snackbar(activity.getText(R.string.reported).toString());
                        }  else {
                            snackbar(activity.getText(R.string.errorproblem).toString());

                        }
                    }   } catch (JSONException e) {

                    notifyDataSetChanged();
                    // Toast.makeText(activity.getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        snackbar(activity.getText(R.string.errorconection).toString());
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
