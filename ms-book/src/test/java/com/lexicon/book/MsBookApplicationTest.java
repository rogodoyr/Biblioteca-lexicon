package com.lexicon.book;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsBookApplicationTest {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring Boot carga correctamente
    }

    @Test
    void mainMethodRunsSuccessfully() {
        MsBookApplication.main(new String[]{});
    }
}
