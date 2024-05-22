package com.salapp;

import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/hello")
public class ExampleResource {

    private static final Logger log = LoggerFactory.getLogger(ExampleResource.class);
    @ConfigProperty(name = "mp.messaging.outgoing.quarkus-rabbitmq.default-routing-key")
    String routingKey;

    @Channel("quarkus-rabbitmq")
    Emitter<JsonObject> emitter;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response hello(Order order) {

        JsonObject orderJson = JsonObject.mapFrom(order);

        emitter.send(orderJson)
                .toCompletableFuture()
                .thenAccept(result -> log.info("Message sent with routing key:{} \nValue:{}: ", routingKey, orderJson))
                .exceptionally(throwable -> {
                    log.error("Error sending message", throwable);
                    return null;
                });
        log.info("Message sent: {}", orderJson);

        return Response.ok(orderJson, MediaType.APPLICATION_JSON).build();
    }
}
