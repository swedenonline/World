package com.bilalbaloch.countries;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manage country list and country details.
 * Can be further extended by DB Adapter class to save country details
 * to make it available for offline mode.
 *
 * @author bilalbaloch
 * @see ArrayList
 * @see Country
 * @see CountryDetail
 */

public class CountryManager {

    private Context context;
    private static CountryManager instance;
    private static List<Country> countries = new ArrayList<>();
    private static List<CountryDetail> listCountriesDetail = new ArrayList<>();
    private static CountryDetail countryDetail = null;
    private Object mLockObject = new Object();

    private CountryManager(Context ctx) {
        context = ctx;
    }

    public static synchronized CountryManager instance() {
        return instance;
    }

    public static void create(Context ctx) {
        instance = new CountryManager(ctx);
    }

    public static boolean isInstanciated() {
        return instance != null;
    }

    /**
     *
     * @return countries list in alphabetical order
     * @see Country
     */
    public List<Country> getCountries() {
        synchronized (mLockObject) {
            Collections.sort(countries);
            return countries;
        }
    }

    /**
     *
     * @param data list of countries
     * @see Country
     */
    public void setCountries(List<Country> data) {
        synchronized (mLockObject) {
            countries = data;
        }
    }

    /**
     *
     * @param data countries detail list
     * @see CountryDetail
     */
    public void setCountriesDetail(List<CountryDetail> data) {
        synchronized (mLockObject) {
            listCountriesDetail = data;
        }
    }

    /**
     *
     * @return coutry detail
     * @see CountryDetail
     */
    public CountryDetail getCountryDetail() {
        synchronized (mLockObject) {
            return countryDetail;
        }
    }

    /**
     *
     * @param data country detail
     * @see CountryDetail
     */
    public void setCountryDetail(CountryDetail data) {
        synchronized (mLockObject) {
            countryDetail = data;
        }
    }
}