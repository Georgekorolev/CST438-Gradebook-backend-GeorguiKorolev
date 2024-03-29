package com.cst438.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
public class AssignmentController {
	
	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Autowired
	AssignmentGradeRepository assignmentGradeRepository;
	
	@GetMapping("/assignment/{id}")
	public AssignmentDTO assignment(@PathVariable("id") Integer assignmentId) {
		Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
		if(assignment != null) return assignment.toDTO();
		System.out.println("Error: No Assignment by that id");
		return null;
	}
	
	@PutMapping("/addAssignment")
	@Transactional
	public AssignmentDTO addAssignment (@RequestBody AssignmentDTO assignmentDTO) {
		
		Course course = courseRepository.findById(assignmentDTO.courseId).orElse(null);
		
		if(course == null) {
			System.out.println("Error: No Course by that id");
			return null;
		}
		
		Assignment assignment = new Assignment();
		
		course.getAssignments().add(assignment);
		
		assignment.setDueDate(Date.valueOf(assignmentDTO.dueDate));
		assignment.setName(assignmentDTO.name);
		if(assignment.getDueDate().compareTo(Date.valueOf(LocalDate.now())) < 1) {
			assignment.setNeedsGrading(1);
		} else assignment.setNeedsGrading(0);
		assignment.setCourse(course);
		
		assignmentRepository.save(assignment);
		return assignment.toDTO();
	}
	
	@PutMapping("/renameAssignment")
	@Transactional
	public AssignmentDTO renameAssignment (@RequestBody AssignmentDTO assignmentDTO) {
		
		Assignment assignment = assignmentRepository.findById(assignmentDTO.assignmentId).orElse(null);
		if(assignment != null) {
			assignment.setName(assignmentDTO.name);
			assignmentRepository.save(assignment);
			return assignment.toDTO();
		}
		System.out.println("Error: No Assignment by that id");
		return null;
	}
	
	@DeleteMapping("/deleteAssignment")
	@Transactional
	public AssignmentDTO deleteAssignment (@RequestBody AssignmentDTO assignmentDTO) {
		Assignment assignment = assignmentRepository.findById(assignmentDTO.assignmentId).orElse(null);
		if(assignment == null) {
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Assignment does not exist. " );
		}
		List<AssignmentGrade> grades = assignment.getGrades();
		boolean isDeletable = true;

		for (AssignmentGrade assignmentGrade : grades) {
			if(!assignmentGrade.getScore().equals("")){
				isDeletable = false;
				break;
			}
		}

		if(isDeletable) {
			for (AssignmentGrade assignmentGrade : grades) {
				assignmentGradeRepository.deleteById(assignmentGrade.getId());
			}
			assignmentRepository.deleteById(assignment.getId());
			return assignment.toDTO();
		}

		throw new ResponseStatusException( HttpStatus.PRECONDITION_FAILED, "Assignment with grades cannot be deleted. " );

	}
}
