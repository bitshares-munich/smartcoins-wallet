package de.bitshares_munich.models;

import com.google.gson.Gson;

/**
 * Created by Syed Muhammad Muzzammil on 6/15/16.
 */
public class Keys {
    public String wif_priv_key;
    public String pub_key;
    public String brain_priv_key;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
