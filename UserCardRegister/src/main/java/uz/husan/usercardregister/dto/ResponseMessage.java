package uz.husan.usercardregister.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseMessage {
    private String message;
    private Boolean status;
    private Object data;
}
