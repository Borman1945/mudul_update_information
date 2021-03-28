package com.bortman_corparation.demo.component;

import com.bortman_corparation.demo.service.ExchangeRateSetService;
import com.bortman_corparation.demo.service.RedisPostgresSynchronization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@EnableScheduling
public class ConnectMonitoring {

    private boolean isConnect;
    private  final RedisPostgresSynchronization redisPostgresSynchronization;

    @Autowired
    DataSource dataSource;

    public ConnectMonitoring(RedisPostgresSynchronization redisPostgresSynchronization) {
        this.redisPostgresSynchronization = redisPostgresSynchronization;
    }


    @Scheduled(fixedRate = 60000)
    public void checkConnectBD() {

        try (Connection connection = dataSource.getConnection()) {
            isConnect = true;
            redisPostgresSynchronization.datSynchronization();
        } catch (SQLException e) {
            isConnect = false;
        } finally {
            System.out.println("Have connection DB: " + isConnect);
        }

    }

    public boolean isConnect() {
        return isConnect;
    }
}
