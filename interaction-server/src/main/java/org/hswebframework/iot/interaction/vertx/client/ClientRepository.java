package org.hswebframework.iot.interaction.vertx.client;

/**
 * The interface Client repository.
 *
 * @author zhouhao
 * @since 1.0.0
 */
public interface ClientRepository {
    /**
     * Gets client.
     *
     * @param idOrClientId the id or client id
     * @return the client
     */
    Client getClient(String idOrClientId);

    /**
     * Register client.
     *
     * @param client the client
     * @return the client
     */
    Client register(Client client);

    /**
     * Unregister client.
     *
     * @param idOrClientId the id or client id
     * @return the client
     */
    Client unregister(String idOrClientId);

    /**
     * Total long.
     *
     * @return the long
     */
    long total();
}
