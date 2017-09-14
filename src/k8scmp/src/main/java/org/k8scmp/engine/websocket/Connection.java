package org.k8scmp.engine.websocket;

/**
 * Created by feiliu206363 on 2015/12/21.
 */
public interface Connection {
    void sendMessage() throws Exception;

    void stopMessage();
}
