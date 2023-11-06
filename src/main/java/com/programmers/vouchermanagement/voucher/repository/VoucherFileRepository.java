package com.programmers.vouchermanagement.voucher.repository;

import com.programmers.vouchermanagement.voucher.domain.Voucher;
import com.programmers.vouchermanagement.voucher.domain.vouchertype.VoucherType;
import com.programmers.vouchermanagement.voucher.repository.util.VoucherFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.programmers.vouchermanagement.util.Message.NOT_DELETED;
import static com.programmers.vouchermanagement.util.Message.NOT_UPDATED;

@Repository
@Profile("file")
public class VoucherFileRepository implements VoucherRepository {
    private static final Logger logger = LoggerFactory.getLogger(VoucherFileRepository.class);

    private final VoucherFileManager voucherFileManager;

    public VoucherFileRepository(VoucherFileManager voucherFileManager) {
        this.voucherFileManager = voucherFileManager;
    }

    @Override
    public void insert(Voucher voucher) {
        voucherFileManager.vouchers.put(voucher.getId(), voucher);
        voucherFileManager.saveFile();
    }

    @Override
    public List<Voucher> findAll() {
        return voucherFileManager.vouchers.values().stream().toList();
    }

    @Override
    public Optional<Voucher> findById(UUID id) {
        return Optional.ofNullable(voucherFileManager.vouchers.get(id));
    }

    @Override
    public List<Voucher> findAllByCreatedAt(LocalDateTime from, LocalDateTime to) {
        return voucherFileManager.vouchers.values().stream().filter(voucher -> {
            LocalDateTime createdAt = voucher.getCreatedAt();
            return createdAt.isAfter(from) && createdAt.isBefore(to);
        }).toList();
    }

    @Override
    public void delete(UUID id) {
        Optional.ofNullable(voucherFileManager.vouchers.remove(id)).orElseThrow(() -> new NoSuchElementException(NOT_DELETED));
        voucherFileManager.saveFile();
    }

    @Override
    public void deleteAll() {
        if (!voucherFileManager.vouchers.isEmpty()) voucherFileManager.vouchers.clear();
        voucherFileManager.saveFile();
    }

    @Override
    public void update(Voucher voucher) {
        Optional.ofNullable(voucherFileManager.vouchers.get(voucher.getId())).orElseThrow(() -> new NoSuchElementException(NOT_UPDATED));
        voucherFileManager.vouchers.put(voucher.getId(), voucher);
        voucherFileManager.saveFile();
    }

    @Override
    public List<Voucher> findAllByType(VoucherType voucherType) {
        return voucherFileManager.vouchers.values().stream().filter(voucher -> voucher.getTypeName().equals(voucherType.getName())).toList();
    }
}
