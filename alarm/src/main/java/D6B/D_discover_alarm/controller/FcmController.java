package D6B.D_discover_alarm.controller;

import D6B.D_discover_alarm.controller.dto.RequestDTO;
import D6B.D_discover_alarm.service.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @PostMapping("/alarm/fcm")
    public ResponseEntity<String> pushMessage(@RequestBody RequestDTO requestDTO) {
        System.out.println(requestDTO.getTargetToken() + " "
                +requestDTO.getTitle() + " " + requestDTO.getBody());
        try {
            firebaseCloudMessageService.sendMessageTo(
                    requestDTO.getTargetToken(),
                    requestDTO.getTitle(),
                    requestDTO.getBody(),
                    requestDTO.getImage());
        } catch (IOException e) {
            e.printStackTrace();
            // 적절한 오류 메시지와 함께 실패 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("sendmessageTo 메서드 오류");
        }
        return ResponseEntity.ok("메시지 전송 성공");
    }
}