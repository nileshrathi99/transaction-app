package dev.codescreen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Ping {

    private String serverTime;
    public Ping(){
        this.serverTime = LocalDateTime.now().toString();
    }
}
