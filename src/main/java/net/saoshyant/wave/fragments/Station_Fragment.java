package net.saoshyant.wave.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.saoshyant.wave.activity.MainActivity.snackbar;


public class Station_Fragment extends Fragment {
    ImageButton play,like,add;
TextView name;
    ProgressBar lprog,aprog;
     NetworkImageView profileimage;
     ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    String URL_REQUEST = "http://saoshyant.net/wave/station.php";
    String nlikestr;
    TextView nlike,textv;
    String   fileURL="";
    public Station_Fragment() {
        // Required empty public constructor
    }

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
        View rootView = inflater.inflate(R.layout.fragment_station, container, false);
        play = (ImageButton) rootView.findViewById(R.id.play);
        like = (ImageButton) rootView.findViewById(R.id.like);
        add = (ImageButton) rootView.findViewById(R.id.add);
        lprog = (ProgressBar) rootView.findViewById(R.id.lprog);
        aprog = (ProgressBar) rootView.findViewById(R.id.aprog);
        nlike = (TextView) rootView.findViewById(R.id.nlike);
        textv = (TextView) rootView.findViewById(R.id.textView3);
        profileimage = (NetworkImageView) rootView.findViewById(R.id.profileimage);
        profileimage.setImageUrl("http://saoshyant.net/profileimages/", imageLoader);
        name = (TextView) rootView.findViewById(R.id.name);
        name.setText("");

