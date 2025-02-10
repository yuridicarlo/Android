package it.bancomatpay.sdkui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.EmptyItemListTile;
import it.bancomatpay.sdkui.model.ListTile;
import it.bancomatpay.sdkui.model.SeparatorItemConsumer;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class StoreLocatorListTileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<ListTile> mValues;
    private List<ListTile> mValuesBackup;
    private final InteractionListener mListener;
    private OnFilterResultsPublished onResultsPublishedCallback;
    private boolean enableTapAnimation;

    private int ITEM_TYPE_DATA = 0;
    private int ITEM_TYPE_SPACING = 1;

    private CharSequence lastFilter = "";

    private Boolean showDetails = true;

    public StoreLocatorListTileAdapter(List<ListTile> items, InteractionListener listener, boolean enableTapAnimation) {
        mValues = new ArrayList<>();
        mValues.addAll(items);
        mValues.add(new EmptyItemListTile());
        mValuesBackup = new ArrayList<>();
        mValuesBackup.addAll(mValues);
        mListener = listener;
        this.enableTapAnimation = enableTapAnimation;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_DATA) {
            View view;
            if(enableTapAnimation) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_tile_item, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_tile_item_variant_no_tap, parent, false);
            }
            return new ListTileViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_view_holder, parent, false);
            return new EmptyViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==(getItemCount()-1) && getItemCount() > 1){
            mListener.onLastItemVisible();
        }
        if (getItemViewType(position) == ITEM_TYPE_SPACING) {
            return;
        }

        final ListTileViewHolder holderItem = (ListTileViewHolder) holder;
        ListTile mItem = mValues.get(position);

        holderItem.title.setText(mItem.getTitle());
        holderItem.leadingIcon.setImageResource(mItem.getLeadingIconRes());

        if(mItem.getSubtitle() != null && showDetails) {
            holderItem.subtitle.setText(mItem.getSubtitle());
            holderItem.subtitle.setVisibility(View.VISIBLE);
        } else {
            holderItem.subtitle.setVisibility(View.GONE);
        }

        if(mItem.getTrailingText() != null) {
            holderItem.trailingText.setText(mItem.getTrailingText());
            holderItem.trailingText.setVisibility(View.VISIBLE);
        } else {
            holderItem.trailingText.setVisibility(View.GONE);
        }

        holderItem.view.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTap(mItem);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mValues.size() - 1) {
            return ITEM_TYPE_DATA;
        } else {
            return ITEM_TYPE_SPACING;
        }
    }

    public void setShowDetails(Boolean isShow) {
        showDetails = isShow;
    }

    public void addItems(List<ListTile> newItems) {
        if(getItemCount() > 0) {
            mValues.remove(getItemCount() - 1);
        }
        mValues.addAll(newItems);
        mValues.add(new EmptyItemListTile());
        mValuesBackup = new ArrayList<>();
        mValuesBackup.addAll(mValues);
        int itemCount = newItems.size();
        int startPosition = getItemCount() - (itemCount);
        notifyItemRangeInserted(startPosition, itemCount);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        mValues = new ArrayList<>();
        mValues.add(new EmptyItemListTile());
        mValuesBackup = new ArrayList<>();
        mValuesBackup.addAll(mValues);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mValues = (List<ListTile>) results.values;
                mValues.add(new EmptyItemListTile());
                notifyDataSetChanged();
                if(onResultsPublishedCallback != null) {
                    onResultsPublishedCallback.onResultPublished();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                lastFilter = constraint;
                FilterResults results = new FilterResults();
                List<ListTile> filteredItems = new ArrayList<>();

                for (int i = 0; i < mValuesBackup.size(); i++) {
                    if (!(mValuesBackup.get(i) instanceof SeparatorItemConsumer)) {
                        if (mValuesBackup.get(i).performFilter(constraint.toString())) {
                            filteredItems.add(mValuesBackup.get(i));
                        }
                    }
                }

                CustomLogger.d("ITEM", "" + filteredItems.size());

                results.count = filteredItems.size();
                results.values = filteredItems;

                return results;
            }
        };
    }

    public void setOnResultsPublishedCallback(OnFilterResultsPublished onResultsPublishedCallback) {
        this.onResultsPublishedCallback = onResultsPublishedCallback;
    }

    public interface OnFilterResultsPublished {
        void onResultPublished();
    }

    private static class ListTileViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        TextView trailingText;
        ImageView leadingIcon;
        View view;

        ListTileViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            trailingText = view.findViewById(R.id.trailing_text);
            leadingIcon = view.findViewById(R.id.leading_icon);
            this.view = view;
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout emptyLinearLayout;
        View view;
        public EmptyViewHolder(View view) {
            super(view);
            this.view = view;
            this.emptyLinearLayout = view.findViewById(R.id.emptyLayout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 200);
            emptyLinearLayout.setLayoutParams(layoutParams);
        }
    }

    public interface InteractionListener {
        void onTap(ListTile item);

        void onLastItemVisible();
    }
}
