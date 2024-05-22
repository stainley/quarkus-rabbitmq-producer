package com.salapp;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZonedDateTime;

@ApplicationScoped
public class GenerateOrder {


    private static final Logger log = LoggerFactory.getLogger(GenerateOrder.class);

    @Outgoing("quarkus-rabbitmq")
    public Multi<Message<Order>> createOrders() {

        log.info("Creating orders");

        return Multi.createFrom().ticks()
                .every(Duration.ofSeconds(4))
                .map(x -> Message.of(new Order(x.intValue(), 2, 3, 4), Metadata.of(new OutgoingRabbitMQMetadata.Builder()
                        .withRoutingKey("express")
                        .withTimestamp(ZonedDateTime.now())
                        .withAppId("APPID-")
                        .withCorrelationId(String.valueOf(x))
                        .withHeader("author-header", "stainley")
                        .withContentType(MediaType.APPLICATION_JSON)
                )));
    }

}
