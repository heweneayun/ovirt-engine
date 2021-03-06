package org.ovirt.engine.core.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ovirt.engine.core.common.businessentities.StorageFormatType;
import org.ovirt.engine.core.common.businessentities.storage.StorageType;
import org.ovirt.engine.core.compat.Version;

/**
 * A utility function to match between {@link Version}s and {@link StorageFormatType}s
 */
public class VersionStorageFormatUtil {
    private interface StorageFormatTypeMapper {
        StorageFormatType getPreferred(StorageType t);
        StorageFormatType getRequired(StorageType t);
    }

    private static class ConstantStorageFormatTypeMapper implements StorageFormatTypeMapper {
        private StorageFormatType formatType;

        ConstantStorageFormatTypeMapper(StorageFormatType formatType) {
            this.formatType = formatType;
        }

        @Override
        public StorageFormatType getPreferred(StorageType t) {
            return formatType;
        }

        @Override
        public StorageFormatType getRequired(StorageType t) {
            return formatType;
        }
    }

    private static final Map<Version, StorageFormatTypeMapper> versionToFormat = new TreeMap<>();
    static {
        versionToFormat.put(Version.v3_5, new ConstantStorageFormatTypeMapper(StorageFormatType.V3));
        versionToFormat.put(Version.v3_6, new ConstantStorageFormatTypeMapper(StorageFormatType.V3));
        versionToFormat.put(Version.v4_0, new ConstantStorageFormatTypeMapper(StorageFormatType.V3));
    };

    private static final Map<StorageFormatType, Version> earliestVersionSupported = new TreeMap<>();
    static {
        // Since versionToFormat is sorted in ascending order of versions, we'll always put
        // the earliest version at the end, overriding the lower ones
        // This is in fact cheaper than iterating the other way and checking if the key already
        // exists in the map
        List<Map.Entry<Version, StorageFormatTypeMapper>> entries = new ArrayList<>(versionToFormat.entrySet());
        for (int i = entries.size() - 1; i >= 0; --i) {
            Map.Entry<Version, StorageFormatTypeMapper> entry = entries.get(i);
            // iSCSI is always the strictest storage type.
            // If this assumption is broken, the flow should be revisited
            earliestVersionSupported .put(entry.getValue().getRequired(StorageType.ISCSI), entry.getKey());
        }
    };

    public static StorageFormatType getPreferredForVersion(Version v, StorageType type) {
        return versionToFormat.get(v).getPreferred(type);
    }

    public static StorageFormatType getRequiredForVersion(Version v, StorageType type) {
        return versionToFormat.get(v).getRequired(type);
    }

    public static Version getEarliestVersionSupported (StorageFormatType type) {
        return earliestVersionSupported.get(type);
    }
}
