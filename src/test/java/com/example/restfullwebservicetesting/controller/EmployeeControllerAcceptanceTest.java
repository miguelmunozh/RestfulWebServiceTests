package com.example.restfullwebservicetesting.controller;

import com.example.restfullwebservicetesting.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// In this test, the full Spring application context is started but without the server
// Random port is helpful when you want to run parallel tests, to avoid port clashing
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Annotation that can be applied to a test class to enable and configure auto-configuration of MockMvc.
@AutoConfigureMockMvc
// ensure that all the changes made by each test are rolled back
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EmployeeControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createEmployee() throws Exception {
        Employee employee = new Employee("create employee name");
        mockMvc.perform(post("/createEmployee")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id",is(1)))
                .andExpect(jsonPath("$.name",is(employee.getName())));
    }

    @Test
    void getAllEmployees() throws Exception {
        mockMvc.perform(get("/getAllEmployees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    void getEmployeeById() throws Exception {
        Employee employee = new Employee("employee by id name");
        // insert a real obj via http request to try to find it
        mockMvc.perform(post("/createEmployee")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andDo(print());

        mockMvc.perform(get("/getEmployeeById/"+1L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)));
    }
    @Test
    @Disabled
    void getEmployeeByIdError() throws Exception {
        long nonExistentUserId = 1L;

        mockMvc.perform(get("/getEmployeeById/"+nonExistentUserId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof RuntimeException).isTrue())
                .andExpect(mvcResult -> assertThat(mvcResult.getResponse().getErrorMessage()).isEqualTo("emploi not found"));
    }

    @Test
    void deleteEmployeeById() throws Exception {
        Employee employee = new Employee("delete employee by id");
        // insert a real obj via http request to try to find it
        mockMvc.perform(post("/createEmployee")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andDo(print());

        // it will be one since is the only obj in the db
        long userId = 1L;
        mockMvc.perform(delete("/deleteEmployeeById/"+userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect((result)->assertThat(result.getResponse()
                        .getContentAsString())
                        .isEqualTo("true"));
    }
    @Test
    void deleteEmployeeByIdError() throws Exception {
        long nonExistentUserId = 1L;
        mockMvc.perform(delete("/deleteEmployeeById/"+nonExistentUserId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect((result)->assertThat(result.getResponse()
                        .getContentAsString())
                        .isEqualTo("false"));
    }
}