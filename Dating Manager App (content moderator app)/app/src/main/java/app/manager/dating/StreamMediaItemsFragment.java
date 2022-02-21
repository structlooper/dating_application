package app.manager.dating;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.manager.dating.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.manager.dating.adapter.ModerateMediaItemsListAdapter;
import app.manager.dating.app.App;
import app.manager.dating.constants.Constants;
import app.manager.dating.model.Image;
import app.manager.dating.util.CustomRequest;


public class StreamMediaItemsFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    private static final int PROFILE_NEW_POST = 4;

    private RecyclerView mRecyclerView;
    private NestedScrollView mNestedView;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View mBottomSheet;

    private TextView mMessage;
    private ImageView mSplash;

    private SwipeRefreshLayout mItemsContainer;

    private ArrayList<Image> itemsList;
    private ModerateMediaItemsListAdapter itemsAdapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    public StreamMediaItemsFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile_media_items_steam, container, false);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new ModerateMediaItemsListAdapter(getActivity(), itemsList);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");

        } else {

            itemsList = new ArrayList<>();
            itemsAdapter = new ModerateMediaItemsListAdapter(getActivity(), itemsList);

            restore = false;
            itemId = 0;
        }

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        //

        mMessage = (TextView) rootView.findViewById(R.id.message);
        mSplash = (ImageView) rootView.findViewById(R.id.splash);

//        // Prepare bottom sheet

        mBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);

        //

        mNestedView = (NestedScrollView) rootView.findViewById(R.id.nested_view);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        itemsAdapter.setOnMoreButtonClickListener(new ModerateMediaItemsListAdapter.OnItemMenuButtonClickListener() {

            @Override
            public void onItemClick(View v, Image obj, int position, int actionId) {

                switch (actionId) {

                    case ITEM_ACTION_REJECT: {

                        itemAction(METHOD_MANAGER_REJECT_GALLERY_ITEM, position);

                        break;
                    }

                    case ITEM_ACTION_APPROVE: {

                        itemAction(METHOD_MANAGER_APPROVE_GALLERY_ITEM, position);

                        break;
                    }

                    case ITEM_ACTION_MENU: {

                        showItemActionDialog(position);

                        break;
                    }
                }
            }
        });

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.setNestedScrollingEnabled(false);


        mNestedView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY < oldScrollY) { // up

                    ((MainActivity)getActivity()).animateSearchBar(false);
                }

                if (scrollY > oldScrollY) { // down

                    ((MainActivity)getActivity()).animateSearchBar(true);
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

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MANAGER_GET_NOT_MODERATED_MEDIA_ITEMS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "StreamFragment Not Added to Activity");

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

                            loadingComplete();

                            Log.d("STREAM success", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "StreamFragment Not Added to Activity");

                    return;
                }

                loadingComplete();

                Log.e("STREAM error", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Integer.toString(itemId));
                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void itemAction(String url, final int position) {

        if (App.getInstance().getAccessLevel() == ADMIN_ACCESS_LEVEL_READ_ONLY_RIGHTS) {

            Toast.makeText(getActivity(), getText(R.string.msg_access_rights_error), Toast.LENGTH_SHORT).show();

            return;
        }


        final Image p = itemsList.get(position);

        itemsList.remove(position);
        itemsAdapter.notifyDataSetChanged();

        App.getInstance().setNewMediaItemsCount(App.getInstance().getNewMediaItemsCount() - 1);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                //
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Log.e("Item.Action", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Item.Action", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(p.getId()));
                params.put("fromUserId", Long.toString(p.getOwner().getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getItemCount() == 0) {

            if (StreamMediaItemsFragment.this.isVisible()) {

                showMessage(getText(R.string.label_empty_list).toString());
            }

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    private void showItemActionDialog(final int position) {

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.item_action_sheet_list, null);

        MaterialRippleLayout mProfileButton = (MaterialRippleLayout) view.findViewById(R.id.profile_button);
        MaterialRippleLayout mRejectButton = (MaterialRippleLayout) view.findViewById(R.id.reject_button);
        MaterialRippleLayout mAcceptButton = (MaterialRippleLayout) view.findViewById(R.id.accept_button);

        final Image item = itemsList.get(position);

        mProfileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                Intent i = new Intent(getActivity(), ProfileActivity.class);
                i.putExtra("profileId", item.getOwner().getId());
                startActivity(i);
            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                itemAction(METHOD_MANAGER_REJECT_GALLERY_ITEM, position);
            }
        });

        mAcceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                itemAction(METHOD_MANAGER_APPROVE_GALLERY_ITEM, position);
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(getActivity());

        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();

        doKeepDialog(mBottomSheetDialog);

        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                mBottomSheetDialog = null;
            }
        });
    }

    // Prevent dialog dismiss when orientation changes
    private static void doKeepDialog(Dialog dialog){

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }

    public void delete(final int position) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getText(R.string.label_delete));

        alertDialog.setMessage(getText(R.string.label_delete_msg));
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

//                final Item item = itemsList.get(position);
//
//                itemsList.remove(position);
//                itemsAdapter.notifyDataSetChanged();
//
//                if (itemsAdapter.getItemCount() == 0) {
//
//                    showMessage(getText(R.string.label_empty_list).toString());
//                }
//
//                if (App.getInstance().isConnected()) {
//
//                    Api api = new Api(getActivity());
//
//                    api.postDelete(item.getId());
//
//                } else {
//
//                    Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
//                }
            }
        });

        alertDialog.show();
    }

    //

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);
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