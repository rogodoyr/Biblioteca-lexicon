package com.lexicon.loan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsLoanApplicationTest {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring Boot carga correctamente
    }

    @Test
    void mainMethodRunsSuccessfully() {
        MsLoanApplication.main(new String[]{});
    }
}
