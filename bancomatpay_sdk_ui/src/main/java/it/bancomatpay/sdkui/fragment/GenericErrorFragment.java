package it.bancomatpay.sdkui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

import org.greenrobot.eventbus.EventBus;

import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.events.TaskEventError;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.ErrorMapper;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;

public class GenericErrorFragment extends FragmentTaskManager {

    public boolean lockGenericError = true;

    @Override
    public void onCompleteWithError(Task<?> task, Error e) {
        super.onCompleteWithError(task, e);
        EventBus.getDefault().post(new TaskEventError(task, e));
    }

    public void showError(StatusCodeInterface statusCodeInterface) {
        int idString = ErrorMapper.getStringFromStatusCode(statusCodeInterface);
        SnackbarUtil.showSnackbarMessageCustom(requireActivity(), getString(idString));
    }

    public void showErrorAndDoAction(StatusCodeInterface statusCodeInterface, DialogInterface.OnClickListener clickListener) {
        if (lockGenericError) {
            lockGenericError = false;

            int idString = ErrorMapper.getStringFromStatusCode(statusCodeInterface);
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(requireContext());
            builder.setTitle(R.string.warning_title);
            builder.setCancelable(false);
            builder.setMessage(idString)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        clickListener.onClick(dialog, which);
                        lockGenericError = true;
                    });
            builder.showDialog(requireActivity());
        }
    }

    public static int isEdgeToEdgeEnabled(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android");
        if (resourceId > 0) {
            return resources.getInteger(resourceId);
        }
        return 0;
    }


    public boolean hasSoftKeys() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            return false;
        }

        boolean hasSoftwareKeys;

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        display.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;

        return hasSoftwareKeys;
    }

}
