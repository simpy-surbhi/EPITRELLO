package fr.epita;

/**
 * Interface class where functions are listed
 *
 */

/*@startuml
autonumber
actor User
User --> Main
activate Main
alt Initialization of System
Main -> DataService: getInstance
activate DataService
DataService -> DBManager: Initialization
activate DBManager
DBManager -> DBManager: Initialization of DB
activate FileWriter
DataService -> FileWriter: Output file is created
Deactivate FileWriter

note right of DataService: Initialization of system which includes, DBManager initialization, DataStructure initialization
end

Main -> DataService: Add User
DataService -> DBManager: Add User
DBManager -> DBManager: Persist user information
activate FileWriter
DataService -> FileWriter: Output is written to file
Deactivate FileWriter
Deactivate DBManager

Main -> DataService: Add List
DataService -> DataService: Lists updated
activate FileWriter
DataService -> FileWriter: Output is written to file
Deactivate FileWriter
alt Task related operation
Main -> DataService: Add Task
DataService -> DataService: Creation of Task Object and added to service
activate FileWriter
DataService -> FileWriter: Output is written to file
Deactivate FileWriter
Main -> DataService: Edit Task
DataService -> DataService: Task updation
activate FileWriter
DataService -> FileWriter: Output is written to file
Deactivate FileWriter
Main -> DataService: Assign Task
DataService -> DataService: Assign Task to a user
note right of DataService: All tasks related operations are performed with DBService
end

Deactivate DataService
Deactivate Main


@enduml
*/

public interface EpitrelloDataServerice {

	
	/**
	 * Function to add users to the system. User is an unique field.
	 * @param user - user information
	 * @return the “User already exists” if a duplicate name is entered for the user other wise SUCCESS message.
	 */
	String addUser(String user);

	/**
	 * Function to add a list to the system. Notice the name of each list in the system is a unique field. 
	 * @param list - list information
	 * @return the “List string already exists” if a duplicate name is entered for the user other wise SUCCESS message.
	 */
	String addList(String list);

	/**
	 * Function to add a task to a list. The name of each task in the system is a unique field
	 * @param list - name of the list
	 * @param name - name of the task 
	 * @param estimatedTime - is a positive number
	 * @param priority - is a positive number
	 * @param description - description of the task
	 * @return If there is no list with the name entered in the system, the “List does not exist” and returns “Task already exists” if it is a duplicate task. If the operation is successful return SUCCESS message.
	 */
	String addTask(String list, String name, int estimatedTime, int priority, String description);

	/**
	 * Function is to change values or properties what we set for the task at the time of construction
	 * @param task - name of the task, for which edit operation is performed. 
	 * @param estimatedTime - is a positive number
	 * @param priority - is a positive number
	 * @param description - description of the task
	 * @return SUCCESS message if edit performance is done successfully else print error.
	 */
	String editTask(String task, int estimatedTime, int priority, String description);

	/**
	 * Function to delegate a task to a user
	 * @param task - name of the task to be assigned. 
	 * @param user - user name to whom task is going to be assigned. 
	 * @return In case of user item In the system is not available, the string “User does not exist” returns. Otherwise in case of success, it returns SUCCESS string.
	 */
	String assignTask(String task, String user);

	/**
	 * Function returns the task details in the following format - 
	 * 
	 * <Task Name>
	 * <Description>
	 * Priority: <Priority value>
	 * Estimated Time: <Estimated Time value>
	 * Assigned to <User>
	 * @param task - name of task
	 * @return task details
	 */
	String printTask(String task);

	/**
	 * Function to mark a task COMPLETED.
	 * @param task: name of the task
	 * @return SUCCESS message in case the operation is successful else error message.
	 */
	String completeTask(String task);

	/**
	 * Function to print performance of the individual user which is equal to the total time estimate of the tasks performed by that user.
	 * @return users in descending order and if equal, return in any order. 
	 */
	String printUsersByPerformance();

	/**
	 * Function to print amount of work a user does is the estimated time that all of the tasks assigned to that user.
	 * @return users in ascending order and if equal, return in any order.
	 */
	String printUsersByWorkload();

	/**
	 * Function to print all tasks not assigned to the system
	 * @return List of prioritized tasks not assigned to the system in the order in which they are equal, returns the desired order.
	 */
	String printUnassignedTasksByPriority();

	/**
	 * Function to remove the task from system. 
	 * @param task : name of the task which needs to be removed. 
	 * @return Returns the Task does not exist if the task is not present in the system.otherwise return SUCCESS message.
	 */
	String deleteTask(String task);

	/**
	 * Function to remove the list and corresponding tasks from system. 
	 * @param List : name of the list which needs to be removed. 
	 * @return Returns the List does not exist if the list is not present in the system.otherwise return SUCCESS message.
	 */
	String deleteList(String list);
	
	/**
	 * Function to print all tasks which are not finished 
	 * @return all tasks not assigned to the system in the order in which they are equal, returns the desired order.
	 */
	String printAllUnfinishedTasksByPriority();

	/**
	 * Function to move a task to another list. 
	 * @param task - name of the task.
	 * @param list - name of the list
	 * @return Returns the “List does not exist” if the destination list is not available in the system.Otherwise return SUCCESS message.
	 */
	String moveTask(String task, String list);

	/**
	 * Function to print all tasks in a list in order of creation.
	 * Output Format: <Task Priority> | <Task name> | User | Estimated Time(h)
	 * 
	 * @param list - name of the list
	 * @return Returns all the tasks in a list in order of creation.
	 * In the first line if the item list comment is not available in the system, returns the “List does not exist” string Returns.
	 */
	String printList(String list);

	/**
	 * Function to print all tasks group by list. Make all the tasks of all lists in order of the list and in each list the tasks in order. 
	 * @return Returns the output in the format of each list as: <Task Priority> | <Task name> | User | Estimated Time(h)
	 */
	String printAllLists();

	/**
	 * Function to print all tasks assigned to the user
	 * @param user: name of the user
	 * @return Returns all user tasks in the order that they were created. If the user is not in the System, returns the string “User does not exist”.
	 */
	String printUserTasks(String user);

	/**
	 * Function which prints workload information of the system.
	 * @return the sum of all user tasks in all lists. If User does not exist in the system, returns 0.
	 */
	String printWorkload();
	
	/**
	 * Function returns the estimated time of completion of all tasks performed. This
	 * @return This value is equal to the maximum the time required by users to perform their remaining tasks. 
	 * The time that users need to perform their remaining tasks is equal to the sum of the estimated time of all tasks assigned to that user that has not been completed.
	 * Note that this function does not include unassigned tasks.
	 */
	String printTotalRemainingTime(); 
	
	/**
	 * Function returns the estimated completion time of all tasks.
	 * @return total estimate time
	 */
	String printTotalEstimateTime();
	
	/**
	 * Function to print unfinished tasks of the user.
	 * @param user: name of the user
	 * @return Returns all tasks performed by the user in sequence. 
	 * Returns the “User does not exist” string if the user is not in the system.
	 */
	String printUserUnfinishedTasks(String user);
}
