package com.mongodb.spring.migration.resource;

import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.records.MemberRequest;
import com.mongodb.spring.migration.records.MemberResponse;
import com.mongodb.spring.migration.service.CustomUserDetailsService;
import com.mongodb.spring.migration.service.MemberService;
import com.mongodb.spring.migration.service.TokenBlacklistService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    private Member member;
    private MemberResponse memberResponse;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        member = new Member();
        member.setId("1");
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");

        List<String> roles = Arrays.asList("ROLE_USER");
        memberResponse = new MemberResponse(member, roles);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testListAllMembers() throws Exception {
        List<MemberResponse> memberResponses = Arrays.asList(memberResponse);
        when(memberService.listAllMembersWithRoles()).thenReturn(memberResponses);

        mockMvc.perform(get("/members").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'member':{'id':'1','name':'John Doe','email':'john.doe@example.com','phoneNumber':'1234567890'},'roles':['ROLE_USER']}]"));
    }

    @Test
    @WithMockUser
    public void testLookupMemberById() throws Exception {
        when(memberService.lookupMemberById("1")).thenReturn(ResponseEntity.of(Optional.of(member)));

        mockMvc.perform(get("/members/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id':'1','name':'John Doe','email':'john.doe@example.com','phoneNumber':'1234567890'}"));
    }

    @Test
    @WithMockUser
    public void testGetMemberByEmail() throws Exception {
        when(memberService.findMemberByEmail("john.doe@example.com")).thenReturn(member);

        mockMvc.perform(get("/members/email/john.doe@example.com").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id':'1','name':'John Doe','email':'john.doe@example.com','phoneNumber':'1234567890'}"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateMember() throws Exception {
        MemberRequest memberRequest = new MemberRequest(member, Arrays.asList("ROLE_USER"), "password");
        when(memberService.registerMemberWithRoles(any(Member.class), any(List.class), anyString())).thenReturn(member);

        mockMvc.perform(post("/members").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"member\":{\"id\":\"1\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"phoneNumber\":\"1234567890\"},\"roles\":[\"ROLE_USER\"],\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{'id':'1','name':'John Doe','email':'john.doe@example.com','phoneNumber':'1234567890'}"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateMember() throws Exception {
        MemberRequest memberRequest = new MemberRequest(member, Arrays.asList("ROLE_USER"), "password");
        when(memberService.updateMember(anyString(), any(Member.class), any(List.class), anyString())).thenReturn(member);

        mockMvc.perform(put("/members/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"member\":{\"id\":\"1\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"phoneNumber\":\"1234567890\"},\"roles\":[\"ROLE_USER\"],\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id':'1','name':'John Doe','email':'john.doe@example.com','phoneNumber':'1234567890'}"));
    }

}