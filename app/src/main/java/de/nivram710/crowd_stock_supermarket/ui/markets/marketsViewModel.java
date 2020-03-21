package de.nivram710.crowd_stock_supermarket.ui.markets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class marketsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public marketsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    LiveData<String> getText() {
        return mText;
    }
}