/*
* Copyright (c) 2010 Red Hat, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*           http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.ovirt.engine.api.restapi.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.ws.rs.Path;

import org.ovirt.engine.api.model.ActionableResource;
import org.ovirt.engine.api.model.AffinityGroup;
import org.ovirt.engine.api.model.Agent;
import org.ovirt.engine.api.model.Application;
import org.ovirt.engine.api.model.Balance;
import org.ovirt.engine.api.model.BaseResource;
import org.ovirt.engine.api.model.Bookmark;
import org.ovirt.engine.api.model.Cdrom;
import org.ovirt.engine.api.model.Cluster;
import org.ovirt.engine.api.model.CpuProfile;
import org.ovirt.engine.api.model.DataCenter;
import org.ovirt.engine.api.model.Disk;
import org.ovirt.engine.api.model.DiskProfile;
import org.ovirt.engine.api.model.DiskSnapshot;
import org.ovirt.engine.api.model.Domain;
import org.ovirt.engine.api.model.Event;
import org.ovirt.engine.api.model.ExternalComputeResource;
import org.ovirt.engine.api.model.ExternalDiscoveredHost;
import org.ovirt.engine.api.model.ExternalHost;
import org.ovirt.engine.api.model.ExternalHostGroup;
import org.ovirt.engine.api.model.ExternalHostProvider;
import org.ovirt.engine.api.model.File;
import org.ovirt.engine.api.model.Filter;
import org.ovirt.engine.api.model.GlusterBrick;
import org.ovirt.engine.api.model.GlusterHook;
import org.ovirt.engine.api.model.GlusterVolume;
import org.ovirt.engine.api.model.GlusterVolumeProfileDetails;
import org.ovirt.engine.api.model.GraphicsConsole;
import org.ovirt.engine.api.model.Group;
import org.ovirt.engine.api.model.Hook;
import org.ovirt.engine.api.model.Host;
import org.ovirt.engine.api.model.HostDevice;
import org.ovirt.engine.api.model.HostDevices;
import org.ovirt.engine.api.model.HostNic;
import org.ovirt.engine.api.model.HostStorage;
import org.ovirt.engine.api.model.Icon;
import org.ovirt.engine.api.model.Image;
import org.ovirt.engine.api.model.InstanceType;
import org.ovirt.engine.api.model.IscsiBond;
import org.ovirt.engine.api.model.Job;
import org.ovirt.engine.api.model.KatelloErratum;
import org.ovirt.engine.api.model.Label;
import org.ovirt.engine.api.model.MacPool;
import org.ovirt.engine.api.model.Network;
import org.ovirt.engine.api.model.NetworkAttachment;
import org.ovirt.engine.api.model.Nic;
import org.ovirt.engine.api.model.NumaNode;
import org.ovirt.engine.api.model.OpenStackImage;
import org.ovirt.engine.api.model.OpenStackImageProvider;
import org.ovirt.engine.api.model.OpenStackNetwork;
import org.ovirt.engine.api.model.OpenStackNetworkProvider;
import org.ovirt.engine.api.model.OpenStackSubnet;
import org.ovirt.engine.api.model.OpenStackVolumeProvider;
import org.ovirt.engine.api.model.OpenStackVolumeType;
import org.ovirt.engine.api.model.OpenstackVolumeAuthenticationKey;
import org.ovirt.engine.api.model.OperatingSystemInfo;
import org.ovirt.engine.api.model.Permission;
import org.ovirt.engine.api.model.Permit;
import org.ovirt.engine.api.model.Qos;
import org.ovirt.engine.api.model.Quota;
import org.ovirt.engine.api.model.QuotaClusterLimit;
import org.ovirt.engine.api.model.QuotaStorageLimit;
import org.ovirt.engine.api.model.ReportedDevice;
import org.ovirt.engine.api.model.Role;
import org.ovirt.engine.api.model.SchedulingPolicy;
import org.ovirt.engine.api.model.SchedulingPolicyUnit;
import org.ovirt.engine.api.model.Session;
import org.ovirt.engine.api.model.Snapshot;
import org.ovirt.engine.api.model.Statistic;
import org.ovirt.engine.api.model.Step;
import org.ovirt.engine.api.model.StorageConnection;
import org.ovirt.engine.api.model.StorageConnectionExtension;
import org.ovirt.engine.api.model.StorageDomain;
import org.ovirt.engine.api.model.Tag;
import org.ovirt.engine.api.model.Template;
import org.ovirt.engine.api.model.UnmanagedNetwork;
import org.ovirt.engine.api.model.User;
import org.ovirt.engine.api.model.VersionCaps;
import org.ovirt.engine.api.model.VirtualNumaNode;
import org.ovirt.engine.api.model.Vm;
import org.ovirt.engine.api.model.VmPool;
import org.ovirt.engine.api.model.VnicProfile;
import org.ovirt.engine.api.model.Watchdog;
import org.ovirt.engine.api.model.Weight;
import org.ovirt.engine.api.resource.AffinityGroupResource;
import org.ovirt.engine.api.resource.AffinityGroupsResource;
import org.ovirt.engine.api.resource.AssignedNetworkResource;
import org.ovirt.engine.api.resource.AssignedNetworksResource;
import org.ovirt.engine.api.resource.AssignedPermissionsResource;
import org.ovirt.engine.api.resource.AssignedRolesResource;
import org.ovirt.engine.api.resource.AssignedTagResource;
import org.ovirt.engine.api.resource.AssignedTagsResource;
import org.ovirt.engine.api.resource.AttachedStorageDomainResource;
import org.ovirt.engine.api.resource.AttachedStorageDomainsResource;
import org.ovirt.engine.api.resource.BalanceResource;
import org.ovirt.engine.api.resource.BalancesResource;
import org.ovirt.engine.api.resource.BookmarkResource;
import org.ovirt.engine.api.resource.BookmarksResource;
import org.ovirt.engine.api.resource.CapabilitiesResource;
import org.ovirt.engine.api.resource.CapabiliyResource;
import org.ovirt.engine.api.resource.ClusterResource;
import org.ovirt.engine.api.resource.ClustersResource;
import org.ovirt.engine.api.resource.CpuProfileResource;
import org.ovirt.engine.api.resource.CpuProfilesResource;
import org.ovirt.engine.api.resource.DataCenterResource;
import org.ovirt.engine.api.resource.DataCentersResource;
import org.ovirt.engine.api.resource.DiskProfileResource;
import org.ovirt.engine.api.resource.DiskProfilesResource;
import org.ovirt.engine.api.resource.DiskResource;
import org.ovirt.engine.api.resource.DiskSnapshotResource;
import org.ovirt.engine.api.resource.DiskSnapshotsResource;
import org.ovirt.engine.api.resource.DisksResource;
import org.ovirt.engine.api.resource.EventResource;
import org.ovirt.engine.api.resource.EventsResource;
import org.ovirt.engine.api.resource.FenceAgentResource;
import org.ovirt.engine.api.resource.FenceAgentsResource;
import org.ovirt.engine.api.resource.FileResource;
import org.ovirt.engine.api.resource.FilesResource;
import org.ovirt.engine.api.resource.FilterResource;
import org.ovirt.engine.api.resource.FiltersResource;
import org.ovirt.engine.api.resource.GraphicsConsoleResource;
import org.ovirt.engine.api.resource.GraphicsConsolesResource;
import org.ovirt.engine.api.resource.HostDeviceResource;
import org.ovirt.engine.api.resource.HostDevicesResource;
import org.ovirt.engine.api.resource.HostHookResource;
import org.ovirt.engine.api.resource.HostHooksResource;
import org.ovirt.engine.api.resource.HostNicResource;
import org.ovirt.engine.api.resource.HostNicsResource;
import org.ovirt.engine.api.resource.HostNumaNodeResource;
import org.ovirt.engine.api.resource.HostNumaNodesResource;
import org.ovirt.engine.api.resource.HostStorageResource;
import org.ovirt.engine.api.resource.IconResource;
import org.ovirt.engine.api.resource.IconsResource;
import org.ovirt.engine.api.resource.ImageResource;
import org.ovirt.engine.api.resource.ImagesResource;
import org.ovirt.engine.api.resource.InstanceTypeNicResource;
import org.ovirt.engine.api.resource.InstanceTypeNicsResource;
import org.ovirt.engine.api.resource.InstanceTypeResource;
import org.ovirt.engine.api.resource.InstanceTypeWatchdogResource;
import org.ovirt.engine.api.resource.InstanceTypeWatchdogsResource;
import org.ovirt.engine.api.resource.InstanceTypesResource;
import org.ovirt.engine.api.resource.IscsiBondResource;
import org.ovirt.engine.api.resource.IscsiBondsResource;
import org.ovirt.engine.api.resource.JobResource;
import org.ovirt.engine.api.resource.JobsResource;
import org.ovirt.engine.api.resource.LabelResource;
import org.ovirt.engine.api.resource.LabelsResource;
import org.ovirt.engine.api.resource.MacPoolResource;
import org.ovirt.engine.api.resource.MacPoolsResource;
import org.ovirt.engine.api.resource.NetworkAttachmentResource;
import org.ovirt.engine.api.resource.NetworkAttachmentsResource;
import org.ovirt.engine.api.resource.NetworkResource;
import org.ovirt.engine.api.resource.NetworksResource;
import org.ovirt.engine.api.resource.OperatingSystemResource;
import org.ovirt.engine.api.resource.OperatingSystemsResource;
import org.ovirt.engine.api.resource.PermissionResource;
import org.ovirt.engine.api.resource.PermitResource;
import org.ovirt.engine.api.resource.PermitsResource;
import org.ovirt.engine.api.resource.QosResource;
import org.ovirt.engine.api.resource.QossResource;
import org.ovirt.engine.api.resource.QuotaClusterLimitResource;
import org.ovirt.engine.api.resource.QuotaClusterLimitsResource;
import org.ovirt.engine.api.resource.QuotaResource;
import org.ovirt.engine.api.resource.QuotaStorageLimitResource;
import org.ovirt.engine.api.resource.QuotaStorageLimitsResource;
import org.ovirt.engine.api.resource.QuotasResource;
import org.ovirt.engine.api.resource.RoleResource;
import org.ovirt.engine.api.resource.RolesResource;
import org.ovirt.engine.api.resource.SchedulingPoliciesResource;
import org.ovirt.engine.api.resource.SchedulingPolicyResource;
import org.ovirt.engine.api.resource.SchedulingPolicyUnitResource;
import org.ovirt.engine.api.resource.SchedulingPolicyUnitsResource;
import org.ovirt.engine.api.resource.SnapshotResource;
import org.ovirt.engine.api.resource.SnapshotsResource;
import org.ovirt.engine.api.resource.StatisticResource;
import org.ovirt.engine.api.resource.StatisticsResource;
import org.ovirt.engine.api.resource.StepResource;
import org.ovirt.engine.api.resource.StepsResource;
import org.ovirt.engine.api.resource.StorageDomainResource;
import org.ovirt.engine.api.resource.StorageDomainTemplateResource;
import org.ovirt.engine.api.resource.StorageDomainTemplatesResource;
import org.ovirt.engine.api.resource.StorageDomainVmResource;
import org.ovirt.engine.api.resource.StorageDomainVmsResource;
import org.ovirt.engine.api.resource.StorageDomainsResource;
import org.ovirt.engine.api.resource.StorageResource;
import org.ovirt.engine.api.resource.StorageServerConnectionExtensionResource;
import org.ovirt.engine.api.resource.StorageServerConnectionExtensionsResource;
import org.ovirt.engine.api.resource.StorageServerConnectionResource;
import org.ovirt.engine.api.resource.StorageServerConnectionsResource;
import org.ovirt.engine.api.resource.SystemPermissionsResource;
import org.ovirt.engine.api.resource.SystemResource;
import org.ovirt.engine.api.resource.TagResource;
import org.ovirt.engine.api.resource.TagsResource;
import org.ovirt.engine.api.resource.TemplateCdromResource;
import org.ovirt.engine.api.resource.TemplateCdromsResource;
import org.ovirt.engine.api.resource.TemplateDiskResource;
import org.ovirt.engine.api.resource.TemplateDisksResource;
import org.ovirt.engine.api.resource.TemplateNicResource;
import org.ovirt.engine.api.resource.TemplateNicsResource;
import org.ovirt.engine.api.resource.TemplateResource;
import org.ovirt.engine.api.resource.TemplateWatchdogResource;
import org.ovirt.engine.api.resource.TemplateWatchdogsResource;
import org.ovirt.engine.api.resource.TemplatesResource;
import org.ovirt.engine.api.resource.UnmanagedNetworkResource;
import org.ovirt.engine.api.resource.UnmanagedNetworksResource;
import org.ovirt.engine.api.resource.VirtualFunctionAllowedNetworkResource;
import org.ovirt.engine.api.resource.VirtualFunctionAllowedNetworksResource;
import org.ovirt.engine.api.resource.VmApplicationResource;
import org.ovirt.engine.api.resource.VmApplicationsResource;
import org.ovirt.engine.api.resource.VmCdromResource;
import org.ovirt.engine.api.resource.VmCdromsResource;
import org.ovirt.engine.api.resource.VmDiskResource;
import org.ovirt.engine.api.resource.VmDisksResource;
import org.ovirt.engine.api.resource.VmGraphicsConsoleResource;
import org.ovirt.engine.api.resource.VmHostDeviceResource;
import org.ovirt.engine.api.resource.VmHostDevicesResource;
import org.ovirt.engine.api.resource.VmNicResource;
import org.ovirt.engine.api.resource.VmNicsResource;
import org.ovirt.engine.api.resource.VmNumaNodeResource;
import org.ovirt.engine.api.resource.VmNumaNodesResource;
import org.ovirt.engine.api.resource.VmPoolResource;
import org.ovirt.engine.api.resource.VmPoolsResource;
import org.ovirt.engine.api.resource.VmReportedDeviceResource;
import org.ovirt.engine.api.resource.VmReportedDevicesResource;
import org.ovirt.engine.api.resource.VmResource;
import org.ovirt.engine.api.resource.VmSessionResource;
import org.ovirt.engine.api.resource.VmSessionsResource;
import org.ovirt.engine.api.resource.VmWatchdogResource;
import org.ovirt.engine.api.resource.VmWatchdogsResource;
import org.ovirt.engine.api.resource.VmsResource;
import org.ovirt.engine.api.resource.VnicProfileResource;
import org.ovirt.engine.api.resource.VnicProfilesResource;
import org.ovirt.engine.api.resource.WeightResource;
import org.ovirt.engine.api.resource.WeightsResource;
import org.ovirt.engine.api.resource.aaa.DomainGroupResource;
import org.ovirt.engine.api.resource.aaa.DomainGroupsResource;
import org.ovirt.engine.api.resource.aaa.DomainResource;
import org.ovirt.engine.api.resource.aaa.DomainUserResource;
import org.ovirt.engine.api.resource.aaa.DomainUsersResource;
import org.ovirt.engine.api.resource.aaa.DomainsResource;
import org.ovirt.engine.api.resource.aaa.GroupResource;
import org.ovirt.engine.api.resource.aaa.GroupsResource;
import org.ovirt.engine.api.resource.aaa.UserResource;
import org.ovirt.engine.api.resource.aaa.UsersResource;
import org.ovirt.engine.api.resource.externalhostproviders.EngineKatelloErrataResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalComputeResourceResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalComputeResourcesResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalDiscoveredHostResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalDiscoveredHostsResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalHostGroupResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalHostGroupsResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalHostProviderResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalHostProvidersResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalHostResource;
import org.ovirt.engine.api.resource.externalhostproviders.ExternalHostsResource;
import org.ovirt.engine.api.resource.externalhostproviders.KatelloErrataResource;
import org.ovirt.engine.api.resource.externalhostproviders.KatelloErratumResource;
import org.ovirt.engine.api.resource.gluster.GlusterBrickResource;
import org.ovirt.engine.api.resource.gluster.GlusterBricksResource;
import org.ovirt.engine.api.resource.gluster.GlusterHookResource;
import org.ovirt.engine.api.resource.gluster.GlusterHooksResource;
import org.ovirt.engine.api.resource.gluster.GlusterVolumeResource;
import org.ovirt.engine.api.resource.gluster.GlusterVolumesResource;
import org.ovirt.engine.api.resource.openstack.OpenstackImageProviderResource;
import org.ovirt.engine.api.resource.openstack.OpenstackImageProvidersResource;
import org.ovirt.engine.api.resource.openstack.OpenstackImageResource;
import org.ovirt.engine.api.resource.openstack.OpenstackImagesResource;
import org.ovirt.engine.api.resource.openstack.OpenstackNetworkProviderResource;
import org.ovirt.engine.api.resource.openstack.OpenstackNetworkProvidersResource;
import org.ovirt.engine.api.resource.openstack.OpenstackNetworkResource;
import org.ovirt.engine.api.resource.openstack.OpenstackNetworksResource;
import org.ovirt.engine.api.resource.openstack.OpenstackSubnetResource;
import org.ovirt.engine.api.resource.openstack.OpenstackSubnetsResource;
import org.ovirt.engine.api.resource.openstack.OpenstackVolumeAuthenticationKeyResource;
import org.ovirt.engine.api.resource.openstack.OpenstackVolumeAuthenticationKeysResource;
import org.ovirt.engine.api.resource.openstack.OpenstackVolumeProviderResource;
import org.ovirt.engine.api.resource.openstack.OpenstackVolumeProvidersResource;
import org.ovirt.engine.api.resource.openstack.OpenstackVolumeTypeResource;
import org.ovirt.engine.api.resource.openstack.OpenstackVolumeTypesResource;
import org.ovirt.engine.api.restapi.invocation.Current;
import org.ovirt.engine.api.restapi.invocation.CurrentManager;
import org.ovirt.engine.api.restapi.invocation.VersionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Contains a static addLinks() method which constructs any href attributes
 * and action links required by a representation.
 *
 * The information used to build links is obtained from the annotations on
 * the API definition interfaces.

 * For example, a link to a VM is the combination of the @Path attribute on
 * VmsResource and the VM id - i.e. '/restapi-definition/vms/{vm_id}'
 *
 * Resource collections which are a sub-resource of a parent collection
 * present a more difficult challenge. For example, the link to a VM tag
 * is the combination of the @Path attribute on VmsResource, the VM id,
 * the @Path attribute on VmResource.getTagsResource() and the tag id -
 * i.e. '/restapi-definition/vms/{vm_id}/tags/{tag_id}'
 * In most cases the parent type may be computed, but in exceptional
 * cases there are a number of equally valid candidates. Disambiguation
 * is achieved via an explicit suggestedParentType parameter.
 *
 * To be able to do this we need, for each collection, the collection type
 * (e.g. AssignedTagsResource), the resource type (e.g. AssignedTagResource)
 * and the parent model type (e.g. VM). The TYPES map below is populated
 * with this information for every resource type.
 */
