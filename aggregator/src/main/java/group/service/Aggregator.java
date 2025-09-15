package group.service;


import group.entity.Delta;
import group.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


@Service
public class Aggregator {
	@Value("${STORAGE_CAPACITY.DEFAULT_TOP_CAPACITY}")
	private Integer maxCapacity;

	private final AtomicReference<Map<Integer, List<User>>> atomicUserMap = new AtomicReference<>(new HashMap<>());
	private final AtomicReference<Map<Integer, List<User>>> atomicLevelMap = new AtomicReference<>(new HashMap<>());

	private final Comparator<User> userInfocomparator = Comparator
			.comparing(User::getResult).reversed()
			.thenComparing(User::getLevelId);

	private final Comparator<User> levelInfoComparator = Comparator
			.comparing(User::getResult).reversed()
			.thenComparing(User::getId);

	@KafkaListener(topics = "userInfo-aggregator", groupId = "fourth-group")
	public void listenUserInfo(Delta delta) {
		atomicUserMap.updateAndGet(oldMap -> {
			Map<Integer, List<User>> newMap = new HashMap<>(oldMap);
			delta.getMap().forEach((userId, incomingList) -> {
				List<User> currentList = newMap.getOrDefault(userId, new ArrayList<>());

				for (User user : incomingList) {
					currentList.stream()
							.filter(u -> u.getLevelId().equals(user.getLevelId()))
							.findFirst()
							.ifPresent(currentList::remove);
					currentList.add(user);
				}

				currentList.sort(userInfocomparator);
				if (currentList.size() > maxCapacity) {
					currentList.subList(maxCapacity, currentList.size()).clear();
				}

				newMap.put(userId, currentList);
			});
			return newMap;
		});
	}

	@KafkaListener(topics = "levelInfo-aggregator", groupId = "third-group")
	public void listenLevelInfo(Delta delta) {
		atomicLevelMap.updateAndGet(oldMap -> {
			Map<Integer, List<User>> newMap = new HashMap<>(oldMap);

			delta.getMap().forEach((userId, incomingList) -> {
				for (User user : incomingList) {
					newMap.computeIfAbsent(user.getLevelId(), k -> new ArrayList<>())
							.removeIf(u -> u.getId().equals(user.getId()));
					newMap.get(user.getLevelId()).add(user);
				}
			});

			newMap.forEach((levelId, list) -> {
				list.sort(levelInfoComparator);
				if (list.size() > maxCapacity) {
					list.subList(maxCapacity, list.size()).clear();
				}
			});

			return newMap;
		});
	}


	public List<User> getUsers(Integer id) {
		Map<Integer, List<User>> mapSnapshot = atomicUserMap.get();
		return mapSnapshot.get(id);
	}

	public List<User> getLevelUsers(Integer id) {
		Map<Integer, List<User>> mapSnapshot = atomicLevelMap.get();
		return mapSnapshot.get(id);
	}
}

