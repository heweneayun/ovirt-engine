package org.ovirt.engine.core.bll;

import org.ovirt.engine.core.common.action.MigrateVmToServerParameters;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.errors.VdcBllMessages;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;

@LockIdNameAttribute(isReleaseAtEndOfExecute = false)
public class MigrateVmToServerCommand<T extends MigrateVmToServerParameters> extends MigrateVmCommand<T> {
    public MigrateVmToServerCommand(T parameters) {
        super(parameters);
        setVdsDestinationId(parameters.getVdsId());
    }

    @Override
    protected boolean canDoAction() {
        VM vm = getVm();
        Guid destinationId = getVdsDestinationId();
        VDS vds = DbFacade.getInstance().getVdsDao().get(destinationId);
        if (vds == null) {
            return failCanDoAction(VdcBllMessages.VDS_INVALID_SERVER_ID);
        }

        if (vm.getRunOnVds() != null && vm.getRunOnVds().equals(destinationId)) {
            return failCanDoAction(VdcBllMessages.ACTION_TYPE_FAILED_MIGRATION_TO_SAME_HOST);
        }

        if (!vm.getVdsGroupId().equals(getDestinationVds().getVdsGroupId())) {
            return failCanDoAction(VdcBllMessages.ACTION_TYPE_FAILED_MIGRATE_BETWEEN_TWO_CLUSTERS);
        }

        return super.canDoAction();
    }

    /**
     * In case we failed to migrate to that specific server, the VM should no longer be pending,
     * and we report failure, without an attempt to rerun
     */
    @Override
    public void rerun() {
        decreasePendingVms(getDestinationVds().getId());
        _isRerun = false;
        setSucceeded(false);

        determineMigrationFailureForAuditLog();
        log();
    }
}
