package org.ovirt.engine.api.restapi.resource;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.UriInfo;

import org.junit.Ignore;
import org.junit.Test;
import org.ovirt.engine.api.model.BootProtocol;
import org.ovirt.engine.api.model.HostNic;
import org.ovirt.engine.api.model.NicStatus;
import org.ovirt.engine.api.resource.HostNicResource;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.network.InterfaceStatus;
import org.ovirt.engine.core.common.businessentities.network.NetworkBootProtocol;
import org.ovirt.engine.core.common.businessentities.network.VdsNetworkInterface;
import org.ovirt.engine.core.common.businessentities.network.VdsNetworkStatistics;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.VdcQueryType;
import org.ovirt.engine.core.compat.Guid;

public class BackendHostNicsResourceTest
    extends AbstractBackendCollectionResourceTest<HostNic, VdsNetworkInterface, BackendHostNicsResource> {

    public static final Guid PARENT_GUID = GUIDS[0];
    public static final Guid NETWORK_GUID = new Guid("33333333-3333-3333-3333-333333333333");
    public static final String NETWORK_NAME = "skynet";
    public static final NetworkBootProtocol BOOT_PROTOCOL = NetworkBootProtocol.STATIC_IP;
    public static final Guid MASTER_GUID = new Guid("99999999-9999-9999-9999-999999999999");
    public static final String MASTER_NAME = "master";
    private static final Guid SLAVE_GUID = new Guid("66666666-6666-6666-6666-666666666666");
    private static final String SLAVE_NAME = "slave";
    private static final int SINGLE_NIC_IDX = GUIDS.length - 2;
    private static final Integer NIC_SPEED = 100;
    private static final InterfaceStatus NIC_STATUS = InterfaceStatus.UP;

    public BackendHostNicsResourceTest() {
        super(new BackendHostNicsResource(PARENT_GUID.toString()), null, null);
    }

    @Test
    @Ignore
    @Override
    public void testQuery() throws Exception {
    }

    @Test
    public void testGet() throws Exception {
        HostNicResource subresource = collection.getNicResource(GUIDS[SINGLE_NIC_IDX].toString());

        setGetVdsQueryExpectations(1);
        setGetNetworksQueryExpectations(1);
        setUriInfo(setUpBasicUriExpectations());
        setUpQueryExpectations("");

        verifyModel(subresource.get(), SINGLE_NIC_IDX);
    }

    @Test
    public void testListIncludeStatistics() throws Exception {
        try {
            accepts.add("application/xml; detail=statistics");
            UriInfo uriInfo = setUpBasicUriExpectations();
            setGetVdsQueryExpectations(1);
            setGetNetworksQueryExpectations(1);
            setUpQueryExpectations("");
            collection.setUriInfo(uriInfo);

            List<HostNic> nics = getCollection();
            assertTrue(nics.get(0).isSetStatistics());

            verifyCollection(nics);
        } finally {
            accepts.clear();
        }
    }

    @Override
    @Test
    public void testList() throws Exception {
        UriInfo uriInfo = setUpBasicUriExpectations();
        setGetVdsQueryExpectations(1);
        setGetNetworksQueryExpectations(1);
        setUpQueryExpectations("");
        collection.setUriInfo(uriInfo);
        verifyCollection(getCollection());
    }

    protected void setUpEntityQueryExpectations(int times) throws Exception {
        setUpEntityQueryExpectations(times, null);
    }

    protected void setUpEntityQueryExpectations(int times, Object failure) throws Exception {
        while (times-- > 0) {
            setUpEntityQueryExpectations(VdcQueryType.GetVdsInterfacesByVdsId,
                                         IdQueryParameters.class,
                                         new String[] { "Id" },
                                         new Object[] { PARENT_GUID },
                                         setUpInterfaces());
        }
    }

    @Override
    protected void setUpQueryExpectations(String query, Object failure) throws Exception {
        assertEquals("", query);

        setUpEntityQueryExpectations(VdcQueryType.GetVdsInterfacesByVdsId,
                                     IdQueryParameters.class,
                                     new String[] { "Id" },
                                     new Object[] { PARENT_GUID },
                                     setUpInterfaces(),
                                     failure);

        control.replay();
    }

    public static List<VdsNetworkInterface> setUpInterfaces() {
        List<VdsNetworkInterface> ifaces = new ArrayList<>();
        for (int i = 0; i < NAMES.length; i++) {
            ifaces.add(getEntitySpecific(i));
        }
        ifaces.add(getMaster());
        ifaces.add(getSlave());
        return ifaces;
    }

    @Override
    protected VdsNetworkInterface getEntity(int index) {
        return getEntitySpecific(index);
    }

    public static VdsNetworkInterface getEntitySpecific(int index) {
        VdsNetworkInterface entity = new VdsNetworkInterface();
        entity.setId(GUIDS[index]);
        entity.setName(NAMES[index]);
        entity.setNetworkName(NETWORK_NAME);
        entity.setSpeed(NIC_SPEED);
        entity = setUpStatistics(entity, GUIDS[index]);
        entity.getStatistics().setStatus(NIC_STATUS);
        entity.setBootProtocol(BOOT_PROTOCOL);
        return entity;
    }

    public static VdsNetworkInterface getMaster() {
        VdsNetworkInterface entity = new VdsNetworkInterface();
        entity.setId(MASTER_GUID);
        entity.setName(MASTER_NAME);
        entity.setNetworkName(NETWORK_NAME);
        entity.setSpeed(NIC_SPEED);
        entity.setBonded(true);
        entity.setBootProtocol(BOOT_PROTOCOL);
        return setUpStatistics(entity, MASTER_GUID);
    }

    public static VdsNetworkInterface getSlave() {
        VdsNetworkInterface entity = new VdsNetworkInterface();
        entity.setId(SLAVE_GUID);
        entity.setName(SLAVE_NAME);
        entity.setNetworkName(NETWORK_NAME);
        entity.setSpeed(NIC_SPEED);
        entity.setBondName(MASTER_NAME);
        entity.setBootProtocol(BOOT_PROTOCOL);
        return setUpStatistics(entity, SLAVE_GUID);
    }

    public static VdsNetworkInterface setUpStatistics(VdsNetworkInterface entity, Guid id) {
        VdsNetworkStatistics statistics = new VdsNetworkStatistics();

        statistics.setId(null);
        statistics.setReceiveDropRate(1D);
        statistics.setReceiveRate(2D);
        statistics.setTransmitDropRate(3D);
        statistics.setTransmitRate(4D);
        statistics.setReceivedBytes(5L);
        statistics.setTransmittedBytes(6L);
        statistics.setVdsId(id);
        statistics.setStatus(null);
        entity.setStatistics(statistics);
        return entity;
    }

    public static org.ovirt.engine.core.common.businessentities.network.Network getNetwork() {
        org.ovirt.engine.core.common.businessentities.network.Network entity = new org.ovirt.engine.core.common.businessentities.network.Network();
        entity.setId(NETWORK_GUID);
        entity.setName(NETWORK_NAME);
        return entity;
    }

    @Override
    protected void verifyModel(HostNic model, int index) {
        verifyModelSpecific(model, index);
        verifyLinks(model);
    }

    public void verifyModelSpecific(HostNic model, int index) {
        assertEquals(GUIDS[index].toString(), model.getId());
        assertEquals(NAMES[index], model.getName());
        assertNotNull(model.getNetwork());
        assertEquals(NETWORK_NAME, model.getNetwork().getName());
        assertEquals(calcSpeed(NIC_SPEED), model.getSpeed());
        assertNotNull(model.getStatus());
        assertEquals(map(NIC_STATUS, null).value(), model.getStatus().getState());
        assertEquals(map(BOOT_PROTOCOL, null).value(), model.getBootProtocol());
    }

    private Long calcSpeed(Integer nicSpeed) {
        return nicSpeed == 0 ?
                             null
                             :
                             nicSpeed * 1000L * 1000;
    }

    protected NicStatus map(InterfaceStatus interfaceStatus, NicStatus params) {
        return getMapper(InterfaceStatus.class, NicStatus.class).map(interfaceStatus, params);
    }

    protected BootProtocol map(NetworkBootProtocol networkBootProtocol, BootProtocol params) {
        return getMapper(NetworkBootProtocol.class, BootProtocol.class).map(networkBootProtocol, params);
    }

    protected void verifyMaster(HostNic model) {
        assertEquals(MASTER_GUID.toString(), model.getId());
        assertEquals(MASTER_NAME, model.getName());
        assertNotNull(model.getNetwork());
        assertEquals(NETWORK_NAME, model.getNetwork().getName());
        assertNotNull(model.getBonding());
        assertNotNull(model.getBonding().getSlaves());
        assertEquals(1, model.getBonding().getSlaves().getHostNics().size());
        assertEquals(SLAVE_GUID.toString(), model.getBonding().getSlaves().getHostNics().get(0).getId());
        assertNotNull(model.getBonding().getSlaves().getHostNics().get(0).getHref());
    }

    protected void verifySlave(HostNic model) {
        assertEquals(SLAVE_GUID.toString(), model.getId());
        assertEquals(SLAVE_NAME, model.getName());
        assertNotNull(model.getNetwork());
        assertEquals(NETWORK_NAME, model.getNetwork().getName());
        assertEquals(4, model.getLinks().size());
        assertTrue("master".equals(model.getLinks().get(0).getRel()) ||
                   "master".equals(model.getLinks().get(1).getRel()));
        assertNotNull(model.getLinks().get(0).getHref());
    }

    @Override
    protected void verifyCollection(List<HostNic> collection) throws Exception {
        assertNotNull(collection);
        assertEquals(NAMES.length + 2, collection.size());
        for (int i = 0; i < NAMES.length; i++) {
            verifyModel(collection.get(i), i);
        }
        verifyMaster(collection.get(NAMES.length));
        verifySlave(collection.get(NAMES.length + 1));
    }

    @Override
    protected List<HostNic> getCollection() {
        return collection.list().getHostNics();
    }

    protected void setGetVdsQueryExpectations(int times) throws Exception {
        while (times-- > 0) {
            VDS vds = new VDS();
            vds.setClusterId(GUIDS[0]);
            setUpEntityQueryExpectations(VdcQueryType.GetVdsByVdsId,
                    IdQueryParameters.class,
                    new String[] { "Id" },
                    new Object[] { PARENT_GUID },
                    vds);
        }
    }

    protected void setGetNetworksQueryExpectations(int times) throws Exception {
        while (times-- > 0) {
            ArrayList<org.ovirt.engine.core.common.businessentities.network.Network> networks = new ArrayList<>();
            org.ovirt.engine.core.common.businessentities.network.Network network = new org.ovirt.engine.core.common.businessentities.network.Network();
            network.setId(GUIDS[0]);
            network.setName("orcus");
            networks.add(network);
            setUpEntityQueryExpectations(VdcQueryType.GetAllNetworksByClusterId,
                    IdQueryParameters.class,
                    new String[] { "Id" },
                    new Object[] { GUIDS[0] },
                    networks);
        }
    }
}
