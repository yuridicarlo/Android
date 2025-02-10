package it.bancomatpay.sdkui.utilities;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import it.bancomatpay.sdkui.R;


public class NavHelper {


    public static boolean popBackStack(FragmentActivity activity) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        return navController.popBackStack();
    }

    public static void replaceFragment(FragmentActivity activity, int destinationId) {
        if (activity != null) {
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
            replaceFragmentImpl(navController, destinationId, null);
        }
    }

    public static void replaceFragment(NavController navController, int destinationId) {
        replaceFragmentImpl(navController, destinationId, null);
    }

    public static void replaceFragment(NavController navController, int destinationId, Bundle args) {
        replaceFragmentImpl(navController, destinationId, args);
    }

    private static void replaceFragmentImpl(NavController navController, int destinationId, Bundle args) {
        int currentFragmentId = navController.getCurrentDestination().getId();
        if (currentFragmentId != destinationId) {
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(currentFragmentId, true)
                    .build();
            navController.navigate(destinationId, args, navOptions);
        }
    }

    public static int getCurrentDestinationId(FragmentActivity activity) {
        if (activity != null) {
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
            if(navController.getCurrentDestination() != null) {
                return navController.getCurrentDestination().getId();
            }
        }
        return -1;
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

