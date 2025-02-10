package it.bancomat.pay.consumer.activation.databank;

import androidx.annotation.NonNull;

public class Bank {

    public String bankUUID;
    public String label;
    public String logo_search;
    public String logo_home;
    public String link_store_android;
    public String tags;
    public String support_email;
    public String support_phone;
    public String support_phone_foreign;
    public String support_opening_time;

    @NonNull
    @Override
    public String toString() {
        return "Bank{" +
                "bankUUID='" + bankUUID + '\'' +
                ", label='" + label + '\'' +
                ", logo_search='" + logo_search + '\'' +
                ", logo_home='" + logo_home + '\'' +
                ", link_store_android='" + link_store_android + '\'' +
                ", tags='" + tags + '\'' +
                ", support_email='" + support_email + '\'' +
                ", support_phone='" + support_phone + '\'' +
                ", support_phone_foreign='" + support_phone_foreign + '\'' +
                ", support_opening_time='" + support_opening_time + '\'' +
                '}';
    }

}
