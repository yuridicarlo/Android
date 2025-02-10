package it.bancomatpay.sdk.manager.utilities;

import com.guardsquare.dexguard.runtime.detection.DebugBlocker;
import com.guardsquare.dexguard.runtime.detection.EmulatorDetector;
import com.guardsquare.dexguard.runtime.detection.HookDetector;
import com.guardsquare.dexguard.runtime.detection.RootDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import it.bancomatpay.sdk.core.PayCore;

public class DexguardSecurityCheck {

    private final static String TAG = DexguardSecurityCheck.class.getSimpleName();

    final int OK = 1;

    public void checkEmulator() {
        int isApplicationHooked = EmulatorDetector.isRunningInEmulator(PayCore.getAppContext(), OK);
        if (isApplicationHooked != OK) {
            throw new SecurityException();
        }
    }

    public void checkHook() {
        int isApplicationHooked = HookDetector.isApplicationHooked(PayCore.getAppContext(), OK, HookDetector.FAST);
        if (isApplicationHooked != OK) {
            throw new SecurityException();
        }
    }

    public void checkRoot() throws InterruptedException{
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Integer> results = new ArrayList<>();
        RootDetector.checkDeviceRooted(PayCore.getAppContext(), new RootDetector.Callback() {
            @Override
            public void onRootDetectionResultReceived(int i, int i1) {
                results.add(i);
                results.add(i1);
                latch.countDown();

            }
        });
        latch.await();
        if (!results.get(0).equals(results.get(1))){
            throw new SecurityException();
        }
    }

    public void checkDebug() {
        int isDebuggerBlocked = DebugBlocker.blockDebugger(new DebugBlocker.OnAttackListener() {
            @Override
            public void onAttack() {

            }
        });

        if (isDebuggerBlocked != 0) {
            // Debug blocker wasn't successful. You can further check the reason of failure.
            if ((isDebuggerBlocked & DebugBlocker.DEBUGGER_CONNECTED) != 0)
            {
                // A debugger is already connected to the application. The application should exit.
                // ...
                throw new SecurityException();
            }
        }
    }
}
