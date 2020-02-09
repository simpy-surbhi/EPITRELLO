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

	private final String SUCCESS = "Success";
	private DataService() {
		// TODO Auto-generated constructor stub
		users = new LinkedHashSet<String>();
		lists = new LinkedHashSet<String>();
		tasks = new LinkedHashMap<String, Task>();
		dbManager = DBManager.getInstance();
		dbManager.initialize();
		users = dbManager.getUsers();

		outputFile = System.getProperty("user.dir")+"/output/"+Calendar.getInstance().getTimeInMillis()+"_Output.txt";
	}
	static EpitrelloDataServerice Creator() {
		if(dataServerice == null) {
			dataServerice = new DataService();
		}
		return dataServerice;
	}
	
	@Override
	public String addUser(String user) {
		// TODO Auto-generated method stub
		if(users.contains(user)) {
			return "User already exists";
		}
		
		dbManager.addUser(user);
		users.add(user);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String addList(String list) {
		// TODO Auto-generated method stub
		return lists.add(list)? SUCCESS:"List string already exists";
	}

	@Override
	public String addTask(String list, String name, int estimatedTime, int priority, String description) {
		// TODO Auto-generated method stub
		if( list == null || list.isEmpty() || name == null || name.isEmpty() || estimatedTime <=0 || priority <=0 ) {
			return "AddTask task failed, reason: entered detail(s) are invalid!!";
		}
		
		if(!lists.contains(list)) {
			return "List does not exist";
		}
		
		Task task = tasks.get(name);
		if(task != null) {
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
		return "Success";
	}

	@Override
	public String editTask(String task_name, int estimatedTime, int priority, String description) {
		
		if( task_name == null || task_name.isEmpty() || estimatedTime <=0 || priority <=0 ) {
			return "EditTask task failed, reason: entered detail(s) are invalid!!";
		}
		
		// TODO Auto-generated method stub
		if(!tasks.containsKey(task_name)) {
			return "Task does not exist";
		}
		
		Task task = tasks.get(task_name);
		task.setDescription(description);
		task.setEstimatedTime(estimatedTime);
		task.setPriority(priority);
		tasks.put(task_name, task);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String assignTask(String task_name, String user) {
		// TODO Auto-generated method stub
		if( task_name == null || task_name.isEmpty() || user == null || user.isEmpty()) {
			return "AssignTask task failed, reason: entered detail(s) are invalid!!";
		}
		
		if(!tasks.containsKey(task_name)) {
			return "Task does not exist";
		}
		
		if(!users.contains(user)) {
			return "User does not exist";
		}
		
		Task task = tasks.get(task_name);
		task.setUser(user);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String printTask(String task_name) {
		// TODO Auto-generated method stub
		if(!tasks.containsKey(task_name)) {
			return "List does not exist";
		}
		String sb = tasks.get(task_name).toString();
		writeToFile(sb);
		return sb;
	}

	@Override
	public String completeTask(String task_name) {
		// TODO Auto-generated method stub
		
		if(!tasks.containsKey(task_name)) {
			return "Task does not exist";
		}
		Task task = tasks.get(task_name);
		task.setStatus(Status.COMPLETE);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String printUsersByPerformance() {
		// TODO Auto-generated method stub
		if(users.size() == 0) {
			return "No user present";
		}

		if(tasks.size() == 0) {
			return "No task present";
		}
		
		String user;
		Integer count;
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
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
		
		
		//Todo Sorting map by values descending i.e. 

		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		 
		map.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		
		Set<String> keys = sortedMap.keySet();
		StringBuffer str = new StringBuffer();
		for(String us: keys) {
			str.append(us).append("\n");
		}
		
		writeToFile(str.toString());
		return str.toString();
	}

	@Override
	public String printWorkload() {
		if(users.size() == 0 || tasks.size() == 0) {
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
		// TODO Auto-generated method stub
		if(users.size() == 0) {
			return "No user present";
		}

		if(tasks.size() == 0) {
			return "No task present";
		}
		
		String user;
		Integer count;
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
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
		//Todo Sorting map by values i.e. 
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		 
		map.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue())
		    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

		Set<String> keys = sortedMap.keySet();
		StringBuffer str = new StringBuffer();
		for(String us: keys) {
			str.append(us).append("\n");
		}
		
		writeToFile(str.toString());
		return str.toString();
	}

	@Override
	public String printUnassignedTasksByPriority() {
		// TODO Auto-generated method stub
		
		List<String> str = new ArrayList<String>();
		String user;
		for (Task task: tasks.values()) {
			user = task.getUser();
			if(user == null || user.isEmpty()) {
				user = "Unassigned";
				str.add(task.getPriority()+" | "+task.getName()+" | "+user+" | "+task.getEstimatedTime()+"h");
			}
		}
		
		Collections.sort(str);
		StringBuffer sb = new StringBuffer();
		for (String st: str) {
			sb.append("\n").append(st);
		}
		writeToFile(sb.toString());
		return sb.toString();
	}

	@Override
	public String deleteTask(String task) {
		// TODO Auto-generated method stub
		if(!tasks.containsKey(task)) {
			return "Task does not exist";
		}
		tasks.remove(task);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String printAllUnfinishedTasksByPriority() {
		// TODO Auto-generated method stub
		List<String> str = new ArrayList<String>();
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
		StringBuffer sb = new StringBuffer();
		for (String st: str) {
			sb.append("\n").append(st);
		}
		writeToFile(sb.toString());
		return sb.toString();
	}

	@Override
	public String moveTask(String task, String list) {
		// TODO Auto-generated method stub
		if(!lists.contains(list)) {
			return "List does not exist";
		}
		
		if(!tasks.containsKey(task)) {
			return "Task does not exist";
		}
		
		Task tempTask = tasks.get(task);
		tempTask.setList(list);
		tasks.put(task, tempTask);
		writeToFile(SUCCESS);
		return SUCCESS;
	}

	@Override
	public String printList(String list) {
		// TODO Auto-generated method stub
		if(list == null || list.isEmpty()) {
			return "\nInvalid list param";
		}
		
		
		if(!lists.contains(list)) {
			return "\nList does not exist";
		}
		
		String user;		
		StringBuffer sb = new StringBuffer("\nList ").append(list);
		
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
		// TODO Auto-generated method stub
		
		StringBuffer sb = new StringBuffer();
		for(String list: lists) {
			sb.append("\n").append(printList(list));
			
		}
		writeToFile(sb.toString());
		return sb.toString();
	}

	@Override
	public String printUserTasks(String user_name) {
		// TODO Auto-generated method stub
		if(user_name==null | user_name.isEmpty()) {
			return "Invalid user input";
		}
		
		if(users.size() == 0) {
			return "User does not exist";
		}

		if(tasks.size() == 0) {
			return "No task present";
		}
		
		String user;
		StringBuffer str = new StringBuffer();
		for(Task task: tasks.values()) {
			user = task.getUser();
			
			if(user!=null && !user.isEmpty() && user.equals(user_name)) {
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
		if(tasks.size() == 0) {
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
			if(task.getStatus().name().equals(Status.NOT_COMPLETE)) {
				effort = effort+task.getEstimatedTime();
			}
		}
		writeToFile(""+effort);
		return ""+effort;
	}
	
	@Override
	public String printTotalEstimateTime() {
		if(tasks.size() == 0) {
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
			return "User does not exist";
		}
		
		List<String> str = new ArrayList<String>();
		for (Task task: tasks.values()) {
			if(task.getStatus().name().equals(Status.NOT_COMPLETE.name()) && user.equals(task.getUser())) {
				str.add(task.getPriority()+" | "+task.getName()+" | "+user+" | "+task.getEstimatedTime()+"h");
			}
		}
		
		Collections.sort(str);
		StringBuffer sb = new StringBuffer();
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
	    BufferedWriter writer;
	    boolean status = false;
		try {
			writer = new BufferedWriter(new FileWriter(outputFile, true));
			writer.write("\n"+message);
		    writer.close();
		    status = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured in writing output to file: "+e.getLocalizedMessage());
		} finally {
			return status;
		}
	    
	}
	

}
