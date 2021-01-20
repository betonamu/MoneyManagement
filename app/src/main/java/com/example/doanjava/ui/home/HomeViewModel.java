package com.example.doanjava.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mPositionSpinnerCategory;

    public HomeViewModel() {
        mPositionSpinnerCategory = new MutableLiveData<>();
    }

    public LiveData<String> getPositionSpinner() {
        return mPositionSpinnerCategory;
    }

    public void setPositionSpinner(String position) {
        mPositionSpinnerCategory.setValue(position);
    }

}