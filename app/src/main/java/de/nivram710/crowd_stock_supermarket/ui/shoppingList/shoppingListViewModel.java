package de.nivram710.crowd_stock_supermarket.ui.shoppingList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class shoppingListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public shoppingListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    LiveData<String> getText() {
        return mText;
    }
}