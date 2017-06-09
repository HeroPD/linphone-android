package mn.mobicom.classes;

import mn.mobicom.oauth2.OauthRequest.RequestType;

public class UserControl {

	public static SipUserType userType = SipUserType.DEFAULT;

	public enum SipUserType {

		MOBILEOFFICE, MNP75, DEFAULT

	}
	
	private static String mainURL = "https://api.mobicom.mn/oauth/resourcev2/";//"https://z-api.mobicom.mn/oauth/resourcev2/";

	public static String getSipURL() {

		switch (userType) {
		case MOBILEOFFICE:
			return mainURL + "mo/v1/get_sip";
		case MNP75:
			return mainURL + "voip/v1/sip";
		default:
			return "default";
		}

	}

	public static RequestType getSipTYPE() {

		switch (userType) {
		case MOBILEOFFICE:
			return RequestType.GET;
		case MNP75:
			return RequestType.POST;
		default:
			return RequestType.DEFAULT;
		}
	}

	public static String getLinkedNumbers(int start, int records) {
		return mainURL + "voip/v1/getlinkednumbers?start=" + start
				+ "&records=" + records;
	}

	public static String getAllMessages(int start, int records, String number) {
		return mainURL + "voip/v1/getallmessage?start=" + start + "&records="
				+ records + "&linkednumber=" + number;
	}

	public static String removeLinkedNumber(String number) {
		return mainURL + "voip/v1/deletemessage?linkednumber=" + number;
	}

	public static String removeSms(String smsId , String linkedNumber) {
		return mainURL + "voip/v1/deletemessage?smsIds=" + smsId+"&linkednumber="+linkedNumber;
	}

	public static String getOutbox() {
		return mainURL + "voip/v1/outbox";
	}

	public static String getSend() {
		return mainURL + "voip/v1/send";
	}

	public static String getCallerIdBlock() {
		return mainURL + "mo/v1/get_callinglineiddeliveryblocking";
	}

	public static String getCallerIdBlockPut() {
		return mainURL + "mo/v1/put_callinglineiddeliveryblocking";
	}

	public static String getCallForwardingNoAnswer() {
		return mainURL + "mo/v1/get_callforwardingnoanswer";
	}

	public static String getCallForwardingNoAnswerPut() {
		return mainURL + "mo/v1/put_callforwardingnoanswer";
	}

	public static String getCallForwardingAlways() {
		return mainURL + "mo/v1/get_callforwardingalways";
	}

	public static String getCallForwardingAlwaysPut() {
		return mainURL + "mo/v1/put_callforwardingalways";
	}

	public static String getCallForwardingNotReachable() {
		return mainURL + "mo/v1/get_callforwardingnotreachable";
	}

	public static String getCallForwardingNotReachablePut() {
		return mainURL + "mo/v1/put_callforwardingnotreachable";
	}

	public static String getCallForwardingBusy() {
		return mainURL + "mo/v1/get_callforwardingbusy";
	}

	public static String getCallForwardingBusyPut() {
		return mainURL + "mo/v1/put_callforwardingbusy";
	}

	public static String getMobility() {
		return mainURL + "mo/v1/get_broadworksmobility";
	}

	public static String getMobilityPut() {
		return mainURL + "mo/v1/put_broadworksmobility";
	}

	public static String getSimultaneous() {
		return mainURL + "mo/v1/get_simultaneousringpersonal";
	}

	public static String getSimultaneousPut() {
		return mainURL + "mo/v1/put_simultaneousringpersonal";
	}

	public static String getSequential() {
		return mainURL + "mo/v1/get_sequentialring";
	}

	public static String getSequentialPut() {
		return mainURL + "mo/v1/put_sequentialring";
	}
	
	public static String getCallThrough(){
		return mainURL + "mo/v1/post_callthrough";
	}

	// mnp 75 settings call forwarding

	public static String getCallForwardCheck() {
		return mainURL + "voip/v1/check";
	}

	public static String getCallForwadingOff() {
		return mainURL + "voip/v1/off";
	}

	public static String getCallForwadingOn() {
		return mainURL + "voip/v1/on";
	}

	// mnp75 userinfo

	public static String getMNP75UserInfo() {
		return mainURL + "voip/v1/userinfo";
	}

	public static String getGroupDirectory() {
		return mainURL + "mo/v1/get_group";
	}
	
	public static String getConnect() {
		return mainURL + "connect/v1/voip";
	}
}
