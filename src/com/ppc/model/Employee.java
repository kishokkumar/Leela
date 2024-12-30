package com.ppc.model;

import java.time.LocalDate;

public class Employee implements IEmployeeDetails {

    public String empId;
    public String firstName;
    public String lastName;
    public String designation;
    public LocalDate onBoardingDate;
    public LocalDate exitDate; 
    
    public void setonBoardingDate(LocalDate onBoardingDate) {
        this.onBoardingDate = onBoardingDate;
    }

    public LocalDate getonBoardingDate() {
        return onBoardingDate;
    }
    
    public void setexitDate(LocalDate exitDate) {
        this.exitDate = exitDate;
    }

    public LocalDate getexitDate() {
        return exitDate;
    
    }
        
    public String getEmpId() {
         return empId;
    }
    
    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
