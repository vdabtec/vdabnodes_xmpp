package vdab.extnodes.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import com.lcrc.af.AnalysisEvent;
import com.lcrc.af.AnalysisTarget;
import com.lcrc.af.constants.IconCategory;

public class XMPPTarget extends AnalysisTarget {

	private String c_Host = "talk.google.com";
	private Integer c_Port = Integer.valueOf(5222);
	private String c_User; 
	private String c_Password;
	private String c_Service = "gmail.com";
	private String c_Receiver; // "vdabtec@gmail.com";
	
	private XMPPConnection c_Connection;
	private ConnectionConfiguration c_Config =null;
	private Presence c_Presence;
	// ATTRIBUTES ---------------------------------------------------
	public Integer get_IconCode(){
		return IconCategory.NODE_XMPP;
	}
	public String get_Host(){
		return c_Host;
	}
	public void set_Host(String host){
		c_Host = host;
	}
	public Integer get_Port(){
		return c_Port;
	}
	public void set_Port(Integer port){
		c_Port = port;
	}
	public String get_User(){
		return c_User;
	}
	public void set_User( String user){
		c_User = user;
	}
	public String get_Password(){
		return c_Password;
	}
	public void set_Password( String password){
		c_Password = password;
	}
	public String get_Service(){
		return c_Service;
	}
	public void set_Service( String service){
		c_Service = service;
	}
	public void set_Receiver(String receiver){
		c_Receiver=receiver;
	}
	public String get_Receiver(){
		return c_Receiver;
	}
	// ANALYSIS NODE Methods -----------------------------------------
	public void _init(){
		super._init();
	}
	public void _reset(){
		super._reset();
		connectToXMPP();
	}
	public void _start(){
		connectToXMPP();
		super._start();
	}
	public synchronized void processEvent(AnalysisEvent event){
			 String txt = event.getStringData();
			 Message msg = new Message(c_Receiver, Message.Type.chat);
	     	 msg.setBody(txt);
		     c_Connection.sendPacket(msg);

   	}
	// SUPPORTING Methods -----------------------------------------------
	private void connectToXMPP(){ 
		
		try {
		c_Config = new ConnectionConfiguration(c_Host, c_Port.intValue(), c_Service);
		c_Connection = new XMPPConnection(c_Config);
		c_Connection.connect();
		c_Connection.login(c_User, c_Password);
		c_Presence = new Presence(Presence.Type.available);
		c_Connection.sendPacket(c_Presence);

		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		c_Connection.addPacketSendingListener(new PacketListener() {
			public void processPacket(Packet packet) {
				Message message = (Message) packet;
			}
		}
		, filter);
		} catch (Exception ex) {
			setError("Unable to establish XMPP connection e>"+ex);
			return; 
		}
		
	}
}
