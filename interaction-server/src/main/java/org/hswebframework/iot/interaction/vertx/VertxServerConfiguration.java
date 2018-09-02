package org.hswebframework.iot.interaction.vertx;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The type Vertx server configuration.
 *
 * @author zhouhao
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class VertxServerConfiguration {

    /**
     * Vertx options vertx options.
     *
     * @return the vertx options
     */
    @Bean
    @ConfigurationProperties(prefix = "vertx")
    public VertxOptions vertxOptions() {
        return new VertxOptions();
    }

    /**
     * Mqtt server options mqtt server options.
     *
     * @return the mqtt server options
     */
    @Bean
    @ConfigurationProperties(prefix = "vertx.mqtt")
    public MqttServerOptions mqttServerOptions() {
        return new MqttServerOptions();
    }

    /**
     * Zookeeper config map.
     *
     * @return the map
     */
    @Bean
    @ConfigurationProperties(prefix = "vertx.cluster.config")
    public Map<String, Object> zookeeperConfig() {
        return new HashMap<>();
    }

    /**
     * Vertx vertx.
     *
     * @return the vertx
     */
    @Bean
    @SneakyThrows
    public Vertx vertx() {
        VertxOptions vertxOptions = vertxOptions();

        log.debug("init vertx : \n{}", vertxOptions);
        Vertx vertx;
        if (vertxOptions.isClustered()) {
            JsonObject config = new JsonObject(zookeeperConfig());
            log.info("use zookeeper config:\n{}", config);
            ClusterManager clusterManager = new ZookeeperClusterManager(config);
            vertxOptions.setClusterManager(clusterManager);
            CountDownLatch clusterLatch = new CountDownLatch(1);
            AtomicReference<Throwable> errorReference = new AtomicReference<>();
            AtomicReference<Vertx> vertxAtomicReference = new AtomicReference<>();
            Vertx.clusteredVertx(vertxOptions, e -> {
                try {
                    if (e.succeeded()) {
                        log.debug("init clustered vertx success");
                        vertxAtomicReference.set(e.result());
                    } else {
                        errorReference.set(e.cause());
                    }
                } finally {
                    clusterLatch.countDown();
                }
            });
            boolean success = clusterLatch.await(1, TimeUnit.MINUTES);
            if (!success) {
                log.warn("wait vertx init timeout!");
            }
            if (errorReference.get() != null) {
                throw errorReference.get();
            }
            vertx = vertxAtomicReference.get();
        } else {
            vertx = Vertx.vertx(vertxOptions);
        }
        return vertx;
    }

    /**
     * Start mqtt server processor verticle register processor.
     *
     * @return the verticle register processor
     */
    @Bean
    public VerticleRegisterProcessor startMqttServerProcessor() {
        return new VerticleRegisterProcessor();
    }

    /**
     * The type Verticle register processor.
     */
    public static class VerticleRegisterProcessor implements CommandLineRunner {

        /**
         * The Verticle factory.
         */
        @Autowired
        private VerticleFactory verticleFactory;

        /**
         * The Verticles.
         */
        @Autowired
        private List<VerticleSupplier> verticles;

        /**
         * The Vertx.
         */
        @Autowired
        private Vertx vertx;

        /**
         * Run.
         *
         * @param args the args
         * @throws Exception the exception
         */
        @Override
        public void run(String... args) throws Exception {
            vertx.registerVerticleFactory(verticleFactory);
            for (VerticleSupplier suplier : verticles) {
                DeploymentOptions options = new DeploymentOptions();
                options.setHa(true);
                options.setInstances(suplier.getInstances());
                vertx.deployVerticle(suplier, options, e -> {
                    if (!e.succeeded()) {
                        log.error("deploy verticle :{} error", suplier, e.succeeded(), e.cause());
                    } else {
                        log.debug("deploy verticle :{} success",suplier);
                    }
                });
            }
        }
    }
}
