package com.laurefindel.finance;

import com.laurefindel.finance.mapper.AccountMapper;
import com.laurefindel.finance.mapper.CurrencyMapper;
import com.laurefindel.finance.mapper.FinancialOperationMapper;
import com.laurefindel.finance.mapper.RoleMapper;
import com.laurefindel.finance.mapper.UserMapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@SpringBootTest
class FinanceApplicationTests {

    @TestConfiguration
    static class TestMappersConfig {

        @Bean
        @Primary
        AccountMapper accountMapper() {
            return mock(AccountMapper.class);
        }

        @Bean
        @Primary
        CurrencyMapper currencyMapper() {
            return mock(CurrencyMapper.class);
        }

        @Bean
        @Primary
        FinancialOperationMapper financialOperationMapper() {
            return mock(FinancialOperationMapper.class);
        }

        @Bean
        @Primary
        RoleMapper roleMapper() {
            return mock(RoleMapper.class);
        }

        @Bean
        @Primary
        UserMapper userMapper() {
            return mock(UserMapper.class);
        }
    }

    @Test
    void contextLoads() {

    }

}
