package group.controller;


import group.entity.User;
import group.service.Aggregator;
import group.service.Sender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {
	private final Aggregator aggregator;
	private final Sender sender;

	public Controller(Aggregator aggregator, Sender sender) {
		this.aggregator = aggregator;
		this.sender = sender;

	}

	@PutMapping("/user")
	public ResponseEntity<?> putUser(@RequestBody User user) {
		sender.SendEvent(user);
		return ResponseEntity.accepted().build();
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<List<User>> getUsers(@PathVariable Integer id) {
		return  ResponseEntity.ok(aggregator.getUsers(id));
	}

	@GetMapping("/level/{id}")
	public ResponseEntity<List<User>> getUsersByLevelId(@PathVariable Integer id) {
		return ResponseEntity.ok(aggregator.getLevelUsers(id));
	}
}
