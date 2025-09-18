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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
@SpringBootTest
public class VerifySignatureTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KmsClient kmsClient;

    @Value("${secrets.aws.kms.key-id}")
    private String keyId;

    @Test
    @DisplayName("Should verify signature")
    void case01() throws Exception {
        User user = new User(
                "John Doe",
                "johndoe@email.com",
                30
        );
        String payload = this.objectMapper.writeValueAsString(user);
        log.info("Payload: {}", payload);

        SignResponse res = generateSignature(payload);
        log.info("Sign response: {}", res.signature());
        String base64 = Base64.getEncoder().encodeToString(res.signature().asByteArray());
        log.info("Base64: {}", base64);

        PublicKey publicKey = this.loadPublicKey();

        boolean isValid = verify(payload, base64, publicKey);
        log.info("Is valid: {}", isValid);

        assertTrue(isValid);
    }

    private SignResponse generateSignature(String payload) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(payload.getBytes());

        SignRequest req = SignRequest.builder()
                .keyId(keyId) // ID da chave do KMS
                .signingAlgorithm(SigningAlgorithmSpec.RSASSA_PKCS1_V1_5_SHA_256)
                .messageType(MessageType.DIGEST)
                .message(SdkBytes.fromByteArray(hash))
                .build();

        return kmsClient.sign(req);
    }

    private PublicKey loadPublicKey() throws Exception {
        String pem = new String(Files.readAllBytes(Paths.get("src/test/resources/public_key.pem")));
        pem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(pem);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return factory.generatePublic(spec);
    }

    private boolean verify(String payload, String base64Signature, PublicKey publicKey) throws Exception {
        byte[] data = payload.getBytes();

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);

        byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);
        return sig.verify(signatureBytes);
    }

}
