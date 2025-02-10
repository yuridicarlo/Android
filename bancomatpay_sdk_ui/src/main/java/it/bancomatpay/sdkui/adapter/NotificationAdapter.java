package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.BankIdRequest;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.DirectDebitRequest;
import it.bancomatpay.sdk.manager.task.model.NotificationData;
import it.bancomatpay.sdk.manager.task.model.NotificationPaymentData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.HomeNotificationData;
import it.bancomatpay.sdkui.model.NotificationAccessItem;
import it.bancomatpay.sdkui.model.NotificationDirectDebitItem;
import it.bancomatpay.sdkui.model.NotificationPaymentItem;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.widgets.ProgressBarChronometer;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PAYMENT_VIEW_TYPE = 0;
    private static final int ACCESS_VIEW_TYPE = 1;
    private static final int DIRECT_DEBIT_VIEW_TYPE = 2;

    private List<HomeNotificationData> mValues;
    private final NotificationAdapter.InteractionListener mListener;

    public NotificationAdapter(NotificationData notificationData, NotificationAdapter.InteractionListener listener) {
        mValues = getList(notificationData);
        mListener = listener;
    }

    private List<HomeNotificationData> getList(NotificationData notificationData) {
        List<HomeNotificationData> newList = new ArrayList<>();

        if (notificationData.getNotificationBankIdRequest() != null
                && notificationData.getNotificationPaymentData() != null && notificationData.getNotificationDirectDebitRequests() != null) {
            for (BankIdRequest accessRequestData : notificationData.getNotificationBankIdRequest()) {
                newList.add(new NotificationAccessItem(accessRequestData));
            }
            for (NotificationPaymentData transactionData : notificationData.getNotificationPaymentData()) {
                newList.add(new NotificationPaymentItem(transactionData));
            }
            for (DirectDebitRequest directDebitData : notificationData.getNotificationDirectDebitRequests()) {
                newList.add(new NotificationDirectDebitItem(directDebitData));
            }
        }

        return newList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == PAYMENT_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_payment_item, parent, false);
            return new ViewHolderPayment(view);
        } else if (viewType == ACCESS_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_access_item, parent, false);
            return new ViewHolderAccess(view);
        } else if (viewType == DIRECT_DEBIT_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_direct_debit_item, parent, false);
            return new ViewHolderDirectDebitRequest(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Context context = PayCore.getAppContext();

        if (holder instanceof ViewHolderPayment) {

            ViewHolderPayment holderUser = (ViewHolderPayment) holder;
            holderUser.mItem = (NotificationPaymentItem) mValues.get(position);
            holderUser.mNameView.setText((holderUser.mItem.getNotificationPaymentData().getItem()).getTitle());

            if (holderUser.mItem.getNotificationPaymentData().getItem() instanceof ShopItem) {

                ShopItem shopItem = ((ShopItem) holderUser.mItem.getNotificationPaymentData().getItem());
                if (shopItem.getAddress() != null && shopItem.getAddress().getStreet() != null) {
                    holderUser.mLabelView.setText(shopItem.getAddress().getStreet().toLowerCase() + ", " + shopItem.getAddress().getCity().charAt(0) + shopItem.getAddress().getCity().substring(1).toLowerCase());
                } else {
                    holderUser.mLabelView.setVisibility(View.GONE);
                }

                holderUser.mPriceView.setText(StringUtils.getFormattedValue(holderUser.mItem.getNotificationPaymentData().getPaymentItem().getAmount()));
                Bitmap bitmap = holderUser.mItem.getBitmap();
                if (bitmap != null) {
                    holderUser.mImageView.setImageBitmap(bitmap);
                    holderUser.mLetterView.setVisibility(View.GONE);
                } else {
                    holderUser.mImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder_merchant));
                    holderUser.mLetterView.setVisibility(View.VISIBLE);
                    holderUser.mLetterView.setText(shopItem.getLetter());
                }
                holderUser.mContactType.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.movimenti_merchant));
                holderUser.mLogo.setVisibility(View.INVISIBLE);
                if(holderUser.mExpirationTimeLayout.getVisibility() == View.VISIBLE){
                    holderUser.mExpirationTimeLayout.setVisibility(View.GONE);
                }

            } else if (holderUser.mItem.getNotificationPaymentData().getItem() instanceof ContactItem) {

                ContactItem contactItem = (ContactItem) holderUser.mItem.getNotificationPaymentData().getItem();
                //ContactItem contact = ApplicationModel.getInstance().getContactItem((contactItem.getPhoneNumber()));
                if (!TextUtils.isEmpty(contactItem.getPhoneNumber())) {
                    //if(contact != null) {
                    //holderUser.mNameView.setText(contact.getTitle());
                    holderUser.mNameView.setText(holderUser.mItem.getNotificationPaymentData().getPaymentItem().getInsignia());
                    //} else {
                    //holderUser.mNameView.setText(contactItem.getPhoneNumber());
                    //}
                    holderUser.mLabelView.setText(contactItem.getPhoneNumber());
                } else {
                    holderUser.mLabelView.setVisibility(View.GONE);
                }
            /*String date = StringUtils.getDateLongFormatted(holderUser.mItem.getNotificationPaymentData().getPaymentItem().getPaymentDate().toString());
            holderUser.mLabelView.setText(date);*/

                holderUser.mPriceView.setText(StringUtils.getFormattedValue(holderUser.mItem.getNotificationPaymentData().getPaymentItem().getAmount()));
                Bitmap bitmap = holderUser.mItem.getBitmap();
                if (bitmap != null) {
                    holderUser.mConsumerImage.setImageBitmap(bitmap);
                    holderUser.mConsumerImage.setVisibility(View.VISIBLE);
                    holderUser.mImageView.setVisibility(View.INVISIBLE);
                    holderUser.mLetterView.setVisibility(View.INVISIBLE);
                } else {
					holderUser.mImageView.setImageResource(R.drawable.placeholder_circle_consumer);
                    holderUser.mConsumerImage.setVisibility(View.GONE);
                    holderUser.mLetterView.setVisibility(View.VISIBLE);
					holderUser.mLetterView.setText(holderUser.mItem.getInitials());
				}
				holderUser.mContactType.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.movimenti_user));

                if(holderUser.mItem.getNotificationPaymentData().getPaymentItem().getExpirationDate() != null && holderUser.mItem.getNotificationPaymentData().getPaymentItem().getPaymentDate() != null){
                    holderUser.mExpirationTimeLayout.setVisibility(View.VISIBLE);
                    holderUser.mProgressBar.setMax(100);
                    holderUser.mProgressBar.setItem(holderUser.mItem);
                    String paymentDay = StringUtils.getDateStringFormatted(holderUser.mItem.getNotificationPaymentData().getPaymentItem().getExpirationDate(), "dd MMMM");
                    String paymentDateTime = StringUtils.getDateStringFormatted(holderUser.mItem.getNotificationPaymentData().getPaymentItem().getExpirationDate(), "HH:mm");
                    holderUser.mExpirationDescription.setText(PayCore.getAppContext().getString(R.string.expiration_message, paymentDay, paymentDateTime));
                }else{
                    holderUser.mExpirationTimeLayout.setVisibility(View.GONE);
                }
			}

            holderUser.mView.setOnClickListener(new CustomOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onPaymentInteraction(holderUser.mItem);
                }
            }));

        } else if (holder instanceof ViewHolderAccess) {

            ViewHolderAccess holderUser = (ViewHolderAccess) holder;
            holderUser.mItem = (NotificationAccessItem) mValues.get(position);

            holderUser.mNameView.setText(holderUser.mItem.getNotificationAccessData().getBankIdMerchantData().getMerchantName());

            String requestTime = "";
            if (holderUser.mItem.getNotificationAccessData() != null
                    && holderUser.mItem.getNotificationAccessData().getRequestDateTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(" - HH:mm", Locale.getDefault());
                requestTime = sdf.format(holderUser.mItem.getNotificationAccessData().getRequestDateTime());
            }
            holderUser.mAccessRequestTimeView.setText(
                    context.getString(R.string.notification_list_access_request_time_label, requestTime));

            holderUser.mView.setOnClickListener(new CustomOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onAccessInteraction(holderUser.mItem);
                }
            }));

        } else if (holder instanceof ViewHolderDirectDebitRequest) {
            ViewHolderDirectDebitRequest holderUser = (ViewHolderDirectDebitRequest) holder;
            holderUser.mItem = (NotificationDirectDebitItem) mValues.get(position);

            holderUser.mMerchantName.setText(holderUser.mItem.getNotificationdirectDebitData().getMerchantName());

			/*String requestTime = "";
			if (holderUser.mItem.getNotificationAccessData() != null
					&& holderUser.mItem.getNotificationAccessData().getRequestDateTime() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(" - HH:mm");
				requestTime = sdf.format(holderUser.mItem.getNotificationAccessData().getRequestDateTime());
			}
			holderUser.mAccessRequestTimeView.setText(
					context.getString(R.string.notification_list_access_request_time_label, requestTime));
*/
            holderUser.mView.setOnClickListener(new CustomOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onDirectDebitInteraction(holderUser.mItem);
                }
            }));
        }

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
        HomeNotificationData item = mValues.get(position);
        if (item instanceof NotificationPaymentItem) {
            return PAYMENT_VIEW_TYPE;
        } else if (item instanceof NotificationAccessItem) {
            return ACCESS_VIEW_TYPE;
        } else if (item instanceof NotificationDirectDebitItem) {
            return DIRECT_DEBIT_VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }

    public void updateList(NotificationData notificationData) {
        mValues = getList(notificationData);
        notifyDataSetChanged();
    }

    public static class ViewHolderPayment extends RecyclerView.ViewHolder {

		View mView;
		TextView mNameView;
		TextView mLabelView;
		TextView mPriceView;
		ImageView mImageView;
        CircleImageView mConsumerImage;
		TextView mLetterView;
		ImageView mContactType;
		ImageView mLogo;
		CardView mConsumerContainer;
		View mExpirationTimeLayout;
		TextView mExpirationDescription;
		ProgressBarChronometer mProgressBar;

		NotificationPaymentItem mItem;

		ViewHolderPayment(View view) {
			super(view);
			mView = view;
			mNameView = view.findViewById(R.id.contact_consumer_name);
			mLabelView = view.findViewById(R.id.contact_consumer_number);
			mPriceView = view.findViewById(R.id.transaction_price);
			mImageView = view.findViewById(R.id.contact_consumer_image_profile);
			mConsumerImage = view.findViewById(R.id.contact_consumer_image_profile_circle);
			mLetterView = view.findViewById(R.id.contact_consumer_letter);
			mContactType = view.findViewById(R.id.contact_type);
			mLogo = view.findViewById(R.id.contact_consumer_is_active);
			mConsumerContainer = view.findViewById(R.id.consumer_container);
            mExpirationTimeLayout = view.findViewById(R.id.expiration_time_layout);
            mProgressBar = view.findViewById(R.id.progress_bar_expiration_date);
            mExpirationDescription = view.findViewById(R.id.expiration_date_description);
		}
	}

    public static class ViewHolderAccess extends RecyclerView.ViewHolder {

        View mView;
        TextView mNameView;
        TextView mAccessRequestTimeView;

        NotificationAccessItem mItem;

        ViewHolderAccess(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.access_request_name);
            mAccessRequestTimeView = view.findViewById(R.id.access_request_time);
        }
    }

    public static class ViewHolderDirectDebitRequest extends RecyclerView.ViewHolder {
        View mView;
        TextView mMerchantName;

        NotificationDirectDebitItem mItem;

        ViewHolderDirectDebitRequest(View view) {
            super(view);
            mView = view;
            mMerchantName = view.findViewById(R.id.dd_merchant_name);

        }
    }


    public interface InteractionListener {
        void onPaymentInteraction(NotificationPaymentItem paymentItem);

        void onAccessInteraction(NotificationAccessItem accessItem);

        void onDirectDebitInteraction(NotificationDirectDebitItem debitItem);
    }

}
