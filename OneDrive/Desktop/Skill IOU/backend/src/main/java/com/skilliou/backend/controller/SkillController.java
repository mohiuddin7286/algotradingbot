package com.skilliou.backend.controller;

import com.skilliou.backend.model.Skill;
import com.skilliou.backend.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
public class SkillController {

    @Autowired
    private SkillRepository skillRepository;

    // 1. Add a new Skill
    @PostMapping
    public Skill createSkill(@RequestBody Skill skill) {
        return skillRepository.save(skill);
    }

    // 2. List all Skills
    @GetMapping
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }
}