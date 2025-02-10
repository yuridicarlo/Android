package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

public class DtoGetBanksConfigurationFileResponse implements Serializable {

    protected String version;
    protected String file;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
