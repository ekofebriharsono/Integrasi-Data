package com.example.root.uasinta;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponeServer  {

    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("result")
    @Expose
    private List<ResultData> result = null;


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<ResultData> getResult() {
        return result;
    }

    public void setResult(List<ResultData> result) {
        this.result = result;
    }
}
