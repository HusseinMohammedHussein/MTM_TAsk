package com.e.mtmtask.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.e.mtmtask.Models.SourceLocationPojo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hussein on 04/02/2021
 */
public class FirestoreDataViewModel extends ViewModel {
    private final MutableLiveData<List<SourceLocationPojo>> getSourceLocation;

    public FirestoreDataViewModel() {
        this.getSourceLocation = new MutableLiveData<>();
    }

    public MutableLiveData<List<SourceLocationPojo>> getFirestoreData(FirebaseFirestore firestore) {
        List<SourceLocationPojo> pojoList = new ArrayList<>();
        firestore.collection("Source")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            pojoList.add(document.toObject(SourceLocationPojo.class));
                            getSourceLocation.setValue(pojoList);
                        }
                    }
                });

        return getSourceLocation;
    }
}
