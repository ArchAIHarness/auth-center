package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.service.UniqueCodeGenerator;

class UniqueCodeGeneratorTest {

    private UniqueCodeGenerator generator = new UniqueCodeGenerator();

    @Test
    void testGenerateCode() {
        String code = generator.generate("client1", "user1");
        assertNotNull(code);
        assertEquals(8, code.length());
    }

    @Test
    void testGenerateUniqueCodes() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            codes.add(generator.generate("client1", "user1"));
        }
        assertEquals(100, codes.size(), "所有生成的code应该唯一");
    }

    @Test
    void testGenerateDifferentClientIds() {
        String code1 = generator.generate("client1", "user1");
        String code2 = generator.generate("client2", "user1");
        assertNotEquals(code1, code2, "不同clientId应生成不同code");
    }

    @Test
    void testGenerateDifferentUserIds() {
        String code1 = generator.generate("client1", "user1");
        String code2 = generator.generate("client1", "user2");
        assertNotEquals(code1, code2, "不同userId应生成不同code");
    }

    @Test
    void testConcurrentGenerate() throws InterruptedException {
        int threadCount = 10;
        int loopCount = 50;
        Set<String> codeSet = new HashSet<>();
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            new Thread(() -> {
                try {
                    for (int i = 0; i < loopCount; i++) {
                        String code = generator.generate("conc_5", "user_888");
                        synchronized (codeSet) {
                            codeSet.add(code);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        assertTrue(codeSet.size() > 0, "应该生成了代码");
    }

    @Test
    void testCodeContainsOnlyDigitsAndLetters() {
        for (int i = 0; i < 100; i++) {
            String code = generator.generate("client1", "user1");
            for (char c : code.toCharArray()) {
                assertTrue(Character.isDigit(c) || (c >= 'a' && c <= 'z'),
                        "code should only contain digits or lowercase letters");
            }
        }
    }
}