public class LinkHelper {
    private static final Logger log = LoggerFactory.getLogger(LinkHelper.class);

    private static final String SEARCH_RELATION = "/search";
    private static final String SEARCH_TEMPLATE = "?search={query}";
    private static final String MATRIX_PARAMETER_TEMPLATE = ";%s={%s}";

    /**
     * A constant representing the pseudo-parent of a top-level collection
     */
    private static final Class<? extends BaseResource> NO_PARENT = BaseResource.class;

    /**
     * A map describing every possible collection
     */
    private static ModelToCollectionsMap TYPES = new ModelToCollectionsMap();

    /**
     * A map for caching relevant resource methods for each class
     */
    private static ConcurrentMap<Class<?>, List<Method>> methodCache = new ConcurrentHashMap<>();

    static {
        ParentToCollectionMap map;

        map = new ParentToCollectionMap(TemplateCdromResource.class, TemplateCdromsResource.class, Template.class);
        map.add(VmCdromResource.class, VmCdromsResource.class, Vm.class);
        TYPES.put(Cdrom.class, map);

        map = new ParentToCollectionMap(GraphicsConsoleResource.class, GraphicsConsolesResource.class);
        map.add(VmGraphicsConsoleResource.class, GraphicsConsolesResource.class, Vm.class);
        map.add(GraphicsConsoleResource.class, GraphicsConsolesResource.class, Template.class);
        map.add(GraphicsConsoleResource.class, GraphicsConsolesResource.class, InstanceType.class);
        TYPES.put(GraphicsConsole.class, map);

        map = new ParentToCollectionMap(VmApplicationResource.class, VmApplicationsResource.class, Vm.class);
        TYPES.put(Application.class, map);

        map = new ParentToCollectionMap(VmReportedDeviceResource.class, VmReportedDevicesResource.class, Vm.class);
        TYPES.put(ReportedDevice.class, map);

        map = new ParentToCollectionMap(ClusterResource.class, ClustersResource.class);
        TYPES.put(Cluster.class, map);

        map = new ParentToCollectionMap(DataCenterResource.class, DataCentersResource.class);
        TYPES.put(DataCenter.class, map);

        map = new ParentToCollectionMap(MacPoolResource.class, MacPoolsResource.class);
        TYPES.put(MacPool.class, map);

        map = new ParentToCollectionMap(DiskResource.class, DisksResource.class);
        map.add(VmDiskResource.class, VmDisksResource.class, Vm.class);
        map.add(TemplateDiskResource.class, TemplateDisksResource.class, Template.class);
        TYPES.put(Disk.class, map);

        map = new ParentToCollectionMap(DiskSnapshotResource.class, DiskSnapshotsResource.class, StorageDomain.class);
        TYPES.put(DiskSnapshot.class, map);

        map = new ParentToCollectionMap(StorageServerConnectionExtensionResource.class, StorageServerConnectionExtensionsResource.class, Host.class);
        TYPES.put(StorageConnectionExtension.class, map);

        map = new ParentToCollectionMap(org.ovirt.engine.api.resource.HostResource.class, org.ovirt.engine.api.resource.HostsResource.class);
        TYPES.put(Host.class, map);

        map = new ParentToCollectionMap(HostNicResource.class, HostNicsResource.class, Host.class);
        TYPES.put(HostNic.class, map);

        map = new ParentToCollectionMap(HostNumaNodeResource.class, HostNumaNodesResource.class, Host.class);
        TYPES.put(NumaNode.class, map);

        map = new ParentToCollectionMap(HostHookResource.class, HostHooksResource.class, Host.class);
        TYPES.put(Hook.class, map);

        map = new ParentToCollectionMap(FileResource.class, FilesResource.class, StorageDomain.class);
        TYPES.put(File.class, map);

        map = new ParentToCollectionMap(ImageResource.class, ImagesResource.class);
        map.add(ImageResource.class, ImagesResource.class, StorageDomain.class);
        TYPES.put(Image.class, map);

        map = new ParentToCollectionMap(GroupResource.class, GroupsResource.class);
        map.add(DomainGroupResource.class, DomainGroupsResource.class, Domain.class);
        TYPES.put(Group.class, map);

        map = new ParentToCollectionMap(PermissionResource.class, AssignedPermissionsResource.class, User.class);
        map.add(PermissionResource.class, AssignedPermissionsResource.class, Group.class);
        map.add(PermissionResource.class, AssignedPermissionsResource.class, Role.class);
        map.add(PermissionResource.class, AssignedPermissionsResource.class, Vm.class);
        map.add(PermissionResource.class, AssignedPermissionsResource.class, Disk.class);
        map.add(PermissionResource.class, SystemPermissionsResource.class, NO_PARENT);
        TYPES.put(Permission.class, map);

        map = new ParentToCollectionMap(NetworkResource.class, NetworksResource.class);
        map.add(AssignedNetworkResource.class, AssignedNetworksResource.class, Cluster.class);
        map.add(NetworkResource.class, NetworksResource.class, Network.class);
        map.add(VirtualFunctionAllowedNetworkResource.class, VirtualFunctionAllowedNetworksResource.class, HostNic.class);
        TYPES.put(Network.class, map);

        map = new ParentToCollectionMap();
        map.add(InstanceTypeNicResource.class, InstanceTypeNicsResource.class, InstanceType.class);
        map.add(TemplateNicResource.class, TemplateNicsResource.class, Template.class);
        map.add(VmNicResource.class, VmNicsResource.class, Vm.class);
        TYPES.put(Nic.class, map);

        map = new ParentToCollectionMap(VmNumaNodeResource.class, VmNumaNodesResource.class, Vm.class);
        TYPES.put(VirtualNumaNode.class, map);

        map = new ParentToCollectionMap(PermitResource.class, PermitsResource.class, Role.class);
        TYPES.put(Permit.class, map);

        map = new ParentToCollectionMap(RoleResource.class, RolesResource.class);
        map.add(RoleResource.class, AssignedRolesResource.class, User.class);
        TYPES.put(Role.class, map);

        map = new ParentToCollectionMap(SnapshotResource.class, SnapshotsResource.class, Vm.class);
        TYPES.put(Snapshot.class, map);

        map = new ParentToCollectionMap(StorageResource.class, HostStorageResource.class, Host.class);
        TYPES.put(HostStorage.class, map);

        map = new ParentToCollectionMap(StorageServerConnectionResource.class, StorageServerConnectionsResource.class);
        TYPES.put(StorageConnection.class, map);

        map = new ParentToCollectionMap(StorageDomainResource.class, StorageDomainsResource.class);
        map.add(AttachedStorageDomainResource.class, AttachedStorageDomainsResource.class, DataCenter.class);
        TYPES.put(StorageDomain.class, map);

        map = new ParentToCollectionMap(TagResource.class, TagsResource.class);
        map.add(AssignedTagResource.class, AssignedTagsResource.class, Host.class);
        map.add(AssignedTagResource.class, AssignedTagsResource.class, User.class);
        map.add(AssignedTagResource.class, AssignedTagsResource.class, Vm.class);
        map.add(AssignedTagResource.class, AssignedTagsResource.class, Template.class);
        map.add(AssignedTagResource.class, AssignedTagsResource.class, Group.class);
        TYPES.put(Tag.class, map);

        map = new ParentToCollectionMap(BookmarkResource.class, BookmarksResource.class);
        TYPES.put(Bookmark.class, map);

        map = new ParentToCollectionMap(IconResource.class, IconsResource.class);
        TYPES.put(Icon.class, map);

        map = new ParentToCollectionMap(TemplateResource.class, TemplatesResource.class);
        map.add(StorageDomainTemplateResource.class, StorageDomainTemplatesResource.class, StorageDomain.class);
        TYPES.put(Template.class, map);

        map = new ParentToCollectionMap(InstanceTypeResource.class, InstanceTypesResource.class);
        TYPES.put(InstanceType.class, map);

        map = new ParentToCollectionMap(UserResource.class, UsersResource.class);
        map.add(DomainUserResource.class, DomainUsersResource.class, Domain.class);
        TYPES.put(User.class, map);

        map = new ParentToCollectionMap(VmResource.class, VmsResource.class);
        map.add(StorageDomainVmResource.class, StorageDomainVmsResource.class, StorageDomain.class);
//        map.add(SnapshotResource.class, SnapshotsResource.class, Snapshot.class);
        TYPES.put(Vm.class, map);

        map = new ParentToCollectionMap(VmPoolResource.class, VmPoolsResource.class);
        TYPES.put(VmPool.class, map);

        map = new ParentToCollectionMap(EventResource.class, EventsResource.class);
        TYPES.put(Event.class, map);

        map = new ParentToCollectionMap(DomainResource.class, DomainsResource.class);
        TYPES.put(Domain.class, map);

        map = new ParentToCollectionMap(StatisticResource.class, StatisticsResource.class, Disk.class);
        map.add(StatisticResource.class, StatisticsResource.class, Host.class);
        map.add(StatisticResource.class, StatisticsResource.class, HostNic.class);
        map.add(StatisticResource.class, StatisticsResource.class, NumaNode.class);
        map.add(StatisticResource.class, StatisticsResource.class, Nic.class);
        map.add(StatisticResource.class, StatisticsResource.class, Vm.class);
        map.add(StatisticResource.class, StatisticsResource.class, GlusterBrick.class);
        TYPES.put(Statistic.class, map);

        map = new ParentToCollectionMap(QuotaResource.class, QuotasResource.class, DataCenter.class);
        TYPES.put(Quota.class, map);

        map = new ParentToCollectionMap(QuotaStorageLimitResource.class, QuotaStorageLimitsResource.class, Quota.class);
        TYPES.put(QuotaStorageLimit.class, map);
        map = new ParentToCollectionMap(QuotaClusterLimitResource.class, QuotaClusterLimitsResource.class, Quota.class);
        TYPES.put(QuotaClusterLimit.class, map);

        map = new ParentToCollectionMap(GlusterVolumeResource.class, GlusterVolumesResource.class, Cluster.class);
        TYPES.put(GlusterVolume.class, map);
        TYPES.put(GlusterVolumeProfileDetails.class, map);

        map = new ParentToCollectionMap(GlusterBrickResource.class, GlusterBricksResource.class, GlusterVolume.class);
        TYPES.put(GlusterBrick.class, map);

        map = new ParentToCollectionMap(GlusterHookResource.class, GlusterHooksResource.class, Cluster.class);
        TYPES.put(GlusterHook.class, map);

        map = new ParentToCollectionMap(CapabiliyResource.class, CapabilitiesResource.class);
        TYPES.put(VersionCaps.class, map);

        map = new ParentToCollectionMap();
        map.add(InstanceTypeWatchdogResource.class, InstanceTypeWatchdogsResource.class, InstanceType.class);
        map.add(TemplateWatchdogResource.class, TemplateWatchdogsResource.class, Template.class);
        map.add(VmWatchdogResource.class, VmWatchdogsResource.class, Vm.class);
        TYPES.put(Watchdog.class, map);

        map = new ParentToCollectionMap(JobResource.class, JobsResource.class);
        TYPES.put(Job.class, map);

        map = new ParentToCollectionMap(StepResource.class, StepsResource.class, Job.class);
        TYPES.put(Step.class, map);

        map = new ParentToCollectionMap(VnicProfileResource.class, VnicProfilesResource.class);
        TYPES.put(VnicProfile.class, map);

        map = new ParentToCollectionMap(LabelResource.class, LabelsResource.class);
        map.add(LabelResource.class, LabelsResource.class, Network.class);
        map.add(LabelResource.class, LabelsResource.class, HostNic.class);
        TYPES.put(Label.class, map);

        map = new ParentToCollectionMap(NetworkAttachmentResource.class, NetworkAttachmentsResource.class, Host.class);
        map.add(NetworkAttachmentResource.class, NetworkAttachmentsResource.class, HostNic.class);
        TYPES.put(NetworkAttachment.class, map);

        map = new ParentToCollectionMap(UnmanagedNetworkResource.class, UnmanagedNetworksResource.class, Host.class);
        TYPES.put(UnmanagedNetwork.class, map);

        map = new ParentToCollectionMap(AffinityGroupResource.class, AffinityGroupsResource.class, Cluster.class);
        TYPES.put(AffinityGroup.class, map);

        map = new ParentToCollectionMap(VmSessionResource.class, VmSessionsResource.class, Vm.class);
        TYPES.put(Session.class, map);

        map = new ParentToCollectionMap(HostDevice.class, HostDevices.class);
        map.add(HostDeviceResource.class, HostDevicesResource.class, Host.class);
        map.add(VmHostDeviceResource.class, VmHostDevicesResource.class, Vm.class);
        TYPES.put(HostDevice.class, map);

        map = new ParentToCollectionMap(SchedulingPolicyUnitResource.class, SchedulingPolicyUnitsResource.class);
        TYPES.put(SchedulingPolicyUnit.class, map);

        map = new ParentToCollectionMap(SchedulingPolicyResource.class, SchedulingPoliciesResource.class);
        TYPES.put(SchedulingPolicy.class, map);

        map = new ParentToCollectionMap(FilterResource.class, FiltersResource.class, SchedulingPolicy.class);
        TYPES.put(Filter.class, map);

        map = new ParentToCollectionMap(WeightResource.class, WeightsResource.class, SchedulingPolicy.class);
        TYPES.put(Weight.class, map);

        map = new ParentToCollectionMap(BalanceResource.class, BalancesResource.class, SchedulingPolicy.class);
        TYPES.put(Balance.class, map);

        map = new ParentToCollectionMap(QosResource.class, QossResource.class, DataCenter.class);
        map.add(QosResource.class, QossResource.class, Network.class);
        TYPES.put(Qos.class, map);

        map = new ParentToCollectionMap(IscsiBondResource.class, IscsiBondsResource.class, DataCenter.class);
        TYPES.put(IscsiBond.class, map);

        map = new ParentToCollectionMap(DiskProfileResource.class, DiskProfilesResource.class);
        TYPES.put(DiskProfile.class, map);

        map = new ParentToCollectionMap(CpuProfileResource.class, CpuProfilesResource.class);
        TYPES.put(CpuProfile.class, map);

        // Operating systems:
        map = new ParentToCollectionMap(OperatingSystemResource.class, OperatingSystemsResource.class);
        TYPES.put(OperatingSystemInfo.class, map);

        // External host providers:
        map = new ParentToCollectionMap(ExternalHostProviderResource.class, ExternalHostProvidersResource.class);
        TYPES.put(ExternalHostProvider.class, map);

        map = new ParentToCollectionMap(ExternalHostResource.class, ExternalHostsResource.class);
        map.add(ExternalHostResource.class, ExternalHostsResource.class, ExternalHostProvider.class);
        TYPES.put(ExternalHost.class, map);

        map = new ParentToCollectionMap(ExternalDiscoveredHostResource.class, ExternalHostsResource.class);
        map.add(ExternalDiscoveredHostResource.class, ExternalDiscoveredHostsResource.class, ExternalHostProvider.class);
        TYPES.put(ExternalDiscoveredHost.class, map);

        map = new ParentToCollectionMap(ExternalHostGroupResource.class, ExternalHostGroupsResource.class);
        map.add(ExternalHostGroupResource.class, ExternalHostGroupsResource.class, ExternalHostProvider.class);
        TYPES.put(ExternalHostGroup.class, map);

        map = new ParentToCollectionMap(ExternalComputeResourceResource.class, ExternalComputeResourcesResource.class);
        map.add(ExternalComputeResourceResource.class, ExternalComputeResourcesResource.class, ExternalHostProvider.class);
        TYPES.put(ExternalComputeResource.class, map);

        // OpenStack image providers:
        map = new ParentToCollectionMap(OpenstackImageProviderResource.class, OpenstackImageProvidersResource.class);
        TYPES.put(OpenStackImageProvider.class, map);

        map = new ParentToCollectionMap(OpenstackImageResource.class, OpenstackImagesResource.class);
        map.add(OpenstackImageResource.class, OpenstackImagesResource.class, OpenStackImageProvider.class);
        TYPES.put(OpenStackImage.class, map);

        // OpenStack volume providers:
        map = new ParentToCollectionMap(OpenstackVolumeProviderResource.class, OpenstackVolumeProvidersResource.class);
        TYPES.put(OpenStackVolumeProvider.class, map);

        map = new ParentToCollectionMap(OpenstackVolumeTypeResource.class, OpenstackVolumeTypesResource.class);
        map.add(OpenstackVolumeTypeResource.class, OpenstackVolumeTypesResource.class, OpenStackVolumeProvider.class);
        TYPES.put(OpenStackVolumeType.class, map);

        map = new ParentToCollectionMap(OpenstackVolumeAuthenticationKeyResource.class, OpenstackVolumeAuthenticationKeysResource.class);
        map.add(OpenstackVolumeAuthenticationKeyResource.class, OpenstackVolumeAuthenticationKeysResource.class, OpenStackVolumeProvider.class);
        TYPES.put(OpenstackVolumeAuthenticationKey.class, map);

        // OpenStack network providers:
        map = new ParentToCollectionMap(OpenstackNetworkProviderResource.class, OpenstackNetworkProvidersResource.class);
        TYPES.put(OpenStackNetworkProvider.class, map);

        map = new ParentToCollectionMap(OpenstackNetworkResource.class, OpenstackNetworksResource.class);
        map.add(OpenstackNetworkResource.class, OpenstackNetworksResource.class, OpenStackNetworkProvider.class);
        TYPES.put(OpenStackNetwork.class, map);

        map = new ParentToCollectionMap(OpenstackSubnetResource.class, OpenstackSubnetsResource.class);
        map.add(OpenstackSubnetResource.class, OpenstackSubnetsResource.class, OpenStackNetwork.class);
        TYPES.put(OpenStackSubnet.class, map);

        map = new ParentToCollectionMap(FenceAgentResource.class, FenceAgentsResource.class, Host.class);
        TYPES.put(Agent.class, map);

        map = new ParentToCollectionMap(KatelloErratumResource.class, KatelloErrataResource.class, Host.class);
        map.add(KatelloErratumResource.class, KatelloErrataResource.class, Vm.class);
        map.add(KatelloErratumResource.class, EngineKatelloErrataResource.class, NO_PARENT);
        TYPES.put(KatelloErratum.class, map);
    }

