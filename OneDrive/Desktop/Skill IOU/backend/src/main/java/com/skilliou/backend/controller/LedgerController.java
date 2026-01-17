package com.skilliou.backend.controller;

import com.skilliou.backend.model.Ledger;
import com.skilliou.backend.repository.LedgerRepository;
import com.skilliou.backend.service.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ledger")
@CrossOrigin(origins = "*")
public class LedgerController {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private LedgerRepository ledgerRepository;

    // 1. Get My Balance
    @GetMapping("/balance/{userId}")
    public Map<String, Double> getBalance(@PathVariable Long userId) {
        Double balance = ledgerService.getUserBalance(userId);
        return Map.of("balance", balance);
    }

    // 2. Get My History
    @GetMapping("/history/{userId}")
    public List<Ledger> getHistory(@PathVariable Long userId) {
        return ledgerRepository.findByUserId(userId);
    }
}