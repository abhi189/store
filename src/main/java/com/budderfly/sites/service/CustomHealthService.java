package com.budderfly.sites.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@Component
public class CustomHealthService implements HealthIndicator {

    private final Logger log = LoggerFactory.getLogger(CustomHealthService.class);

    @Value("${spring.jpa.properties.hibernate.cache.hazelcast.instance_name}")
    private String HAZELCAST_INSTANCE_NAME;

    @Override
    public Health health() {

        int errorCode = 0;
        StringBuilder downServiceList = new StringBuilder();

        // test free memory
        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        if (runtime.freeMemory() == 0
            || mbean.getHeapMemoryUsage().getUsed() == mbean.getHeapMemoryUsage().getMax()) {
            log.error("no free memory available {0}/{1} (used/total).",
                ((runtime.totalMemory() - runtime.freeMemory()) / (1024*1024)),
                (runtime.totalMemory() / (1024*1024))
            );
            log.error(mbean.getHeapMemoryUsage().toString());
            downServiceList.append("OUT_OF_MEMORY, ");
            errorCode |= 1;
        }

        // test hazelcast
        HazelcastInstance hazelCastInstance = Hazelcast.getHazelcastInstanceByName(HAZELCAST_INSTANCE_NAME);
        if (hazelCastInstance == null || !hazelCastInstance.getLifecycleService().isRunning()) {
            log.error("Hazelcast is DOWN.");
            downServiceList.append("Hazelcast, ");
            errorCode |= 2;
        }

        if (errorCode != 0) {
            // delete the last comma (,)
            downServiceList.setLength(downServiceList.length() - 1);
            return Health.down().withDetail("internal_services_down", downServiceList.toString()).build();
        }

        return Health.up().build();
    }
}
