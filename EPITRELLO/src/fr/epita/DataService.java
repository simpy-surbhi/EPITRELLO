package fr.epita;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.epita.db.DBManager;

/**
 * Implementation class of the interface EpitrelloDataServerice
 *
 */
public class DataService implements EpitrelloDataServerice {

	private static DataService dataServerice;
	private Set<String> users;
	private Set<String> lists;
	private Map<String, Task> tasks;
	private DBManager dbManager;
	private String outputFile;

	private static final String SUCCESS = "Success";
	private static final String LIST_ERROR = "List does not exist";
	private static final String TASK_ERROR = "Task does not exist";
	private static final String USER_ERROR = "User does not exist";
	
	/**
	 * Initialization of collections for storing the list, tasks information
	 */
	private DataService() {
		users = new LinkedHashSet<>();
		lists = new LinkedHashSet<>();
		tasks = new LinkedHashMap<>();
		dbManager = DBManager.getInstance();
		dbManager.initialize();
		users = dbManager.getUsers();

		outputFile = System.getProperty("user.dir")+"/output/"+Calendar.getInstance().getTimeInMillis()+"_Output.txt";
	}
	
	/**
	 * creator function to return the object of DataService class
	 * @return the object of type EpitrelloDataServerice
	 */
	public static EpitrelloDataServerice creator() {
		if(dataServerice == null) {
			dataServerice = new DataService();
		}
		return dataServerice;
	}
	
