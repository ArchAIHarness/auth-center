package top.cloudlab.auth.infrastructure.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * 雪花算法工具类
 */
public class SnowId {
    public static class SnowIdRuntimeException extends RuntimeException {
        public SnowIdRuntimeException(String message) {
            super(message);
        }
    }

    /**
     * 开始时间截 (2021-01-01)
     */
    private static final long START_TIMESTAMP = 1609459200000L;

    /**
     * 机器id所占的位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 数据标识id所占的位数
     */
    private static final long DATA_CENTER_ID_BITS = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /**
     * 序列在id中占的位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 机器ID向左移12位
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    /**
     * 工作机器ID(0~31)
     */
    private final long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private final long dataCenterId;

    /**
     * 不用SnowflakeIdFactory
     */
    private static final SnowId ID_WORKER;

    static {
        /**
         * 单例实现
         */
        ID_WORKER = new SnowId(getWorkId(), getDataCenterId());
    }

    /**
     * 构造函数
     */
    private SnowId(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("workerId can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("dataCenterId can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     */
    private synchronized long getNextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
            throw new SnowIdRuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            // 如果是同一时间生成的，则进行毫秒内序列
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {// 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        // 上次生成ID的时间截
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT) | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 当前时间戳（毫秒）
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取机器ID（SnowflakeIdFactory没有）
     */
    private static Long getWorkId() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            return createId(hostAddress);
        } catch (UnknownHostException e) { // 如果获取失败，则使用随机数备用
            return ThreadLocalRandom.current().nextLong(0, 32);
        }
    }

    private static Long getDataCenterId() {
        String hostName;
        if (SystemUtils.IS_OS_MAC) {
            hostName = getHostNameForLinux();
        } else {
            hostName = SystemUtils.getHostName();
        }
        return createId(Objects.isNull(hostName) ? "local" : "");
    }

    private static long createId(String hostName) {
        int[] ints = StringUtils.toCodePoints(hostName);
        int sums = 0;
        for (int i : ints) {
            sums += i;
        }
        return sums % 32;
    }

    private static String getHostNameForLinux() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            /* host = "hostname: hostname" */
            String host = uhe.getMessage();
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }

    public static Long next() {
        return ID_WORKER.getNextId();
    }

    public static String string() {
        return next().toString();
    }

}
