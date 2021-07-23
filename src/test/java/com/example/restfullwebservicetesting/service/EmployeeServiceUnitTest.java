package com.example.restfullwebservicetesting.service;

import com.example.restfullwebservicetesting.model.Employee;
import com.example.restfullwebservicetesting.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests - were since im mocking the values of the repository calls i dont need to test against the h2 db,
 *              because it is not an integration test between the db and the repository
 *
 * the thing is that we need the db in order to test the repository against the db, so
 * we use only h2 db when testing (not mocking) the repository
 *
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceUnitTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService underTest;

    @BeforeEach
    void setUp() {
        underTest = new EmployeeService(employeeRepository);
    }

    @Test
    void findAll() {
        // there might be no need to mock the values if im not using the db if nothing happens when calling the methods
        // of a mocked obj, the only time would be for when we need the result to either return correctly or throw an error
        // given
//        List<Employee> list = Collections.emptyList();
        // mock the result of the method call of the repository & verify the method was called, then assert the result
//        when(employeeRepository.findAll()).thenReturn(list);
        // when
        // to save it in a var like this is correct
//        List<Employee> listReturned =
        underTest.findAll();
        // then
        verify(employeeRepository).findAll();
        // this verifies that the mock call was mocked correctly
//        assertThat(list).isEqualTo(listReturned);

    }

    @Test
    void findEmployeeById() {
        // given
        Employee expected = new Employee(1L,"name");
        // mock a value for when the employee is found
        when(employeeRepository.findById(any())).thenReturn(Optional.of(expected));
        // when
        Employee actual = underTest.findEmployeeById(any());
        // then
        verify(employeeRepository).findById(any());
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void findEmployeeByIdError() {
        // mock the error when the method is called
        when(employeeRepository.findById(any()))
                .thenThrow(new RuntimeException("emploi not found"));
        // when / then
        assertThatThrownBy(()-> underTest.findEmployeeById(any()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("emploi not found");
    }

    @Test
    void saveEmployee() {
        // given
        Employee expected = new Employee("name");
        when(employeeRepository.save(any())).thenReturn(expected);

        // when
        Employee actual = underTest.saveEmployee(expected);

        // then
        ArgumentCaptor<Employee> argumentCaptor = ArgumentCaptor
                .forClass(Employee.class);

        // verify that the internal method was called and capture the argument
        verify(employeeRepository).save(argumentCaptor.capture());
        // get the captured argument
        Employee capturedEmployee = argumentCaptor.getValue();

        assertThat(capturedEmployee).isEqualTo(expected);
        // assertThat(expected).isEqualTo(actual);
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void deleteEmployeeById() {
        // given
        when(employeeRepository.existsById(any())).thenReturn(true);
        // when
        // since it returns void, just verify if the method was called or not
        underTest.deleteEmployeeById(any());
        // then
        verify(employeeRepository).deleteById(any());
    }

    @Test
    void deleteEmployeeByIdError() {
        when(employeeRepository.existsById(any())).thenReturn(false);
        // because it stoops at the exists method since it doesnt exist
        underTest.deleteEmployeeById(any());
        // we dont verify since it wont be called, instead we look for the error
        verify(employeeRepository, never()).deleteById(any());
    }
}