    /**
     * Obtain the relative path to a top-level collection
     *
     * The path is the value of the {@link Path} annotation on resource locator method of the root resource that
     * returns a reference to this class of resource. For example, if the class is {@link BookmarksResource} then
     * returned value should be the value of the {@link Path} annotation on the
     * {@link SystemResource#getBookmarksResource()} method.
     *
     * @param clz the collection resource type
     * @return the relative path to the collection
     */
    private static String getRelativePath(Class<?> clz) {
        for (Method method : SystemResource.class.getMethods()) {
            if (method.getReturnType() == clz) {
                Path annotation = method.getAnnotation(Path.class);
                if (annotation != null) {
                    return annotation.value();
                }
            }
        }
        log.error("Can't find relative path for class \"" + clz.getName() + "\", will return null");
        return null;
    }

    /**
     * Obtain the relative path to a sub-collection.
     *
     * The path is obtained from the @Path annotation on the method on @parent
     * which returns an instance of @clz.
     *
     * @param clz    the collection resource type (e.g. AssignedTagsResource)
     * @param parent the parent resource type (e.g. VmResource)
     * @return       the relative path to the collection
     */
    private static String getRelativePath(Class<?> clz, Class<?> parent) {
        for (Method method : parent.getMethods()) {
            if (method.getName().startsWith("get") && method.getReturnType() == clz) {
                Path pathAnnotation = method.getAnnotation(Path.class);
                return pathAnnotation.value();
            }
        }
        return null;
    }

