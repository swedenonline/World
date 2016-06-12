package com.bilalbaloch.countries;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilalbaloch.countries.Helper.CPopupWindow;
import com.bilalbaloch.countries.Service.CountryService;
import com.bilalbaloch.countries.Helper.CustomToast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bilalbaloch
 */
public class CountryActivity extends Activity implements StatusBar {

    private static final String TAG = CountryActivity.class.getSimpleName();
    private static CountryActivity instance = null;
    public CustomToast cToast;
    private GridView lView = null;
    private List<Country> countries = new ArrayList<>();
    private CountryListAdapter cla = null;
    private RelativeLayout overlay,no_record,status_bar,main_container;
    private CountryService mFeedService;
    private boolean mBound = false;
    private BroadcastReceiver mBroadcastReceiver;
    private static int clickedIndex = -1;

    public static final synchronized CountryActivity instance() {
        if(instance == null) {
            instance = new CountryActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        instance = this;

        cToast = new CustomToast(this, getApplicationContext());

        ((TextView) findViewById(R.id.nav_label)).setText(getString(R.string.title_activity_country));
        findViewById(R.id.nav_back).setVisibility(View.GONE);

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeView.setColorSchemeResources(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        swipeView.setEnabled(false);

        main_container = (RelativeLayout) findViewById(R.id.main_container);
        status_bar = (RelativeLayout) findViewById(R.id.status_bar);
        overlay = (RelativeLayout) findViewById(R.id.overlay);
        no_record = (RelativeLayout) findViewById(R.id.no_record);
        lView = (GridView) findViewById(R.id.list);

        cla = new CountryListAdapter(this, countries);
        lView.setAdapter(cla);

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedIndex = position;
                requestCountryDetail(countries.get(position));
            }
        });

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        //requestCountryList();
                    }
                }, 0);
            }
        });

        lView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) swipeView.setEnabled(true);
                else swipeView.setEnabled(false);
            }
        });

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == CountryService.ACTION_COUNTRY_DETAIL) {
                    //displayDetail((CountryDetail) intent.getSerializableExtra(CountryService.PARAM_COUNTRY_DETAIL));
                    displayDetail(CountryManager.instance().getCountryDetail());
                }else {
                    updateCountryList();
                }
            }
        };
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mFeedService = ((CountryService.LocalBinder)service).getService();
            mBound = true;
            requestCountryList();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mFeedService = null;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        bindService(new Intent(CountryActivity.this, CountryService.class), mConnection, BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter(CountryService.ACTION_COUNTRY_LIST);
        filter.addAction(CountryService.ACTION_COUNTRY_DETAIL);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void showLoader() {
        overlay.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        overlay.setVisibility(View.GONE);
    }

    @Override
    public void onNavBackClicked(View v) {
        //finish();
    }

    @Override
    public void onNavMenuClicked(View v) {
        requestCountryList();
    }

    public void updateCountryList() {
        countries = CountryManager.instance().getCountries();
        if(countries != null && countries.size() > 0) {
            cla.updateDataAndRefreshList(countries);
            no_record.setVisibility(View.GONE);
        }else {
            no_record.setVisibility(View.VISIBLE);
            cla.notifyDataSetInvalidated();
        }

        hideLoader();
    }

    public void displayDetail(CountryDetail c) {
        hideLoader();
        detailDialog(c);
    }

    public void detailDialog(CountryDetail c) {

        if(c == null) {
            cToast.displayMessage(getString(R.string.no_record));
            return;
        }

        //cache country detail...
        if(clickedIndex != -1) {
            countries.get(clickedIndex).setDetail(c);
            cla.updateDataAndRefreshList(countries);
            clickedIndex = -1;
        }

        showLoader();

        String code = c.getAlpha2Code().toLowerCase();
        int flagId = -1;

        //do is reserved java keyword, renamed do.png to do_.png
        if(code.equalsIgnoreCase("do"))
            flagId = (CountryActivity.instance().findResourceByName(code + "_"));
        else
            flagId = CountryActivity.instance().findResourceByName(code);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.country_detail, null);
        ImageView flag = (ImageView) dialogView.findViewById(R.id.flag);

        ((TextView)dialogView.findViewById(R.id.name)).setText(c.getName());
        ((TextView)dialogView.findViewById(R.id.nativeName)).setText(c.getNativeName());
        ((TextView)dialogView.findViewById(R.id.capital)).setText(c.getCapital());
        ((TextView)dialogView.findViewById(R.id.region)).setText(c.getRegion());
        ((TextView)dialogView.findViewById(R.id.subRegion)).setText(c.getSubregion());
        ((TextView)dialogView.findViewById(R.id.demonym)).setText(c.getDemonym());
        ((TextView)dialogView.findViewById(R.id.area)).setText(c.getArea());
        ((TextView)dialogView.findViewById(R.id.alpha2Code)).setText(c.getAlpha2Code());
        ((TextView)dialogView.findViewById(R.id.alpha3Code)).setText(c.getAlpha3Code());

        if(flagId > 0) flag.setImageResource(flagId);

        final CPopupWindow popupWindow = new CPopupWindow(
                dialogView,
                main_container,
                null,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hideLoader();
            }
        });
    }

    public void requestCountryList() {
        if(mFeedService != null) {
            showLoader();
            mFeedService.requestCountryList();
        }
    }

    public void requestCountryDetail(Country c) {
        if(NetworkManager.getInstance().isWifiConnected()) {
            if(mFeedService != null) {
                showLoader();
                mFeedService.requestCountryDetailByAlpha2Code(c.getCode());
            }
        }else {
            cToast.displayMessage(getString(R.string.no_internet));
            //check if country detail already cached...
            if(c.getDetail() != null) {
                clickedIndex = -1;
                detailDialog(c.getDetail());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public int findResourceByName(String str) {
        return CustomUtils.findResourceByName(getApplicationContext(), str, "drawable");
    }

}