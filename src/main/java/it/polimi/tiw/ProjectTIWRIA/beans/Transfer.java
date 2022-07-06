package it.polimi.tiw.ProjectTIWRIA.beans;


import java.sql.Timestamp;

public class Transfer {

	private Timestamp date;
	private double amount; //incoming is positive, outcoming is negative
	private String motivation;
	private String accountCodeOrigin;
	private String accountCodeDest;
	private double balanceOrigin;
	private double balanceDest;
	private int accountId;
	

	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getMotivation() {
		return motivation;
	}
	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}
	public String getAccountCodeOrigin() {
		return accountCodeOrigin;
	}
	public void setAccountCodeOrigin(String accountCodeOrigin) {
		this.accountCodeOrigin = accountCodeOrigin;
	}
	public String getAccountCodeDest() {
		return accountCodeDest;
	}
	public void setAccountCodeDest(String accountCodeDest) {
		this.accountCodeDest = accountCodeDest;
	}
	public double getBalanceOrigin() {
		return balanceOrigin;
	}
	public void setBalanceOrigin(double balanceOrigin) {
		this.balanceOrigin = balanceOrigin;
	}
	public double getBalanceDest() {
		return balanceDest;
	}
	public void setBalanceDest(double balanceDest) {
		this.balanceDest = balanceDest;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	
	
	
	
	
}
