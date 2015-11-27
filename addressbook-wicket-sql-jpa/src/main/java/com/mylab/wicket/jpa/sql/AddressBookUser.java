package com.mylab.wicket.jpa.sql;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "addressbookuser", uniqueConstraints = @UniqueConstraint(columnNames = { "username" }) )
@NamedQuery(
        name="findAllUsersWithName",
        query="SELECT u FROM AddressBookUser u WHERE u.username LIKE :username"
)
public class AddressBookUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4389303820316932997L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Version
	@Column(name = "version")
	private int version;
	@Column(length = 50, name = "username")
	private String username;
	@Column(length = 50, name = "password")
	private String password;
	@Column(length = 50, name = "role")
	private String role;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean hasRole(String role) {
		return this.role.equalsIgnoreCase(role);
	}
}
