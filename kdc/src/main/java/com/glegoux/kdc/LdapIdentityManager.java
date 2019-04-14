package com.glegoux.kdc;

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldif.LDIFException;

public class LdapIdentityManager implements AutoCloseable {

  public LDAPConnection connection;
  public String groupDN = "ou=groups,dc=example,dc=com";
  public String userDN = "ou=users,dc=example,dc=com";


  public static LdapIdentityManager build()
    throws LDAPException {

    LdapIdentityManager groupManager = new LdapIdentityManager();
    groupManager.connection = new LDAPConnection("localhost", 1389);
    return groupManager;

  }

  @Override
  public void close() {

    if (this.connection != null) {
      this.connection.close();
    }

  }


  public LDAPResult createGroup(String groupName)
    throws LDAPException, LDIFException {

    AddRequest addRequest = new AddRequest(
      String.format("dn: %s", getGroupDN(groupName)),
      "objectClass: top",
      "objectClass: groupOfNames",
      "member:",
      String.format("cn: %s", groupName)
    );

    return connection.add(addRequest);

  }


  public LDAPResult addMember(String groupName, String userName)
    throws LDAPException, LDIFException {

    ModifyRequest modifyRequest = new ModifyRequest(

    );

    return connection.modify(modifyRequest);

  }


  public LDAPResult removeMember(String groupName, String userName)
    throws LDAPException, LDIFException {

    ModifyRequest modifyRequest = new ModifyRequest(

    );

    return connection.modify(modifyRequest);

  }

  public String[] getGroupMembers(String groupName)
    throws LDAPException {

    SearchRequest ldapRequest = new SearchRequest(
      groupDN,
      SearchScope.SUB,
      String.format("(cn=%s)", groupName),
      "member"
    );
    return connection.search(ldapRequest)
      .getSearchEntries()
      .get(0)
      .getAttribute("member")
      .getValues();

  }


  public LDAPResult deleteGroup(String groupName)
    throws LDAPException {

    DeleteRequest deleteRequest = new DeleteRequest(getGroupDN(groupName));
    return connection.delete(deleteRequest);

  }


  private String getGroupDN(String groupName) {

    return String.format("cn=%s,%s", groupName, groupDN);

  }


  public static void main(String[] args)
    throws Exception {

    LdapIdentityManager ldapIdentityManager = LdapIdentityManager.build();

    //ldapIdentityManager.createGroup("hello");
    //ldapIdentityManager.deleteGroup("hello");
    System.out.println(ldapIdentityManager.getGroupMembers("groupa")[0]);

  }

}
