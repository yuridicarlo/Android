/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package it.bancomatpay.sdkui.widgets.fastscroll;

import androidx.annotation.NonNull;

public interface ViewHelperProvider {

    @NonNull
    FastScroller.ViewHelper getViewHelper();
}