    /**
     * Obtain a set of inline BaseResource objects from @obj
     *
     * i.e. return the value of any properties on @obj which are a
     * sub-type of BaseResource
     *
     * @param obj the object to check
     * @return    a list of any inline BaseResource objects
     */
    private static List<BaseResource> getInlineResources(Object obj) {
        ArrayList<BaseResource> ret = new ArrayList<>();

        for (Method method : getRelevantMethods(obj.getClass())) {
            // We need to recursively scan everything that is in the model package, as there may be references
            // to resources deeply nested:
            Object inline = null;
            try {
                inline = method.invoke(obj);
            } catch (Exception e) {
                // invocation target exception should not occur on simple getter
            }
            if (inline != null) {
                if (inline instanceof BaseResource) {
                    ret.add((BaseResource) inline);
                }
                else {
                    ret.addAll(getInlineResources(inline));
                }
            }
        }

        return ret;
    }

    /**
     * Gets all the relevant possible inline resources methods of a class. Data is cached for future use.
     * @param clz
     *            The class to examine
     * @return The list of relevant methods.
     */
    private static List<Method> getRelevantMethods(Class<?> clz) {
        List<Method> methods = methodCache.get(clz);
        if (methods == null) {
            methods = new ArrayList<>();
            for (Method method : clz.getMethods()) {
                if (method.getName().startsWith("get")) {
                    if (method.getReturnType().getPackage() == BaseResource.class.getPackage()) {
                        methods.add(method);
                    }
                }
            }
            methodCache.put(clz, methods);
        }

        return methods;
    }
    /**
     * Unset the property on @model of type @type
     *
     * @param model the object with the property to unset
     * @param type  the type of the property
     */
    private static void unsetInlineResource(BaseResource model, Class<?> type) {
        for (Method method : model.getClass().getMethods()) {
            if (method.getName().startsWith("set")) {
                try {
                    if (type.isAssignableFrom(method.getParameterTypes()[0])) {
                        method.invoke(model, new Object[]{null});
                        return;
                    }
                } catch (Exception e) {
                    // invocation target exception should not occur on simple setter
                }
            }
        }
    }

