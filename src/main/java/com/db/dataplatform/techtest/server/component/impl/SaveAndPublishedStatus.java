package com.db.dataplatform.techtest.server.component.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
public class SaveAndPublishedStatus<T> {

    @NotNull
    private final T entity;

    @Setter
    @NotNull
    @NonNull
    private SaveAndPublishStatus status;

    public enum SaveAndPublishStatus{
        SAVED,
        SAVED_AND_PUBLISHED,
        SAVED_NOT_PUBLISHED
    }

}
