package com.banking.platform.customer;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerStateMachineTest {

    // small helper: a fresh customer always starts PENDING (factory forces it)
    private Customer newPendingCustomer() {
        return Customer.create(
                UUID.randomUUID(),          // tenantId
                "CUST-00000001",            // customerNumber
                CustomerType.INDIVIDUAL,    // type
                "Test User",                // fullName
                "test@example.com",         // email
                null,                       // phone
                LocalDate.of(1990, 1, 1),   // dateOfBirth
                null                        // registrationNo
        );
    }

    @Test
    void newCustomerStartsPending() {
        Customer c = newPendingCustomer();
        assertThat(c.getStatus()).isEqualTo(CustomerStatus.PENDING);
        assertThat(c.getKycStatus()).isEqualTo(KycStatus.NOT_VERIFIED);
    }

    @Test
    void pendingCanActivate() {
        Customer c = newPendingCustomer();
        c.activate();
        assertThat(c.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    void activeCanSuspendThenActivateAgain() {
        Customer c = newPendingCustomer();
        c.activate();
        c.suspend();
        assertThat(c.getStatus()).isEqualTo(CustomerStatus.SUSPENDED);
        c.activate();                                   // SUSPENDED -> ACTIVE allowed
        assertThat(c.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    void pendingCannotSuspend() {
        Customer c = newPendingCustomer();
        assertThatThrownBy(c::suspend)
                .isInstanceOf(InvalidCustomerStateException.class);
    }

    @Test
    void closedCannotActivate() {
        Customer c = newPendingCustomer();
        c.close();
        assertThat(c.getStatus()).isEqualTo(CustomerStatus.CLOSED);
        assertThatThrownBy(c::activate)
                .isInstanceOf(InvalidCustomerStateException.class);
    }

    @Test
    void closingTwiceIsRejected() {
        Customer c = newPendingCustomer();
        c.close();
        assertThatThrownBy(c::close)
                .isInstanceOf(InvalidCustomerStateException.class);
    }
}
