/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compatibility;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * Compatibility class for Idea 182. Signature of processQuery method was changed there.
 * BUNCH: 182
 */
public abstract class QueryExecutorBaseWrapper<Result, Param> extends QueryExecutorBase<Result, Param> {
    public QueryExecutorBaseWrapper(boolean requireReadAction) {
        super(requireReadAction);
    }

    public QueryExecutorBaseWrapper() {
    }

    /**
     * Method shouldn't be call. Use processQueryEx instead.
     */
    @Deprecated
    @Override
    public void processQuery(@NotNull Param queryParameters, @NotNull Processor<Result> consumer) {
        processQueryEx(queryParameters, consumer);
    }

    /**
     * Find some results according to queryParameters and feed them to consumer. If consumer returns false, stop.
     */
    abstract public void processQueryEx(@NotNull Param queryParameters, @NotNull Processor<? super Result> consumer);
}
