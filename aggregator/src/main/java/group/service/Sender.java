package group.service;

import group.entity.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Sender {
	private final KafkaTemplate<String, User> kafkaTemplate;

	public Sender(KafkaTemplate<String, User> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void SendEvent(User user) {
		kafkaTemplate.send("user-info", user);
		kafkaTemplate.send("level-info", user);
	}
}
