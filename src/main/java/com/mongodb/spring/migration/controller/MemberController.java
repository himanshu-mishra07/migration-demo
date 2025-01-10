package com.mongodb.spring.migration.controller;

import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/kitchensink")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/index")
    public String getMembersAndForm(Model model) {
        model.addAttribute("newMember", new Member());

        List<Member> members = memberService.listAllMembers();
        model.addAttribute("members", members);

        return "index";
    }

    @GetMapping("/user")
    public String getMembersAndFormForUser(Model model) {

        List<Member> members = memberService.listAllMembers();
        model.addAttribute("members", members);

        return "user";
    }

    @PostMapping("/index")
    public String registerNewMember(Member newMember, Model model) {
        List<Member> members = memberService.listAllMembers();
        model.addAttribute("members", members);

        try {
            memberService.registerMember(newMember);
            model.addAttribute("message", "Registered!");
            members = memberService.listAllMembers();
            model.addAttribute("members", members);
            model.addAttribute("newMember", new Member());
        } catch (Exception e) {
            log.error("Error occurred while registering new member: {}", e.getMessage());
            model.addAttribute("newMember", newMember);
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "index";
    }


}
