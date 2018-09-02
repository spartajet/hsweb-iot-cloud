package org.hswebframework.iot.interaction.vertx.client;


import org.hswebframework.iot.interaction.core.IotCommand;

/**
 * The interface Client.
 *
 * @author zhouhao
 * @since 1.0.0
 */
public interface Client {

    /**
     * Gets id.
     *
     * @return the id
     */
    String getId();

    /**
     * Gets client id.
     *
     * @return the client id
     */
    String getClientId();

    /**
     * Last ping time long.
     *
     * @return the long
     */
    long lastPingTime();

    /**
     * Send.
     *
     * @param topic   the topic
     * @param command the command
     */
    void send(String topic, IotCommand command);

    /**
     * Close.
     */
    void close();

    /**
     * Ping.
     */
    void ping();

}
