package org.k8scmp.exception;

/**
 */
public class DeploymentEventException extends Exception {
    // this exception should used for event status error
    public DeploymentEventException() {}
    public DeploymentEventException(String message) {
        super(message);
    }
    public DeploymentEventException(Exception e) {
        super(e);
    }
    public DeploymentEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
