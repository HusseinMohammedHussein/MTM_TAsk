package com.e.mtmtask;

import com.e.mtmtask.Models.DestinationLocationPojo;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Hussein on 04/02/2021
 */
public interface Services {

    @POST("place/findplacefromtext/json")
    Call<DestinationLocationPojo> getDestinationLocation(@Query("input") String input,
                                                         @Query("inputtype") String inputtype,
                                                         @Query("fields") String[] fields,
                                                         @Query("key") String key);
}
