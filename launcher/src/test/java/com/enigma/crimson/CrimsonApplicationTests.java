package com.enigma.crimson;

import com.enigma.crimson.transactions.infrastructure.persistence.AccountRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.CustomerRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.TransactionRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class CrimsonApplicationTests {

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private TransactionRecordRepository transactionRecordRepository;

    @Test
    void contextLoads() {
    }
}
