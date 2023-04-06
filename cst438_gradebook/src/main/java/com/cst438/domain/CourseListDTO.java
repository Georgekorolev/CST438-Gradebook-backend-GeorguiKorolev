package com.cst438.domain;

import java.util.ArrayList;

/*
 * a transfer object that is a list of Course details
 */
public class CourseListDTO {
	
	public ArrayList<CourseDTO> courses = new ArrayList<>();

	@Override
	public String toString() {
		return "CourseListDTO " + courses ;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CourseListDTO other = (CourseListDTO) obj;
		if (courses == null) {
			if (other.courses != null)
				return false;
		} else if (!courses.equals(other.courses))
			return false;
		return true;
	}
	
}
