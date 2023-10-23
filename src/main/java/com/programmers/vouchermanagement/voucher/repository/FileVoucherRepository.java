package com.programmers.vouchermanagement.voucher.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.vouchermanagement.properties.AppProperties;
import com.programmers.vouchermanagement.voucher.domain.FixedAmountVoucher;
import com.programmers.vouchermanagement.voucher.domain.PercentVoucher;
import com.programmers.vouchermanagement.voucher.domain.Voucher;
import com.programmers.vouchermanagement.voucher.domain.VoucherType;
import com.programmers.vouchermanagement.voucher.dto.GeneralVoucherDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Repository
@Profile("prod")
public class FileVoucherRepository implements VoucherRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String INVALID_VOUCHER_TYPE_MESSAGE = "Voucher type should be either fixed amount or percent discount voucher.";

    private final String filePath;
    private final Map<UUID, Voucher> vouchers;

    public FileVoucherRepository(AppProperties appProperties) {
        this.filePath = appProperties.getResources().getPath() + appProperties.getDomains().get("voucher.file-name");
        System.out.println(filePath);
        this.vouchers = new HashMap<>();
        loadFile();
    }

    @Override
    public void save(Voucher voucher) {
        vouchers.put(voucher.getVoucherId(), voucher);
        saveFile();
    }

    @Override
    public List<Voucher> findAll() {
        return vouchers.values().stream().toList();
    }

    private void loadFile() {
        try {
            File file = new File(filePath);
            Map[] voucherObjects = objectMapper.readValue(file, Map[].class);
            loadVouchers(voucherObjects);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadVouchers(Map[] voucherObjects) {
        Arrays.stream(voucherObjects).forEach(voucherObject -> {
            Voucher voucher = objectToVoucher(voucherObject);
            vouchers.put(voucher.getVoucherId(), voucher);
        });
    }

    private Voucher objectToVoucher(Map voucherObject) {
        UUID voucherId = UUID.fromString((String) voucherObject.get("voucher_id"));
        long discountValue = Long.parseLong(String.valueOf(voucherObject.get("discount_value")));
        String voucherTypeName = (String) voucherObject.get("voucher_type");
        VoucherType voucherType = VoucherType.findCreateMenu(voucherTypeName)
                .orElseThrow(() -> new NoSuchElementException(INVALID_VOUCHER_TYPE_MESSAGE));
        return switch (voucherType) {
            case FIXED -> new FixedAmountVoucher(voucherId, discountValue);
            case PERCENT -> new PercentVoucher(voucherId, discountValue);
        };
    }

    public void saveFile() {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            List<HashMap<String, Object>> voucherObjects = new ArrayList<>();
            if (!vouchers.isEmpty()) {
                vouchers.values().forEach(voucher -> {
                    HashMap<String, Object> voucherObject = voucherToObject(voucher.toVoucherDTO());
                    voucherObjects.add(voucherObject);
                });
            }
            String jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(voucherObjects);
            fileWriter.write(jsonStr);
            fileWriter.flush();
        } catch (Exception e) {
            throw new RuntimeException("File Exception");
        }
    }

    private HashMap<String, Object> voucherToObject(GeneralVoucherDTO generalVoucherDTO) {
        HashMap<String, Object> voucherObject = new HashMap<>();
        voucherObject.put("voucher_id", generalVoucherDTO.getVoucherId().toString());
        voucherObject.put("discount_value", generalVoucherDTO.getDiscountValue());
        voucherObject.put("voucher_type", generalVoucherDTO.getVoucherType());
        return voucherObject;
    }
}