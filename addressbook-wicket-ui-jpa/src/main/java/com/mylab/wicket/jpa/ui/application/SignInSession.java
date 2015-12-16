package com.mylab.wicket.jpa.ui.application;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import com.mylab.wicket.jpa.sql.AddressBookUser;

public final class SignInSession extends AuthenticatedWebSession {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AddressBookUser userInSession;

	/**
	 * Constructor
	 * 
	 * @param request
	 */
	protected SignInSession(Request request) {

		super(request);
	}
	
	@PersistenceContext
	public static AddressBookUser query_name(String username) {
		AddressBookUser user = null;
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<AddressBookUser> users = em.createNamedQuery("findAllUsersWithName")
	            .setParameter("username", username)
	            .getResultList();
		if ((users != null) && !users.isEmpty()) {
			user = users.get(0);
			System.out.println("user with name " + user.getUsername() + " found");
		} else {
			System.out.println("user with name " + username + " not found");
		}
		em.getTransaction().commit();
		em.close();
		return user;
	}
	/**
	 * Checks the given username and password, returning a AddressBookUser object if the
	 * username and password identify a valid user.
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 * @return True if the AddressBookUser was authenticated
	 */
	@PersistenceContext
	public final boolean authenticate(final String username, final String password) {

		if (userInSession == null) {

			AddressBookUser u = query_name(username);

			if (u != null) {
				if (u.getPassword().equalsIgnoreCase(password)) {
					userInSession = u;
				}
			}
		}

		return userInSession != null;
	}

	/**
	 * @return User
	 */
	public AddressBookUser getUser() {
		return userInSession;
	}

	/**
	 * @param user
	 *            New user
	 */
	public void setUser(final AddressBookUser user) {
		this.userInSession = user;
	}

	/**
	 * @see org.apache.wicket.authentication.AuthenticatedWebSession#getRoles()
	 */
	@Override
	public Roles getRoles() {

		Roles roles = new Roles();

		// If AddressBookUser is signed in add the relative role:
		if (isSignedIn())
			roles.add("SIGNED_IN");

		// Add the user's role:
		if (userInSession != null && userInSession.getRole().equals("ADMIN")) {
			roles.add("ADMIN");
		}
		if (userInSession != null && userInSession.getRole().equals("USER")) {
			roles.add("USER");
		}
		if (userInSession != null && userInSession.getRole().equals("GUEST")) {
			roles.add("GUEST");
		}

		return roles;
	}

	@Override
	public void signOut() {
		super.signOut();
		userInSession = null;
	}
}