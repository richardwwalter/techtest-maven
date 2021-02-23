package com.db.dataplatform.techtest;

import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetrySynchronizationManager;

public class RetryTestHelper {

    public static void register(){
        RetrySynchronizationManager.register(new RetryContext() {
            @Override
            public void setExhaustedOnly() {}
            @Override
            public boolean isExhaustedOnly() {
                return false;
            }
            @Override
            public RetryContext getParent() {
                return null;
            }
            @Override
            public int getRetryCount() {
                return 0;
            }
            @Override
            public Throwable getLastThrowable() {
                return null;
            }
            @Override
            public void setAttribute(String name, Object value) {}
            @Override
            public Object getAttribute(String name) {
                return null;
            }
            @Override
            public Object removeAttribute(String name) {
                return null;
            }
            @Override
            public boolean hasAttribute(String name) {
                return false;
            }
            @Override
            public String[] attributeNames() {
                return new String[0];
            }
        });
    }

    public static void clear(){
        RetrySynchronizationManager.clear();
    }

}
