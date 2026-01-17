package com.skilliou.backend.service;

import com.skilliou.backend.model.Iou;
import com.skilliou.backend.model.Ledger;
import com.skilliou.backend.model.User;
import com.skilliou.backend.repository.IouRepository;
import com.skilliou.backend.repository.LedgerRepository;
import com.skilliou.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LedgerService {

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private IouRepository iouRepository;

    @Autowired
    private UserRepository userRepository;

    // This runs as ONE atomic transaction. All or nothing.
    @Transactional
    public Iou confirmIou(Long iouId) {
        // 1. Find IOU
        Iou iou = iouRepository.findById(iouId)
                .orElseThrow(() -> new RuntimeException("IOU not found"));

        // Prevent double confirmation
        if (!"PENDING".equals(iou.getStatus())) {
            throw new RuntimeException("IOU is already processed!");
        }

        // 2. Update Status
        iou.setStatus("CONFIRMED");
        iouRepository.save(iou);

        // 3. Credit the Giver (+)
        Ledger entryGiver = new Ledger();
        entryGiver.setUser(iou.getGiver());
        entryGiver.setCreditChange(iou.getCredits()); // Positive
        entryGiver.setReason("IOU #" + iou.getId() + " Confirmed (Help Given)");
        ledgerRepository.save(entryGiver);

        // 4. Debit the Receiver (-)
        Ledger entryReceiver = new Ledger();
        entryReceiver.setUser(iou.getReceiver());
        entryReceiver.setCreditChange(-iou.getCredits()); // Negative
        entryReceiver.setReason("IOU #" + iou.getId() + " Confirmed (Help Received)");
        ledgerRepository.save(entryReceiver);

        // 5. Update Trust Score (+5 for Giver)
        User giver = iou.getGiver();
        giver.setTrustScore(giver.getTrustScore() + 5);
        userRepository.save(giver);

        return iou;
    }
    
    public Double getUserBalance(Long userId) {
        return ledgerRepository.getBalance(userId);
    }
}