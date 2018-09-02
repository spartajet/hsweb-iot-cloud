package org.hswebframework.iot.interaction.vertx.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.iot.interaction.core.IotCommand;
import org.hswebframework.iot.interaction.vertx.client.Client;

/**
 * The type Mqtt client.
 *
 * @author zhouhao
 * @since 1.0.0
 */
@Slf4j
public class MqttClient implements Client {

    /**
     * The Endpoint.
     */
    private MqttEndpoint endpoint;

    /**
     * The Last ping time.
     */
    private volatile long lastPingTime = System.currentTimeMillis();

    /**
     * Instantiates a new Mqtt client.
     *
     * @param endpoint the endpoint
     */
    public MqttClient(MqttEndpoint endpoint) {
        endpoint.pingHandler(r -> ping());
        this.endpoint = endpoint;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Override
    public String getId() {
        return getClientId();
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    @Override
    public String getClientId() {
        return endpoint.clientIdentifier();
    }

    /**
     * Last ping time long.
     *
     * @return the long
     */
    @Override
    public long lastPingTime() {
        return lastPingTime;
    }

    /**
     * Send.
     *
     * @param topic   the topic
     * @param command the command
     */
    @Override
    public void send(String topic, IotCommand command) {
        endpoint.publish(topic, Buffer.buffer(command.toString()), MqttQoS.AT_MOST_ONCE, false, false);
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        if (endpoint.isConnected()) {
            endpoint.close();
        }

    }

    /**
     * Ping.
     */
    @Override
    public void ping() {
        log.debug("mqtt client[{}] ping", getClientId());
        lastPingTime = System.currentTimeMillis();
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "MQTT Client[" + getClientId() + "]";
    }
}
