/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;
import org.wisdom.core.utxo.UTXOSets;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roman Mandeleil
 * @since 14.11.2014
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Start {
    private static final String CODE_ASSERTION_ENV = "ENABLE_CODE_ASSERTION";

    // 开启断言 用于调试
    public static final boolean ENABLE_ASSERTION = System.getenv(CODE_ASSERTION_ENV) != null && System.getenv(CODE_ASSERTION_ENV).equals("true");

    public static void main(String[] args) {
        // 关闭 grpc 日志
        SpringApplication.run(Start.class, args);
    }

    // wisdom chain configuration
    @Bean
    public JdbcTemplate getJDBCTemplate(BasicDataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager manager) {
        TransactionTemplate tmpl = new TransactionTemplate();
        tmpl.setTransactionManager(manager);
        return tmpl;
    }

    @Bean
    public UTXOSets getUTXOSets() {
        UTXOSets utxoSets = new UTXOSets();
        return utxoSets;
    }

    @Bean
    public Genesis genesis(JSONEncodeDecoder codec, @Value("${wisdom.consensus.genesis}") String genesis)
            throws Exception {
        Resource resource = new FileSystemResource(genesis);
        if (!resource.exists()){
            resource = new ClassPathResource(genesis);
        }
        return codec.decodeGenesis(IOUtils.toByteArray(resource.getInputStream()));
    }

}
