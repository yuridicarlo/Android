//package it.bancomatpay.sdkui.utilities;
//
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.DiffUtil;
//
//import java.util.List;
//
//import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
//
//public class LoyaltyCardsDiffUtilCallback extends DiffUtil.Callback {
//
//	private List<LoyaltyCard> newList;
//	private List<LoyaltyCard> oldList;
//
//	public LoyaltyCardsDiffUtilCallback(List<LoyaltyCard> newList, List<LoyaltyCard> oldList) {
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
//		return true;
//	}
//
//	@Override
//	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//		boolean result;
//
//		LoyaltyCard newItem = newList.get(newItemPosition);
//		LoyaltyCard oldItem = oldList.get(oldItemPosition);
//		result = newItem.equals(oldItem);
//
//		return result;
//	}
//
//	@Nullable
//	@Override
//	public Object getChangePayload(int oldItemPosition, int newItemPosition) {
//		LoyaltyCard newItem = newList.get(newItemPosition);
//		LoyaltyCard oldItem = oldList.get(oldItemPosition);
//
//		Bundle diff = new Bundle();
//		if (!newItem.getLoyaltyCardId().equals(oldItem.getLoyaltyCardId())) {
//			diff.putString("loyaltyCardId", newItem.getLoyaltyCardId());
//		}
//		if (newItem.getOperationCounter() != oldItem.getOperationCounter()) {
//			diff.putInt("operationCounter", newItem.getOperationCounter());
//		}
//		if (!newItem.getBarCodeNumber().equals(oldItem.getBarCodeNumber())) {
//			diff.putString("barCodeNumber", newItem.getBarCodeNumber());
//		}
//		if (!newItem.getBarCodeType().equals(oldItem.getBarCodeType())) {
//			diff.putString("barCodeType", newItem.getBarCodeType());
//		}
//		if (!newItem.getBrand().equals(oldItem.getBrand())) {
//			diff.putSerializable("brand", newItem.getBrand());
//		}
//		if (diff.size() == 0) {
//			return null;
//		}
//		return diff;
//	}
//
//}
