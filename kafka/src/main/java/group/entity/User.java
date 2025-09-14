package entity;

public class User {
	private Integer id;
	private Integer levelId;
	private Integer result;

	public User(){}

	public User(Integer id, Integer levelId, Integer result) {
		this.id = id;
		this.levelId = levelId;
		this.result = result;
	}

	public Integer getId() {
		return id;
	}

	public Integer getLevelId() {
		return levelId;
	}

	public Integer getResult() {
		return result;
	}
}
