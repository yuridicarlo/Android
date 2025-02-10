package it.bancomatpay.sdk.manager.events;


import it.bancomatpay.sdk.core.Task;

public class TaskEventError {

    private Task<?> task;
    private Error error;

    public TaskEventError(Task<?> task, Error error) {
        this.task = task;
        this.error = error;
    }

    public Task<?> getTask() {
        return task;
    }

    public Error getError() {
        return error;
    }

}
