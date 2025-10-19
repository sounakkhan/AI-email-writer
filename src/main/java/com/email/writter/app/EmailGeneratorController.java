package com.email.writter.app;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailGeneratorController {
    private final EditGeneratorService editGeneratorService;
    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody Emailrequest emailrequest){
        String response=editGeneratorService.generateEmailReply(emailrequest);
        return  ResponseEntity.ok(response);
    }
}
