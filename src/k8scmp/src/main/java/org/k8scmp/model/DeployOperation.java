package org.k8scmp.model;

/**
 */
public enum DeployOperation {
    UPDATE,
    ROLLBACK,
    SCALE_UP,
    SCALE_DOWN,
    START,
    STOP,
    ABORT_UPDATE,
    ABORT_ROLLBACK,
    ABORT_SCALE_UP,
    ABORT_SCALE_DOWN,
    ABORT_START,
    KUBERNETES
}
