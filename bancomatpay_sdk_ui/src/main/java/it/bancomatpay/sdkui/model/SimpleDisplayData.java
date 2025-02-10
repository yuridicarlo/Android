package it.bancomatpay.sdkui.model;

import java.io.Serializable;

interface SimpleDisplayData extends Serializable {
    String getLetter();
    String getLetterSurname();
    String getInitials();
}
