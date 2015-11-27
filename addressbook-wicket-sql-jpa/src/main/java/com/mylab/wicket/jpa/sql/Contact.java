package com.mylab.wicket.jpa.sql;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "contact", uniqueConstraints = @UniqueConstraint(columnNames = { "mailAddress" }) )
@NamedQuery(name = "findAllContacts", query = "SELECT c FROM Contact c")
public class Contact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1405085871717174911L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Version
	@Column(name = "version")
	private int version;
	@Column(length = 50, name = "firstName")
	private String firstName;
	@Column(length = 50, name = "lastName")
	private String lastName;
	@Column(length = 50, name = "mailAddress")
	private String mailAddress;
	@Column(name = "dateOfBirth")
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Address> addresses = new HashSet<Address>();

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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String name) {
		this.firstName = name;
	}

	public Contact addAddress(final Address address) {
		this.addresses.add(address);
		address.setContact(this);
		return this;
	}

	public Contact removeAddress(final Address address) {
		this.addresses.remove(address);
		return this;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

}
