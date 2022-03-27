package com.hanghae99.boilerplate.trace.callback;

import com.hanghae99.boilerplate.trace.TraceStatus;
import com.hanghae99.boilerplate.trace.logtrace.LogTrace;
import org.springframework.stereotype.Component;

@Component
public class TraceTemplate {

    private  final LogTrace trace;

    public TraceTemplate(LogTrace trace) {
        this.trace = trace;
    }

    public <T> T execute(String message, TraceCallBack<T> callBack) {
        TraceStatus status = null;
        try {
            status = trace.begin(message);

            // 로직 호출
            T result = callBack.call();
            trace.end(status);
            return result;

        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
