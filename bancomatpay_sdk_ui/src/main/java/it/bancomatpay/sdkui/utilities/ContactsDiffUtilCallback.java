package it.bancomatpay.sdkui.utilities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import it.bancomatpay.sdkui.model.ContactsItemConsumer;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;

public class ContactsDiffUtilCallback extends DiffUtil.Callback {

	private List<ItemInterfaceConsumer> newList;
	private List<ItemInterfaceConsumer> oldList;

	public ContactsDiffUtilCallback(List<ItemInterfaceConsumer> newList, List<ItemInterfaceConsumer> oldList) {
		this.newList = newList;
		this.oldList = oldList;
	}

	@Override
	public int getOldListSize() {
		return oldList != null ? oldList.size() : 0;
	}

	@Override
	public int getNewListSize() {
		return newList != null ? newList.size() : 0;
	}

	@Override
	public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
		return newList.get(newItemPosition).getTitle().equals(oldList.get(oldItemPosition).getTitle());
	}

	@Override
	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
		boolean result = false;

		if (newList.get(newItemPosition) instanceof ContactsItemConsumer && oldList.get(oldItemPosition) instanceof ContactsItemConsumer) {
			ContactsItemConsumer newItem = (ContactsItemConsumer) newList.get(newItemPosition);
			ContactsItemConsumer oldItem = (ContactsItemConsumer) oldList.get(oldItemPosition);
			result = newItem.compareTo(oldItem) == 0;
		}

		return result;
	}

	@Nullable
	@Override
	public Object getChangePayload(int oldItemPosition, int newItemPosition) {
		if (newList.get(newItemPosition) instanceof ContactsItemConsumer && oldList.get(oldItemPosition) instanceof ContactsItemConsumer) {

			ContactsItemConsumer newContact = (ContactsItemConsumer) newList.get(newItemPosition);
			ContactsItemConsumer oldContact = (ContactsItemConsumer) oldList.get(oldItemPosition);

			Bundle diff = new Bundle();
			if (newContact.getDbModel().getPinningTime() != oldContact.getDbModel().getPinningTime()) {
				diff.putLong("pinningTime", newContact.getDbModel().getPinningTime());
			}
			if (diff.size() == 0) {
				return null;
			}
			return diff;

		} else {
			return null;
		}
	}

}
