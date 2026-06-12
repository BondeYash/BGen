package com.banking.platform.ledger;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * The set of ledger legs for ONE financial event. Can only be constructed if the
 * legs obey double-entry: all same currency, every amount > 0, and
 * SUM(debits) == SUM(credits). An invalid posting cannot be built — so an
 * unbalanced set of entries can never reach the database.
 */
public final class LedgerPosting {

    /** One leg, before it becomes a row. accountRef = customer account id (as text) or "BANK_CASH". */
    public record Leg(String accountRef, EntryDirection direction, BigDecimal amount, String currency) {}

    private final List<Leg> legs;

    private LedgerPosting(List<Leg> legs) {
        this.legs = legs;
    }

    public static LedgerPosting balanced(List<Leg> legs) {
        if (legs == null || legs.size() < 2) {
            throw new UnbalancedLedgerException("A posting needs at least two legs.");
        }

        String currency = legs.get(0).currency();
        BigDecimal debits = BigDecimal.ZERO;
        BigDecimal credits = BigDecimal.ZERO;

        for (Leg leg : legs) {
            if (leg.amount() == null || leg.amount().signum() <= 0) {
                throw new UnbalancedLedgerException("Every leg amount must be > 0.");
            }
            if (!currency.equals(leg.currency())) {
                throw new UnbalancedLedgerException("All legs must share one currency.");
            }
            switch (leg.direction()) {
                case DEBIT  -> debits  = debits.add(leg.amount());
                case CREDIT -> credits = credits.add(leg.amount());
            }
        }

        if (debits.compareTo(credits) != 0) {
            throw new UnbalancedLedgerException(
                    "Debits " + debits + " != credits " + credits + " — posting does not net to zero.");
        }
        return new LedgerPosting(legs);
    }

    /** Turn the validated legs into persistable entities for a given event. */
    public List<LedgerEntry> toEntries(UUID tenantId, UUID transactionId) {
        return legs.stream()
                .map(l -> LedgerEntry.of(tenantId, transactionId, l.accountRef(), l.direction(), l.amount(), l.currency()))
                .toList();
    }
}
