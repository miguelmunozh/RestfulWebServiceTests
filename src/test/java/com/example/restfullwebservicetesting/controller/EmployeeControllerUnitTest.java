package com.example.restfullwebservicetesting.controller;

import com.example.restfullwebservicetesting.model.Employee;
import com.example.restfullwebservicetesting.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Tests for controller layer
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeControllerUnitTest {
        @Mock
        private EmployeeService employeeService;

        // class under test
        @Autowired
        private EmployeeController underTest;

        @BeforeEach
        void setUp() {
            underTest = new EmployeeController(employeeService);
        }

        @Test
        void createEmployee() {
            // given
            // mock the service/dependency calls
            Employee expected = new Employee(1L,"name");
            when(employeeService.saveEmployee(expected)).thenReturn(expected);
            // when
            ResponseEntity<Employee> actual = underTest.createEmployee(expected);
            // then
            verify(employeeService).saveEmployee(expected);
            // assert the result of the method to what we expect it to return
            assertThat(expected).isEqualTo(actual.getBody());
        }

        @Test
        void getAllEmployees() {
            List<Employee> expected = Collections.emptyList();
            when(employeeService.findAll()).thenReturn(expected);

            ResponseEntity<List<Employee>> actual = underTest.getAllEmployees();

            verify(employeeService).findAll();
            assertThat(expected).isEqualTo(actual.getBody());
        }

        @Test
        void getEmployeeById() {
            // given
            Employee expected = new Employee(1L,"name");
            when(employeeService.findEmployeeById(1L)).thenReturn(expected);
            // when
            ResponseEntity<Employee> actual = underTest.getEmployeeById(1L);
            // then
            verify(employeeService).findEmployeeById(any());
            assertThat(expected).isEqualTo(actual.getBody());
        }

        @Test
        void getEmployeeByIdError() {
            // given
            when(employeeService.findEmployeeById(any())).thenThrow(
                    new RuntimeException("emploi not found")
            );
            // when
            // then
            assertThatThrownBy(()-> underTest.getEmployeeById(any()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("emploi not found");

            verify(employeeService).findEmployeeById(any());

        }

        @Test
        void deleteEmployeeById() {
            // given
            when(employeeService.deleteEmployeeById(any())).thenReturn(true);
            // when
            underTest.deleteEmployeeById(any());
            // then
            verify(employeeService).deleteEmployeeById(any());
            assertThat(employeeService.deleteEmployeeById(any())).isTrue();
        }
        // check for errors thrown  even when they are tested in the service layer?
        @Test
        void deleteEmployeeByIdError() {
            // given
            when(employeeService.deleteEmployeeById(any())).thenReturn(false);
            // when
            underTest.deleteEmployeeById(any());
            // then
            verify(employeeService).deleteEmployeeById(any());
            assertThat(employeeService.deleteEmployeeById(any())).isFalse();
        }
    }
