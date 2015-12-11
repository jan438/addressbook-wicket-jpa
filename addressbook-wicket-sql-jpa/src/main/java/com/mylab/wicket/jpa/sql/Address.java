package com.mylab.wicket.jpa.sql;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "address",
uniqueConstraints=
@UniqueConstraint(columnNames={"street", "zipcode", "city"}))
public class Address implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6219549916699837934L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Version
	@Column(name = "version")
	private int version;
	@Column(length = 50, name = "street")
	private String street;
	@Column(length = 50, name = "zipcode")
	private String zipcode;
	@Column(length = 50, name = "city")
	private String city;
	@Column(length = 50, name = "country")
	private String country;
	@Column(length = 50, name = "isWorkAddress")
	private boolean isWorkAddress;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean getIsWorkAddress() {
		return isWorkAddress;
	}

	public void setIsWorkAddress(boolean isWorkAddress) {
		this.isWorkAddress = isWorkAddress;
	}

	@ManyToOne(optional = false, targetEntity=Contact.class)
	private Contact contact;

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Address() {

	}
}
