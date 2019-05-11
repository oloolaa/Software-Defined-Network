package net.floodlightcontroller.l2l3tracker;

import java.util.Collection;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.TransportPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;
import java.util.ArrayList;
import net.floodlightcontroller.mactracker.MACTracker;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2L3Tracker implements IFloodlightModule, IOFMessageListener {
	protected static Logger logger;
	protected IFloodlightProviderService floodlightProvider;
	
	@Override
	public String getName() {
		return L2L3Tracker.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		switch (msg.getType()) {
			case PACKET_IN:
				logger.info("Received a PACKET_IN message!");
				Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
				
				logger.info("Switch: "+ sw.getId().toString());
	
				MacAddress srcMac = eth.getSourceMACAddress();
				MacAddress desMac = eth.getDestinationMACAddress();
				logger.info("MAC adddress: " + srcMac.toString() + " -> " + desMac.toString() + ".");
				
				if (eth.getEtherType() == EthType.IPv4) {
					logger.info("This is an IPv4 packet.");
					IPv4 ipv4 = (IPv4) eth.getPayload();
					
					IPv4Address dstIp = ipv4.getDestinationAddress();
					IPv4Address srcIp = ipv4.getSourceAddress();
					
					logger.info("IP adddress: " + srcIp.toString() + " -> " + dstIp.toString() + ".");
					
					if (ipv4.getProtocol().equals(IpProtocol.TCP)) {
						logger.info("This is a TCP packet.");
						TCP tcp = (TCP) ipv4.getPayload();
	
						TransportPort srcPort = tcp.getSourcePort();
						TransportPort dstPort = tcp.getDestinationPort();
						
						logger.info("Port: " + srcPort.toString() + " -> " + dstPort.toString() + ".");
					} else if (ipv4.getProtocol().equals(IpProtocol.UDP)) {
						logger.info("This is a UDP packet.");
						UDP udp = (UDP) ipv4.getPayload();
	
						TransportPort srcPort = udp.getSourcePort();
						TransportPort dstPort = udp.getDestinationPort();
						
						logger.info("Port: " + srcPort.toString() + " -> " + dstPort.toString() + ".");
					} else if (ipv4.getProtocol().equals(IpProtocol.ICMP)) {
						logger.info("This is a ICMP packet.");
					}
	
				} else if (eth.getEtherType() == EthType.ARP) {
					logger.info("This is an ARP packet.");
					ARP arp = (ARP) eth.getPayload();
					logger.info("Here is the ARP payload: " + arp.toString());
	
				} else {
					/* Unhandled ethertype */
				}
				break;
			default:
				break;
		}
		return Command.CONTINUE;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(MACTracker.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}

}
