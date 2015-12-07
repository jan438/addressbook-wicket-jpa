package com.mylab.wicket.jpa.sql;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.mylab.wicket.jpa.sql.Address;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.Contact;

public class JPAFunctions {

	@PersistenceContext
	public static void remove_user(long id) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		AddressBookUser user = em.find(AddressBookUser.class, id);
		if (user != null) {
			em.remove(user);
			em.getTransaction().commit();
			System.out.println("user with id " + id + " removed");
		} else {
			System.out.println("user with id " + id + " not found");
		}
		em.close();
	}

	@PersistenceContext
	public static void persist_newuser(AddressBookUser user) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
		em.close();
	}

	@PersistenceContext
	public static void persist_existinguser(AddressBookUser dbuser) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		AddressBookUser user = em.find(AddressBookUser.class, dbuser.getId());
		user.setPassword(dbuser.getPassword());
		user.setRole(dbuser.getRole());
		em.getTransaction().commit();
		em.close();
	}

	@PersistenceContext
	public static boolean query_id_user(long id) {
		boolean success = false;
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		AddressBookUser user = em.find(AddressBookUser.class, id);
		if (user != null) {
			System.out.println("user with id " + id + " name " + user.getUsername());
			success = true;
		} else {
			System.out.println("user with id " + id + " not found");
		}
		em.getTransaction().commit();
		em.close();
		return success;
	}

	@PersistenceContext
	public static boolean query_name_user(String username) {
		boolean success = false;
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<AddressBookUser> users = em.createNamedQuery("findAllUsersWithName").setParameter("username", username)
				.getResultList();
		if ((users != null) && (!users.isEmpty())) {
			AddressBookUser user = users.get(0);
			System.out.println("user with name " + user.getUsername() + " found");
			success = true;
		} else {
			System.out.println("user with name " + username + " not found");
		}
		em.getTransaction().commit();
		em.close();
		return success;
	}

	@PersistenceContext
	public static List<AddressBookUser> getAllUsers() {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<AddressBookUser> users = em.createNamedQuery("findAllUsers").getResultList();
		if (users != null) {
			System.out.println(users.toString());
		} else {
			System.out.println("no users found");
		}
		em.getTransaction().commit();
		em.close();
		return users;
	}

	@PersistenceContext
	public static List<AddressBookUser> getUsers(String s) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<AddressBookUser> users = em.createNamedQuery("findAllUsersWithName").setParameter("username", s)
				.getResultList();
		if (users != null) {
			AddressBookUser user = users.get(0);
			System.out.println("user with name " + user.getUsername() + " found");
		} else {
			System.out.println("user with name " + s + " not found");
		}
		em.getTransaction().commit();
		em.close();
		return users;
	}

	@PersistenceContext
	public static void remove_contact(long id) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		Contact contact = em.find(Contact.class, id);
		if (contact != null) {
			em.remove(contact);
			em.getTransaction().commit();
			System.out.println("contact with id " + id + " removed");
		} else {
			System.out.println("contact with id " + id + " not found");
		}
		em.close();
	}

	@PersistenceContext
	public static boolean query_mail_existingcontacts(String mailAddress) {
		boolean success = false;
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<Contact> contacts = em.createNamedQuery("findAllContactsWithMailAddress")
				.setParameter("mailAddress", mailAddress).getResultList();
		if ((contacts != null) && (!contacts.isEmpty())) {
			Contact contact = contacts.get(0);
			System.out.println("contact with mailaddress " + contact.getMailAddress() + " found "
					+ contact.getFirstName() + " " + contact.getLastName());
			success = true;
		} else {
			System.out.println("contact with mailaddress " + mailAddress + " not found");
		}
		em.getTransaction().commit();
		em.close();
		return success;
	}

	@PersistenceContext
	public static boolean query_mail_existingcontact(Contact dbcontact) {
		boolean success = false;
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		String mailaddress = dbcontact.getMailAddress();
		@SuppressWarnings("unchecked")
		List<Contact> contacts = em.createNamedQuery("findAllContactsWithMailAddress")
				.setParameter("mailAddress", mailaddress).getResultList();
		if ((contacts != null) && (!contacts.isEmpty())) {
			Contact contact = contacts.get(0);
			if (contact.getId() != dbcontact.getId()) {
				System.out.println("contact with mailaddress " + contact.getMailAddress() + " found "
						+ contact.getFirstName() + " " + contact.getLastName());
				success = true;
			}
		} else {
			System.out.println("contact with mailaddress " + mailaddress + " not found");
		}
		em.getTransaction().commit();
		em.close();
		return success;
	}

	@PersistenceContext
	public static boolean persist_newcontact(Contact contact) {
		boolean success = false;
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(contact);
		em.getTransaction().commit();
		success = true;
		em.close();
		return success;
	}

	@PersistenceContext
	public static boolean persist_existingcontact(Contact dbcontact) {
		boolean success = false;
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		Contact contact = em.find(Contact.class, dbcontact.getId());
		contact.setAddresses(dbcontact.getAddresses());
		contact.setDateOfBirth(dbcontact.getDateOfBirth());
		contact.setMailAddress(dbcontact.getMailAddress());
		contact.setFirstName(dbcontact.getFirstName());
		contact.setLastName(dbcontact.getLastName());
		em.persist(contact);
		em.getTransaction().commit();
		success = true;
		em.close();
		return success;
	}

	@PersistenceContext
	public static void query_id_contact(long id) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		AddressBookUser user = em.find(AddressBookUser.class, id);
		if (user != null) {
			System.out.println("user with id " + id + " name " + user.getUsername());
		} else {
			System.out.println("user with id " + id + " not found");
		}
		em.getTransaction().commit();
		em.close();
	}

	@PersistenceContext
	public static void query_name_contact(String username) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<AddressBookUser> users = em.createNamedQuery("findAllUsersWithName").setParameter("username", username)
				.getResultList();
		if (users != null) {
			AddressBookUser user = users.get(0);
			System.out.println("user with name " + user.getUsername() + " found");
		} else {
			System.out.println("user with name " + username + " not found");
		}
		em.getTransaction().commit();
		em.close();
	}

	@PersistenceContext
	public static List<Contact> getContacts() {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<Contact> contacts = em.createNamedQuery("findAllContacts").getResultList();
		if (contacts != null) {
			System.out.println(contacts.toString());
		} else {
			System.out.println("no contacts found");
		}
		em.getTransaction().commit();
		em.close();
		return contacts;
	}

	@PersistenceContext
	public static boolean remove_address(long id) {
		boolean success = false;
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		Address address = em.find(Address.class, id);
		if (address != null) {
			Contact contact = address.getContact();
			contact.removeAddress(address);
			em.persist(contact);
			em.remove(address);
			success = true;
			System.out.println("address with id " + id + " removed");
		} else {
			System.out.println("address with id " + id + " was not found");
		}
		em.getTransaction().commit();
		em.close();
		return success;
	}

	@PersistenceContext
	public static void persist_newaddress(Address address) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(address);
		Contact contact = em.find(Contact.class, address.getContact().getId());
		em.persist(contact);
		em.getTransaction().commit();
		em.close();
	}

	@PersistenceContext
	public static void persist_existingaddress(Address address) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		Address dbaddress = em.find(Address.class, address.getId());
		dbaddress.setStreet(address.getStreet());
		dbaddress.setZipcode(address.getZipcode());
		dbaddress.setCity(address.getCity());
		dbaddress.setCountry(address.getCountry());
		dbaddress.setIsWorkAddress(address.getIsWorkAddress());
		em.persist(dbaddress);
		em.getTransaction().commit();
		em.close();
	}

	@PersistenceContext
	public static Set<Address> getAddresses(long id) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getEntityManagerFactory().getCache().evictAll();
		em.getTransaction().begin();
		Contact contact = em.find(Contact.class, id);
		Set<Address> addresses = contact.getAddresses();
		em.getTransaction().commit();
		em.close();
		return addresses;
	}

	public static void main(String[] args) {
		// remove_user(1234);
		// AddressBookUser user1 = new AddressBookUser();
		// user1.setPassword("ndrlnd17");
		// user1.setUsername("jan");
		// user1.setRole("ADMIN");
		// user1.setVersion(1);
		// persist_user(user1);
		// AddressBookUser user2 = new AddressBookUser();
		// user2.setPassword("ndrlnd17");
		// user2.setUsername("frans");
		// user2.setRole("GUEST");
		// user2.setVersion(1);
		// persist_user(user2);
		query_id_user(1);
		query_id_user(2);
		query_name_user("jan");
		query_name_user("frans");
		List<Contact> contacts = getContacts();
		Iterator<Contact> cit = contacts.iterator();
		while (cit.hasNext()) {
			Contact contact = cit.next();
			System.out.println(contact.getFirstName() + " " + contact.getLastName());
			Set<Address> addresses = contact.getAddresses();
			Iterator<Address> ait = addresses.iterator();
			while (ait.hasNext()) {
				Address address = ait.next();
				System.out.println(address.getStreet() + " " + address.getZipcode() + " " + address.getCity() + " "
						+ address.getCountry());
			}
		}
	}
}