	@Override
	public String addUser(String user) {
		if(users.contains(user)) {
			writeToFile("User already exists");
			return "User already exists";
		}
		
		dbManager.addUser(user);
		users.add(user);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String addList(String list) {
		boolean status = lists.add(list);
		if(status) {
			writeToFile(SUCCESS);
			return SUCCESS;
		} else {
			writeToFile("List string already exists");
			return "List string already exists";
		}
	}

	@Override
	public String addTask(String list, String name, int estimatedTime, int priority, String description) {
		if( list == null || list.isEmpty() || name == null || name.isEmpty() || estimatedTime <=0 || priority <=0 ) {
			return "AddTask task failed, reason: entered detail(s) are invalid!!";
		}
		
		if(!lists.contains(list)) {
			writeToFile(LIST_ERROR);
			return LIST_ERROR;
		}
		
		Task task = tasks.get(name);
		if(task != null) {
			writeToFile("Task already exists");
			return "Task already exists";
		}
		
		task = new Task();
		task.setDescription(description);
		task.setEstimatedTime(estimatedTime);
		task.setList(list);
		task.setPriority(priority);
		task.setName(name);
		tasks.put(name, task);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String editTask(String taskName, int estimatedTime, int priority, String description) {
		
		if( taskName == null || taskName.isEmpty() || estimatedTime <=0 || priority <=0 ) {
			return "EditTask task failed, reason: entered detail(s) are invalid!!";
		}
		
		if(!tasks.containsKey(taskName)) {
			writeToFile(TASK_ERROR);
			return TASK_ERROR;
		}
		
		Task task = tasks.get(taskName);
		task.setDescription(description);
		task.setEstimatedTime(estimatedTime);
		task.setPriority(priority);
		tasks.put(taskName, task);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String assignTask(String taskName, String user) {
		if( taskName == null || taskName.isEmpty() || user == null || user.isEmpty()) {
			return "AssignTask task failed, reason: entered detail(s) are invalid!!";
		}
		
		if(!tasks.containsKey(taskName)) {
			writeToFile(TASK_ERROR);
			return TASK_ERROR;
		}
		
		if(!users.contains(user)) {
			writeToFile(USER_ERROR);
			return USER_ERROR;
		}
		
		Task task = tasks.get(taskName);
		task.setUser(user);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String printTask(String taskName) {
		if(!tasks.containsKey(taskName)) {
			writeToFile(LIST_ERROR);
			return LIST_ERROR;
		}
		String sb = tasks.get(taskName).toString();
		writeToFile(sb);
		return sb;
	}

	@Override
	public String completeTask(String taskName) {
		if(!tasks.containsKey(taskName)) {
			writeToFile(TASK_ERROR);
			return TASK_ERROR;
		}
		Task task = tasks.get(taskName);
		task.setStatus(Status.COMPLETE);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String printUsersByPerformance() {
		if(users.isEmpty()) {
			return "No user present";
		}

		if(tasks.isEmpty()) {
			return "No task present";
		}
		
		String user;
		Integer count;
		Map<String, Integer> map = new LinkedHashMap<>();
		for(Task task: tasks.values()) {
			user = task.getUser();
			
			if(user!=null && !user.isEmpty() && Status.COMPLETE.name().equals(task.getStatus().name())) {
				count = map.get(user);
				if(count ==null) {
					count = 0;
				} 
				map.put(user, count+task.getEstimatedTime());
			}
		}
		
		if(map.size() ==0) {
			return "None of tasks are assigned to user";
		}
		
		
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		 
		map.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		
		Set<String> keys = sortedMap.keySet();
		StringBuilder str = new StringBuilder();
		for(String us: keys) {
			str.append(us).append("\n");
		}
		
		writeToFile(str.toString());
		return str.toString();
	}

	@Override
	public String printWorkload() {
		if(users.isEmpty() || tasks.isEmpty()) {
			writeToFile("0");
			return "0";
		}
		
		int effort = 0;
		String user;
		for(Task task: tasks.values()) {
			user = task.getUser();
			if(user!=null && !user.isEmpty()) {
				effort = effort+task.getEstimatedTime();
			}
		}
		writeToFile(""+effort);
		return ""+effort;
		
	}
	
	
	@Override
	public String printUsersByWorkload() {
		if(users.isEmpty()) {
			return "No user present";
		}

		if(tasks.isEmpty()) {
			return "No task present";
		}
		
		String user;
		Integer count;
		Map<String, Integer> map = new LinkedHashMap<>();
		for(Task task: tasks.values()) {
			user = task.getUser();
			
			if(user!=null && !user.isEmpty()) {
				count = map.get(user);
				if(count ==null) {
					count = 0;
				} 
				map.put(user, count+task.getEstimatedTime());
			}
		}
		
		if(map.size() ==0) {
			return "None of tasks are assigned to user";
		}
		//reorder to ascending
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		 
		map.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue())
		    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

		Set<String> keys = sortedMap.keySet();
		StringBuilder str = new StringBuilder();
		for(String us: keys) {
			str.append(us).append("\n");
		}
		
		writeToFile(str.toString());
		return str.toString();
	}

	@Override
	public String printUnassignedTasksByPriority() {
		List<String> str = new ArrayList<>();
		String user;
		for (Task task: tasks.values()) {
			user = task.getUser();
			if(user == null || user.isEmpty()) {
				user = "Unassigned";
				str.add(task.getPriority()+" | "+task.getName()+" | "+user+" | "+task.getEstimatedTime()+"h");
			}
		}
		
		Collections.sort(str);
		StringBuilder sb = new StringBuilder();
		for (String st: str) {
			sb.append("\n").append(st);
		}
		writeToFile(sb.toString());
		return sb.toString();
	}

	@Override
	public String deleteTask(String task) {
		if(!tasks.containsKey(task)) {
			return TASK_ERROR;
		}
		tasks.remove(task);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String printAllUnfinishedTasksByPriority() {
		List<String> str = new ArrayList<>();
		String user;
		for (Task task: tasks.values()) {
			if(task.getStatus().name().equals(Status.NOT_COMPLETE.name())) {
				user = task.getUser();
				if(user == null || user.isEmpty()) {
					user = "Unassigned";
				}
				str.add(task.getPriority()+" | "+task.getName()+" | "+user+" | "+task.getEstimatedTime()+"h");
			}
		}
		
		Collections.sort(str);
		StringBuilder sb = new StringBuilder();
		for (String st: str) {
			sb.append("\n").append(st);
		}
		writeToFile(sb.toString());
		return sb.toString();
	}

	@Override
	public String moveTask(String task, String list) {
		if(!lists.contains(list)) {
			writeToFile(LIST_ERROR);
			return LIST_ERROR;
		}
		
		if(!tasks.containsKey(task)) {
			writeToFile(TASK_ERROR);
			return TASK_ERROR;
		}
		
		Task tempTask = tasks.get(task);
		tempTask.setList(list);
		tasks.put(task, tempTask);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String printList(String list) {
		if(list == null || list.isEmpty()) {
			return "\nInvalid list param";
		}
		
		
		if(!lists.contains(list)) {
			return "\nList does not exist";
		}
		
		String user;		
		StringBuilder sb = new StringBuilder("\nList ").append(list);
		
		for(Task task:tasks.values()) {
			if(list.equals(task.getList())) {
				user = task.getUser();
					if(user == null || user.isEmpty()) {
						user = "Unassigned";
					}
					sb.append("\n"+task.getPriority()+" | "+task.getName()+" | "+user+" | "+task.getEstimatedTime()+"h");
			}
		}
		writeToFile(sb.toString());
		return sb.toString();
	}

	@Override
	public String printAllLists() {
		
		StringBuilder sb = new StringBuilder();
		for(String list: lists) {
			sb.append("\n").append(printList(list));
			
		}
		writeToFile(sb.toString());
		return sb.toString();
	}

	@Override
	public String printUserTasks(String userName) {
		if(userName==null || userName.isEmpty()) {
			return "Invalid user input";
		}
		
		if(users.isEmpty() || !users.contains(userName)) {
			writeToFile(USER_ERROR);
			return USER_ERROR;
		}

		if(tasks.isEmpty()) {
			return "No task present";
		}
		
		String user;
		StringBuilder str = new StringBuilder();
		for(Task task: tasks.values()) {
			user = task.getUser();
			
			if(user!=null && !user.isEmpty() && user.equals(userName)) {
				str.append("\n").append(task.getPriority()).append(" | ").append(task.getDescription()).append(" | ").append(task.getUser()).append(" | ").append(task.getEstimatedTime()).append("h");
			}
		}
		if(str.toString().isEmpty()) {
			str.append("None of the tasks are assigned to user");
		}
		
		writeToFile(str.toString());
		
		return str.toString();
	}
	
	@Override
	public String printTotalRemainingTime() {
		if(tasks.isEmpty()) {
			writeToFile("0");
			return "0";
		}
		
		int effort = 0;
		String user;
		for(Task task: tasks.values()) {
			user = task.getUser();
			if(user == null || user.isEmpty()) {
				continue;
			}
			if(task.getStatus().name().equals(Status.NOT_COMPLETE.name())) {
				effort = effort+task.getEstimatedTime();
			}
		}
		writeToFile(""+effort);
		return ""+effort;
	}
	
	@Override
	public String printTotalEstimateTime() {
		if(tasks.isEmpty()) {
			writeToFile("0");
			return "0";
		}
		
		int effort = 0;
		String user;
		for(Task task: tasks.values()) {
			user = task.getUser();
			if(user == null || user.isEmpty()) {
				continue;
			}
				effort = effort+task.getEstimatedTime();
		}
		writeToFile(""+effort);
		return ""+effort;
	}
	
	@Override
	public String printUserUnfinishedTasks(String user) {
		
		if(user ==null || user.isEmpty()) {
			return "Invalid user input";
		}
		
		if(!users.contains(user)) {
			writeToFile(USER_ERROR);
			return USER_ERROR;
		}
		
		List<String> str = new ArrayList<>();
		for (Task task: tasks.values()) {
			if(task.getStatus().name().equals(Status.NOT_COMPLETE.name()) && user.equals(task.getUser())) {
				str.add(task.getPriority()+" | "+task.getName()+" | "+user+" | "+task.getEstimatedTime()+"h");
			}
		}
		
		Collections.sort(str);
		StringBuilder sb = new StringBuilder();
		for (String st: str) {
			sb.append("\n").append(st);
		}
		writeToFile(sb.toString());
		return sb.toString();
	}
	/**
	 * Function to write the message in a given file path. New messages will be appended to the file.
	 * @param message
	 * @return status: true/false, file write operation status value.
	 */
	private boolean writeToFile(String message) 
	{
	    BufferedWriter writer = null;
	    boolean status = false;
		try {
			writer = new BufferedWriter(new FileWriter(outputFile, true));
			writer.write("\n"+message);
		    writer.close();
		    status = true;
		} catch (IOException e) {
			System.err.println("Error occured in writing output to file: "+e.getLocalizedMessage());
			try {
				if(writer!=null) {
					writer.close();
				}
			} catch (IOException e1) {
				System.err.println("Error occured in closing filewriter: "+e1.getLocalizedMessage());
			}
			return status;
		}
		return status; 
	    
	}
	@Override
	public String deleteList(String list) {
		if(list == null || list.isEmpty()) {
			return "Invalid input";
		}
		if(lists.isEmpty() || !lists.contains(list)) {
			writeToFile(LIST_ERROR);
			return LIST_ERROR;
		}
		
		lists.remove(list);
		for(Task task: tasks.values()) {
			if(task.getList().equals(list)) {
				tasks.remove(task.getName());
			}
		}
		writeToFile(SUCCESS);
		return SUCCESS;
	}
	

}