        play.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (Objects.equals(fileURL, "")) {
                    playnext();
                    play.setImageResource(R.drawable.stop);
                } else  {
                    fileURL="";
                    MainActivity.mediaplay("", "stop", "station", "", "");
                    play.setImageResource(R.drawable.play);
                }
            }
        });
        return rootView;
    }

    public  void playnext() {
        String idno, code,contrycode;

        DatabaseHandler db1 = new DatabaseHandler(getActivity().getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
        idno = user.get("idno");
        code = user.get("code");
        contrycode = user.get("contrycode");
        final Map<String, String> params = new HashMap<>();
        params.put("tag", "station");
        params.put("id", idno);
        params.put("code", code);
        params.put("contrycode", contrycode);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_REQUEST, new Response.Listener<String>() {
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

                        if (Integer.parseInt(res) == 1) {
                            like.setVisibility(View.VISIBLE);
                            nlike.setVisibility(View.VISIBLE);
                            add.setVisibility(View.VISIBLE);
                            profileimage.setVisibility(View.VISIBLE);
                     final        String id = feedObj.getString("id");
                      final       String userid = feedObj.getString("userid");
                            String wave = feedObj.getString("wave");
                            final String realname = feedObj.getString("realname");
                            final String profileimagename = feedObj.getString("profileimage");
                            String text = feedObj.getString("text");
                            textv.setText(text);
                             nlikestr = feedObj.getString("nlike");
                            int nlikes= Integer.parseInt(feedObj.getString("nlike"));
                            if(nlikes<100){nlikestr=feedObj.getString("nlike");}
                            else if(100<=nlikes & nlikes<500){nlikestr="+100";}
                            else if(500<=nlikes & nlikes<1000){nlikestr="+500";}
                            else if(1000<=nlikes & nlikes<2000){nlikestr="+1k";}
                            else if(2000<=nlikes & nlikes<3000){nlikestr="+2k";}
                            nlike.setText(nlikestr);
                            String mylike=feedObj.getString("mylike");
                            if(Objects.equals(mylike, "1")){
                                like.setImageResource(R.drawable.feed_button_like_active);
                            }else  if(Objects.equals(mylike, "2")){
                                like.setImageResource(R.drawable.feed_button_like);
                            }
                            String myadd=feedObj.getString("myadd");
                            if(Objects.equals(myadd, "1")){
                                add.setImageResource(R.drawable.subtract_icon);
                            }else  if(Objects.equals(myadd, "2")){
                                add.setImageResource(R.drawable.add_icon);
                            }
                               fileURL="http://saoshyant.net/voice/"+wave;
                            if(Objects.equals(fileURL, "")) {
                                play.setImageResource(R.drawable.play);
                            }else
                            {
                                play.setImageResource(R.drawable.stop);
                            }
                     //      MainActivity.mediaplay(fileURL,n,"station");
                            MainActivity.mediaplay(fileURL, "play", "station","","");
                            name.setText(realname);
                            if (Objects.equals(profileimagename, "")) {
                                profileimage.setImageUrl("http://saoshyant.net/profileimages/ic_profile.png", imageLoader);
                                add.setBackgroundColor(getResources().getColor(R.color.accent_material_light));
                            } else {
                                profileimage.setImageUrl("http://saoshyant.net/profileimages/" + profileimagename, imageLoader);
                                add.setBackground(null);
                            }



                            profileimage.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View v) {
                                }
                            });
                            like.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View v) {
                                    like.setVisibility(View.INVISIBLE);
                                    lprog.setVisibility(View.VISIBLE);
                                    Processlike(id,userid);
                                }
                            });
                            add.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View v) {
                                    add.setVisibility(View.INVISIBLE);
                                    aprog.setVisibility(View.VISIBLE);
                                    Processadd(userid,realname,profileimagename);
                                }
                            });
                            //sendto mainactivity
                            //play
                        } else {
                            play.setImageResource(R.drawable.play);
                            snackbar(getText(R.string.errorproblem).toString());
                        }


                    }

                } catch (JSONException e) {
                    play.setImageResource(R.drawable.play);
                  //  Toast.makeText(getActivity().getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        play.setImageResource(R.drawable.play);
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
        play.setImageResource(R.drawable.play);
    }

    void    Processlike(String postid,String userid) {



        String idno, code,name,profilepic;

        DatabaseHandler db1 = new DatabaseHandler(getActivity().getApplicationContext());
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
        params.put("profilepic", profilepic);
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
                            snackbar(getText(R.string.errorfunblock).toString());

                        } else if (Integer.parseInt(res) == 52) {
                            snackbar(getText(R.string.errorblock).toString());

                        } else if (Integer.parseInt(res) == 1) {

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
                            //hh
                            lprog.setVisibility(View.INVISIBLE);
                            like.setVisibility(View.VISIBLE);
                            like.setImageResource(R.drawable.feed_button_like_active);

                            int b = Integer.parseInt(nlike.getText().toString());
                            int c = b + 1;
                            String d = String.valueOf(c);
                            nlike.setText(d);

                        } else if (Integer.parseInt(res) == 2) {
                            int b = Integer.parseInt(nlike.getText().toString());
                            int c = b - 1;
                            String d = String.valueOf(c);
                            nlike.setText(d);
                            lprog.setVisibility(View.INVISIBLE);
                            like.setVisibility(View.VISIBLE);
                            like.setImageResource(R.drawable.feed_button_like);
                        }else {
                            lprog.setVisibility(View.INVISIBLE);
                            like.setVisibility(View.VISIBLE);
                            snackbar(getText(R.string.errorproblem).toString());
                        }
                    }   } catch (JSONException e) {
                    lprog.setVisibility(View.INVISIBLE);
                    like.setVisibility(View.VISIBLE);
                   // Toast.makeText(getActivity().getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        snackbar(getText(R.string.errorconection).toString());
                      //  Toast.makeText(getActivity().getApplicationContext(), "Error1: connection error", Toast.LENGTH_SHORT).show();
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

    void    Processadd(String userid,String fname,String fprofilepic) {



        String idno, code,myname,myprofilepic;

        DatabaseHandler db1 = new DatabaseHandler(getActivity().getApplicationContext());
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
                            add.setVisibility(View.VISIBLE);
                            aprog.setVisibility(View.INVISIBLE);
                            snackbar(getActivity().getText(R.string.errorfunblock).toString());

                        } else if (Integer.parseInt(res) == 52) {
                            add.setVisibility(View.VISIBLE);
                            aprog.setVisibility(View.INVISIBLE);
                            snackbar(getActivity().getText(R.string.errorblock).toString());

                        } else if (Integer.parseInt(res) == 1) {
                            //hh

                            add.setVisibility(View.VISIBLE);
                            aprog.setVisibility(View.INVISIBLE);
                             add.setImageResource(R.drawable.add_icon);

                        } else if (Integer.parseInt(res) == 3) {
                            add.setVisibility(View.VISIBLE);
                            aprog.setVisibility(View.INVISIBLE);
                             add.setImageResource(R.drawable.subtract_icon);
                        }else{
                            add.setVisibility(View.VISIBLE);
                            aprog.setVisibility(View.INVISIBLE);
                            snackbar(getActivity().getText(R.string.errorproblem).toString());
                        }
                    }   } catch (JSONException e) {
                    add.setVisibility(View.VISIBLE);
                    aprog.setVisibility(View.INVISIBLE);
                    // Toast.makeText(activity.getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                        add.setVisibility(View.VISIBLE);
                        aprog.setVisibility(View.INVISIBLE);
                        snackbar(getActivity().getText(R.string.errorconection).toString());
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
