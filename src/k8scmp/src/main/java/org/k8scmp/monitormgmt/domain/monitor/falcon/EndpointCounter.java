package org.k8scmp.monitormgmt.domain.monitor.falcon;

/**
 * Created by baokangwang on 2016/3/1.
 */
public class EndpointCounter {

    private String endpoint;
    private String counter;

    public EndpointCounter() {
    }

    public EndpointCounter(String endpoint, String counter) {
        this.endpoint = endpoint;
        this.counter = counter;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }
}