    /**
     * Return any parent object set on @model
     *
     * i.e. return the value of any bean property whose type matches @parentType
     *
     * @param model      object to check
     * @param parentType the type of the parent
     * @return           the parent object, or null if not set
     */
    private static <R extends BaseResource> BaseResource getParentModel(R model, Class<?> parentType) {
        for (BaseResource inline : getInlineResources(model)) {
            if (parentType.isAssignableFrom(inline.getClass())) {
                return inline;
            }
        }
        return null;
    }

    /**
     * Lookup the #Collection instance which represents this object
     *
     * i.e. for a VM tag (i.e. a Tag object which its VM property set)
     * return the #Collection instance which encapsulates AssignedTagResource,
     * AssignedTagsResource and VM.
     *
     * @param model the object to query for
     * @return      the #Collection instance representing the object's collection
     */
    private static Collection getCollection(BaseResource model) {
        return getCollection(model, null);
    }

    /**
     * Lookup the #Collection instance which represents this object
     *
     * i.e. for a VM tag (i.e. a Tag object which its VM property set)
     * return the #Collection instance which encapsulates AssignedTagResource,
     * AssignedTagsResource and VM.
     *
     * @param model                the object to query for
     * @param suggestedParentType  the suggested parent type
     * @return                     the #Collection instance representing the object's collection
     */
    private static Collection getCollection(BaseResource model, Class<? extends BaseResource> suggestedParentType) {
        ParentToCollectionMap collections = TYPES.get(model.getClass());

        if (collections == null) {
            return null;
        }

        if (suggestedParentType != null) {
            for (Entry<Class<? extends BaseResource>, Collection> entry : collections.entrySet()) {
                if (entry.getKey().equals(suggestedParentType)) {
                    return entry.getValue();
                }
            }
        }

        for (Entry<Class<? extends BaseResource>, Collection> parentTypeEntry : collections.entrySet()) {
            if (parentTypeEntry.getKey() != NO_PARENT &&
                getParentModel(model, parentTypeEntry.getKey()) != null) {
                return parentTypeEntry.getValue();
            }
        }

        return collections.get(NO_PARENT);
    }

