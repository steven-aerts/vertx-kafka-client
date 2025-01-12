/*
 * Copyright 2016 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertx.kafka.client.producer;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.streams.WriteStream;
import io.vertx.kafka.client.producer.impl.KafkaWriteStreamImpl;
import io.vertx.kafka.client.serialization.VertxSerdes;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.Serializer;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A {@link WriteStream} for writing to Kafka {@link ProducerRecord}.
 * <p>
 * The {@link #write(Object)} provides global control over writing a record.
 * <p>
 */
public interface KafkaWriteStream<K, V> extends WriteStream<ProducerRecord<K, V>> {

  int DEFAULT_MAX_SIZE = 1024 * 1024;

  /**
   * Create a new KafkaWriteStream instance
   *
   * @param vertx Vert.x instance to use
   * @param config  Kafka producer configuration
   * @return  an instance of the KafkaWriteStream
   */
  static <K, V> KafkaWriteStream<K, V> create(Vertx vertx, Properties config) {
    return KafkaWriteStreamImpl.create(vertx, config);
  }

  /**
   * Create a new KafkaWriteStream instance
   *
   * @param vertx Vert.x instance to use
   * @param config  Kafka producer configuration
   * @param keyType class type for the key serialization
   * @param valueType class type for the value serialization
   * @return  an instance of the KafkaWriteStream
   */
  static <K, V> KafkaWriteStream<K, V> create(Vertx vertx, Properties config, Class<K> keyType, Class<V> valueType) {
    Serializer<K> keySerializer = VertxSerdes.serdeFrom(keyType).serializer();
    Serializer<V> valueSerializer = VertxSerdes.serdeFrom(valueType).serializer();
    return KafkaWriteStreamImpl.create(vertx, config, keySerializer, valueSerializer);
  }

  /**
   * Create a new KafkaWriteStream instance
   *
   * @param vertx Vert.x instance to use
   * @param config  Kafka producer configuration
   * @param keySerializer key serializer
   * @param valueSerializer value serializer
   * @return  an instance of the KafkaWriteStream
   */
  static <K, V> KafkaWriteStream<K, V> create(Vertx vertx, Properties config, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
    return KafkaWriteStreamImpl.create(vertx, config, keySerializer, valueSerializer);
  }

  /**
   * Create a new KafkaWriteStream instance
   *
   * @param vertx Vert.x instance to use
   * @param config  Kafka producer configuration
   * @return  an instance of the KafkaWriteStream
   */
  static <K, V> KafkaWriteStream<K, V> create(Vertx vertx, Map<String, Object> config) {
    return KafkaWriteStreamImpl.create(vertx, config);
  }

  /**
   * Create a new KafkaWriteStream instance
   *
   * @param vertx Vert.x instance to use
   * @param config  Kafka producer configuration
   * @param keyType class type for the key serialization
   * @param valueType class type for the value serialization
   * @return  an instance of the KafkaWriteStream
   */
  static <K, V> KafkaWriteStream<K, V> create(Vertx vertx, Map<String, Object> config, Class<K> keyType, Class<V> valueType) {
    Serializer<K> keySerializer = VertxSerdes.serdeFrom(keyType).serializer();
    Serializer<V> valueSerializer = VertxSerdes.serdeFrom(valueType).serializer();
    return KafkaWriteStreamImpl.create(vertx, config, keySerializer, valueSerializer);
  }

  /**
   * Create a new KafkaWriteStream instance
   *
   * @param vertx Vert.x instance to use
   * @param config  Kafka producer configuration
   * @param keySerializer key serializer
   * @param valueSerializer value serializer
   * @return  an instance of the KafkaWriteStream
   */
  static <K, V> KafkaWriteStream<K, V> create(Vertx vertx, Map<String, Object> config, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
    return KafkaWriteStreamImpl.create(vertx, config, keySerializer, valueSerializer);
  }

  /**
   * Create a new KafkaWriteStream instance
   *
   * @param vertx Vert.x instance to use
   * @param producer  native Kafka producer instance
   */
  static <K, V> KafkaWriteStream<K, V> create(Vertx vertx, Producer<K, V> producer) {
    return new KafkaWriteStreamImpl<>(vertx.getOrCreateContext(), producer);
  }

  @Fluent
  @Override
  KafkaWriteStream<K, V> exceptionHandler(Handler<Throwable> handler);

  @Fluent
  @Override
  KafkaWriteStream<K, V> setWriteQueueMaxSize(int i);

  @Fluent
  @Override
  KafkaWriteStream<K, V> drainHandler(@Nullable Handler<Void> handler);

  /**
   * Asynchronously write a record to a topic
   *
   * @param record  record to write
   * @return a {@code Future} completed with the record metadata
   */
  Future<RecordMetadata> send(ProducerRecord<K, V> record);

  /**
   * Asynchronously write a record to a topic
   *
   * @param record  record to write
   * @param handler handler called on operation completed
   * @return  current KafkaWriteStream instance
   */
  KafkaWriteStream<K, V> send(ProducerRecord<K, V> record, Handler<AsyncResult<RecordMetadata>> handler);

  /**
   * Get the partition metadata for the give topic.
   *
   * @param topic topic partition for which getting partitions info
   * @param handler handler called on operation completed
   * @return  current KafkaWriteStream instance
   */
  KafkaWriteStream<K, V> partitionsFor(String topic, Handler<AsyncResult<List<PartitionInfo>>> handler);

  /**
   * Like {@link #partitionsFor(String, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<List<PartitionInfo>> partitionsFor(String topic);

  /**
   * Invoking this method makes all buffered records immediately available to write
   *
   * @param completionHandler handler called on operation completed
   * @return  current KafkaWriteStream instance
   */
  KafkaWriteStream<K, V> flush(Handler<AsyncResult<Void>> completionHandler);

  /**
   * Like {@link #flush(Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> flush();

  /**
   * Close the stream
   */
  Future<Void> close();

  /**
   * Close the stream
   *
   * @param completionHandler handler called on operation completed
   */
  void close(Handler<AsyncResult<Void>> completionHandler);

  /**
   * Close the stream
   *
   * @param timeout timeout to wait for closing
   * @param completionHandler handler called on operation completed
   */
  void close(long timeout, Handler<AsyncResult<Void>> completionHandler);

  /**
   * Like {@link #close(long, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> close(long timeout);

  /**
   * @return the underlying producer
   */
  Producer<K, V> unwrap();
}
