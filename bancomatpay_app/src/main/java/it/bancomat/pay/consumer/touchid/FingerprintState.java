package it.bancomat.pay.consumer.touchid;

public enum FingerprintState {

    ENABLED, //la funzione di pagamento HCE con impronta digitale è attiva. Switch: ON, sensitive;

    DISABLED, // la funzione di pagamento HCE con impronta digitale non è attiva. Switch: OFF, sensitive;

    MISSING_FINGERPRINT_SENSOR, // la funzione di pagamento HCE con impronta digitale non è attivabile per mancanza del sensore a bordo del dispositivo. Switch: OFF, insensitive;

    MISSING_SOFTWARE_REQUISITES, // la funzione di pagamento HCE con impronta digitale non è attivabile per mancanza dei requisiti minimi necessari al funzionamento delle android.hardware.fingerprint API di Android 6+ (build level 23). Switch: OFF, insensitive;

    FINGERPRINT_NOT_ENABLED_ON_DEVICE, //l’utente non ha registrato le proprie impronte sul dispositivo. Switch: OFF, insensitive;

    MISSING_KEYGUARD_MANAGER, // la funzione di pagamento HCE con impronta digitale non è attivabile perchè l'utente non ha impostato lo sblocco dello schermo con PIN, password o sequenza. Switch: OFF, insensitive;

    PIN_NOT_SET, //l’utente non ha ancora scelto il proprio PIN HCE. Switch: OFF, insensitive;

    DEVICE_NOT_PAIRED //l’utente non ha ancora associato il device. Switch: OFF, insensitive;

}
