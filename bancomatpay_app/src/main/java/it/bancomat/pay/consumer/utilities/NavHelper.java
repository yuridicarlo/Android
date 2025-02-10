package it.bancomat.pay.consumer.utilities;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import it.bancomat.pay.consumer.exception.ServerException;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;


public class NavHelper {


    public static void popBackStack(FragmentActivity activity) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        navController.popBackStack();
    }

    public static void popBackStack(FragmentActivity activity, int destinationId, boolean inclusive) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        navController.popBackStack(destinationId, inclusive);
    }


    public static void showSnackBarMessage(FragmentActivity activity, Throwable throwable) {
        if (throwable instanceof ServerException) {
            showSnackBarMessage(activity, ((ServerException) throwable).getStatusCode());
        }else {
            SnackbarUtil.showSnackbarMessageCustom(activity, activity.getString(R.string.generic_error_message), v -> { });
        }
    }

    public static void showSnackBarMessage(FragmentActivity activity, StatusCodeInterface statusCodeInterface) {

        int idString = AppErrorMapper.getStringFromStatusCode(statusCodeInterface);
        SnackbarUtil.showSnackbarMessageCustom(activity, activity.getString(idString), v -> { });

    }

    public static void navigate(FragmentActivity activity, NavDirections directions) {
        if(activity != null) {
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
            navigate(navController, directions);
        }
    }

    public static void navigate(NavController navController, NavDirections directions) {

        int popUpTo = -1;
        boolean isPopUpToInclusive = false;
        boolean isLaunchSingleTop = false;

        if (navController.getCurrentDestination() != null) {
            NavAction action = navController.getCurrentDestination().getAction(directions.getActionId());
            if (action != null) {
                NavOptions navOptions = action.getNavOptions();
                if (navOptions != null) {
                    popUpTo = navOptions.getPopUpTo();
                    isPopUpToInclusive = navOptions.isPopUpToInclusive();
                    isLaunchSingleTop = navOptions.shouldLaunchSingleTop();
                }
            }
        }

        NavOptions.Builder builder = new NavOptions.Builder()
                .setEnterAnim(R.anim.enter_from_right)
                .setExitAnim(R.anim.exit_to_left)
                .setPopEnterAnim(R.anim.enter_from_left)
                .setPopExitAnim(R.anim.exit_to_right)
                .setLaunchSingleTop(isLaunchSingleTop);

        if (popUpTo != -1) {
            builder.setPopUpTo(popUpTo, isPopUpToInclusive);
        }

        navigate(navController, directions, builder.build());

    }

    private static void navigate(NavController navController, NavDirections directions, NavOptions navOptions) {
        if (navController.getCurrentDestination() != null) {
            if (navController.getCurrentDestination().getAction(directions.getActionId()) != null) {
                navController.navigate(directions, navOptions);
            }
        }
    }

}
