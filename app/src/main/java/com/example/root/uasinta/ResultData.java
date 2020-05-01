package com.example.root.uasinta;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultData {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nama_produk")
    @Expose
    private String namaProduk;
    @SerializedName("harga")
    @Expose
    private int harga;
    @SerializedName("link_gambar")
    @Expose
    private String linkGambar;
    @SerializedName("link_produk")
    @Expose
    private String linkProduk;
    @SerializedName("from")
    @Expose
    private String from;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getLinkGambar() {
        return linkGambar;
    }

    public void setLinkGambar(String linkGambar) {
        this.linkGambar = linkGambar;
    }

    public String getLinkProduk() {
        return linkProduk;
    }

    public void setLinkProduk(String linkProduk) {
        this.linkProduk = linkProduk;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}