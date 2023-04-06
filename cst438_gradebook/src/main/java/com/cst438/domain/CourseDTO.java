package com.cst438.domain;

public class CourseDTO {
    public int courseId;
    public String title;
    public String instructor;
    public int year;
    public String semester;

    public CourseDTO(int courseId, String title, String instructor,
            int year, String semester) {
        this.courseId = courseId;
        this.title = title;
        this.instructor = instructor;
        this.year = year;
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "[courseId=" + courseId + ", title=" + title + ", instructor="
                + instructor + ", year=" + year + ", semester=" + semester + "]";
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CourseDTO other = (CourseDTO) obj;
        if (courseId != other.courseId)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (year != other.year)
            return false;
        if (instructor == null) {
            if (other.instructor != null)
                return false;
        } else if (!instructor.equals(other.instructor))
            return false;
        if (semester == null) {
            if (other.semester != null)
                return false;
        } else if (!semester.equals(other.semester))
            return false;
        return true;
    }

}