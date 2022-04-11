package ie.ul.ethics.scieng.users.models;

import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import ie.ul.ethics.scieng.users.authorization.Roles;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * This class represents a user of the system.
 * A user consists of their account used for authentication and other details
 */
@Entity
@Table(name="UserProfiles")
public class User {
    /**
     * The username should be the same as the username on their account
     */
    @Id
    private String username;
    /**
     * This field holds the user's full name
     */
    private String name;
    /**
     * This field holds the user's account used for authentication
     */
    @OneToOne(fetch = FetchType.EAGER)
    private Account account;
    /**
     * The department this user is based in
     */
    private String department;
    /**
     * The User's role
     */
    @OneToOne(fetch = FetchType.EAGER)
    private Role role;

    /**
     * Create a default user
     */
    public User() {
        this(null, null, null, null, Roles.APPLICANT);
    }

    /**
     * Creates a user setting the username to the same username as the account passed in
     * @param name the name of the user
     * @param account the account of the user. The username is set based from this account
     * @param department the department name the user is associated with
     * @param role the role the user belongs to, determining their permissions
     */
    public User(String name, Account account, String department, Role role) {
        this(null, name, null, department, role);
        setAccount(account);
    }

    /**
     * Creates a user with no account but just a username; This should be used when creating a new user and to load the user
     * account. It should not be used to create an existing account
     * @param username the username of the new user.
     * @param name the name for the user
     * @param department the department the user is situated in
     */
    public User(String username, String name, String department) {
        this(username, name, null, department, Roles.APPLICANT);
    }

    /**
     * Creates a user with the provided parameters
     * @param username the username of the user
     * @param name the name of the user
     * @param account the account for the user
     * @param department the department the user belongs to
     * @param role the role the user owns
     */
    private User(String username, String name, Account account, String department, Role role) {
        this.username = username;
        this.name = name;
        this.account = account;
        this.department = department;
        this.role = role;
    }

    /**
     * Retrieve the username of the user
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user by changing the username of the account also
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;

        if (this.account != null) {
            this.account.setUsername(username);
        }
    }

    /**
     * Retrieve the user's full name
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Set the user's name
     * @param name the user's new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieve the account of the user used for authentication
     * @return the account used for authentication by the user
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the user's account and username from the account
     * @param account the new account to set
     */
    public void setAccount(Account account) {
        if (account != null)
            username = account.getUsername();

        this.account = account;
    }

    /**
     * Retrieve the department this user is based in
     * @return the user's department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Set the user's department
     * @param department the department the user belongs to
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Get the user's role
     * @return the role of the user
     */
    public Role getRole() {
        return role;
    }

    /**
     * Set the role of the user
     * @param role the user's role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Check if the provided object is equal to this User
     * @param o the object to check
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(name, user.name) && Objects.equals(account, user.account)
                && Objects.equals(department, user.department) && Objects.equals(role, user.role);
    }

    /**
     * Generate the hash code for this User
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, name, account, department, role);
    }
}
