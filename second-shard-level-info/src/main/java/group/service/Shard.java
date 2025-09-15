package group.service;

import group.entity.Delta;
import group.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class Shard {
	@Value("${STORAGE_CAPACITY.DEFAULT_TOP_CAPACITY}")
	private Integer maxCapacity;

	private final AtomicReference<Map<Integer, List<User>>> atomicMap = new AtomicReference<>(new HashMap<>());

	private final KafkaTemplate<String, Delta> kafkaTemplate;

	private final Comparator<User> comparator = Comparator
			.comparing(User::getResult).reversed()
			.thenComparing(User::getId);

	@Autowired
	public Shard(KafkaTemplate<String, Delta> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@KafkaListener(topics = "level-info", groupId = "second-group")
	public void listen(User user) {
		Delta delta = new Delta();
		atomicMap.updateAndGet(oldMap -> {
			Map<Integer, List<User>> newMap = new HashMap<>(oldMap);
			List<User> oldList = oldMap.get(user.getLevelId());
			List<User> newList = (oldList == null) ? new ArrayList<>() : new ArrayList<>(oldList);

			User existing = newList.stream()
					.filter(u -> u.getId().equals(user.getId()))
					.findFirst()
					.orElse(null);

			if (existing == null || user.getResult() > existing.getResult()) {
				if (existing != null) {
					newList.remove(existing);
				}
				newList.add(user);
			}
			newList.sort(comparator);
			if (newList.size() > maxCapacity) {
				newList.subList(maxCapacity, newList.size()).clear();
			}

			delta.getMap().put(user.getLevelId(), newList);
			newMap.put(user.getLevelId(), newList);
			return newMap;
		});

		if (!delta.getMap().isEmpty()) {
			kafkaTemplate.send("levelInfo-aggregator", delta);
		}
	}
}
