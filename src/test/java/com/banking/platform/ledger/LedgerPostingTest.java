package com.banking.platform.ledger;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LedgerPostingTest {

    private static final BigDecimal HUNDRED = new BigDecimal("100.0000");

    @Test
    void balancedTwoLegPostingBuilds() {
        var posting = LedgerPosting.balanced(List.of(
                new LedgerPosting.Leg("BANK_CASH", EntryDirection.DEBIT,  HUNDRED, "INR"),
                new LedgerPosting.Leg("acct-1",    EntryDirection.CREDIT, HUNDRED, "INR")
        ));
        assertNotNull(posting);
    }

    @Test
    void scaleDifferenceStillBalances() {
        // 100 (scale 0) vs 100.0000 (scale 4) must be treated equal — compareTo, not equals.
        assertDoesNotThrow(() -> LedgerPosting.balanced(List.of(
                new LedgerPosting.Leg("BANK_CASH", EntryDirection.DEBIT,  new BigDecimal("100"),      "INR"),
                new LedgerPosting.Leg("acct-1",    EntryDirection.CREDIT, new BigDecimal("100.0000"), "INR")
        )));
    }

    @Test
    void unbalancedPostingThrows() {
        var ex = assertThrows(UnbalancedLedgerException.class, () -> LedgerPosting.balanced(List.of(
                new LedgerPosting.Leg("BANK_CASH", EntryDirection.DEBIT,  HUNDRED,                  "INR"),
                new LedgerPosting.Leg("acct-1",    EntryDirection.CREDIT, new BigDecimal("90.0000"), "INR")
        )));
        assertTrue(ex.getMessage().contains("net to zero"));
    }

    @Test
    void mixedCurrencyThrows() {
        assertThrows(UnbalancedLedgerException.class, () -> LedgerPosting.balanced(List.of(
                new LedgerPosting.Leg("BANK_CASH", EntryDirection.DEBIT,  HUNDRED, "INR"),
                new LedgerPosting.Leg("acct-1",    EntryDirection.CREDIT, HUNDRED, "USD")
        )));
    }

    @Test
    void nonPositiveLegThrows() {
        assertThrows(UnbalancedLedgerException.class, () -> LedgerPosting.balanced(List.of(
                new LedgerPosting.Leg("BANK_CASH", EntryDirection.DEBIT,  BigDecimal.ZERO, "INR"),
                new LedgerPosting.Leg("acct-1",    EntryDirection.CREDIT, BigDecimal.ZERO, "INR")
        )));
    }

    @Test
    void singleLegThrows() {
        assertThrows(UnbalancedLedgerException.class, () -> LedgerPosting.balanced(List.of(
                new LedgerPosting.Leg("acct-1", EntryDirection.CREDIT, HUNDRED, "INR")
        )));
    }
}
