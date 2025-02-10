package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoGetBanksConfigurationFileRequest implements Serializable {

    protected String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
