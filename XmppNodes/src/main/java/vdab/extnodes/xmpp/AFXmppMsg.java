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

import com.lcrc.af.AnalysisCompoundData;
import com.lcrc.af.AnalysisData;
import com.lcrc.af.datatypes.AF_ADType_A;

public class AFXmppMsg extends AF_ADType_A{
	private static final long serialVersionUID = -2529274986662147982L;

	private String c_From;
	private String c_Message;

	private final static String LABEL_XMPP = "xmpp";
	private final static String LABEL_FROM  = "From";
	private final static String LABEL_MESSAGE = "Message";

	public final static String PATH_FROM = LABEL_XMPP +"."+LABEL_FROM;
	public final static String PATH_MESSAGE = LABEL_XMPP +"."+LABEL_MESSAGE;

	public static boolean isXmppAnalysisData(AnalysisData ad){
		if (!ad.getLabel().equals(LABEL_XMPP))
			return false;
		return true;
	}
	public AFXmppMsg(String from, String msg){
		c_From = from;
		c_Message = msg;
	}
	public AnalysisData getAnalysisData(){
		AnalysisCompoundData acd = new AnalysisCompoundData(LABEL_XMPP);
		acd.addAnalysisData(LABEL_FROM, c_From);
		acd.addAnalysisData(LABEL_MESSAGE, c_Message);
		return acd;			
	}
	@Override
	public AnalysisData getExpandedData(String label) {
		return getAnalysisData();
	}
}
