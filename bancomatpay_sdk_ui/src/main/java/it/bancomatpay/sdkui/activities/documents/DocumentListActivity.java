package it.bancomatpay.sdkui.activities.documents;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.DocumentsAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmDocumentListBinding;
import it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_DOCUMENT_ADD;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_DOCUMENT_SELECTED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_DOCUMENT_TYPE;

public class DocumentListActivity extends GenericErrorActivity implements DocumentsAdapter.InteractionListener {

	private DocumentsAdapter adapter;
	private List<BcmDocument> documentList;

	private PopupMenu popupMenu;
	private ActivityBcmDocumentListBinding binding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActivityName(DocumentListActivity.class.getSimpleName());
		binding = ActivityBcmDocumentListBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
		if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
			binding.toolbarSimple.setOnClickRightCenterImageListener(v -> TutorialFlowManager.goToDocuments(this));
		} else {
			binding.toolbarSimple.setRightCenterImageVisibility(false);
		}

		initPopupMenu();
		binding.toolbarSimple.setOnClickRightImageListener(view -> popupMenu.show());

		binding.refresh.setColorSchemeResources(R.color.colorAccentBancomat);

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		binding.recyclerViewDocuments.setLayoutManager(layoutManager);

		documentList = new ArrayList<>();
		adapter = new DocumentsAdapter(documentList, this);
		binding.recyclerViewDocuments.setAdapter(adapter);

		binding.refresh.setOnRefreshListener(this::doRequest);

	}

	@Override
	protected void onResume() {
		super.onResume();
		doRequest();
	}

	@Override
	protected void onDestroy() {
		BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
		super.onDestroy();
	}

	@SuppressLint("RtlHardcoded")
	private void initPopupMenu() {
		ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this, R.style.DocumentsPopupMenu);
		popupMenu = new PopupMenu(contextThemeWrapper, binding.toolbarSimple.getRightImageReference());
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.menu_document_list, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(item -> {
			int itemId = item.getItemId();

			if (itemId == R.id.menu_documents_carta_identita_cartacea) {
				checkAlreadyRegisteredDocumentType(DtoDocument.DocumentTypeEnum.PAPER_IDENTITY_CARD);
			} else if (itemId == R.id.menu_documents_carta_identita_elettronica) {
				checkAlreadyRegisteredDocumentType(DtoDocument.DocumentTypeEnum.ELECTRONIC_IDENTITY_CARD);
			} else if (itemId == R.id.menu_documents_tessera_sanitaria) {
				checkAlreadyRegisteredDocumentType(DtoDocument.DocumentTypeEnum.HEALTH_INSURANCE_CARD);
			} else if (itemId == R.id.menu_documents_patente) {
				checkAlreadyRegisteredDocumentType(DtoDocument.DocumentTypeEnum.DRIVING_LICENSE);
			} else if (itemId == R.id.menu_documents_passaporto) {
				checkAlreadyRegisteredDocumentType(DtoDocument.DocumentTypeEnum.PASSPORT);
			} else if (itemId == R.id.menu_documents_other) {
				HashMap<String, String> mapEventParams = new HashMap<>();
				mapEventParams.put(PARAM_DOCUMENT_TYPE, "Other");
				CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_DOCUMENT_ADD, mapEventParams, false);
				DocumentsFlowManager.goToAddDocument(this, DtoDocument.DocumentTypeEnum.OTHER);
			}
			return true;
		});

		popupMenu.setGravity(Gravity.RIGHT);
	}

	private void doRequest() {
		binding.refresh.setRefreshing(true);
		BancomatSdkInterface.Factory.getInstance().doGetDocuments(this, result -> {

			binding.refresh.setRefreshing(false);

			if (result != null) {
				if (result.isSuccess()) {
					if (result.getResult() != null && result.getResult().getDocumentList() != null) {
						documentList = result.getResult().getDocumentList();
						if (!documentList.isEmpty()) {
							boolean isListEmpty = false;
							if (adapter.getItemCount() == 0) {
								isListEmpty = true;
							}
							if (isListEmpty) {
								adapter = new DocumentsAdapter(documentList, this);
								binding.recyclerViewDocuments.setAdapter(adapter);
								AnimationRecyclerViewUtil.runLayoutAnimation(binding.recyclerViewDocuments);
							} else {
								adapter.updateList(documentList);
							}

							updateCachedDocumentList(documentList);
							showEmptyText(false);

						} else {
							showEmptyText(true);
						}
					}
				} else if (result.isSessionExpired()) {
					BCMAbortCallback.getInstance().getAuthenticationListener()
							.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
					//finishAffinity();
				} else {
					showError(result.getStatusCode());
				}
			}
		}, SessionManager.getInstance().getSessionToken());
	}

	private void updateCachedDocumentList(List<BcmDocument> documentList) {
		HashMap<DtoDocument.DocumentTypeEnum, Boolean> documentTypeMap = new HashMap<>();
		for (BcmDocument item : documentList) {
			if (item.getDocumentType() != DtoDocument.DocumentTypeEnum.OTHER) {
				documentTypeMap.put(item.getDocumentType(), true);
			}
		}
		ApplicationModel.getInstance().setDocumentTypeMap(documentTypeMap);
	}

	public void showEmptyText(boolean isEmpty) {
		if (isEmpty) {
			AnimationFadeUtil.startFadeInAnimationV1(binding.documentsLayoutEmpty, DEFAULT_DURATION);
			binding.recyclerViewDocuments.setVisibility(View.INVISIBLE);
		} else {
			binding.documentsLayoutEmpty.setVisibility(View.INVISIBLE);
			binding.recyclerViewDocuments.setVisibility(View.VISIBLE);
		}
	}

	private void showDocumentAlreadyRegisteredDialog(DtoDocument.DocumentTypeEnum documentType) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning_title)
				.setMessage(getString(R.string.error_document_already_registered, fromDocumentTypeEnumToString(documentType)))
				.setPositiveButton(R.string.yes, (dialog, id) -> DocumentsFlowManager.goToAddDocument(this, documentType))
				.setNegativeButton(R.string.no, null)
				.setCancelable(false);
		builder.show();
	}

	private String fromDocumentTypeEnumToString(DtoDocument.DocumentTypeEnum documentType) {
		String documentName;
		switch (documentType) {
			case PAPER_IDENTITY_CARD:
				documentName = getString(R.string.popup_menu_voice_1);
				break;
			case ELECTRONIC_IDENTITY_CARD:
				documentName = getString(R.string.popup_menu_voice_2);
				break;
			case HEALTH_INSURANCE_CARD:
				documentName = getString(R.string.popup_menu_voice_3);
				break;
			case DRIVING_LICENSE:
				documentName = getString(R.string.popup_menu_voice_4);
				break;
			case PASSPORT:
				documentName = getString(R.string.popup_menu_voice_5);
				break;
			default:
				documentName = "";
				break;
		}
		return documentName.toLowerCase();
	}

	@Override
	public void onListViewInteraction(BcmDocument document) {
		if (!binding.refresh.isRefreshing()) {
			CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_DOCUMENT_SELECTED, null, false);
			DocumentsFlowManager.goToDocumentDetail(this, document);
		}
	}

	private void checkAlreadyRegisteredDocumentType(DtoDocument.DocumentTypeEnum documentType) {
		HashMap<String, String> mapEventParams = new HashMap<>();
		mapEventParams.put(PARAM_DOCUMENT_TYPE, documentType.toString());
		CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_DOCUMENT_ADD, mapEventParams, false);

		HashMap<DtoDocument.DocumentTypeEnum, Boolean> documentTypeMap = ApplicationModel.getInstance().getDocumentTypeMap();
		if (documentTypeMap != null) {
			if (documentTypeMap.containsKey(documentType)) {
				showDocumentAlreadyRegisteredDialog(documentType);
			} else {
				DocumentsFlowManager.goToAddDocument(this, documentType);
			}
		} else {
			DocumentsFlowManager.goToAddDocument(this, documentType);
		}
	}

}
