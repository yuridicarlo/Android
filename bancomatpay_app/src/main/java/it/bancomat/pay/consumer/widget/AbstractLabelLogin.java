package it.bancomat.pay.consumer.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import it.bancomatpay.sdkui.widgets.KeyboardListener;

public abstract class AbstractLabelLogin extends LinearLayout implements KeyboardListener<String> {

    protected StringBuilder mText;
    private final static String TEXT = "TEXT";
    private final static String STATE_TO_SAVE = "STATE_TO_SAVE";

    @Override
    public String getValue(){
        return mText.toString();
    }

    public AbstractLabelLogin(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(mText == null){
            mText = new StringBuilder();
        }
    }

    @Override
    public void onTextEntered(String text) {
        mText.append(text);
        updateView(false);
    }

    @Override
    public void onDeleteCharacter() {
        if (mText.length() > 0) {
            // we want to drop the last digit so even though we are losing accuracy it is what we need & want
            mText.deleteCharAt(mText.length() -1);
            updateView(true);
        }
    }

    @Override
    public void onDeleteAllText() {
        if (mText.length() > 0) {
            mText.delete(0, mText.length());
            updateView(true);
        }
    }

    abstract protected void updateView(boolean isDeleting);

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_TO_SAVE, super.onSaveInstanceState());
        bundle.putString(TEXT, mText.toString());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mText = new StringBuilder(bundle.getString(TEXT, ""));
            state = bundle.getParcelable(STATE_TO_SAVE);
            updateView(false);
        }
        super.onRestoreInstanceState(state);
    }

}
