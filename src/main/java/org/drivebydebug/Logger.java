package org.drivebydebug;

import java.util.List;

public interface Logger {

    public void onBreakpoint(List<Evaluation> evaluations);

    public void onError(Throwable ball);
}