package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsToolbarHomeBinding;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.LogoBankSingleton;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class ToolbarHome extends RelativeLayout {

	/*@BindView(R2.id.toolbar_root_layout)
	protected RelativeLayout rootLayout;

	@BindView(R2.id.toolbar_back_btn)
	protected AppCompatImageButton leftImage;

	@BindView(R2.id.toolbar_center_left_img)
	protected ImageView centerLeftImage;

	@BindView(R2.id.toolbar_center_right_img)
	protected ImageView centerRightImage;

	@BindView(R2.id.space_margin_middle)
	Space spaceMarginMiddle;*/

	private final WidgetsToolbarHomeBinding binding;

	public ToolbarHome(Context context, AttributeSet attrs) {
		super(context, attrs);
		binding = WidgetsToolbarHomeBinding.inflate(LayoutInflater.from(context), this, true);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToolbarHome);
		int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.ToolbarHome_leftImgHome) {
				binding.toolbarBackBtn.setImageDrawable(a.getDrawable(attr));
			} else if (attr == R.styleable.ToolbarHome_backgroundSrcHome) {
				binding.toolbarRootLayout.setBackground(a.getDrawable(attr));
			} else if (attr == R.styleable.ToolbarHome_centerRightImgHome) {
				binding.toolbarCenterRightImg.setImageDrawable(a.getDrawable(attr));
			} else if (attr == R.styleable.ToolbarHome_centerLeftImgHome) {
				binding.toolbarCenterLeftImg.setImageDrawable(a.getDrawable(attr));
			} else if (attr == R.styleable.ToolbarHome_hasDoubleLogo) {
				if (a.getBoolean(attr, false)) {
					setCenterLeftImageDrawable(LogoBankSingleton.getInstance().getLogoBank());
				} else {
					setCenterLeftImageDrawable(null);
				}
			}
		}
		//Imposto l'icona lente per avere la simmetria del titolo

        /*if (!rightImageSet) {
            rightImage.setVisibility(View.INVISIBLE);
            rightImage.setImageResource(R.drawable.ico_lente);
        }*/
		a.recycle();
	}

	public void setOnClickLeftImageListener(OnClickListener listener) {
		binding.toolbarBackBtn.setOnClickListener(new CustomOnClickListener(listener));
	}

	public void setLeftImageVisibility(boolean isVisible) {
		if (isVisible) {
			AnimationFadeUtil.startFadeInAnimationV1(binding.toolbarBackBtn, DEFAULT_DURATION);
		} else {
			binding.toolbarBackBtn.setVisibility(View.INVISIBLE);
		}
	}

	public boolean isLeftImageVisible() {
		return binding.toolbarBackBtn.getVisibility() == View.VISIBLE;
	}

	public ImageView getCenterLeftImageReference() {
		return binding.toolbarCenterLeftImg;
	}

	public void setCenterLeftImageVisibility(int visibility){
		binding.toolbarCenterLeftContainer.setVisibility(visibility);
		binding.spaceMarginMiddle.setVisibility(visibility);
	}

	public void setCenterLeftImageDrawable(Drawable drawable) {
		if(drawable == null){
			binding.toolbarCenterLeftContainer.setVisibility(GONE);
			binding.spaceMarginMiddle.setVisibility(View.GONE);
		}else {
			binding.toolbarCenterLeftImg.setImageDrawable(drawable);
			binding.toolbarCenterLeftContainer.setVisibility(VISIBLE);
			binding.spaceMarginMiddle.setVisibility(View.VISIBLE);
		}
	}

}
