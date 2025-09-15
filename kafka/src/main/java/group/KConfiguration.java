package group;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KConfiguration {

	private Map<String, Object> commonConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9092");
		props.put(CommonClientConfigs.SESSION_TIMEOUT_MS_CONFIG, 45000);
		props.put(CommonClientConfigs.HEARTBEAT_INTERVAL_MS_CONFIG, 15000);
		props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
		props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
		props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
		props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1048576);
		props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 10485760);
		props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 5242880);
		return props;
	}

	private Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>(commonConfigs());
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 131072);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 268435456);
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");
		props.put(ProducerConfig.RETRIES_CONFIG, 3);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		props.put(JsonSerializer.TYPE_MAPPINGS, "user:group.entity.User, delta:group.entity.Delta");
		return props;
	}



	private Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>(commonConfigs());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 2000);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TYPE_MAPPINGS, "user:group.entity.User, delta:group.entity.Delta");
		return props;
	}

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, ?> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public ConsumerFactory<String, Object> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}
