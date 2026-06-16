package com.banking.platform.common;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(UUID tenantId) { CURRENT.set(tenantId); }

    public static UUID getTenantId() {
        UUID t = CURRENT.get();
        if (t == null) throw new IllegalStateException("No tenant in context (request not authenticated?)");
        return t;
    }

    public static void clear() { CURRENT.remove(); }
}
