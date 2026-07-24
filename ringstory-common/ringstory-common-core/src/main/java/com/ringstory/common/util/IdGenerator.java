package com.ringstory.common.util;

import java.util.UUID;

public class IdGenerator {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static long snowflakeId() {
        return Snowflake.getInstance().nextId();
    }

    private static class Snowflake {
        private static final Snowflake INSTANCE = new Snowflake();
        private final long workerId = 1L;
        private final long dataCenterId = 1L;
        private long sequence = 0L;
        private long lastTimestamp = -1L;
        private final long workerIdBits = 5L;
        private final long dataCenterIdBits = 5L;
        private final long sequenceBits = 12L;
        private final long workerIdShift = sequenceBits;
        private final long dataCenterIdShift = sequenceBits + workerIdBits;
        private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
        private final long sequenceMask = -1L ^ (-1L << sequenceBits);

        private Snowflake() {
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException("Worker ID can't be greater than " + maxWorkerId + " or less than 0");
            }
            if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
                throw new IllegalArgumentException("DataCenter ID can't be greater than " + maxDataCenterId + " or less than 0");
            }
        }

        private static final long maxWorkerId = -1L ^ (-1L << 5L);
        private static final long maxDataCenterId = -1L ^ (-1L << 5L);

        public static Snowflake getInstance() {
            return INSTANCE;
        }

        public synchronized long nextId() {
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
            }
            if (lastTimestamp == timestamp) {
                long seq = (sequence + 1) & sequenceMask;
                if (seq == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            return ((timestamp - 1288834974657L) << timestampLeftShift)
                    | (dataCenterId << dataCenterIdShift)
                    | (workerId << workerIdShift)
                    | sequence;
        }

        private long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        private long timeGen() {
            return System.currentTimeMillis();
        }
    }
}
