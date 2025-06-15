package uz.husan.signup.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseMessage {
    private Boolean success;
    private String message;
    private Object data;
}
