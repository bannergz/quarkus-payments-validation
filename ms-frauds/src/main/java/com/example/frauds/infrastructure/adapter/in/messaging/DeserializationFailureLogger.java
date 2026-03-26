package com.example.frauds.infrastructure.adapter.in.messaging;

import io.smallrye.common.annotation.Identifier;
import io.smallrye.reactive.messaging.kafka.DeserializationFailureHandler;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.common.header.Headers;
import org.jboss.logging.Logger;

/**
 * Catches and logs any deserialization failures that would otherwise
 * be silently swallowed before reaching the @Incoming consumer method.
 */
@ApplicationScoped
@Identifier("deserialization-failure-logger")
public class DeserializationFailureLogger
    implements DeserializationFailureHandler<Object> {

  private static final Logger LOG = Logger.getLogger(DeserializationFailureLogger.class);

  @Override
  public Object handleDeserializationFailure(String channel, boolean isKey,
      String deserializer, byte[] data, Exception exception, Headers headers) {
    LOG.errorf(exception,
        "[DeserializationFailure] channel=%s, isKey=%s, deserializer=%s, "
            + "payload-bytes=%d, first-bytes=%s",
        channel, isKey, deserializer,
        data != null ? data.length : -1,
        data != null && data.length > 0
            ? String.format("0x%02X", data[0])
            : "empty");
    // Returning null causes SmallRye to nack the message and apply failure-strategy
    return null;
  }
}
