package com.bilalbaloch.countries;

/**
 *
 * @author bilalbaloch
 * @see CountryDetail
 */

import java.io.Serializable;

public class Country implements Serializable, Comparable<Country> {

    private static final long serialVersionUID = 7526472295622776147L;

    public String name;
    public String code;
    public CountryDetail detail;

    public Country(String c, String n) {
        code = c;
        name = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CountryDetail getDetail() {
        return detail;
    }

    public void setDetail(CountryDetail detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    @Override
    public int compareTo(Country c) {
        String name = getName();
        String c_name = c.getName();
        return name.compareTo(c_name);
    }
}