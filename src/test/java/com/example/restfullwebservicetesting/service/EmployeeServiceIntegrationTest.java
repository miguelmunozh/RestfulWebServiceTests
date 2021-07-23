package com.example.restfullwebservicetesting.service;

import com.example.restfullwebservicetesting.model.Employee;
import com.example.restfullwebservicetesting.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import java.util.Collections;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
// ensure that all the changes made by each test are rolled back
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService underTest;

    @BeforeEach
    void setUp() {
        underTest = new EmployeeService(employeeRepository);
    }

    @Test
    @DisplayName("Should return an empty list since the db is empty")
    void findAll() {
        // when
        underTest.findAll();
        // then
        assertThat(employeeRepository.findAll()).isEqualTo(Collections.emptyList());
    }

    @Test
    void findEmployeeById() {
        // given
        Employee expected = new Employee("findEmployeeName");
        underTest.saveEmployee(expected);

        // when
        Employee actual = underTest.findEmployeeById(1L);
        // then
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    @Test
    void findEmployeeByIdError() {
        Long unExistentUserId = 1L;
        // when ,  then
        assertThatThrownBy(()-> underTest.findEmployeeById(unExistentUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("emploi not found");
    }

    @Test
    void saveEmployee() {
        // given
        Employee expected = new Employee("save employee name");

        // when
        Employee actual = underTest.saveEmployee(expected);

        // then
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getId()).isEqualTo(1);
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void deleteEmployeeById() {
        // given
        Employee expected = new Employee("save employee name");
        Employee actual = underTest.saveEmployee(expected);
        // when
        Boolean deleted = underTest.deleteEmployeeById(actual.getId());
        // then
        assertThat(deleted).isTrue();
    }

    @Test
    void deleteEmployeeByIdError() {
        // given
        Long anyNumber = 1L;
        // when
        Boolean deleted = underTest.deleteEmployeeById(anyNumber);
        // then
        assertThat(deleted).isFalse();
    }
}
