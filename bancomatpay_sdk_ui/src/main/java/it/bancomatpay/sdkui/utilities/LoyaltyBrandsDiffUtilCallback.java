//package it.bancomatpay.sdkui.utilities;
//
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.DiffUtil;
//
//import java.util.List;
//
//import it.bancomatpay.sdk.manager.task.model.LoyaltyBrand;
//
//public class LoyaltyBrandsDiffUtilCallback extends DiffUtil.Callback {
//
//	private List<LoyaltyBrand> newList;
//	private List<LoyaltyBrand> oldList;
//
//	public LoyaltyBrandsDiffUtilCallback(List<LoyaltyBrand> newList, List<LoyaltyBrand> oldList) {
//		this.newList = newList;
//		this.oldList = oldList;
//	}
//
//	@Override
//	public int getOldListSize() {
//		return oldList != null ? oldList.size() : 0;
//	}
//
//	@Override
//	public int getNewListSize() {
//		return newList != null ? newList.size() : 0;
//	}
//
//	@Override
//	public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//		return newList.get(newItemPosition).getBrandUuid().equals(oldList.get(oldItemPosition).getBrandUuid());
//	}
//
//	@Override
//	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//		boolean result;
//
//		LoyaltyBrand newItem = newList.get(newItemPosition);
//		LoyaltyBrand oldItem = oldList.get(oldItemPosition);
//		result = newItem.compareTo(oldItem) == 0;
//
//		return result;
//	}
//
//	@Nullable
//	@Override
//	public Object getChangePayload(int oldItemPosition, int newItemPosition) {
//		LoyaltyBrand newItem = newList.get(newItemPosition);
//		LoyaltyBrand oldItem = oldList.get(oldItemPosition);
//
//		Bundle diff = new Bundle();
//		if (!newItem.getBrandUuid().equals(oldItem.getBrandUuid())) {
//			diff.putString("brandUuid", newItem.getBrandUuid());
//		}
//		if (diff.size() == 0) {
//			return null;
//		}
//		return diff;
//	}
//
//}
