package it.bancomatpay.sdkui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;


public class WindowViewModel extends ViewModel {

	private final static String TAG = WindowViewModel.class.getSimpleName();
	private final MutableLiveData<Integer> statusBarColor;
	private final MutableLiveData<Integer> navigationBarColor;
	private final MutableLiveData<Integer> loader;


	public WindowViewModel() {
		this.statusBarColor = new MutableLiveData<>();
		this.navigationBarColor = new MutableLiveData<>();
		this.loader = new MutableLiveData<>(0);
	}

	public void setStatusBarColor(int color){
		statusBarColor.setValue(color);
	}

	public void setNavigationBarColor(int color){
		navigationBarColor.setValue(color);
	}

	public LiveData<Integer> getNavigationBarColor() {
		return navigationBarColor;
	}

	public LiveData<Integer> getStatusBarColor() {
		return statusBarColor;
	}

	public LiveData<Integer> getLoader() {
		return loader;
	}

	public synchronized boolean isShowingLoader(){
		return loader.getValue() != null && loader.getValue() != 0;
	}

	public synchronized void showLoader() {
		CustomLogger.d(TAG, "loader value = " + loader.getValue());
		int old = loader.getValue() != null ? loader.getValue() : 0;
		loader.setValue(old + 1);
	}

	public synchronized void hideLoader() {
		CustomLogger.d(TAG, "loader value = " + loader.getValue());
		int value = loader.getValue() - 1;
		if (value >= 0) {
			loader.setValue(value);
		}
	}
}
