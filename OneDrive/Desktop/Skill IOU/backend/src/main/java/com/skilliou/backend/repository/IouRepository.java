package com.skilliou.backend.repository;

import com.skilliou.backend.model.Iou;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IouRepository extends JpaRepository<Iou, Long> {
    
    // Find all IOUs where I am the receiver (Things I owe)
    List<Iou> findByReceiverId(Long receiverId);

    // Find all IOUs where I am the giver (Things owed to me)
    List<Iou> findByGiverId(Long giverId);
}