package com.bilalbaloch.countries.Service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import com.bilalbaloch.countries.Country;
import com.bilalbaloch.countries.CountryDetail;
import com.bilalbaloch.countries.CustomUtils;
import com.bilalbaloch.countries.NetworkManager;
import com.bilalbaloch.countries.CountryManager;
import com.bilalbaloch.countries.Helper.JSONParser;
import com.bilalbaloch.countries.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Country{@link Service} responsible for handling country list & country detail requests
 * in a separate thread. Currently it provides service on request, but in future this can be
 * used as background service with implementation of Alarm Manager / Job Scheduler to fetch
 * countries detail even activity is not running.
 *
 * Responsibilities: <br />
 * <ul>
 *     <li>
 *         loads country detail from server
 *     </li>
 *     <li>
 *         loads country list from assets
 *     </li>
 * </ul>
 *
 * @author bilalbaloch
 * @see Service
 * @see URL
 */
public class CountryService extends Service {

    private static final String TAG = CountryService.class.getSimpleName();
    public static final String ACTION_COUNTRY_LIST = "countryList";
    public static final String ACTION_COUNTRY_DETAIL = "countryDetail";
    public static final String PARAM_COUNTRY_DETAIL = "CountryDetail";
    private static CountryService instance = null;
    private IBinder mBinder = new LocalBinder();

    public static boolean isReady() {
        return (instance != null);
    }

    public static CountryService instance() {
        if(isReady()) return instance;
        else throw new NullPointerException();
    }

    public class LocalBinder extends Binder {
        public CountryService getService() {
            return CountryService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NetworkManager.create(getApplicationContext());//App manager constructor call
        NetworkManager.getInstance().updateNetworkReachability();
        CountryManager.create(getApplicationContext()); //initialize CountryManager
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * process activity request for country list
     */
    public void requestCountryList() {
        processCountryList();
    }

    /**
     * process activity request for country detail
     * @param code country code required to fetch country detail.
     */
    public void requestCountryDetailByAlpha2Code(String code) {
        String url = getString(R.string.country_detail_rest_api)+code.toLowerCase();
        if(NetworkManager.getInstance().isWifiConnected()) {
            new ProcessCountryDetail(url).execute();
        }
    }

    private static class ProcessCountryDetail extends AsyncTask<Void, Void, CountryDetail> {

        private static String url = null;
        public ProcessCountryDetail(String u) {
            url = u;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CountryDetail doInBackground(Void... arg0) {
            return prepareCountryDetail(JSONParser.jsonToMap(readJSONStringFromServer(url)));
        }

        @Override
        protected void onPostExecute(CountryDetail result) {
            super.onPostExecute(result);
            //save country detail
            CountryManager.instance().setCountryDetail(result);
            ackActivityForCountryDetail(result);
        }
    }

    private static void ackActivityForCountryList() {
        Intent refreshIntent = new Intent();
        refreshIntent.setAction(CountryService.ACTION_COUNTRY_LIST);
        CountryService.instance().sendBroadcast(refreshIntent);
    }

    /**
     * acknowledge activity after request is processed.
     * We are not broadcasting result, CountryManager class will take care of it.
     * @param c country detail object
     */
    private static void ackActivityForCountryDetail(CountryDetail c) {
        Intent refreshIntent = new Intent();
        //refreshIntent.putExtra(PARAM_COUNTRY_DETAIL, c);
        refreshIntent.setAction(CountryService.ACTION_COUNTRY_DETAIL);
        CountryService.instance().sendBroadcast(refreshIntent);
    }

    private void processCountryList() {
        List<Country> countries = new ArrayList<>();
        try {

            String json = CustomUtils.readJSONStringFromAsset(getApplicationContext(), getString(R.string.country_list_file_name));

            if(json != null) {

                JSONObject obj = new JSONObject(json);

                Iterator<String> iterator = obj.keys();
                while (iterator.hasNext()) {
                    String code   = iterator.next();
                    String name = obj.getString(code);
                    countries.add(new Country(code , name));
                }

                //save countries list
                CountryManager.instance().setCountries(countries);

                ackActivityForCountryList();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * reads json output from server
     * @param url server url
     * @return json string
     */
    private static String readJSONStringFromServer(String url) {

        try {
            InputStream in = new URL(url).openConnection().getInputStream();
            return (CustomUtils.convertStreamToString(in));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static CountryDetail prepareCountryDetail(Map<String, Object> result) {
        CountryDetail c = new CountryDetail();
        c.setName(result.get("name").toString());
        c.setRegion(result.get("region").toString());
        c.setSubregion(result.get("subregion").toString());
        c.setPopulation(result.get("population").toString());
        c.setGini(String.valueOf(result.get("gini")));
        c.setNativeName(result.get("nativeName").toString());
        c.setDemonym(result.get("demonym").toString());
        c.setCapital(result.get("capital").toString());
        c.setAlpha2Code(result.get("alpha2Code").toString());
        c.setAlpha3Code(result.get("alpha3Code").toString());
        c.setArea(String.valueOf(result.get("area")));
        return c;
    }
}