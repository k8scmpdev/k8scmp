package org.k8scmp.exception;

/**
 */
public class K8sDriverException extends Exception {
    public K8sDriverException() {
    }

    public K8sDriverException(String message) {
        super(message);
    }
}
