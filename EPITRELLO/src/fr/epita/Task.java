package fr.epita;

public class Task {
	
	public Task() {
		status = Status.NOT_COMPLETE;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getList() {
		return list;
	}
	public void setList(String list) {
		this.list = list;
	}
	public int getEstimatedTime() {
		return estimatedTime;
	}
	public void setEstimatedTime(int estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	private String user;
	private String list;
	private int estimatedTime; 
	private int priority;
	private String description;
	private Status status;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if(user == null || user.isEmpty()) {
			return new StringBuffer(name).append('\n').append(description).append("\nPriority: ").append(priority).append("\nEstimated Time: ").append(estimatedTime).append("\nUnassigned").toString();
		}
		return new StringBuffer(name).append('\n').append(description).append("\nPriority: ").append(priority).append("\nEstimated Time: ").append(estimatedTime).append("\nAssigned to ").append(user).toString();
	}
	
}

enum Status {
	NOT_COMPLETE,
	COMPLETE;
}
