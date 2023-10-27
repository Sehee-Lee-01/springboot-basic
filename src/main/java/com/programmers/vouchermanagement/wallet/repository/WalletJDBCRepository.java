package com.programmers.vouchermanagement.wallet.repository;

import com.programmers.vouchermanagement.customer.domain.Customer;
import com.programmers.vouchermanagement.util.DomainMapper;
import com.programmers.vouchermanagement.voucher.domain.Voucher;
import com.programmers.vouchermanagement.wallet.WalletMessage;
import com.programmers.vouchermanagement.wallet.domain.Ownership;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletJDBCRepository implements WalletRepository {
    private static final Logger logger = LoggerFactory.getLogger(WalletJDBCRepository.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DomainMapper domainMapper;

    public WalletJDBCRepository(NamedParameterJdbcTemplate jdbcTemplate, DomainMapper domainMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.domainMapper = domainMapper;
    }

    @Override
    public void save(Ownership ownership) {
        int update = jdbcTemplate.update(WalletQuery.INSERT, domainMapper.ownershipToParamMap(ownership));
        if (update != 1) {
            logger.error(WalletMessage.CAN_NOT_INSERT_OWNERSHIP);
            throw new RuntimeException(WalletMessage.CAN_NOT_INSERT_OWNERSHIP);
        }
    }

    @Override
    public Optional<Customer> findCustomerByVoucherId(UUID voucherId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(WalletQuery.FIND_CUSTOMER_BY_VOUCHER_ID,
                    Collections.singletonMap(DomainMapper.ID_KEY, voucherId.toString().getBytes()),
                    domainMapper.customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error(WalletMessage.NOT_FOUND_VOUCHER_ALLOCATION_INFORMATION, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Voucher> findAllVoucherByCustomerId(UUID customerId) {
        List<Voucher> vouchers = jdbcTemplate.query(WalletQuery.FIND_ALL_VOUCHER_BY_CUSTOMER_ID,
                Collections.singletonMap(DomainMapper.ID_KEY, customerId.toString().getBytes()),
                domainMapper.voucherRowMapper);
        if (vouchers.isEmpty())
            logger.warn(WalletMessage.NOT_FOUND_CUSTOMER_ALLOCATION_INFORMATION);
        return vouchers;
    }

    @Override
    public void delete(UUID voucherId) {
        int update = jdbcTemplate.update(WalletQuery.DELETE_OWNERSHIP, domainMapper.uuidToParamMap(voucherId));
        if (update != 1) {
            logger.error(WalletMessage.NOT_FOUND_VOUCHER_ALLOCATION_INFORMATION);
            throw new RuntimeException(WalletMessage.NOT_FOUND_VOUCHER_ALLOCATION_INFORMATION);
        }
    }

    @Override
    public void deleteAll() {
        int update = jdbcTemplate.update(WalletQuery.DELETE_ALL, Collections.emptyMap());
        if (update == 0) {
            logger.warn(WalletMessage.ALREADY_EMPTY_TABLE);
        }
    }
}
