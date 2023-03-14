package com.cst438.domain;

import java.util.Objects;

public class AssignmentDTO {
	public int assignmentId;
	public int courseId;
	public int needsGrading;
	public String name;
	public String dueDate;
	
	public AssignmentDTO(){
		
	}
	
	public AssignmentDTO(int assignmentId, int courseId, int needsGrading, String name, String dueDate) {
		this.assignmentId = assignmentId;
		this.courseId = courseId;
		this.needsGrading = needsGrading;
		this.name = name;
		this.dueDate = dueDate;
	}
	
	public AssignmentDTO(int courseId, int needsGrading, String name, String dueDate) {
		this(0, courseId, needsGrading, name, dueDate);
	}
	

	public AssignmentDTO(int courseId, String name, String dueDate) {
		this(0, courseId, 1, name, dueDate);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AssignmentDTO))
			return false;
		AssignmentDTO other = (AssignmentDTO) obj;
		return assignmentId == other.assignmentId && courseId == other.courseId
				&& Objects.equals(dueDate, other.dueDate) && Objects.equals(name, other.name)
				&& needsGrading == other.needsGrading;
	}

	@Override
	public String toString() {
		return "AssignmentDTO [assignmentId=" + assignmentId + ", courseId=" + courseId + ", needsGrading="
				+ needsGrading + ", name=" + name + ", dueDate=" + dueDate + "]";
	}
	
}
