package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumber;

public class DialogPrefixAdapter extends ArrayAdapter<DataPrefixPhoneNumber> {

	private LayoutInflater mInflater;
	private List<DataPrefixPhoneNumber> mItems;

	DialogPrefixAdapter(@NonNull Context context, @NonNull List<DataPrefixPhoneNumber> items) {
		super(context, R.layout.dialog_prefix_item, 0, items);
		mInflater = LayoutInflater.from(context);
		this.mItems = items;
	}

	@Override
	@NonNull
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			view = mInflater.inflate(R.layout.dialog_prefix_item, parent, false);
		}
		TextView textPrefix = view.findViewById(R.id.text_prefix_value);
		String prefix = mItems.get(position).getPrefix();
		textPrefix.setText(prefix);
		ImageView imageFlag = view.findViewById(R.id.image_flag);
		String flagPath = mItems.get(position).getLogoFlag();
		int resId = getContext().getResources().getIdentifier(flagPath,"drawable",getContext().getPackageName());
		imageFlag.setImageResource(resId);

		TextView textCountryCode = view.findViewById(R.id.text_prefix_header);
		String country = mItems.get(position).getCountry();
		textCountryCode.setText(country);

		return view;
	}

	@Override
	public int getCount() {
		if (mItems != null) {
			return mItems.size();
		}
		return 0;
	}

	@Nullable
	@Override
	public DataPrefixPhoneNumber getItem(int position) {
		return mItems.get(position);
	}

}
