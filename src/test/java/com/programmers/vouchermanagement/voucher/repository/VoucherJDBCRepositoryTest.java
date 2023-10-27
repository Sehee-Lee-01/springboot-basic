package com.programmers.vouchermanagement.voucher.repository;

import com.programmers.vouchermanagement.voucher.domain.Voucher;
import com.programmers.vouchermanagement.voucher.domain.VoucherType;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VoucherJDBCRepositoryTest {
    private final static UUID NON_EXISTENT_VOUCHER_ID = UUID.randomUUID();
    @Autowired
    VoucherJDBCRepository voucherJDBCRepository;

    @Test
    @DisplayName("🆗 고정 금액 할인 바우처를 추가할 수 있다.")
    void saveFixedAmountVoucher() {
        Voucher newVoucher = new Voucher(UUID.randomUUID(), 555, VoucherType.FIXED);
        voucherJDBCRepository.save(newVoucher);

        Optional<Voucher> retrievedVoucher = voucherJDBCRepository.findById(newVoucher.voucherId());

        assertThat(retrievedVoucher.isEmpty()).isFalse();
        assertThat(retrievedVoucher.get().voucherId()).isEqualTo(newVoucher.voucherId());
    }

    @Test
    @DisplayName("🆗 퍼센트 할인 바우처를 추가할 수 있다.")
    void savePercentVoucher() {
        Voucher newVoucher = new Voucher(UUID.randomUUID(), 50, VoucherType.PERCENT);
        voucherJDBCRepository.save(newVoucher);

        Optional<Voucher> retrievedVoucher = voucherJDBCRepository.findById(newVoucher.voucherId());

        assertThat(retrievedVoucher.isEmpty()).isFalse();
        assertThat(retrievedVoucher.get().voucherId()).isEqualTo(newVoucher.voucherId());
    }

    @Test
    @DisplayName("🆗 모든 바우처를 조회할 수 있다. 단, 없다면 빈 list를 반환한다.")
    void findAllVoucher() {
        for (int i = 1; i < 6; i++)
            voucherJDBCRepository.save(new Voucher(UUID.randomUUID(), i * 100, VoucherType.PERCENT));

        List<Voucher> vouchers = voucherJDBCRepository.findAll();

        assertThat(vouchers.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("🆗 바우처를 아이디로 조회할 수 있다.")
    void findVoucherById() {
        Voucher voucher = new Voucher(UUID.randomUUID(), 1234, VoucherType.FIXED);
        voucherJDBCRepository.save(voucher);

        Optional<Voucher> retrievedVoucher = voucherJDBCRepository.findById(voucher.voucherId());

        assertThat(retrievedVoucher.isPresent()).isTrue();
        assertThat(retrievedVoucher.get().voucherId()).isEqualTo(voucher.voucherId());
        assertThat(retrievedVoucher.get().discountValue()).isEqualTo(voucher.discountValue());
        assertThat(retrievedVoucher.get().voucherType()).isEqualTo(voucher.voucherType());
    }

    @Test
    @DisplayName("🚨 해당하는 바우처가 없다면, 바우처를 아이디로 조회할 수 없다.")
    void findNonExistentVoucherById() {
        Optional<Voucher> retrievedVoucher = voucherJDBCRepository.findById(NON_EXISTENT_VOUCHER_ID);

        assertThat(retrievedVoucher.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("🆗 바우처를 아이디로 삭제할 수 있다.")
    void deleteVoucher() {
        Voucher voucher = new Voucher(UUID.randomUUID(), 5555, VoucherType.FIXED);
        voucherJDBCRepository.save(voucher);

        voucherJDBCRepository.delete(voucher.voucherId());

        assertThat(voucherJDBCRepository.findById(voucher.voucherId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("🚨 해당하는 바우처가 없다면, 바우처를 아이디로 삭제할 수 없다.")
    void deleteNonExistentVoucher() {
        assertThrows(RuntimeException.class, () -> voucherJDBCRepository.delete(NON_EXISTENT_VOUCHER_ID));
    }

    @Test
    @DisplayName("🆗 바우처를 업데이트 할 수 있다.")
    void updateVoucher() {
        Voucher voucher = new Voucher(UUID.randomUUID(), 5555, VoucherType.FIXED);
        voucherJDBCRepository.save(voucher);

        Voucher updatedVoucher = new Voucher(voucher.voucherId(), 100, VoucherType.PERCENT);
        voucherJDBCRepository.update(updatedVoucher);

        Optional<Voucher> retrievedVoucher = voucherJDBCRepository.findById(voucher.voucherId());
        assertThat(retrievedVoucher.isEmpty()).isFalse();
        assertThat(retrievedVoucher.get().discountValue()).isEqualTo(updatedVoucher.discountValue());
        assertThat(retrievedVoucher.get().voucherType()).isEqualTo(updatedVoucher.voucherType());
    }

    @Test
    @DisplayName("🚨 해당하는 바우처가 없다면, 바우처를 업데이트 할 수 없다.")
    void updateNonExistentVoucher() {
        assertThrows(RuntimeException.class, () -> voucherJDBCRepository.update(new Voucher(NON_EXISTENT_VOUCHER_ID, 100, VoucherType.PERCENT)));
    }

    @Configuration
    @ComponentScan(
            basePackages = {"com.programmers.vouchermanagement"}
    )
    static class Config {
        @Bean
        public DataSource dataSource() {
            var dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:3306/test")
                    .username("root")
                    .password("980726")
                    .type(HikariDataSource.class)
                    .build();
            dataSource.setMaximumPoolSize(1000);
            dataSource.setMinimumIdle(100);
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }
    }
}