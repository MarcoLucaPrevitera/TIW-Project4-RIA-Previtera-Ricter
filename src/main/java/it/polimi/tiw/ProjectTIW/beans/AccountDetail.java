package it.polimi.tiw.ProjectTIW.beans;

import java.util.List;

public class AccountDetail {
	private Account account;
	private List<Transfer> transferList;
	
	
	public AccountDetail(Account account, List<Transfer> transferList) {
		this.account = account;
		this.transferList = transferList;
	}
	
	public Account getAccount() {
		return account;
	}
	public List<Transfer> getTransferList() {
		return transferList;
	}
}
