/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.custom.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.CollectionType;

/**
 * @author Gavin King
 * @author Steve Ebersole
 */
@Entity
@Table(name = "UC_BSC_USER")
public class User {
	private String userName;
	private IMyList<Email> emailAddresses = new MyList<Email>();
	private Map sessionData = new HashMap();

	User() {

	}
	public User(String name) {
		userName = name;
	}

	@Id
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@OneToMany( fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true )
	@CollectionType( type = "org.hibernate.test.collection.custom.basic.MyListType" )
	@JoinColumn( name = "userName" )
	@OrderColumn( name = "displayOrder" )
	public List<Email> getEmailAddresses() {
// does not work :(
//	public IMyList<Email> getEmailAddresses() {
		return emailAddresses;
	}
	public void setEmailAddresses(IMyList<Email> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	@Transient
	public Map getSessionData() {
		return sessionData;
	}
	public void setSessionData(Map sessionData) {
		this.sessionData = sessionData;
	}
}
