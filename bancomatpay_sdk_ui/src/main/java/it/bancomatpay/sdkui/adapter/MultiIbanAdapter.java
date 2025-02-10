package it.bancomatpay.sdkui.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdk.manager.task.model.InstrumentData.EHeaderType.HEADER_GET_MONEY;
import static it.bancomatpay.sdk.manager.task.model.InstrumentData.EHeaderType.HEADER_OTHER_BANK;
import static it.bancomatpay.sdk.manager.task.model.InstrumentData.EHeaderType.HEADER_SEND_MONEY;

public class MultiIbanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final IbanListClickListener mListener;
    private final Drawable logoBank;
    private final List<InstrumentData> mValues;

    public MultiIbanAdapter(List<InstrumentData> ibanList, Drawable logoBank, IbanListClickListener listener) {
        this.mValues = createListWithHeaders(ibanList);
        this.logoBank = logoBank;
        this.mListener = listener;
    }

    private ArrayList<InstrumentData> createListWithHeaders(List<InstrumentData> ibanList) {
        ArrayList<InstrumentData> listRet = new ArrayList<>();
        InstrumentData headerSendMoney = new InstrumentData();
        headerSendMoney.setHeaderType(HEADER_SEND_MONEY);
        InstrumentData headerGetMoney = new InstrumentData();
        headerGetMoney.setHeaderType(HEADER_GET_MONEY);
        listRet.add(headerSendMoney);
        for (InstrumentData instrument : ibanList) {
            InstrumentData data = new InstrumentData(instrument.getInstrument());
            data.setIbanCategory(InstrumentData.EIbanCategory.IBAN_SEND_MONEY);
            listRet.add(data);
        }
        listRet.add(headerGetMoney);
        for (InstrumentData instrument : ibanList) {
            InstrumentData data = new InstrumentData(instrument.getInstrument());
            data.setIbanCategory(InstrumentData.EIbanCategory.IBAN_GET_MONEY);
            listRet.add(data);
        }
        if (ApplicationModel.getInstance().getUserData() != null && ApplicationModel.getInstance().getUserData().isDefaultReceiverOtherBank()) {
            InstrumentData headerOtherBank = new InstrumentData();
            headerOtherBank.setHeaderType(HEADER_OTHER_BANK);
            listRet.add(headerOtherBank);
        }
        return listRet;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_accounts_management_send_money_header, parent, false);
                return new ViewHolderHeaderSendMoneyAccount(view);
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_accounts_management_get_money_header, parent, false);
                return new ViewHolderHeaderGetMoneyAccount(view);
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_accounts_management_other_bank, parent, false);
                return new ViewHolderOtherBank(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_accounts_management_iban_item, parent, false);
                return new ViewHolderIban(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
            case 1:
                break;
            case 2:
                ViewHolderOtherBank holderOtherBank = (ViewHolderOtherBank) holder;
                manageViewHolder(holderOtherBank);
                break;
            default:
                ViewHolderIban holderIban = (ViewHolderIban) holder;
                manageViewHolder(holderIban, position);
                break;
        }
    }

    private void manageViewHolder(ViewHolderOtherBank holder) {

        if (ApplicationModel.getInstance().getUserData().isDefaultReceiverOtherBank()) {
            holder.imageCheck.setVisibility(View.VISIBLE);
        } else {
            holder.imageCheck.setVisibility(View.INVISIBLE);
            holder.textLabel.setTextColor(ContextCompat.getColor(PayCore.getAppContext(), R.color.grey_money_label));
        }

    }

    private void manageViewHolder(final ViewHolderIban holder, int position) {

        holder.mItem = mValues.get(position); //InstrumentData

        if (holder.mItem.getIbanCategory() == InstrumentData.EIbanCategory.IBAN_SEND_MONEY) {
            if (holder.mItem.isDefaultOutgoing()) {
                holder.imageCheck.setVisibility(View.VISIBLE);
            } else {
                holder.imageCheck.setVisibility(View.INVISIBLE);
            }
        } else if (holder.mItem.getIbanCategory() == InstrumentData.EIbanCategory.IBAN_GET_MONEY) {
            if (holder.mItem.isDefaultIncoming()) {
                holder.imageCheck.setVisibility(View.VISIBLE);
            } else {
                holder.imageCheck.setVisibility(View.INVISIBLE);
            }
        }

        if (logoBank != null) {
            holder.imageBankLogo.setImageDrawable(logoBank);
        }

        holder.textIban.setText(holder.mItem.getIban());
        holder.view.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                if (!holder.mItem.isDefaultOutgoing() && holder.mItem.getIbanCategory() == InstrumentData.EIbanCategory.IBAN_SEND_MONEY) {
                    mListener.onIbanClicked(holder.mItem);
                } else if (!holder.mItem.isDefaultIncoming() && holder.mItem.getIbanCategory() == InstrumentData.EIbanCategory.IBAN_GET_MONEY) {
                    mListener.onIbanClicked(holder.mItem);
                }
            }
        }));

    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 3; //ibanHolder
        if (mValues.get(position).getHeaderType() != null) {
            if (mValues.get(position).getHeaderType() == HEADER_SEND_MONEY) {
                viewType = 0; //headerSendMoney
            } else if (mValues.get(position).getHeaderType() == HEADER_GET_MONEY) {
                viewType = 1; //headerGetMoney
            } else if (mValues.get(position).getHeaderType() == HEADER_OTHER_BANK) {
                viewType = 2; //headerOtherBank
            }
        }
        return viewType;
    }

    private static class ViewHolderIban extends RecyclerView.ViewHolder {
        ImageView imageBankLogo;
        TextView textIban;
        ImageView imageCheck;
        View view;
        InstrumentData mItem;

        ViewHolderIban(View view) {
            super(view);
            imageBankLogo = view.findViewById(R.id.profile_iban_image_logo);
            textIban = view.findViewById(R.id.profile_iban_text);
            imageCheck = view.findViewById(R.id.profile_iban_image_check);
            this.view = view;
        }
    }

    private static class ViewHolderOtherBank extends RecyclerView.ViewHolder {
        View view;
        TextView textLabel;
        ImageView imageCheck;

        ViewHolderOtherBank(View view) {
            super(view);
            this.view = view;
            textLabel = view.findViewById(R.id.text_label_other_bank);
            imageCheck = view.findViewById(R.id.profile_iban_image_check);
        }
    }

    private static class ViewHolderHeaderSendMoneyAccount extends RecyclerView.ViewHolder {
        ViewHolderHeaderSendMoneyAccount(View view) {
            super(view);
        }
    }

    private static class ViewHolderHeaderGetMoneyAccount extends RecyclerView.ViewHolder {
        ViewHolderHeaderGetMoneyAccount(View view) {
            super(view);
        }
    }

    public interface IbanListClickListener {
        void onIbanClicked(InstrumentData clickedIban);
    }

}