    /**
     * Computes the path for the given object. For example, for a tag of a virtual machine returns the path
     * {@code /ovirt-engine/api/vms/{vm:id}/tags/{tag:id}}.
     *
     * @param object the object
     * @return the path for the object, or {@code null} if the path can't be determined
     */
    public static String getPath(BaseResource object) {
        return getPath(object, null);
    }

    /**
     * Computes the path for the given object, using the given type to find out what is the type of the parent.
     *
     * @param object the object
     * @param suggestedParentType the suggested parent type
     * @return the path for the object, or {@code null} if the path can't be determined
     */
    public static String getPath(BaseResource object, Class<? extends BaseResource> suggestedParentType) {
        Collection collection = getCollection(object, suggestedParentType);
        if (collection == null) {
            return null;
        }

        if (collection.getParentType() != NO_PARENT) {
            BaseResource parent = getParentModel(object, collection.getParentType());
            if (parent == null) {
                return null;
            }
            Collection parentCollection = getCollection(parent, suggestedParentType);
            if (parentCollection == null) {
                return null;
            }
            String parentPath = getPath(parent);
            if (parentPath == null) {
                return null;
            }
            String relativePath = getRelativePath(collection.getCollectionType(), parentCollection.getResourceType());
            return String.join("/", parentPath, relativePath, object.getId());
        }

        Current current = CurrentManager.get();
        StringBuilder buffer = new StringBuilder();
        buffer.append(current.getPrefix());
        if (current.getVersionSource() == VersionSource.URL) {
            buffer.append("/v");
            buffer.append(current.getVersion());
        }
        buffer.append("/");
        buffer.append(getRelativePath(collection.getCollectionType()));
        buffer.append("/");
        buffer.append(object.getId());
        return buffer.toString();
    }

