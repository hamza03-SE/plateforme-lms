package com.coopchal.lms;

import com.coopchal.lms.dtos.RegisterRequest;
import com.coopchal.lms.enums.Role;
import com.coopchal.lms.services.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @BeforeEach
    void waitForMinio() throws InterruptedException {
        int tries = 10;
        while (tries > 0) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:9000").openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 200) break;
            } catch (IOException e) {
                // MinIO non prêt
            }
            Thread.sleep(3000);
            tries--;
        }
    }

    @Test
    public void testRegisterWithRealDbAndMinio() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@example.com", "password", Role.APPRENANT);
        var response = authService.register(request);
        assertNotNull(response.getToken());
    }
}
