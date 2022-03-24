package com.hanghae99.boilerplate.trace.template;

import com.hanghae99.boilerplate.trace.TraceStatus;
import com.hanghae99.boilerplate.trace.logtrace.LogTrace;

public abstract class AbstractTemplate<T> {

    private final LogTrace trace;

    public AbstractTemplate(LogTrace trace) {
        this.trace = trace;
    }

    public T execute(String message) { // 타입에 대한 정보가 정해지는 시점을 객체 생성되거나 하는 때로 미루는 것.
        TraceStatus status = null;
        try {
            status = trace.begin(message);

            // 로직 호출
            T result = call();
            trace.end(status);
            return result;

        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

    protected abstract T call();

}
