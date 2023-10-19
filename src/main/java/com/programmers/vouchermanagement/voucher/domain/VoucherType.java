package com.programmers.vouchermanagement.voucher.domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum VoucherType {
    FIXED("Fixed Amount"),
    PERCENT("Percent");

    private final String typeName;

    VoucherType(String typeName) {
        this.typeName = typeName;
    }

    //set static to tell that this method does not depend on a particular Menu value
    public static Optional<VoucherType> findCreateMenu(String input) {
        return Arrays.stream(VoucherType.values())
                .filter(menu -> menu.isMatching(input))
                .findFirst();
    }

    private boolean isMatching(String input) {
        return Objects.equals(this.name().toLowerCase(), input);
    }

    public String displayTypeName() {
        return typeName;
    }
}