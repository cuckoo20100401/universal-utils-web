package org.cuckoo.universal.utils.web.auth;

public class VerifyTokenResult {

	private boolean result;
	private int resultCode;
	private String[] currAuthUserRoles = new String[]{};
	private String[] currAuthUserPerms = new String[]{};
	
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String[] getCurrAuthUserRoles() {
		return currAuthUserRoles;
	}
	public void setCurrAuthUserRoles(String[] currAuthUserRoles) {
		this.currAuthUserRoles = currAuthUserRoles;
	}
	public String[] getCurrAuthUserPerms() {
		return currAuthUserPerms;
	}
	public void setCurrAuthUserPerms(String[] currAuthUserPerms) {
		this.currAuthUserPerms = currAuthUserPerms;
	}
}
