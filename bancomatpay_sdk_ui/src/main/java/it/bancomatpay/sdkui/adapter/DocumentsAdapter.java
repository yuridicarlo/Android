package it.bancomatpay.sdkui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class DocumentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final List<BcmDocument> mValues;
	private final InteractionListener mListener;

	public DocumentsAdapter(List<BcmDocument> items, InteractionListener listener) {
		mValues = items;
		mListener = listener;
	}

	@NonNull
	@Override
	public DocumentsAdapter.ViewHolderDocument onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.document_item, parent, false);
		return new ViewHolderDocument(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

		ViewHolderDocument holderDocument = (ViewHolderDocument) holder;
		holderDocument.mItem = mValues.get(position);

		switch (holderDocument.mItem.getDocumentType()) {
			case ELECTRONIC_IDENTITY_CARD:
				holderDocument.mDocumentImage.setImageResource(R.drawable.carta_identita_elettronica);
				holderDocument.mTextDocumentName.setVisibility(View.INVISIBLE);
				break;
			case PAPER_IDENTITY_CARD:
				holderDocument.mDocumentImage.setImageResource(R.drawable.carta_identita);
				holderDocument.mTextDocumentName.setVisibility(View.INVISIBLE);
				break;
			case HEALTH_INSURANCE_CARD:
				holderDocument.mDocumentImage.setImageResource(R.drawable.tessera_sanitaria);
				holderDocument.mTextDocumentName.setVisibility(View.INVISIBLE);
				break;
			case DRIVING_LICENSE:
				holderDocument.mDocumentImage.setImageResource(R.drawable.patente);
				holderDocument.mTextDocumentName.setVisibility(View.INVISIBLE);
				break;
			case PASSPORT:
				holderDocument.mDocumentImage.setImageResource(R.drawable.passaporto);
				holderDocument.mTextDocumentName.setVisibility(View.INVISIBLE);
				break;
			case OTHER:
				holderDocument.mDocumentImage.setImageResource(R.drawable.altro_documento_placeholder);
				holderDocument.mTextDocumentName.setText(holderDocument.mItem.getDocumentName());
				holderDocument.mTextDocumentName.setVisibility(View.VISIBLE);
			default:
				break;
		}

		holderDocument.mView.setOnClickListener(new CustomOnClickListener(v -> {
			if (mListener != null) {
				mListener.onListViewInteraction(holderDocument.mItem);
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

	public void updateList(List<BcmDocument> documentList) {
		mValues.clear();
		mValues.addAll(documentList);
		notifyDataSetChanged();
	}

	public static class ViewHolderDocument extends RecyclerView.ViewHolder {

		View mView;
		ImageView mDocumentImage;
		TextView mTextDocumentName;

		BcmDocument mItem;

		ViewHolderDocument(View view) {
			super(view);
			mView = view;
			mDocumentImage = view.findViewById(R.id.image_document);
			mTextDocumentName = view.findViewById(R.id.text_document_name);
		}
	}

	public interface InteractionListener {
		void onListViewInteraction(BcmDocument document);
	}

}
