package org.drivebydebug;

import java.util.List;

public interface Logger {

    public void onBreakpoint(Break breakpoint);

    public void onError(Throwable ball);
}