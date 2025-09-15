package group.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Delta {
	private final Map<Integer, List<User>> map;

	public Delta() {
		map = new HashMap<>();
	}

	public Map<Integer, List<User>> getMap() {
		return map;
	}
}
