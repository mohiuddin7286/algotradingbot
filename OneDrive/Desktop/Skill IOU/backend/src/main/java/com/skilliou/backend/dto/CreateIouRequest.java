package com.skilliou.backend.dto;

import lombok.Data;

@Data
public class CreateIouRequest {
    private Long giverId;    // Who helped?
    private Long receiverId; // Who got help?
    private Long skillId;    // What skill?
    private Double credits;  // How much worth?
}