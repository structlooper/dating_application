package com.raccoonsquare.dating;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.raccoonsquare.dating.adapter.GalleryListAdapter;
import com.raccoonsquare.dating.adapter.ProfilesSpotlightListAdapter;
import com.raccoonsquare.dating.app.App;
import com.raccoonsquare.dating.constants.Constants;
import com.raccoonsquare.dating.model.Image;
import com.raccoonsquare.dating.model.Profile;
import com.raccoonsquare.dating.util.CustomRequest;
import com.raccoonsquare.dating.util.Helper;
import com.raccoonsquare.dating.view.SpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";
    private static final String STATE_LIST_2 = "State Adapter Data 2";
    private static final String STATE_LIST_3 = "State Adapter Data 3";

    TextView mMessage;
    ImageView mSplash;

    CardView mSpotlightCard, mSearchCard, mStreamCard, mFeedCard;
    Button mSpotlightMoreButton, mSearchMoreButton, mStreamMoreButton;
    TextView mFeedEmptyText;

    RecyclerView mRecyclerView, mSpotlightRecyclerView, mSearchRecyclerView;
    NestedScrollView mNestedView;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<Profile> spotlightList;
    private ProfilesSpotlightListAdapter spotlightAdapter;

    private ArrayList<Profile> searchList;
    private ProfilesSpotlightListAdapter searchAdapter;

    private ArrayList<Image> itemsList;
    private GalleryListAdapter itemsAdapter;

    long itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    private Double lat = 39.9199, lng = 32.8543; // Ankara

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent i = getActivity().getIntent();

        itemId = i.getLongExtra("itemId", 0);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new GalleryListAdapter(getActivity(), itemsList);

            spotlightList = savedInstanceState.getParcelableArrayList(STATE_LIST_2);
            spotlightAdapter = new ProfilesSpotlightListAdapter(getActivity(), spotlightList);

            searchList = savedInstanceState.getParcelableArrayList(STATE_LIST_3);
            searchAdapter = new ProfilesSpotlightListAdapter(getActivity(), searchList);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getLong("itemId");

        } else {

            itemsList = new ArrayList<Image>();
            itemsAdapter = new GalleryListAdapter(getActivity(), itemsList);

            spotlightList = new ArrayList<Profile>();
            spotlightAdapter = new ProfilesSpotlightListAdapter(getActivity(), spotlightList);

            searchList = new ArrayList<Profile>();
            searchAdapter = new ProfilesSpotlightListAdapter(getActivity(), searchList);

            restore = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        getActivity().setTitle(R.string.app_name);

        mItemsContainer = rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mSpotlightCard = rootView.findViewById(R.id.spotlight_card);
        mSearchCard = rootView.findViewById(R.id.search_card);
        mStreamCard = rootView.findViewById(R.id.media_stream_card);
        mFeedCard = rootView.findViewById(R.id.media_feed_card);

        mSpotlightCard.setVisibility(View.GONE);
        mSearchCard.setVisibility(View.GONE);
        mStreamCard.setVisibility(View.GONE);
        mFeedCard.setVisibility(View.GONE);

        mFeedEmptyText = rootView.findViewById(R.id.media_feed_card_sub_title_2);
        mFeedEmptyText.setVisibility(View.GONE);

        mSearchMoreButton = rootView.findViewById(R.id.search_card_button);
        mSpotlightMoreButton = rootView.findViewById(R.id.spotlight_card_button);
        mStreamMoreButton = rootView.findViewById(R.id.media_stream_card_button);

        mSpotlightRecyclerView = rootView.findViewById(R.id.spotlight_recycler_view);
        mSearchRecyclerView = rootView.findViewById(R.id.search_recycler_view);

        //

        mSpotlightRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mSpotlightRecyclerView.setAdapter(spotlightAdapter);

        spotlightAdapter.setOnItemClickListener(new ProfilesSpotlightListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, Profile obj, int position) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", obj.getId());
                startActivity(intent);
            }
        });

        mSpotlightMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SpotlightActivity.class);
                startActivity(intent);
            }
        });

        //

        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mSearchRecyclerView.setAdapter(searchAdapter);

        searchAdapter.setOnItemClickListener(new ProfilesSpotlightListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, Profile obj, int position) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", obj.getId());
                startActivity(intent);
            }
        });

        mSearchMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        //

        mStreamMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StreamActivity.class);
                startActivity(intent);
            }
        });

        //

        mMessage = rootView.findViewById(R.id.message);
        mSplash = rootView.findViewById(R.id.splash);

        mNestedView = rootView.findViewById(R.id.nested_view);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);

        int columns = 3;

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), columns);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(columns, Helper.dpToPx(getActivity(), 4), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.addOnItemTouchListener(new GalleryListAdapter.RecyclerTouchListener(getActivity(), mRecyclerView, new GalleryListAdapter.ClickListener() {

            @Override
            public void onClick(View view, int position) {

                Image img = itemsList.get(position);

                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.putExtra("itemId", img.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mRecyclerView.setNestedScrollingEnabled(false);

        mNestedView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY < oldScrollY) { // up

                }

                if (scrollY > oldScrollY) { // down

                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    if (!loadingMore && (viewMore) && !(mItemsContainer.isRefreshing())) {

                        mItemsContainer.setRefreshing(true);

                        loadingMore = true;

                        getItems();
                    }
                }
            }
        });

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();

        } else {

            updateView();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void updateView() {

        hideMessage();

        mStreamCard.setVisibility(View.VISIBLE);
        mFeedCard.setVisibility(View.VISIBLE);

        if (itemsAdapter.getItemCount() == 0) {

            mFeedEmptyText.setVisibility(View.VISIBLE);

        } else {

            mFeedEmptyText.setVisibility(View.GONE);
        }

        if (spotlightAdapter.getItemCount() == 0) {

            mSpotlightCard.setVisibility(View.GONE);

        } else {

            mSpotlightCard.setVisibility(View.VISIBLE);
        }

        if (searchAdapter.getItemCount() == 0) {

            mSearchCard.setVisibility(View.GONE);

        } else {

            mSearchCard.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;

            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putLong("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
        outState.putParcelableArrayList(STATE_LIST_2, spotlightList);
        outState.putParcelableArrayList(STATE_LIST_3, searchList);
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GALLERY_FEED, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "MainFragment Not Added to Activity");

                            return;
                        }

                        if (!loadingMore) {

                            itemsList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            Image item = new Image(itemObj);

                                            itemsList.add(item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            if (spotlightAdapter.getItemCount() == 0 && searchAdapter.getItemCount() == 0) {

                                getSpotlightItems();
                            }

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "MainFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(itemId));
                params.put("language", "en");

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getSpotlightItems() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_SPOTLIGHT_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "MainFragment Not Added to Activity");

                            return;
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                if (response.has("items")) {

                                    JSONArray usersArray = response.getJSONArray("items");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject userObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(userObj);

                                            spotlightList.add(profile);
                                        }
                                    }
                                }

                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            spotlightAdapter.notifyDataSetChanged();

                            getSearchItems();

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "MainFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(0));
                params.put("language", "en");

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getSearchItems() {

        if (App.getInstance().getLat() != 0.000000 && App.getInstance().getLng() != 0.000000) {

            lat = App.getInstance().getLat();
            lng = App.getInstance().getLng();
        }

        Log.e("dimon", Double.toString(lat));
        Log.e("dimon", Double.toString(lng));

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_SEARCH_PEOPLE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "MainFragment Not Added to Activity");

                            return;
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                if (response.has("items")) {

                                    JSONArray usersArray = response.getJSONArray("items");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject userObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(userObj);

                                            searchList.add(profile);
                                        }
                                    }
                                }

                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            searchAdapter.notifyDataSetChanged();

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "MainFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(0));
                params.put("distance", Integer.toString(1000));
                params.put("lat", Double.toString(lat));
                params.put("lng", Double.toString(lng));
                params.put("gender", Integer.toString(3));
                params.put("online", Integer.toString(0));
                params.put("photo", Integer.toString(0));
                params.put("pro", Integer.toString(0));
                params.put("ageFrom", Integer.toString(18));
                params.put("ageTo", Integer.toString(105));
                params.put("language", "en");

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        viewMore = arrayLength == LIST_ITEMS;

        hideMessage();

        itemsAdapter.notifyDataSetChanged();

        mStreamCard.setVisibility(View.VISIBLE);
        mFeedCard.setVisibility(View.VISIBLE);

        if (itemsAdapter.getItemCount() == 0) {

            mFeedEmptyText.setVisibility(View.VISIBLE);

        } else {

            mFeedEmptyText.setVisibility(View.GONE);
        }

        if (spotlightAdapter.getItemCount() == 0) {

            mSpotlightCard.setVisibility(View.GONE);

        } else {

            mSpotlightCard.setVisibility(View.VISIBLE);
        }

        if (searchAdapter.getItemCount() == 0) {

            mSearchCard.setVisibility(View.GONE);

        } else {

            mSearchCard.setVisibility(View.VISIBLE);
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);

        mSpotlightCard.setVisibility(View.GONE);
        mSearchCard.setVisibility(View.GONE);
        mStreamCard.setVisibility(View.GONE);
        mFeedCard.setVisibility(View.GONE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);

        mSplash.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}