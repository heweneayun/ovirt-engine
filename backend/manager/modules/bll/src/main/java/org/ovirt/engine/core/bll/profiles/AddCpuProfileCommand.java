package org.ovirt.engine.core.bll.profiles;

import java.util.Collections;
import java.util.List;

import org.ovirt.engine.core.bll.context.CommandContext;
import org.ovirt.engine.core.bll.utils.PermissionSubject;
import org.ovirt.engine.core.common.AuditLogType;
import org.ovirt.engine.core.common.VdcObjectType;
import org.ovirt.engine.core.common.action.CpuProfileParameters;
import org.ovirt.engine.core.common.businessentities.profiles.CpuProfile;
import org.ovirt.engine.core.common.errors.EngineMessage;
import org.ovirt.engine.core.dao.profiles.ProfilesDao;

public class AddCpuProfileCommand extends AddProfileCommandBase<CpuProfileParameters, CpuProfile, CpuProfileValidator> {

    public AddCpuProfileCommand(CpuProfileParameters parameters, CommandContext cmdContext) {
        super(parameters, cmdContext);
    }

    @Override
    protected CpuProfileValidator getProfileValidator() {
        return new CpuProfileValidator(getProfile());
    }

    @Override
    protected ProfilesDao<CpuProfile> getProfileDao() {
        return getCpuProfileDao();
    }

    @Override
    public List<PermissionSubject> getPermissionCheckSubjects() {
        return Collections.singletonList(new PermissionSubject(getParameters().getProfile() != null ? getParameters().getProfile()
                .getClusterId()
                : null,
                VdcObjectType.Cluster,
                getActionType().getActionGroup()));
    }

    @Override
    protected void setActionMessageParameters() {
        super.setActionMessageParameters();
        addValidationMessage(EngineMessage.VAR__TYPE__CPU_PROFILE);
    }

    @Override
    public AuditLogType getAuditLogTypeValue() {
        return getSucceeded() ? AuditLogType.USER_ADDED_CPU_PROFILE : AuditLogType.USER_FAILED_TO_ADD_CPU_PROFILE;
    }
}
