package com.example.vincent.notstealingyoursmsapp.api;

import com.example.vincent.notstealingyoursmsapp.api.model.MySms;
import com.example.vincent.notstealingyoursmsapp.api.model.Rule;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Vincent on 07/02/2017.
 */

public interface ApiInterface {

    @POST("/api/sms")
    Call<MySms> postSms(
            @Body MySms sms
    );

    @GET("/api/rule")
    Call<List<Rule>> getAllRules();

}
