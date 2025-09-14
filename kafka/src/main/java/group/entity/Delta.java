package entity;

import java.util.ArrayList;
import java.util.List;

public class Delta {
	@Value
	private maxCapactity

	private List<User> toAdd = new ArrayList<>();
	private List<User> toRemove = new ArrayList<>();

	public Delta(){}

	public List<User> getAdded() {
		return toAdd;
	}

	public List<User> getDeleted() {
		return toRemove;
	}
}
