/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.exceptions;

import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * Timeout error.
 */
public class TimeoutError extends CdbException {

    /**
     * Default constructor.
     */
    public TimeoutError() {
        super();
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public TimeoutError(String message) {
        super(message);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public TimeoutError(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public TimeoutError(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public int getErrorCode() {
        return CdbStatus.CDB_TIMEOUT_ERROR;
    }    
}
