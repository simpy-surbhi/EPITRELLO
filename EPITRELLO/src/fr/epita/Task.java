package fr.epita;


/**
 * Task class which contains the information related to task like Assigned User, List, Estimated time, Priority, Description
 * @author kuamit
 *
 */
public class Task {
	
	public Task() {
		status = Status.NOT_COMPLETE;
	}
	
	/**
	 * Function to get User information
	 * @return User information
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Function to set User
	 * @param user - name of user
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	/**
	 * Function to get the list which contains the task
	 * @return the list which contains the task 
	 */
	public String getList() {
		return list;
	}
	
	/**
	 * Function to assign task to list
	 * @param list - name of the list
	 */
	public void setList(String list) {
		this.list = list;
	}
	
	/**
	 * Function to get the information of estimated time to perform the task
	 * @return Estimated time value
	 */
	public int getEstimatedTime() {
		return estimatedTime;
	}
	
	/**
	 * Function to set the estimated time to perform the task
	 * @param estimatedTime - value of Estimate time
	 */
	public void setEstimatedTime(int estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	
	/**
	 * Function to get the priority information of the task
	 * @return Priority value 
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * Function to set the priority information of the task
	 * @param priority - of the task
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/**
	 * Function to get the description information of the task
	 * @return description information
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Function to set the description information of the task
	 * @param description of the task
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Function to get the status of the task
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * Function to set the status of the task
	 * @param status information 
	 */
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
	
	/**
	 * Function to get the name of the task
	 * @return name of the task
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Function to set the name of the task
	 * @param name of the task
	 */
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


/**
 * Enum value of Task status. 
 *
 */
enum Status {
	/**
	 * Not Complete status
	 */
	NOT_COMPLETE,
	/**
	 * Complete Status
	 */
	COMPLETE;
}
