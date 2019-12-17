package com.chatopera.cc.app.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "uk_pay_msg", schema = "contactcenter", catalog = "")
public class UkPayMsgEntity {
	private int id;
	private String code;
	private String name;
	private String msg;
	private Integer type;
	private Integer status;
	private String createUser;
	private Date createTime;
	private String updateUser;
	private Date updateTime;
	private String deleteUser;
	private Date deleteTime;

	@Id
	@Column(name = "id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Basic
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "msg")
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Basic
	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Basic
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Basic
	@Column(name = "create_user")
	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	@Basic
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Basic
	@Column(name = "update_user")
	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	@Basic
	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Basic
	@Column(name = "delete_user")
	public String getDeleteUser() {
		return deleteUser;
	}

	public void setDeleteUser(String deleteUser) {
		this.deleteUser = deleteUser;
	}

	@Basic
	@Column(name = "delete_time")
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
		UkPayMsgEntity that = (UkPayMsgEntity) o;
		return id == that.id &&
				Objects.equals(code, that.code) &&
				Objects.equals(name, that.name) &&
				Objects.equals(msg, that.msg) &&
				Objects.equals(type, that.type) &&
				Objects.equals(status, that.status) &&
				Objects.equals(createUser, that.createUser) &&
				Objects.equals(createTime, that.createTime) &&
				Objects.equals(updateUser, that.updateUser) &&
				Objects.equals(updateTime, that.updateTime) &&
				Objects.equals(deleteUser, that.deleteUser) &&
				Objects.equals(deleteTime, that.deleteTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, code, name, msg, type, status, createUser, createTime, updateUser, updateTime, deleteUser, deleteTime);
	}
}
