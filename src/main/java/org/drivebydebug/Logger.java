package org.drivebydebug;

public interface Logger {

    public void onBreakpoint(Break breakpoint);

    public void onError(Throwable ball);
}