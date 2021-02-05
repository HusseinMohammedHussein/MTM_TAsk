package com.e.mtmtask.Repositories;

import androidx.lifecycle.MutableLiveData;

import com.e.mtmtask.Models.DestinationLocationPojo;
import com.e.mtmtask.MyApplication;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Hussein on 04/02/2021
 */
public class DestinationLocationRepo {
    private final MutableLiveData<DestinationLocationPojo> getDestinationData;

    public DestinationLocationRepo() {
        this.getDestinationData = new MutableLiveData<>();
    }

    public MutableLiveData<DestinationLocationPojo> getDestinationLocData(String input, String inputType, String[] fields, String key) {
        Call<DestinationLocationPojo> call = MyApplication.getInstance().getDestinationLocation(input, inputType, fields, key);
        call.enqueue(new Callback<DestinationLocationPojo>() {
            @Override
            public void onResponse(@NotNull Call<DestinationLocationPojo> call, @NotNull Response<DestinationLocationPojo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    getDestinationData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<DestinationLocationPojo> call, @NotNull Throwable t) {
                Timber.d("DestinationLocationRepo-onFailure: %s", t.getLocalizedMessage());
            }
        });
        return getDestinationData;
    }
}
