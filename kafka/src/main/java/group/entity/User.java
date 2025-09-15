package group.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	@JsonProperty("id")
	private Integer userId;
	private Integer levelId;
	private Integer result;

	public User(){}

	public User(Integer id, Integer levelId, Integer result) {
		this.userId = id;
		this.levelId = levelId;
		this.result = result;
	}

	public Integer getId() {
		return userId;
	}

	public Integer getLevelId() {
		return levelId;
	}

	public Integer getResult() {
		return result;
	}
}
