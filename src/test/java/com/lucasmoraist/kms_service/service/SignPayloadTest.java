package com.lucasmoraist.kms_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasmoraist.kms_service.model.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.MessageType;
import software.amazon.awssdk.services.kms.model.SignRequest;
import software.amazon.awssdk.services.kms.model.SignResponse;
import software.amazon.awssdk.services.kms.model.SigningAlgorithmSpec;

import java.security.MessageDigest;
import java.util.Base64;

@Log4j2
@SpringBootTest
public class SignPayloadTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KmsClient kmsClient;

    @Value("${secrets.aws.kms.key-id}")
    private String keyId;

    @Test
    @DisplayName("Should sign payload")
    void case01() throws Exception {
        User user = new User(
                "John Doe",
                "johndoe@email.com",
                30
        );
        String payload = this.objectMapper.writeValueAsString(user);
        log.info("Payload: {}", payload);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(payload.getBytes());

        SignRequest req = SignRequest.builder()
                .keyId(keyId) // ID da chave do KMS
                .signingAlgorithm(SigningAlgorithmSpec.RSASSA_PKCS1_V1_5_SHA_256)
                .messageType(MessageType.DIGEST)
                .message(SdkBytes.fromByteArray(hash))
                .build();

        SignResponse res = kmsClient.sign(req);
        log.info("Sign response: {}", res.signature());
        String base64 = Base64.getEncoder().encodeToString(res.signature().asByteArray());
        log.info("Base64: {}", base64);
    }

}
