package com.chatopera.cc.app.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "uk_pay_account", schema = "contactcenter", catalog = "")
public class UkPayAccountEntity {
	private int id;
	private Integer payFuctionId;
	private String account;
	private String accountUsername;
	private String accountBank;
	private Integer weights;
	private Integer type;
	private Integer status;
	private String createUser;
	private Date createTime;
	private String updateUser;
	private Date updateTime;
	private String lockUser;
	private Date lockTime;
	private String unlockUser;
	private Date unlockTime;
	private String deleteUser;
	private Date deleteTime;
	private String payFuctionName;

	@Transient
	public String getPayFuctionName() {
		return payFuctionName;
	}

	public void setPayFuctionName(String payFuctionName) {
		this.payFuctionName = payFuctionName;
	}

	@Id
	@Column(name = "id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "pay_fuction_id", nullable = true)
	public Integer getPayFuctionId() {
		return payFuctionId;
	}

	public void setPayFuctionId(Integer payFuctionId) {
		this.payFuctionId = payFuctionId;
	}

	@Basic
	@Column(name = "account", nullable = true, length = 255)
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Basic
	@Column(name = "account_username", nullable = true, length = 255)
	public String getAccountUsername() {
		return accountUsername;
	}

	public void setAccountUsername(String accountUsername) {
		this.accountUsername = accountUsername;
	}

	@Basic
	@Column(name = "account_bank", nullable = true, length = 255)
	public String getAccountBank() {
		return accountBank;
	}

	public void setAccountBank(String accountBank) {
		this.accountBank = accountBank;
	}

	@Basic
	@Column(name = "weights", nullable = true)
	public Integer getWeights() {
		return weights;
	}

	public void setWeights(Integer weights) {
		this.weights = weights;
	}

	@Basic
	@Column(name = "type", nullable = true)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Basic
	@Column(name = "status", nullable = true)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Basic
	@Column(name = "create_user", nullable = true, length = 255)
	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	@Basic
	@Column(name = "create_time", nullable = true)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Basic
	@Column(name = "update_user", nullable = true, length = 255)
	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	@Basic
	@Column(name = "update_time", nullable = true)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Basic
	@Column(name = "lock_user", nullable = true, length = 255)
	public String getLockUser() {
		return lockUser;
	}

	public void setLockUser(String lockUser) {
		this.lockUser = lockUser;
	}

	@Basic
	@Column(name = "lock_time", nullable = true)
	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	@Basic
	@Column(name = "unlock_user", nullable = true, length = 255)
	public String getUnlockUser() {
		return unlockUser;
	}

	public void setUnlockUser(String unlockUser) {
		this.unlockUser = unlockUser;
	}

	@Basic
	@Column(name = "unlock_time", nullable = true)
	public Date getUnlockTime() {
		return unlockTime;
	}

	public void setUnlockTime(Date unlockTime) {
		this.unlockTime = unlockTime;
	}

	@Basic
	@Column(name = "delete_user", nullable = true, length = 255)
	public String getDeleteUser() {
		return deleteUser;
	}

	public void setDeleteUser(String deleteUser) {
		this.deleteUser = deleteUser;
	}

	@Basic
	@Column(name = "delete_time", nullable = true)
	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UkPayAccountEntity that = (UkPayAccountEntity) o;
		return id == that.id &&
				Objects.equals(payFuctionId, that.payFuctionId) &&
				Objects.equals(account, that.account) &&
				Objects.equals(accountUsername, that.accountUsername) &&
				Objects.equals(weights, that.weights) &&
				Objects.equals(type, that.type) &&
				Objects.equals(status, that.status) &&
				Objects.equals(createUser, that.createUser) &&
				Objects.equals(createTime, that.createTime) &&
				Objects.equals(updateUser, that.updateUser) &&
				Objects.equals(updateTime, that.updateTime) &&
				Objects.equals(lockUser, that.lockUser) &&
				Objects.equals(lockTime, that.lockTime) &&
				Objects.equals(unlockUser, that.unlockUser) &&
				Objects.equals(unlockTime, that.unlockTime) &&
				Objects.equals(deleteUser, that.deleteUser) &&
				Objects.equals(deleteTime, that.deleteTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, payFuctionId, account, accountUsername, weights, type, status, createUser, createTime, updateUser, updateTime, lockUser, lockTime, unlockUser, unlockTime, deleteUser, deleteTime);
	}
}