    /**
     * Set the href attribute on the supplied object
     *
     * e.g. set href = '/restapi-definition/vms/{vm_id}/tags/{tag_id}' on a VM tag
     *
     * @param model the object
     */
    private static void setHref(BaseResource model) {
        setHref(model, null);
    }

    /**
     * Set the href attribute on the supplied object
     *
     * e.g. set href = '/restapi-definition/vms/{vm_id}/tags/{tag_id}' on a VM tag
     *
     * @param model the object
     * @param suggestedParentType  the suggested parent type
     */
    private static void setHref(BaseResource model, Class<? extends BaseResource> suggestedParentType) {
        String path = getPath(model, suggestedParentType);
        if (path != null) {
            model.setHref(path);
        }
    }

    /**
     * Construct the set of action links for an object
     *
     * @param model   the object
     * @param suggestedParentType  the suggested parent type
     */
    private static void setActions(BaseResource model, Class<? extends BaseResource> suggestedParentType) {
        Collection collection = getCollection(model);
        if (collection != null) {
            String path = getPath(model, suggestedParentType);
            if (path != null) {
                ActionsBuilder actionsBuilder = new ActionsBuilder(path, collection.getResourceType());
                model.setActions(actionsBuilder.build());
            }
        }
    }

    /**
     * Adds the set of action links for an object
     *
     * @param model the object to add actions to
     * @param collection the object to get implemented methods from
     */
    public static <R extends ActionableResource> void addActions(R model, Object collection) {
        Current current = CurrentManager.get();
        String base = current.getPrefix() + current.getPath();
        if (base != null) {
            ActionsBuilder actionsBuilder = new ActionsBuilder(base, model.getClass(), collection.getClass());
            model.setActions(actionsBuilder.build());
        }
    }

