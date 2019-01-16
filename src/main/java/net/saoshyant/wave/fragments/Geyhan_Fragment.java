package net.saoshyant.wave.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xingliuhua.lib_refreshlayout.RefreshLayout;

import net.saoshyant.wave.R;
import net.saoshyant.wave.app.DatabaseHandler;
import net.saoshyant.wave.app.Feedlistadapter.FeedListAdapter_geyhan;
import net.saoshyant.wave.app.MyApplication;
import net.saoshyant.wave.app.data.FeedItem_geyhan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.saoshyant.wave.activity.MainActivity.snackbar;


public class Geyhan_Fragment extends Fragment {
    private ListView listView_geyhan;
    private FeedListAdapter_geyhan listAdapter_geyhan;
    private List<net.saoshyant.wave.app.data.FeedItem_geyhan> FeedItem_geyhan;
    private String pid = "0";
    private int pidint = 0;
    EditText input_search;
    String search_txt;
    RefreshLayout refreshLayout;
    public static final String URL_REQUEST_get_feedlist_geyhan = "http://saoshyant.net/wave/get_feedlist_geyhan.php";
    public Geyhan_Fragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_geyhan, container, false);

        input_search= (EditText) rootView.findViewById(R.id.input_search);
        ImageButton search = (ImageButton) rootView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                FeedItem_geyhan.clear();
                listAdapter_geyhan.notifyDataSetChanged();
                get_feedlist_geyhan("get_feedlist_geyhan");
            }
        });
        input_search.addTextChangedListener(new TextWatcher() {

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
                if(s.length() % 3 == 0 )
                {
                    FeedItem_geyhan.clear();
                    listAdapter_geyhan.notifyDataSetChanged();
                    get_feedlist_geyhan("get_feedlist_geyhan");
            }
            }
        });
        listView_geyhan= (ListView) rootView.findViewById(R.id.list);
      //  listView_geyhan.setVisibility(View.INVISIBLE);
        FeedItem_geyhan = new ArrayList<>();

        listAdapter_geyhan = new FeedListAdapter_geyhan(getActivity(), FeedItem_geyhan);
        listView_geyhan.setAdapter(listAdapter_geyhan);



        refreshLayout = (RefreshLayout) rootView. findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedItem_geyhan.clear();
                listAdapter_geyhan.notifyDataSetChanged();
                get_feedlist_geyhan("get_feedlist_geyhan");



            }

            @Override
            public void onLoadmore() {
                if (9 < FeedItem_geyhan.size()) {
                    //listView_geyhan.setVisibility(View.VISIBLE);
                    get_feedlist_geyhan("get_feedlist_geyhan_loadmore");

                }

            }
        });
        get_feedlist_geyhan("get_feedlist_geyhan");
        return rootView;
    }
    public  void get_feedlist_geyhan(final String tag) {
        String idno, code;

        DatabaseHandler db1 = new DatabaseHandler(getActivity().getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
        idno = user.get("idno");
        code = user.get("code");

search_txt=input_search.getText().toString();
        final Map<String, String> params = new HashMap<>();
        params.put("tag", tag);
        params.put("id", idno);
        params.put("code", code);
        params.put("pid", pid);
        params.put("search", search_txt);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_REQUEST_get_feedlist_geyhan, new Response.Listener<String>() {
            //response from the server
            @Override
            public void onResponse(String response) {
                Log.d("responce", response);
                try {
                    JSONObject responseObj = new JSONObject(response);

                    if (responseObj.isNull("user")) {
                        //  new newNetCheck().execute();
                    } else {
                        JSONArray feedArray = responseObj.getJSONArray("user");
                        for (int i = 0; i < feedArray.length(); i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                            FeedItem_geyhan item = new FeedItem_geyhan();
                            //postid
                            item.setUserId(feedObj.getString("userid"));
                            item.setName(feedObj.getString("name"));
                            // Image might be null sometimes
                           item.setProfilePic(feedObj.getString("profilePic"));
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
                            FeedItem_geyhan.add(item);
                        }
                        listAdapter_geyhan.notifyDataSetChanged();
                        if (tag.equals("get_feedlist_geyhan")) {
                        //    listView_geyhan.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                        } else if (tag.equals("get_feedlist_geyhan_loadmore")) {
                         //   listView_geyhan.onRefreshDownComplete(null);
                            refreshLayout.setLoadMoreing(false);
                        }


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
                   // Toast.makeText(getActivity().getApplicationContext(), "Error2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        refreshLayout.setRefreshing(false);
                        refreshLayout.setLoadMoreing(false);
                        snackbar(getText(R.string.errorconection).toString());
                        //  Log.e(TAG, "Error1: " + error.getMessage());
                       // Toast.makeText(getActivity().getApplicationContext(), "Error1: connection error", Toast.LENGTH_SHORT).show();
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
int FGS=FeedItem_geyhan.size();
        if(!(FGS ==0)){
            for (int i = 0; i <FGS; i++) {
                FeedItem_geyhan      item = FeedItem_geyhan.get(i);

                item.setPprog("2");

            listAdapter_geyhan.notifyDataSetChanged();
            }
        }
    }
}
