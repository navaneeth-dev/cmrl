package com.rizexor.cmrl;

import com.google.gson.annotations.SerializedName;

/* loaded from: classes.dex */
public class Login {
    @SerializedName("QRjwttoken")
    private String QRjwttoken;
    @SerializedName("QRurl")
    private String QRurl;

    public String getQRurl() {
        return this.QRurl;
    }

    public void setQRjwttoken(String str) {
        this.QRjwttoken = str;
    }

    public String getQRjwttoken() {
        return this.QRjwttoken;
    }

    public void setQRurl(String str) {
        this.QRurl = str;
    }
}