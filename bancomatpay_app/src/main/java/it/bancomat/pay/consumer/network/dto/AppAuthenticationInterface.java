package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;

public interface AppAuthenticationInterface extends Serializable {

    byte[] getSeed();

    byte[] getHmacKey();

}
