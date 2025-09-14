package group.service;

import group.entities.Delta;
import group.entities.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;



@Service
public class Shard {
	@Value("${STORAGE_CAPACITY}")
	private Integer maxCapacity;

	private final AtomicReference<List<User>> localTop20 = new AtomicReference<>(new ArrayList<>());
	private final Comparator<User> comparator = Comparator
			.comparing(User::getResult).reversed()
			.thenComparing(User::getId);
	private final KafkaTemplate<String, Delta> kafkaTemplate;

	public Shard(@Qualifier("deltaKafkaTemplate") KafkaTemplate<String, Delta> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@KafkaListener(topics = "user-info", groupId = "first-topic", containerFactory = "userListenerFactory")
	public void listen(ConsumerRecord<String, User> record) {
		Delta delta = new Delta(maxCapacity);
		User user = record.value();

		localTop20.updateAndGet(oldTop -> {
			List<User> merged = oldTop.stream()
					.filter(u -> !u.getId().equals(user.getId()))
					.collect(Collectors.toList());
			merged.add(user);
			merged.sort(comparator);

			if(merged.size() > maxCapacity) {
				List<User> toRemove = merged.subList(maxCapacity, merged.size());
				delta.getDeleted().addAll(toRemove);
				merged = new ArrayList<>(merged.subList(0, maxCapacity));
			}

			if(merged.contains(user)) {
				delta.getAdded().add(user);
			}

			return Collections.unmodifiableList(merged);
		});

		if(!delta.getAdded().isEmpty() || !delta.getDeleted().isEmpty()) {
			kafkaTemplate.send("third-topic", delta);
		}
	}
}
