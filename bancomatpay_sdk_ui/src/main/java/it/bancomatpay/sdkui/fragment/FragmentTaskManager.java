package it.bancomatpay.sdkui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Set;

import it.bancomatpay.sdk.core.OnCompleteListener;
import it.bancomatpay.sdk.core.Task;

public class FragmentTaskManager extends Fragment implements OnCompleteListener {

	Set<Task<?>> taskSet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		taskSet = new HashSet<>();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		for (Task<?> task : taskSet) {
			task.removeListener();
		}
		taskSet.clear();
	}

	public synchronized void addTask(Task<?> task) {
		if (!taskSet.contains(task)) {
			task.setMasterListener(this);
			taskSet.add(task);
			task.execute();
		}
	}

	@Override
	public void onComplete(Task<?> task) {
		taskSet.remove(task);
	}

	@Override
	public void onCompleteWithError(final Task<?> task, Error e) {
		taskSet.remove(task);
	}

}
