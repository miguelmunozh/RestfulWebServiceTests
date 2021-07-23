package com.example.restfullwebservicetesting.service;

import com.example.restfullwebservicetesting.model.Employee;
import com.example.restfullwebservicetesting.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }

    public Employee findEmployeeById(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("emploi not found"));
    }

    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    public boolean deleteEmployeeById(Long id){
        if (employeeRepository.existsById(id)){
            employeeRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }
}

