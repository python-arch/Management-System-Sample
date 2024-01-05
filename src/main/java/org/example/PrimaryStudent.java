package org.example;

public class PrimaryStudent extends  Student{
    private String year;
    PrimaryStudent(String name , String grade , String year){
        super(name , grade);
        this.year = year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
       return year;
    }

    @Override
    public String toString() {
        return "The Student's Name : " + this.getName() + ", The grade: " + this.getGrade() + ", The current year: " + this.year;
    }
}
