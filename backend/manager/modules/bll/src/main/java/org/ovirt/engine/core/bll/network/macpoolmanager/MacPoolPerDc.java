package org.ovirt.engine.core.bll.network.macpoolmanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.commons.lang.math.LongRange;
import org.ovirt.engine.core.common.businessentities.MacPool;
import org.ovirt.engine.core.common.businessentities.MacRange;
import org.ovirt.engine.core.common.businessentities.StoragePool;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;
import org.ovirt.engine.core.dao.MacPoolDao;
import org.ovirt.engine.core.utils.DisjointRanges;
import org.ovirt.engine.core.utils.MacAddressRangeUtils;
import org.ovirt.engine.core.utils.lock.AutoCloseableLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class MacPoolPerDc {

    @Inject
    private MacPoolDao macPoolDao;

    static final String UNABLE_TO_CREATE_MAC_POOL_IT_ALREADY_EXIST = "This MAC Pool already exist";
    static final String INEXISTENT_POOL_EXCEPTION_MESSAGE = "Coding error, pool for requested GUID does not exist";
    static final String POOL_TO_BE_REMOVED_DOES_NOT_EXIST_EXCEPTION_MESSAGE =
            "Trying to removed pool which does not exist.";
    private static final Logger log = LoggerFactory.getLogger(MacPoolPerDc.class);
    private final Map<Guid, MacPoolManagerStrategy> macPools = new HashMap<>();
    private final ReentrantReadWriteLock lockObj = new ReentrantReadWriteLock();

    public MacPoolPerDc() {}

    MacPoolPerDc(MacPoolDao macPoolDao) {
        this.macPoolDao = macPoolDao;
    }

    @PostConstruct
    void initialize() {
        try {
            List<MacPool> macPools = macPoolDao.getAll();
            for (MacPool macPool : macPools) {
                initializeMacPool(macPool);
            }
            log.info("Successfully initialized");
        } catch (RuntimeException e) {
            log.error("Error initializing: {}", e.getMessage());
            throw e;
        }
    }

    private void initializeMacPool(MacPool macPool) {
        List<String> macsForMacPool = macPoolDao.getAllMacsForMacPool(macPool.getId());

        final MacPoolManagerStrategy pool = createPoolWithoutLocking(macPool);
        for (String mac : macsForMacPool) {
            pool.forceAddMac(mac);
        }
    }

    public MacPoolManagerStrategy poolForDataCenter(Guid dataCenterId) {
        try (AutoCloseableLock lock = readLockResource()) {
            return getPoolWithoutLocking(getMacPoolId(dataCenterId));
        }
    }

    public MacPoolManagerStrategy getPoolById(Guid macPoolId) {
        try (AutoCloseableLock lock = readLockResource()) {
            return getPoolWithoutLocking(macPoolId);
        }
    }

    private Guid getMacPoolId(Guid dataCenterId) {
        final StoragePool storagePool = DbFacade.getInstance().getStoragePoolDao().get(dataCenterId);
        return storagePool == null ? null : storagePool.getMacPoolId();
    }

    private MacPoolManagerStrategy getPoolWithoutLocking(Guid macPoolId) {
        final MacPoolManagerStrategy result = macPools.get(macPoolId);

        if (result == null) {
            throw new IllegalStateException(INEXISTENT_POOL_EXCEPTION_MESSAGE);
        }
        return result;
    }

    /**
     * @param macPool pool definition
     */
    public void createPool(MacPool macPool) {
        try (AutoCloseableLock lock = writeLockResource()) {
            createPoolWithoutLocking(macPool);
        }
    }

    private MacPoolManagerStrategy createPoolWithoutLocking(MacPool macPool) {
        if (macPools.containsKey(macPool.getId())) {
            throw new IllegalStateException(UNABLE_TO_CREATE_MAC_POOL_IT_ALREADY_EXIST);
        }

        MacPoolManagerStrategy poolForScope = new MacPoolManagerRanges(macPoolToRanges(macPool),
                macPool.isAllowDuplicateMacAddresses());
        poolForScope.initialize();
        macPools.put(macPool.getId(), poolForScope);
        return poolForScope;
    }

    /**
     * @param macPool pool definition to re-init the pool
     */
    public void modifyPool(MacPool macPool) {
        try (AutoCloseableLock lock = writeLockResource()) {
            if (!macPools.containsKey(macPool.getId())) {
                throw new IllegalStateException(INEXISTENT_POOL_EXCEPTION_MESSAGE);
            }

            removeWithoutLocking(macPool.getId());
            initializeMacPool(macPool);
        }
    }

    private Collection<LongRange> macPoolToRanges(MacPool macPool) {
        final DisjointRanges disjointRanges = new DisjointRanges();
        for (MacRange macRange : macPool.getRanges()) {
            disjointRanges.addRange(MacAddressRangeUtils.macToLong(macRange.getMacFrom()),
                    MacAddressRangeUtils.macToLong(macRange.getMacTo()));
        }
        return MacAddressRangeUtils.clipMultiCastsFromRanges(disjointRanges.getRanges());
    }

    public void removePool(Guid macPoolId) {
        try (AutoCloseableLock lock = writeLockResource()) {
            removeWithoutLocking(macPoolId);
        }
    }

    private void removeWithoutLocking(Guid macPoolId) {
        macPools.remove(macPoolId);
    }

    protected AutoCloseableLock writeLockResource() {
        return new AutoCloseableLock(lockObj.writeLock());
    }

    protected AutoCloseableLock readLockResource() {
        return new AutoCloseableLock(lockObj.readLock());
    }
}
