package com.mongodb.spring.migration.controller;

import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class MemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    public void testGetMembersAndForm() throws Exception {
        when(memberService.listAllMembers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/kitchensink/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attributeExists("newMember"))
                .andExpect(model().attributeExists("members"));
    }

    @Test
    public void testGetMembersAndFormForUser() throws Exception {
        when(memberService.listAllMembers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/kitchensink/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-page"))
                .andExpect(model().attributeExists("members"));
    }

    @Test
    public void testRegisterNewMember() throws Exception {
        when(memberService.listAllMembers()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/kitchensink/index")
                        .param("name", "John Doe")
                        .param("email", "john.doe@example.com")
                        .param("phoneNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attributeExists("newMember"))
                .andExpect(model().attributeExists("members"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    public void testRegisterNewMemberWithError() throws Exception {
        when(memberService.listAllMembers()).thenReturn(Collections.emptyList());
        when(memberService.registerMember(new Member())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/kitchensink/index")
                        .param("name", "John Doe")
                        .param("email", "john.doe@example.com")
                        .param("phoneNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attributeExists("newMember"))
                .andExpect(model().attributeExists("members"));
    }
}