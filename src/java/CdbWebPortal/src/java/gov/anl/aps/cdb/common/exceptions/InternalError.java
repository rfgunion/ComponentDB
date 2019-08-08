/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.exceptions;

import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * Internal error exception.
 */
public class InternalError extends CdbException {

    /**
     * Default constructor.
     */
    public InternalError() {
        super();
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public InternalError(String message) {
        super(message);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public InternalError(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public InternalError(String message, Throwable throwable) {
        super(message, throwable);
    }
    
    @Override
    public int getErrorCode() {
        return CdbStatus.CDB_INTERNAL_ERROR;
    }
}
