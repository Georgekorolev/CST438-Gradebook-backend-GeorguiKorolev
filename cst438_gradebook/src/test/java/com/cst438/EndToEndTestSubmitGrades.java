package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import org.openqa.selenium.support.ui.Select;


/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *  In SpringBootTest environment, the test program may use Spring repositories to 
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestSubmitGrades {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:\\Users\\George\\Downloads\\chromedriver_win32\\chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_STUDENT_NAME = "Test";

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addCourseTest() throws Exception {

//		Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle(TEST_COURSE_TITLE);

//	    add an assignment that needs grading for course 99999
		Assignment a = new Assignment();
		a.setCourse(c);
		// set assignment due date to 24 hours ago
		a.setDueDate(new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		a.setName(TEST_ASSIGNMENT_NAME);
		a.setNeedsGrading(1);

//	    add a student TEST into course 99999
		Enrollment e = new Enrollment();
		e.setCourse(c);
		e.setStudentEmail(TEST_USER_EMAIL);
		e.setStudentName(TEST_STUDENT_NAME);

		courseRepository.save(c);
		a = assignmentRepository.save(a);
		e = enrollmentRepository.save(e);

		AssignmentGrade ag = null;

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
			/*
			* locate input element for test assignment by assignment name
			* 
			* To select a radio button in a Datagrid display
			* 1.  find the elements in the assignmentName column of the data grid table.
			* 2.  locate the element with test assignment name and click the input tag.
			*/
			
			List<WebElement> elements  = driver.findElements(By.xpath("//div[@data-field='assignmentName']/div"));
			boolean found = false;
			for (WebElement we : elements) {
				System.out.println(we.getText()); // for debug
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found=true;
					we.findElement(By.xpath("descendant::input")).click();
					break;
				}
			}
			assertTrue( found, "Unable to locate TEST ASSIGNMENT in list of assignments to be graded.");

			/*
			 *  Locate and click Grade button to indicate to grade this assignment.
			 */
			
			driver.findElement(By.id("gradeAssignments")).click();
			Thread.sleep(SLEEP_DURATION);

			/*
			 *  Locate row for student name "Test" and enter score of "99.9" into the grade field
			 *  there should only be one row in the data grid table.
			 *  find the student name, then go to the grade column and enter 99.9
			 */
			
			elements  = driver.findElements(By.xpath("//div[@data-field='name' and @role='cell']"));
			for (WebElement element : elements) {
				System.out.println(element.getText());
				if (element.getText().equals(TEST_STUDENT_NAME)) {
					element.findElement(By.xpath("following-sibling::div[@data-field='grade']")).sendKeys("99.9"+Keys.ENTER);
					Thread.sleep(SLEEP_DURATION);
					break;
				}
			}
			
			/*
			 *  Locate submit button and click
			 */
			driver.findElement(By.xpath("//button[@id='Submit']")).click();
			Thread.sleep(SLEEP_DURATION);

			/*
			 *  verify that score show up in updated data grid table
			 */
			
			 WebElement w = driver.findElement(By.xpath("//div[@data-field='name' and @role='cell']"));
			 w =  w.findElement(By.xpath("following-sibling::div[@data-field='grade']"));
			assertEquals("99.9", w.getText(), "score does not show value entered as 99.9");

			// verify that assignment_grade has been added to database with score of 99.9
			ag = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(a.getId(), TEST_USER_EMAIL);
			assertEquals("99.9", ag.getScore());

		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  clean up database so the test is repeatable.
			 */
			ag = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(a.getId(), TEST_USER_EMAIL);
			if (ag!=null) assignnmentGradeRepository.delete(ag);
			enrollmentRepository.delete(e);
			assignmentRepository.delete(a);
			courseRepository.delete(c);

			driver.quit();
		}

	}

	@Test
	public void addAssignmentTest() throws Exception {
		//Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle(TEST_COURSE_TITLE);

		//add an assignment that needs grading for course 99999
		Assignment a = new Assignment();
		Assignment assignment = new Assignment();
		a.setCourse(c);

		//set assignment due date to 24 hours ago
		a.setDueDate(new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		a.setName(TEST_ASSIGNMENT_NAME);
		a.setNeedsGrading(1);
		int assignmentId = 0;

		//add a student TEST into course 99999
		Enrollment e = new Enrollment();
		e.setCourse(c);
		e.setStudentEmail(TEST_USER_EMAIL);
		e.setStudentName(TEST_STUDENT_NAME);

		
		courseRepository.save(c);
		e = enrollmentRepository.save(e);

		AssignmentGrade ag = null;

		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);

		try {
			
			/*
			 *  Locate and click Add New Assignment button to indicate to grade this assignment.
			 */
			
			driver.findElement(By.id("newAssignment")).click();
			Thread.sleep(SLEEP_DURATION);

			/*
			 * Click on the course select
			 */
			WebElement selectElement = driver.findElement(By.id("course_select"));
			selectElement.click();

			/*
			 * Click on the correct course given by c.getTitle()
			 */
			WebElement optionElement = driver.findElement(By.xpath("//li[contains(text(),'" + c.getTitle() + "')]"));
			optionElement.click();
			
			WebElement textElement = driver.findElement(By.id("assignmentName"));
			textElement.sendKeys(a.getName());

			// Locate the input element for the date picker
			//WebElement datePickerInput = driver.findElement(By.xpath("//button[@aria-label='Choose date']"));
			WebElement datePickerInput = driver.findElement(By.xpath("//input[@placeholder='MM / DD / YYYY']"));
			datePickerInput.click();
			Date date = a.getDueDate();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			String formattedDate = formatter.format(date);

			datePickerInput.sendKeys(formattedDate);


			/*
			 *  Locate submit button and click
			 */
			driver.findElement(By.xpath("//button[@id='Submit']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			List<Assignment> assignmentList = assignmentRepository.findAssignmentByName(a.getName());
			assignment = assignmentList.get(0);
			assertEquals(a.getName(), assignment.getName());
			assertEquals(a.getCourse().getCourse_id(), assignment.getCourse().getCourse_id());
			assertEquals(a.getDueDate().toString(), assignment.getDueDate().toString());

		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  clean up database so the test is repeatable.
			 */
			enrollmentRepository.delete(e);
			assignmentRepository.delete(assignment);
			courseRepository.delete(c);

			driver.quit();
		}
	}
}
