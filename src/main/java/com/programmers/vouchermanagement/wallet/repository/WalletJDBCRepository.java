package com.programmers.vouchermanagement.wallet.repository;

import com.programmers.vouchermanagement.customer.domain.Customer;
import com.programmers.vouchermanagement.util.DomainMapper;
import com.programmers.vouchermanagement.voucher.domain.Voucher;
import com.programmers.vouchermanagement.wallet.domain.Ownership;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.programmers.vouchermanagement.util.Constant.UPDATE_ONE_FLAG;
import static com.programmers.vouchermanagement.util.Constant.UPDATE_ZERO_FLAG;
import static com.programmers.vouchermanagement.util.Message.*;
import static com.programmers.vouchermanagement.wallet.repository.WalletQuery.*;

@Repository
public class WalletJDBCRepository implements WalletRepository {
    private static final Logger logger = LoggerFactory.getLogger(WalletJDBCRepository.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WalletJDBCRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(Ownership ownership) {
        int update = jdbcTemplate.update(INSERT, DomainMapper.ownershipToParamMap(ownership));
        if (update != UPDATE_ONE_FLAG) {
            logger.error(CAN_NOT_INSERT_OWNERSHIP);
            throw new EmptyResultDataAccessException(UPDATE_ONE_FLAG);
        }
    }

    @Override
    public Optional<Customer> findCustomerByVoucherId(UUID voucherId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_CUSTOMER_BY_VOUCHER_ID,
                    Collections.singletonMap(DomainMapper.ID_KEY, voucherId.toString().getBytes()),
                    DomainMapper.customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Voucher> findAllVoucherByCustomerId(UUID customerId) {
        return jdbcTemplate.query(FIND_ALL_VOUCHER_BY_CUSTOMER_ID,
                Collections.singletonMap(DomainMapper.ID_KEY, customerId.toString().getBytes()),
                DomainMapper.voucherRowMapper);
    }

    @Override
    public void delete(UUID voucherId) {
        int update = jdbcTemplate.update(DELETE_OWNERSHIP, DomainMapper.uuidToParamMap(voucherId));
        if (update != UPDATE_ONE_FLAG) {
            logger.error(NOT_FOUND_VOUCHER_ALLOCATION);
            throw new NoSuchElementException(NOT_FOUND_VOUCHER_ALLOCATION);
        }
    }

    @Override
    public void deleteAll() {
        int update = jdbcTemplate.update(DELETE_ALL, Collections.emptyMap());
        if (update == UPDATE_ZERO_FLAG) {
            logger.warn(ALREADY_EMPTY_TABLE);
        }
    }
}
