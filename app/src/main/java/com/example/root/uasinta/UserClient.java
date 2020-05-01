package com.example.root.uasinta;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserClient {

    @FormUrlEncoded
    @POST("uasinta/tes.php")
    Call<ResponeServer> dataScema(@Field("keyword") String keyword,
                                  @Field("user") String user);

    @FormUrlEncoded
    @POST("uasinta/add_to_favorite.php")
    Call<ResponeAddToFavorite> addToFavorite(@Field("id") String id,
                                             @Field("user") String user,
                                             @Field("nama_produk") String nama_produk,
                                             @Field("harga") int harga,
                                             @Field("link_gambar") String link_gambar,
                                             @Field("link_produk") String link_produk,
                                             @Field("from") String from);

    @FormUrlEncoded
    @POST("uasinta/list_favorite.php")
    Call<ResponeServer> listFavorite(@Field("user") String user);

    @FormUrlEncoded
    @POST("uasinta/list_data.php")
    Call<ResponeServer> listData(@Field("user") String user);

    @FormUrlEncoded
    @POST("uasinta/list_data_desc.php")
    Call<ResponeServer> listDataDesc(@Field("user") String user);

    @FormUrlEncoded
    @POST("uasinta/hapus_semua_favorite.php")
    Call<ResponeAddToFavorite> deleteAllFavorite(@Field("user") String user);

    @FormUrlEncoded
    @POST("uasinta/hapus_data_favorite.php")
    Call<ResponeAddToFavorite> deleteFavorite(@Field("id") String id);
}