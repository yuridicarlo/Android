//package it.bancomatpay.sdkui.prefixphonenumber;
//
//import android.content.Context;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.List;
//
//import it.bancomatpay.sdk.core.PayCore;
//import it.bancomatpay.sdkui.R;
//
//public class DataPrefixPhoneNumberAdapter extends RecyclerView.Adapter<DataPrefixPhoneNumberAdapter.ViewHolder> {
//
//    private final List<DataPrefixPhoneNumber> mValues;
//    private final OnListInteractionListener mListener;
//
//    public DataPrefixPhoneNumberAdapter(List<DataPrefixPhoneNumber> items, OnListInteractionListener listener) {
//        mValues = items;
//        mListener = listener;
//    }
//
//    @NonNull
//    @Override
//    public DataPrefixPhoneNumberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_data_prefix_phone_number, parent, false);
//        return new DataPrefixPhoneNumberAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final DataPrefixPhoneNumberAdapter.ViewHolder holder, final int position) {
//        holder.mItem = mValues.get(position);
//
//        Context context = PayCore.getAppContext();
//
//        holder.name.setText(holder.mItem.getCountry());
//        holder.shortName.setText(context.getString(R.string.country_short_name, holder.mItem.getCountryCode()));
//        holder.prefix.setText(holder.mItem.getPrefix());
//
//        holder.itemView.setOnClickListener(new CustomOnClickListener(v -> mListener.onListInteraction(holder.mItem)));
//    }
//
//    @Override
//    public int getItemCount() {
//        return mValues.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        DataPrefixPhoneNumber mItem;
//        TextView name;
//        TextView shortName;
//        TextView prefix;
//
//        ViewHolder(View view) {
//            super(view);
//            name = view.findViewById(R.id.data_prefix_phone_number_name);
//            shortName = view.findViewById(R.id.data_prefix_phone_number_short_name);
//            prefix = view.findViewById(R.id.data_prefix_phone_number_prefix);
//        }
//    }
//
//    public interface OnListInteractionListener {
//        void onListInteraction(DataPrefixPhoneNumber item);
//    }
//
//}
//
