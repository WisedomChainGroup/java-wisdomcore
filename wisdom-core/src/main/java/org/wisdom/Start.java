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
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;
import org.wisdom.core.utxo.UTXOSets;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Roman Mandeleil
 * @since 14.11.2014
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Start {
    public static void main(String args[]) {
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
    public Genesis genesis(JSONEncodeDecoder codec) throws Exception {
        Resource resource = new ClassPathResource("genesis/wisdom-genesis-generator.json");
        return codec.decodeGenesis(IOUtils.toByteArray(resource.getInputStream()));
    }
}