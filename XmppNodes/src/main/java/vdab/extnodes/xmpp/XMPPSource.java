/*LICENSE*
 * Copyright (C) 2013 - 2018 MJA Technology LLC 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package vdab.extnodes.xmpp;

import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.lcrc.af.AnalysisEvent;
import com.lcrc.af.AnalysisSource;
import com.lcrc.af.constants.IMPresenceType;
import com.lcrc.af.constants.IconCategory;

public class XMPPSource extends AnalysisSource {

	private final static Integer PRESENCE_PRIORITY = Integer.valueOf(100);
	private String c_Host = "talk.google.com";
	private Integer c_Port = Integer.valueOf(5222);
	private String c_User; 
	private String c_Password; ;
	private String c_Service = "gmail.com";
	private String c_OnlyFrom;
	private Integer c_StartingPresence;

	private XMPPConnection c_Connection;
	private ConnectionConfiguration c_Config =null;
	private Roster c_Roster;
	private Presence c_Presence;
	protected static final String EVENTNAME_PRESENCECHANGE = "PresenceChange";
	

	public XMPPSource(){
		super();
	}

	// ATTRIBUTE Methods
	public Integer get_IconCode(){
		return IconCategory.NODE_XMPP;
	}
	public String get_Host(){
		return c_Host;
	}
	public void set_Host(String host){
		c_Host = host;
	}
	public String get_OnlyFrom(){
		return c_OnlyFrom;
	}
	public void set_OnlyFrom(String from){
		c_OnlyFrom = from;
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
	public Integer get_StartingPresence(){
		return c_StartingPresence;
	}
	public void set_StartingPresence(Integer p){
		c_StartingPresence = p;
	}
	// ANALYSIS NODE Methods
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
	// SUPPORTING Methods --------------------------------------
	// HACKALERT - This definitely needs to clean up previous resources when recalled.
	public void connectToXMPP(){
		try {

			// Set up the connection, login and create presences
			c_Config = new ConnectionConfiguration(c_Host, c_Port.intValue(), c_Service);
			c_Connection = new XMPPConnection(c_Config);
			c_Connection.connect();
			c_Connection.login(c_User, c_Password);
			
			if (c_StartingPresence != null) {
				String status = "";
				int mode = c_StartingPresence.intValue();
				status = IMPresenceType.getEnum().getLabel(mode);
				switch (mode){
				case IMPresenceType.AVAILABLE:
					c_Presence = new Presence(Presence.Type.available, status, PRESENCE_PRIORITY, Presence.Mode.available);
					break;
					
				case IMPresenceType.DND:
					c_Presence = new Presence(Presence.Type.available, status, PRESENCE_PRIORITY, Presence.Mode.dnd);
					break;		
					
				case IMPresenceType.AWAY:
					c_Presence = new Presence(Presence.Type.available, status, PRESENCE_PRIORITY , Presence.Mode.away);
					break;	

				case IMPresenceType.XA:
					c_Presence = new Presence(Presence.Type.available, status, PRESENCE_PRIORITY , Presence.Mode.xa);
					break;	
					
				case IMPresenceType.CHAT:
					c_Presence = new Presence(Presence.Type.available, status, PRESENCE_PRIORITY , Presence.Mode.chat);
					break;		
										
				default:
					c_Presence = new Presence(Presence.Type.available);
					break;
				}
			}
			else {
				c_Presence = new Presence(Presence.Type.available);
			}
			c_Connection.sendPacket(c_Presence);
			c_Roster = c_Connection.getRoster();   
			// Create the roster listener
			c_Roster.addRosterListener(new RosterListener() {
				// Ignored events public void entriesAdded(Collection<String> addresses) {}
				public void entriesDeleted(Collection<String> addresses) {
				}
				public void entriesUpdated(Collection<String> addresses) {
				}
				public void presenceChanged(Presence presence) {
					if (isRunning() )
						publish(new AnalysisEvent(XMPPSource.this, EVENTNAME_PRESENCECHANGE,presence.getFrom()+" "+presence.getType()+" "+presence.getStatus()));
				}
				public void entriesAdded(Collection<String> arg0) {
				}
			});

			// Create a packet listener	
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			c_Connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					String fromName = StringUtils.parseBareAddress(message.getFrom());					
						if (isRunning() && (message.getBody() != null)){
	
							if (c_OnlyFrom == null || fromName.equalsIgnoreCase(fromName)){
								AFXmppMsg afMsg = new AFXmppMsg(fromName, message.getBody());
								publish(new AnalysisEvent(XMPPSource.this, afMsg.getExpandedData()));
							}
						}
				}
			}, filter);

		} catch (Exception e) {
			setError("Failed connection to XMPP Server e>"+e.toString());
			return; 
		}
	}
}
