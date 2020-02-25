package com.imvlabs.hbey.Entities;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * A model for object person to store in DB
 */
public class Person extends RealmObject {
    @PrimaryKey
    private String user_name;
    private boolean isMale;
    private int age;
    private RealmList<ResultData> results;

    public Person() {
    }

    public Person(String user_name, boolean isMale, int age) {
        this.user_name = user_name;
        this.isMale = isMale;
        this.age = age;
        results = new RealmList<>();
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setResults(RealmList<ResultData> results) {
        this.results = results;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setIsMale(boolean isMale) {
        this.isMale = isMale;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public RealmList<ResultData> getResults() {
        return results;
    }
}
