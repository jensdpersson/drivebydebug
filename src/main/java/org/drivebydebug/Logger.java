package org.drivebydebug;

public interface Logger {

    public void onEvaluation(Evaluation evaluation);

    public void onError(Throwable ball);
}