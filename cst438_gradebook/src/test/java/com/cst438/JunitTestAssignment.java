package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Date;
import java.util.Optional;

import com.cst438.controllers.AssignmentController;
import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.services.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

/* 
 * Example of using Junit with Mockito for mock objects
 *  the database repositories are mocked with test data.
 *  
 * Mockmvc is used to test a simulated REST call to the RestController
 * 
 * the http response and repository is verified.
 * 
 *   Note: This tests uses Junit 5.
 *  ContextConfiguration identifies the controller class to be tested
 *  addFilters=false turns off security.  (I could not get security to work in test environment.)
 *  WebMvcTest is needed for test environment to create Repository classes.
 */
@ContextConfiguration(classes = { AssignmentController.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestAssignment {

	static final String URL = "http://localhost:8081";
	public static final int TEST_ASSIGNMENT_ID = 1;
	public static final int TEST_COURSE_ID = 123456;
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME = "test";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int TEST_YEAR = 2021;
	public static final String TEST_SEMESTER = "Fall";

	@MockBean
	AssignmentRepository assignmentRepository;

	@MockBean
	AssignmentGradeRepository assignmentGradeRepository;

	@MockBean
	CourseRepository courseRepository; // must have this to keep Spring test happy

	@MockBean
	RegistrationService registrationService; // must have this to keep Spring test happy

	@Autowired
	private MockMvc mvc;

	@Test
	public void addAssignment() throws Exception{

		MockHttpServletResponse response;

		
		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setTitle("test_course");
		course.setAssignments(new java.util.ArrayList<Assignment>());
		
		Assignment assignment = new Assignment();
		assignment.setId(TEST_ASSIGNMENT_ID);
		java.sql.Date dueDate = new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
		assignment.setDueDate(dueDate);
		String assignmentName = "Assignment 1";
		assignment.setName(assignmentName);
		assignment.setNeedsGrading(1);
		assignment.setCourse(course);
		course.getAssignments().add(assignment);
		
		given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
		
		response = mvc
				.perform(MockMvcRequestBuilders.put("/addAssignment").accept(MediaType.APPLICATION_JSON)
						.content("{ \"dueDate\": \""+ dueDate +"\", \"name\": \"" + assignmentName + "\", \"courseId\": " + TEST_COURSE_ID + "}").contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		verify(assignmentRepository, times(1)).save(any(Assignment.class));
		
		AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

		
		assertEquals(0, result.needsGrading);
		assertEquals("Assignment 1", result.name);
		assertEquals(dueDate.toString(), result.dueDate);
		assertEquals(course.getCourse_id(), result.courseId);
		
	}
	
	@Test
	public void changeAssignmentName() throws Exception{

		MockHttpServletResponse response;
		
		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setTitle("test_course");
		course.setAssignments(new java.util.ArrayList<Assignment>());
		
		Assignment assignment = new Assignment();
		assignment.setId(TEST_ASSIGNMENT_ID);
		java.sql.Date dueDate = new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
		assignment.setDueDate(dueDate);
		String assignmentName = "Assignment 1";
		assignment.setName(assignmentName);
		assignment.setNeedsGrading(1);
		assignment.setCourse(course);
		
		String newAssignmentName = "Assignment 2";
		

		given(assignmentRepository.findById(TEST_ASSIGNMENT_ID)).willReturn(Optional.of(assignment));
		
		response = mvc
				.perform(MockMvcRequestBuilders.put("/renameAssignment").accept(MediaType.APPLICATION_JSON)
						.content("{ \"assignmentId\": "+ TEST_ASSIGNMENT_ID + ", \"name\": \"" + newAssignmentName + "\"}").contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

		assertEquals(newAssignmentName, result.name);
		
	}
	
	@Test
	public void deleteAssignment() throws Exception{

		MockHttpServletResponse response;

		
		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setTitle("test_course");
		course.setAssignments(new java.util.ArrayList<Assignment>());
		
		Assignment assignment = new Assignment();
		assignment.setId(TEST_ASSIGNMENT_ID);
		java.sql.Date dueDate = new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
		assignment.setDueDate(dueDate);
		String assignmentName = "Assignment 1";
		assignment.setName(assignmentName);
		assignment.setNeedsGrading(1);
		assignment.setCourse(course);
		course.getAssignments().add(assignment);
		
		given(assignmentRepository.findById(TEST_ASSIGNMENT_ID)).willReturn(Optional.of(assignment));
		
		response = mvc
				.perform(MockMvcRequestBuilders.delete("/deleteAssignment").accept(MediaType.APPLICATION_JSON)
						.content("{ \"assignmentId\": " + TEST_ASSIGNMENT_ID + "}").contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		verify(assignmentRepository, times(1)).findById(TEST_ASSIGNMENT_ID);

		verify(assignmentRepository, times(1)).deleteById(TEST_ASSIGNMENT_ID);
		
		AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

		assertEquals(assignmentName, result.name);
		
	}
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T fromJsonString(String str, Class<T> valueType) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
