package com.e.mtmtask.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.e.mtmtask.Models.DestinationLocationPojo;
import com.e.mtmtask.Repositories.DestinationLocationRepo;

/**
 * Created by Hussein on 04/02/2021
 */
public class DestinationLocationViewModel extends ViewModel {
    private final DestinationLocationRepo getDestinationDataRepo;


    public DestinationLocationViewModel() {
        this.getDestinationDataRepo = new DestinationLocationRepo();
    }

    public MutableLiveData<DestinationLocationPojo> getDestinationDataMutable(String input, String inputType, String[] fields, String key) {
        return getDestinationDataRepo.getDestinationLocData(input, inputType, fields, key);
    }
}
