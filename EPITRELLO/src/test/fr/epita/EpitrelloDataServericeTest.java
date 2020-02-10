package test.fr.epita;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import fr.epita.DataService;
import fr.epita.EpitrelloDataServerice;

class EpitrelloDataServericeTest {
	EpitrelloDataServerice dataService = DataService.creator();
	@Test
	void test1() {
		String status;
		status = dataService.addUser("User1");
		status = dataService.addUser("User1");
		assertEquals(status, "User already exists");
		status = dataService.addUser("User1");
		assertEquals(status, "User already exists");
		
		//list testcase
		status = dataService.addList("list1");
		assertEquals(status, "Success");
		status = dataService.addList("list1");
		assertEquals(status, "List string already exists");
	}
	
	@Test
	void test2() {
		String status = dataService.addTask("Code", "Do Everything", 12, 1, "Write the whole code");
		assertEquals(status, "List does not exist");
		status = dataService.editTask("Do Everything", 12, 10, "Write the whole code");
		assertEquals(status, "Task does not exist");
		
		status = dataService.addList("Code");
		
		status = dataService.addTask("Code", "Do Everything", 12, 1, "Write the whole code");
		assertEquals(status, "Success");
		status = dataService.editTask("Do Everything", 12, 10, "Write the whole code");
		assertEquals(status, "Success");
		
		
		status = dataService.assignTask("Do Everything", "Rabih");
		assertEquals(status, "Success");
		
		status = dataService.printTask("Do Everything");
		assertEquals(status, "Do Everything\n" + 
				"Write the whole code\n" + 
				"Priority: 10\n" + 
				"Estimated Time: 12\n" + 
				"Assigned to Rabih" + 
				"");
	}
	
	@Test
	void test3() {
		String status = dataService.completeTask("Do Everything");
		assertEquals(status, "Success");

		status = dataService.printUsersByPerformance();
		assertEquals(status, "Rabih\n");
		
		status = dataService.printUsersByPerformance();
		assertEquals(status, "Rabih\n");
		
		dataService.addTask("list1", "Draw a diagram", 4, 2, "draw the diagram on the paper");
		
		status = dataService.printUnassignedTasksByPriority();
		assertEquals(status, "\n2 | Draw a diagram | Unassigned | 4h");
		
		status = dataService.deleteTask("Upload Assignment");
		assertEquals(status, "Task does not exist");
		
		status = dataService.deleteTask("Do Everything");
		assertEquals(status, "Success");
		
		status = dataService.printAllUnfinishedTasksByPriority();
		assertEquals(status, "\n2 | Draw a diagram | Unassigned | 4h");
	}
	
	@Test
	void test4() {
	dataService.addList("Misc");
		String status = dataService.addTask("Misc", "Have fun", 10, 2, "Just do it");
		assertEquals(status, "Success");
		status = dataService.moveTask("Have fun", "Code");
		assertEquals(status, "Success");
		status = dataService.printTask("Have fun");
		
		assertEquals(status, "Have fun\n" + 
				"Just do it\n" + 
				"Priority: 2\n" + 
				"Estimated Time: 10\n" + 
				"Unassigned");
		
		status = dataService.printList("Code");
		assertEquals(status, "\nList Code\n" + 
				"2 | Have fun | Unassigned | 10h");
}

	@Test
	void test5() {
		String status = dataService.printAllLists();
		assertEquals(status, "\n\nList list1\n" + 
				"2 | Draw a diagram | Unassigned | 4h\n" + 
				"\n" + 
				"List Code\n" + 
				"2 | Have fun | Unassigned | 10h\n" + 
				"\n" + 
				"List Misc");
		dataService.assignTask("Have fun", "User1");
		status = dataService.printUserTasks("User1");
		assertEquals(status, "\n2 | Just do it | User1 | 10h");
	}
	
	@Test
	void test6() {
	    String status = dataService.printUnassignedTasksByPriority();
	    assertEquals(status, "\n" + 
	    		"2 | Draw a diagram | Unassigned | 4h");
	    
	    status = dataService.printAllUnfinishedTasksByPriority();
	    assertEquals(status, "\n" + 
	    		"2 | Draw a diagram | Unassigned | 4h\n" + 
	    		"2 | Have fun | User1 | 10h");
		
	}
}
