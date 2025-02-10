package it.bancomat.pay.consumer.touchid.dialog;

public class UpdateStatus {

    public enum Status {
        SUCCESS, ERROR, ERROR_FATAL
    }

    private Status status;
    private String message;

    public UpdateStatus(Status status, String message) {
        this.message = message;
        this.status = status;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public boolean isDismiss() {
        return status == Status.ERROR_FATAL;
    }

}
