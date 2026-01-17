package com.skilliou.backend.controller;

import com.skilliou.backend.dto.CreateIouRequest;
import com.skilliou.backend.model.Iou;
import com.skilliou.backend.model.Skill;
import com.skilliou.backend.model.User;
import com.skilliou.backend.repository.IouRepository;
import com.skilliou.backend.repository.SkillRepository;
import com.skilliou.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ious")
@CrossOrigin(origins = "*")
public class IouController {

    @Autowired
    private IouRepository iouRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private com.skilliou.backend.service.LedgerService ledgerService;

    // 1. Create an IOU (The most complex part so far)
    @PostMapping
    public Iou createIou(@RequestBody CreateIouRequest request) {
        // Fetch the real objects from Database using IDs
        User giver = userRepository.findById(request.getGiverId())
                .orElseThrow(() -> new RuntimeException("Giver not found"));
        
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        // Build the IOU
        Iou iou = new Iou();
        iou.setGiver(giver);
        iou.setReceiver(receiver);
        iou.setSkill(skill);
        iou.setCredits(request.getCredits());
        iou.setStatus("PENDING"); // Default status

        return iouRepository.save(iou);
    }

    // 2. Get My Debts (What I owe)
    @GetMapping("/owed/{userId}")
    public List<Iou> getMyDebts(@PathVariable Long userId) {
        return iouRepository.findByReceiverId(userId);
    }
    @PostMapping("/{id}/confirm")
    public Iou confirmIou(@PathVariable Long id) {
        return ledgerService.confirmIou(id);
    }
}