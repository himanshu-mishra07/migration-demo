package com.mongodb.spring.migration.resource;

import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.service.CustomUserDetailsService;
import com.mongodb.spring.migration.service.MemberService;
import com.mongodb.spring.migration.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberResource.class)
public class MemberResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    private Member member;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        member = new Member();
        member.setId("1");
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");
    }

    @Test
    @WithMockUser
    public void testListAllMembers() throws Exception {
        List<Member> members = Arrays.asList(member);
        when(memberService.listAllMembers()).thenReturn(members);

        mockMvc.perform(get("/rest/members"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'id':'1','name':'John Doe','email':'john.doe@example.com','phoneNumber':'1234567890'}]"));
    }

    @Test
    @WithMockUser
    public void testLookupMemberById() throws Exception {
        when(memberService.lookupMemberById("1")).thenReturn(ResponseEntity.of(Optional.of(member)));

        mockMvc.perform(get("/rest/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id':'1','name':'John Doe','email':'john.doe@example.com','phoneNumber':'1234567890'}"));
    }

}