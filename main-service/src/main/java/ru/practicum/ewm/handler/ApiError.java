package ru.practicum.ewm.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiError {
    String errors;
    String message;
    String reason;
    String status;
    String timestamp;
}
