package net.saoshyant.wave.app.Feedlistadapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import net.saoshyant.wave.app.data.FeedItem_geyhan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.saoshyant.wave.activity.MainActivity.snackbar;


public class FeedListAdapter_geyhan extends BaseAdapter {

    private static
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    ImageButton btnlike,play;
    FeedItem_geyhan item;
    String userid;
    String id;
    int pos;
    ProgressBar lprog;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem_geyhan> feedItem_geyhan;
    public FeedListAdapter_geyhan(Activity activity, List<FeedItem_geyhan> feedItem_geyhan) {
        this.activity = activity;
        this.feedItem_geyhan = feedItem_geyhan;
    }


    public int getCount() {
        return feedItem_geyhan.size();
    }


    public Object getItem(int location) {
        return feedItem_geyhan.get(location);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item_geyhan, null);

        if (imageLoader == null)
            imageLoader = MyApplication.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView nlike = (TextView) convertView.findViewById(R.id.nlike);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profileimage);
        btnlike = (ImageButton) convertView.findViewById(R.id.like);
        play = (ImageButton) convertView.findViewById(R.id.play);
        lprog = (ProgressBar) convertView.findViewById(R.id.lprog);
     //   String fontPath = "fonts/BYekan.ttf";
     //   Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
       // name.setTypeface(tf);
    //    timestamp.setTypeface(tf);
     //   statusMsg.setTypeface(tf);
    //    nlike.setTypeface(tf);
        item = feedItem_geyhan.get(position);
        DatabaseHandler db1 = new DatabaseHandler(activity);
        HashMap<String, String> user;
        user = db1.getUserDetails();
        id = user.get("idno");



        name.setText(item.getName());


        nlike.setText(item.getNLike());


        // user profile pic

        String image = "http://saoshyant.net/profileimages/" + item.getProfilePic();
        if (("http://saoshyant.net/profileimages/").equals(image)) {image = "http://saoshyant.net/profileimages/ic_profile.png";}
        profilePic.setImageUrl( image, imageLoader);
        if ("1".equals(item.getPprog())) {

      play.setImageResource(R.drawable.stop);
            play.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    pos = position;
                    userid = String.valueOf(feedItem_geyhan.get(position).getUserId());

                    for (int i = 0; i < feedItem_geyhan.size(); i++) {
                        feedItem_geyhan.get(i).setPprog("2");
                    }
                    MainActivity.play_geyhan("0", userid, "stop");
                    feedItem_geyhan.get(position).setPprog("2");
                    notifyDataSetChanged();


                }
            });

        } else if ("2".equals(item.getPprog())) {

            play.setImageResource(R.drawable.play);
            play.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    pos = position;
                    userid = String.valueOf(feedItem_geyhan.get(position).getUserId());

                    for (int i = 0; i < feedItem_geyhan.size(); i++) {
                        feedItem_geyhan.get(i).setPprog("2");
                    }
                    MainActivity.play_geyhan("0", userid, "play");
                    feedItem_geyhan.get(position).setPprog("1");
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
        if (id.equals(item.getUserId())) {
            btnlike.setVisibility(View.INVISIBLE);
        }else {
            btnlike.setVisibility(View.VISIBLE);
            if ("1".equals(item.getMylike())) {
                btnlike.setImageResource(R.drawable.subtract_icon);

                btnlike.setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {

                        pos = position;
                        userid = String.valueOf(feedItem_geyhan.get(position).getUserId());
                        String  fname = String.valueOf(feedItem_geyhan.get(position).getName());
                        String   fprofilepic = String.valueOf(feedItem_geyhan.get(position).getProfilePic());
                        feedItem_geyhan.get(position).setlprog("1");
                        notifyDataSetChanged();
                        Processadd(userid,fname,fprofilepic);

                    }
                });

            } else if ("2".equals(item.getMylike())) {

                btnlike.setImageResource(R.drawable.add_icon);

                btnlike.setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {

                        pos = position;
                        userid = String.valueOf(feedItem_geyhan.get(position).getUserId());
                      String  fname = String.valueOf(feedItem_geyhan.get(position).getName());
                     String   fprofilepic = String.valueOf(feedItem_geyhan.get(position).getProfilePic());
                        feedItem_geyhan.get(position).setlprog("1");
                        notifyDataSetChanged();
                        Processadd(userid,fname,fprofilepic);

                    }
                });

            }
        }

        return convertView;

    }





  void    Processadd(String userid,String fname,String fprofilepic) {



        String idno, code,myname,myprofilepic;

        DatabaseHandler db1 = new DatabaseHandler(activity.getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
      myname = user.get("realname");
      myprofilepic = user.get("profileimage");
      idno = user.get("idno");
      code = user.get("code");
        final Map<String, String> params = new HashMap<>();
        params.put("tag", "add_delete_friend");
        params.put("id", idno);
        params.put("code", code);
      params.put("userid", userid);
      params.put("fid", userid);
      params.put("fname", fname);
      params.put("fprofilepic", fprofilepic);
      params.put("myname", myname);
      params.put("myprofilepic", myprofilepic);
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://saoshyant.net/wave/add_delete_friend.php", new Response.Listener<String>() {
            //response from the server
            @Override
            public void onResponse(String response) {
                Log.d("a:", response);
                try {
                    JSONObject responseObj = new JSONObject(response);
                    JSONArray feedArray = responseObj.getJSONArray("post");
                    JSONObject feedObj = (JSONObject) feedArray.get(0);
                    if (feedObj.getString("success") != null) {
                        String res = feedObj.getString("success");

                        if (Integer.parseInt(res) == 51) {
                        feedItem_geyhan.get(pos).setlprog("2");
                        notifyDataSetChanged();
                            snackbar(activity.getText(R.string.errorfunblock).toString());

                    } else if (Integer.parseInt(res) == 52) {
                        feedItem_geyhan.get(pos).setlprog("2");
                        notifyDataSetChanged();
                            snackbar(activity.getText(R.string.errorblock).toString());

                    } else if (Integer.parseInt(res) == 1) {
                            //hh
                        feedItem_geyhan.get(pos).setMylike("2");
                            int b = Integer.parseInt(feedItem_geyhan.get(pos).getNLike());
                            int c = b - 1;
                            String d = String.valueOf(c);
                            feedItem_geyhan.get(pos).setNLike(d);
                       // btnlike.setImageResource(R.drawable.add);
                        feedItem_geyhan.get(pos).setlprog("2");
                        notifyDataSetChanged();

                    } else if (Integer.parseInt(res) == 3) {
                            feedItem_geyhan.get(pos).setMylike("1");
                            int b = Integer.parseInt(feedItem_geyhan.get(pos).getNLike());
                            int c = b + 1;
                            String d = String.valueOf(c);
                            feedItem_geyhan.get(pos).setNLike(d);
                           // btnlike.setImageResource(R.drawable.subtr);
                            feedItem_geyhan.get(pos).setlprog("2");
                            notifyDataSetChanged();
                    }else{
                            feedItem_geyhan.get(pos).setlprog("2");
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






}
