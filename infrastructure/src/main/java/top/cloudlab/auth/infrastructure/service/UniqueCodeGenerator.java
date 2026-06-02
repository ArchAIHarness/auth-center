package top.cloudlab.auth.infrastructure.service;

import org.springframework.stereotype.Service;

import top.cloudlab.auth.domain.oauth.CodeGenerator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UniqueCodeGenerator implements CodeGenerator {

    private final ReentrantLock lock = new ReentrantLock();
    private long lastTimestamp = -1;
    private int sequence = 0;

    // 纯小写字母 + 数字
    // private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int SEQUENCE_MAX = 99;

    @Override
    public String generate(String clientId, String userId) {
        lock.lock();
        try {
            long now = System.currentTimeMillis();

            if (now == lastTimestamp) {
                sequence++;
                if (sequence > SEQUENCE_MAX) {
                    now = waitNextMilli(now);
                    sequence = 0;
                }
            } else {
                sequence = 0;
            }
            lastTimestamp = now;

            // 唯一原始字符串
            String raw = clientId + "|" + userId + "|" + now + "|" + sequence;
            String hash = md5(raw);

            // 生成自然均衡的16位优雅码
            return generateNaturalBalancedCode(hash);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 核心：自然随机 + 数字字母均衡分布
     * 不会连续太多字母，也不会刻意交替
     */
    private String generateNaturalBalancedCode(String hash) {
        StringBuilder sb = new StringBuilder();
        // int length = 16;
        int length = 8;

        for (int i = 0; i < length; i++) {
            int pos = (i * 3) % hash.length(); // 打乱位置，更均匀
            char c = hash.charAt(pos);
            int val = c;

            // 核心算法：自然随机，数字字母均衡
            boolean wantDigit = (val % 5) < 2; // 控制 40% 概率出数字
            if (wantDigit) {
                sb.append((char) ('0' + (val % 10)));
            } else {
                sb.append((char) ('a' + (val % 26)));
            }
        }
        return sb.toString();
    }

    private long waitNextMilli(long last) {
        long now;
        do {
            now = System.currentTimeMillis();
        } while (now <= last);
        return now;
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}