    /**
     * Set the href attribute on the object (and its inline objects)
     * and construct its set of action links
     *
     * @param model the object
     * @return the object, with href attributes and action links
     */
    public static <R extends BaseResource> R addLinks(R model) {
        return addLinks(model, null);
    }

    public static <R extends BaseResource> R addLinks(R model, Class<? extends BaseResource> suggestedParentType) {
        return addLinks(model, suggestedParentType, true);
    }

    public static <R extends BaseResource> R addLinks(R model, Class<? extends BaseResource> suggestedParentType, boolean addActions) {
        setHref(model, suggestedParentType);
        if (addActions) {
            setActions(model, suggestedParentType);
        }

        for (BaseResource inline : getInlineResources(model)) {
            if (inline.getId() != null) {
                setHref(inline);
            }
            for (BaseResource grandParent : getInlineResources(inline)) {
                unsetInlineResource(inline, grandParent.getClass());
            }
        }

        return model;
    }

    /**
     * A #Map sub-class which maps a model type (e.g. Tag.class) to a
     * set of suitable collection definitions.
     */
    private static class ModelToCollectionsMap extends HashMap<Class<? extends BaseResource>, ParentToCollectionMap> {}

    /**
     * A #Map sub-class which maps a parent model type to collection
     * definition.
     *
     * e.g. the map for Tag contains a collection definition for the
     * describing the VM, Host and User tags sub-collections. It also
     * contains a collection definition describing the top-level
     * tags collection which is keyed on the NO_PARENT key.
     */
    private static class ParentToCollectionMap extends LinkedHashMap<Class<? extends BaseResource>, Collection> {
        public ParentToCollectionMap() {
            super();
        }

        public ParentToCollectionMap(Class<?> resourceType,
                                     Class<?> collectionType,
                                     Class<? extends BaseResource> parentType) {
            super();
            add(resourceType, collectionType, parentType);
        }

        public ParentToCollectionMap(Class<?> resourceType,
                                     Class<?> collectionType) {
            this(resourceType, collectionType, NO_PARENT);
        }

        public void add(Class<?> resourceType,
                        Class<?> collectionType,
                        Class<? extends BaseResource> parentType) {
            put(parentType, new Collection(resourceType, collectionType, parentType));
        }
    }

    /**
     * A description of a collection type, its resource type and the parent
     * resource which contains it, if any.
     *
     * e.g. for the VM tags collection, resourceType is AssignedTagResource,
     * collectionType is AssignedTagsResource and parentType is VM
     */
    private static class Collection {
        private final Class<?> resourceType;
        private final Class<?> collectionType;
        private final Class<?> parentType;

        public Collection(Class<?> resourceType, Class<?> collectionType, Class<?> parentType) {
            this.resourceType = resourceType;
            this.collectionType = collectionType;
            this.parentType = parentType;
        }

        public Class<?> getResourceType() {
            return resourceType;
        }

        public Class<?> getCollectionType() {
            return collectionType;
        }

        public Class<?> getParentType() {
            return parentType;
        }
    }
}
