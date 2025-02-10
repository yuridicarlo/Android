package it.bancomatpay.sdkui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomProgressDialogFragment;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.adapter.BlockedContactsAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmBlockedContactsV2Binding;
import it.bancomatpay.sdkui.model.ContactsItemConsumer;
import it.bancomatpay.sdkui.model.InteractionListener;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.fastscroll.FastScroller;
import it.bancomatpay.sdkui.widgets.fastscroll.FastScrollerBuilder;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

public class BlockedContactsActivity extends GenericErrorActivity implements InteractionListener {

	private static final int PERMISSION_CONTACT = 1000;

	private ActivityBcmBlockedContactsV2Binding binding;

	private List<ItemInterfaceConsumer> itemsContacts;
	private BlockedContactsAdapter adapter;

	private CustomProgressDialogFragment progressLoading;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActivityName(BlockedContactsActivity.class.getSimpleName());
		binding = ActivityBcmBlockedContactsV2Binding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		//Prevent keyboard open
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());
		LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(this);
		binding.recyclerViewContacts.setLayoutManager(layoutManagerPanel);
		itemsContacts = new ArrayList<>();
		adapter = new BlockedContactsAdapter(this, itemsContacts, this);
		binding.recyclerViewContacts.setAdapter(adapter);

//		FastScroller fastScroller = new FastScrollerBuilder(binding.recyclerViewContacts).build();
//
//		fastScroller.setOnTouchCallback(isInViewTouchTarget -> {
//			if (binding.refresh.isEnabled() == isInViewTouchTarget) {
//				binding.refresh.setEnabled(!isInViewTouchTarget);
//			}
//		});

		binding.recyclerViewContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
			private boolean lastVisibility = true;
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				boolean isVisible = isVisible();
				if(isVisible != lastVisibility) {
					if (isVisible) {
						binding.refresh.setEnabled(true);
					} else {
						binding.refresh.setEnabled(false);
					}
					lastVisibility = isVisible;
				}
			}

			boolean isVisible() {
				return layoutManagerPanel.findFirstVisibleItemPosition() == 0;
			}
		});
		binding.refresh.setOnRefreshListener(() -> {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
				contactList();
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_CONTACT);
			}
		});
		binding.refresh.setColorSchemeColors(
				ContextCompat.getColor(this, R.color.colorAccentBancomat));

		binding.recyclerViewContacts.setVisibility(View.INVISIBLE);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			contactList();
		} else {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_CONTACT);
		}

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable text) {
				if (adapter != null) {
					adapter.getFilter().filter(text);
				}

				if (text.length() > 0) {
					if(binding.cancelButtonSearch.getVisibility() != View.VISIBLE){
					AnimationFadeUtil.startFadeInAnimationV1(binding.cancelButtonSearch, 250);
				} }else {
					AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
				}
			}
		};
		binding.searchContactEditText.addTextChangedListener(textWatcher);
		binding.cancelButtonSearch.setOnClickListener(new CustomOnClickListener(v -> binding.searchContactEditText.getText().clear()));

	}

	@Override
	protected void onDestroy() {
		BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
		super.onDestroy();
	}

	private void contactList() {
		BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(this, result -> {
			if (result != null) {
				if (result.isSuccess()) {
					if (adapter != null) {
						manageListContacts(ApplicationModel.getInstance().getContactBcmItems());
					}
				} else if (result.isSessionExpired()) {
					BCMAbortCallback.getInstance().getAuthenticationListener()
							.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
				} else {
					showError(result.getStatusCode());
				}
			} else {
				manageEmptyList();
			}
		}, true, SessionManager.getInstance().getSessionToken());
		manageListContacts(ApplicationModel.getInstance().getContactBcmItems());
	}

	private void manageListContacts(ArrayList<ContactItem> contactItemArrayList) {

		if (contactItemArrayList != null) {
			itemsContacts = new ArrayList<>();
			for (ContactItem contactItem : contactItemArrayList) {
				itemsContacts.add(new ContactsItemConsumer(contactItem));
			}
		}

		if (itemsContacts != null && !itemsContacts.isEmpty()) {
			doRequest();
		} else {
			manageEmptyList();
			binding.refresh.setRefreshing(false);
		}

	}

	private void doRequest() {

		BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();
		sdk.doGetBlacklistContacts(this, result -> {

			if (result != null) {
				if (result.isSuccess()) {

					ArrayList<ItemInterfaceConsumer> blockedContacts = new ArrayList<>();
					for (ContactItem contact : result.getResult()) {
						blockedContacts.add(new ContactsItemConsumer(contact));
					}

					Iterator<ItemInterfaceConsumer> iterator = itemsContacts.iterator();
					while (iterator.hasNext()) {
						ItemInterfaceConsumer item = iterator.next();
						if (item instanceof ContactsItemConsumer
								&& ((ContactsItemConsumer) item).isBlocked()) {
							iterator.remove();
						}
					}

					itemsContacts.addAll(0, blockedContacts);

					adapter.updateModel(itemsContacts);

					manageEmptyList();
					binding.recyclerViewContacts.setVisibility(View.VISIBLE);

				} else if (result.isSessionExpired()) {
					BCMAbortCallback.getInstance().getAuthenticationListener()
							.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
				} else {
					binding.contactListEmpty.setVisibility(View.VISIBLE);
					binding.recyclerViewContacts.setVisibility(View.INVISIBLE);
					showError(result.getStatusCode());
				}
			}

			//timeout per evitare contatto duplicato con click compulsivi
			new Handler().postDelayed(() -> {
				if (progressLoading != null && progressLoading.isVisible()) {
					progressLoading.dismissAllowingStateLoss();
				}
				binding.refresh.setRefreshing(false);
				binding.recyclerViewContacts.setEnabled(true);
			}, 500);

		}, SessionManager.getInstance().getSessionToken());

		if (progressLoading == null || !progressLoading.isVisible()) {
			binding.refresh.setRefreshing(true);
		}
	}

	private void manageEmptyList() {
		if (adapter != null && adapter.getItemCount() > 0) {
			binding.contactListEmpty.setVisibility(View.GONE);
			binding.recyclerViewContacts.setVisibility(View.VISIBLE);
		} else {
			binding.contactListEmpty.setVisibility(View.VISIBLE);
			binding.recyclerViewContacts.setVisibility(View.INVISIBLE);
		}
	}

	private long lastClickTime = 0;

	@Override
	public void onConsumerInteraction(final ItemInterfaceConsumer item) {

		//Prevent double click
		if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
			return;
		}
		lastClickTime = SystemClock.elapsedRealtime();

		if (binding.recyclerViewContacts.isEnabled()) {

			binding.recyclerViewContacts.setEnabled(false);

			BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();
			sdk.doAllowPaymentRequestP2P(
					this, result -> {

						if (result != null) {
							if (result.isSuccess()) {
								if (item instanceof ContactsItemConsumer) {
									ContactsItemConsumer contact = ((ContactsItemConsumer) item);
									ContactItem contactItem = ApplicationModel.getInstance().getContactItem(item.getPhoneNumber());
									if (contact.isBlocked()) {
										contact.setBlocked(false);
										contactItem.setBlocked(false);
									} else {
										contact.setBlocked(true);
										contactItem.setBlocked(true);
									}
									//adapter.updateModel(itemsWithSeparator);
									contactList();
								}
							} else if (result.isSessionExpired()) {
								BCMAbortCallback.getInstance().getAuthenticationListener()
										.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
							} else {
								showError(result.getStatusCode());
								if (progressLoading != null && progressLoading.isVisible()) {
									progressLoading.dismissAllowingStateLoss();
								}
							}
						} else {
							if (progressLoading != null && progressLoading.isVisible()) {
								progressLoading.dismissAllowingStateLoss();
							}
							binding.recyclerViewContacts.setEnabled(true);
						}

					},
					((ContactsItemConsumer) item).isBlocked(),
					item.getPhoneNumber(), SessionManager.getInstance().getSessionToken());

			progressLoading = new CustomProgressDialogFragment();
			progressLoading.show(getSupportFragmentManager(), "");
		}
	}

	@Override
	public void onMerchantInteraction(ItemInterfaceConsumer item) {
		//Qui stiamo boni
	}

	@Override
	public void onImageConsumerInteraction(ItemInterfaceConsumer item) {
		//Qui stiamo boni
	}

	@Override
	public void onImageMerchantInteraction(ItemInterfaceConsumer item) {
		//Qui stiamo boni
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_CONTACT) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				contactList();
			} else {
				HashMap<String, String> mapEventParams = new HashMap<>();
				mapEventParams.put(PARAM_PERMISSION, "Contacts");
				CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
				binding.contactListEmpty.setVisibility(View.VISIBLE);
			}
		}
	}

}
