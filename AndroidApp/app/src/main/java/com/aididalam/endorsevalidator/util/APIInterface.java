package com.aididalam.endorsevalidator.util;

import com.aididalam.endorsevalidator.pojo.AllAsset;
import com.aididalam.endorsevalidator.pojo.Asset;
import com.aididalam.endorsevalidator.pojo.AssetTransactionPojo;
import com.aididalam.endorsevalidator.pojo.CreateAssetPojo;
import com.aididalam.endorsevalidator.pojo.Login;
import com.aididalam.endorsevalidator.pojo.PendingAssetPojo;
import com.aididalam.endorsevalidator.pojo.PendingTransferSignPojo;
import com.aididalam.endorsevalidator.pojo.ResponsePojo;
import com.aididalam.endorsevalidator.pojo.Transfer;
import com.aididalam.endorsevalidator.pojo.User;
import com.aididalam.endorsevalidator.pojo.UserFullDetails;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIInterface {
    @Headers({"Accept: application/json"})
    @POST("/auth/login")
    @FormUrlEncoded
    Call<Login> login(
                                @Field("email") String email,
                                @Field("password") String password
                           );

    @GET("/profile")
    Call<User> profile(@Header("Authorization") String token);

    @GET("/my-assets")
    Call<AllAsset> my_asset(@Header("Authorization") String token);

    @GET("/my-assets-info/{asse_id}")
    Call<Asset> asset_details(@Header("Authorization") String token, @Path(value = "asse_id", encoded = true) String asse_id);

    @GET("/assetAPI/{asse_id}")
    Call<List<AssetTransactionPojo>> asset_transaction(@Path(value = "asse_id", encoded = true) String asse_id);

    @POST("/getPrevTransferInfoOfAsset")
    @FormUrlEncoded
    Call<ResponsePojo> checkTransferPermission(
            @Field("id") String asset_id,
            @Field("receiver") String receiver,
            @Header("Authorization") String token
    );

    @POST("/requestTransferAsset")
    @FormUrlEncoded
    Call<Transfer> requestAssetTransfer(
            @Field("asset_id") String asset_id,
            @Field("amount") String amount,
            @Field("description") String description,
            @Field("sender") String sender,
            @Header("Authorization") String token
    );

    @POST("/requestLostTransfer")
    @FormUrlEncoded
    Call<Transfer> requestLostTransfer(
            @Field("asset_id") String asset_id,
            @Field("amount") String amount,
            @Field("description") String description,
            @Header("Authorization") String token
    );

    @GET("/getPendingTransferAll")
    Call<List<PendingTransferSignPojo>> pendingTransferSign(
            @Header("Authorization") String token
    );

    @POST("/signTransfer")
    @FormUrlEncoded
    Call<JsonElement> signTransfer(
            @Field("id") String asset_id,
            @Header("Authorization") String token
    );

    @POST("/addasset")
    @FormUrlEncoded
    Call<CreateAssetPojo> addAsset(
            @Field("name") String name,
            @Field("amount") String amount,
            @Field("unit") String unit,
            @Field("description") String description,
            @Header("Authorization") String token
    );

    @GET("/getPendingAssetAll")
    Call<List<PendingAssetPojo>> getPendingAssetAll(
            @Header("Authorization") String token
    );

    @POST("/signAsset")
    @FormUrlEncoded
    Call<JsonElement> signAsset(
            @Field("id") String asset_id,
            @Header("Authorization") String token
    );

    @GET("/profileFullInfo")
    Call<UserFullDetails> profileFullInfo(
            @Header("Authorization") String token
    );

    @POST("/updateProfileFullInfo")
    @Multipart
    Call<UserFullDetails> updateProfileFullInfo(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part profileIMG,
            @Header("Authorization") String token
    );

    @POST("/changePassword")
    @FormUrlEncoded
    Call<ResponsePojo> changePassword(
            @Field("oldPassword") String oldPassword,
            @Field("password") String password,
            @Header("Authorization") String token
    );

    @POST("/auth/register")
    @FormUrlEncoded
    Call<User> Register(
            @Field("name") String name,
            @Field("password") String password,
            @Field("email") String email,
            @Field("description") String description,
            @Field("type") int type
    );

    @POST("/sendOtp")
    @FormUrlEncoded
    Call<ResponsePojo> sendOTP(
            @Field("email") String email
    );

    @POST("/checkOtp")
    @FormUrlEncoded
    Call<ResponsePojo> checkOTP(
            @Field("email") String email,
            @Field("otp") String otp
    );
}

