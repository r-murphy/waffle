/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2017 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;
import waffle.spring.mappers.ExcludedAuthoritiesMapper;

/**
 * The Class WindowsAuthenticationTokenTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationTokenTests {

    /** The principal. */
    private WindowsPrincipal           principal;

    /** The token. */
    private WindowsAuthenticationToken token;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        final List<String> mockGroups = new ArrayList<>();
        mockGroups.add("group1");
        mockGroups.add("group2");
        final MockWindowsIdentity mockIdentity = new MockWindowsIdentity("localhost\\user1", mockGroups);
        this.principal = new WindowsPrincipal(mockIdentity);
        this.token = new WindowsAuthenticationToken(this.principal);
    }

    /**
     * Test windows authentication token.
     */
    @Test
    public void testWindowsAuthenticationToken() {
        Assert.assertNull(this.token.getCredentials());
        Assert.assertNull(this.token.getDetails());
        Assert.assertTrue(this.token.isAuthenticated());
        Assert.assertEquals("localhost\\user1", this.token.getName());
        final Collection<GrantedAuthority> authorities = this.token.getAuthorities();
        Assert.assertEquals(3, authorities.size());

        final List<String> list = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("ROLE_GROUP1", list.get(0));
        Assert.assertEquals("ROLE_GROUP2", list.get(1));
        Assert.assertEquals("ROLE_USER", list.get(2));
        Assert.assertEquals(this.principal, this.token.getPrincipal());
    }

    /**
     * Test custom granted authority factory.
     */
    @Test
    public void testCustomGrantedAuthorityFactory() {

        final WindowsAuthenticationToken myToken = new WindowsAuthenticationToken(this.principal,
                new FqnGrantedAuthorityFactory(null, false),
                WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITIES_MAPPER,
                null);

        Assert.assertNull(myToken.getCredentials());
        Assert.assertNull(myToken.getDetails());
        Assert.assertTrue(myToken.isAuthenticated());
        Assert.assertEquals("localhost\\user1", myToken.getName());
        final Collection<GrantedAuthority> authorities = myToken.getAuthorities();
        Assert.assertEquals(2, authorities.size());

        final List<String> list = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("group1", list.get(0));
        Assert.assertEquals("group2", list.get(1));
        Assert.assertEquals(this.principal, myToken.getPrincipal());
    }

    @Test
    public void testCustomGrantedAuthoritiesMapper() {

        final WindowsAuthenticationToken myToken = new WindowsAuthenticationToken(this.principal,
                WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY,
                new ExcludedAuthoritiesMapper("ROLE_GROUP1"),
                null);

        Assert.assertNull(myToken.getCredentials());
        Assert.assertNull(myToken.getDetails());
        Assert.assertTrue(myToken.isAuthenticated());
        Assert.assertEquals("localhost\\user1", myToken.getName());
        final Collection<GrantedAuthority> authorities = myToken.getAuthorities();
        Assert.assertEquals(1, authorities.size());

        final List<String> list = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("ROLE_GROUP2", list.get(0));
        Assert.assertEquals(this.principal, myToken.getPrincipal());
    }

    /**
     * Test authenticated.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticated() {
        Assert.assertTrue(this.token.isAuthenticated());
        this.token.setAuthenticated(true);
    }
}
