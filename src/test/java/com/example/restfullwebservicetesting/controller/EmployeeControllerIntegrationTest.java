package com.example.restfullwebservicetesting.controller;

import com.example.restfullwebservicetesting.model.Employee;
import com.example.restfullwebservicetesting.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * integration tests between http request and method
 */
// instantiates the web layer rather than the whole context
@WebMvcTest
class EmployeeControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    // used so that we dont have to mock all the dependencies used by this service
    @MockBean
    private EmployeeService employeeService;
    // ObjectMapper instance to serialize responses and deserialize requests to json
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createEmployee() throws Exception {
        // given
        Employee inRequest = new Employee(1L,"name");
        Employee inResponse = new Employee(1L,"name");
        when(employeeService.saveEmployee(any())).thenReturn(inResponse);
        /**
         * {
         *      "id" : 1,
         *      "name" : "myName"
         * }
         */
        // when
        mockMvc.perform(post("/createEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(inResponse.getId().intValue())))
                .andExpect(jsonPath("$.name", is(inResponse.getName())));

        verify(employeeService).saveEmployee(any());
    }

    @Test
    void getAllEmployees() throws Exception {
        // given
        List<Employee> expected = Collections.emptyList();
        when(employeeService.findAll()).thenReturn(expected);
        // when / then
        mockMvc.perform(get("/getAllEmployees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$",hasSize(0)));
        //      .andExpect(jsonPath("$[0].name", is("Jane Doe")));
        verify(employeeService).findAll();
    }

    @Test
    void getEmployeeById() throws Exception {
        Employee expected = new Employee(1L,"myname");
        when(employeeService.findEmployeeById(any())).thenReturn(expected);

        mockMvc.perform(get("/getEmployeeById/"+expected.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(expected.getId().intValue())));

        verify(employeeService).findEmployeeById(any());
    }

    @Test
    @Disabled
    void getEmployeeByIdError() throws Exception {
        // throw the error when the method is called, mock the error
        when(employeeService.findEmployeeById(1L))
                .thenThrow(new RuntimeException("emploi not found"));
//        MockHttpServletResponse response = mockMvc.perform(get("/getEmployeeById/"+1L).content("{}")
//        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
//
//        assertThat(response.getErrorMessage()).isEqualTo("emploi not found");

        mockMvc.perform(get("/getEmployeeById/"+1L).content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        mvcResult -> assertThat(mvcResult.getResponse()
                                .getErrorMessage()).isEqualTo("emploi not found"));
    }
    @Test
    void deleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployeeById(any())).thenReturn(true);
        mockMvc.perform(delete("/deleteEmployeeById/"+1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect((result)->assertThat(result.getResponse()
                        .getContentAsString())
                        .isEqualTo("true"));
        verify(employeeService).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeByIdError() throws Exception {
        when(employeeService.deleteEmployeeById(any())).thenReturn(false);
        mockMvc.perform(delete("/deleteEmployeeById/"+1L)
                .contentType(MediaType.APPLICATION_JSON))
                // meaning that the method returned correctly a falsy value
                .andExpect(status().isOk())
                .andExpect(
                        (result)->assertThat(result.getResponse()
                                .getContentAsString())
                                .isEqualTo("false"));
    }
}