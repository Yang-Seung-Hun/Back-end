package com.hanghae99.boilerplate.trace.logtrace;

import com.hanghae99.boilerplate.trace.TraceStatus;

public interface LogTrace {

    TraceStatus begin(String message);

    void end(TraceStatus status);

    void exception(TraceStatus status, Exception e);

}
