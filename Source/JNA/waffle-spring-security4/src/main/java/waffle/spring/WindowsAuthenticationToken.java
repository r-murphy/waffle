/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
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
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;

import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.WindowsAccount;

/**
 * A Windows authentication token.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationToken implements Authentication {

    /** The Constant serialVersionUID. */
    private static final long                    serialVersionUID                   = 1L;

    /**
     * The {@link GrantedAuthorityFactory} that is used by default if a custom one is not specified. This default
     * {@link GrantedAuthorityFactory} is a {@link FqnGrantedAuthorityFactory} with prefix {@code "ROLE_"} and will
     * convert the fqn to uppercase
     */
    public static final GrantedAuthorityFactory  DEFAULT_GRANTED_AUTHORITY_FACTORY  = new FqnGrantedAuthorityFactory(
            "ROLE_", true);

    /**
     * The {@link GrantedAuthoritiesMapper} that is used by default if a custom one is not specified. This default
     * {@link GrantedAuthoritiesMapper} is a {@NullGrantedAuthoritiesMapper} which returns the authorities without
     * mapping.
     */
    public static final GrantedAuthoritiesMapper DEFAULT_GRANTED_AUTHORITIES_MAPPER = new NullAuthoritiesMapper();

    /**
     * The {@link GrantedAuthority} that will be added to every WindowsAuthenticationToken, unless another (or null) is
     * specified.
     */
    public static final GrantedAuthority         DEFAULT_GRANTED_AUTHORITY          = new SimpleGrantedAuthority(
            "ROLE_USER");

    /** The principal. */
    private final WindowsPrincipal               principal;

    /** The authorities. */
    private final Collection<GrantedAuthority>   authorities;

    /**
     * Convenience constructor that calls
     * {@link #WindowsAuthenticationToken(WindowsPrincipal, GrantedAuthorityFactory, GrantedAuthority)} with:
     * <ul>
     * <li>the given identity,</li>
     * <li>the {@link #DEFAULT_GRANTED_AUTHORITY_FACTORY}</li>
     * <li>the {@link #DEFAULT_GRANTED_AUTHORITY}</li>
     * </ul>
     * .
     *
     * @param identity
     *            the identity
     */
    public WindowsAuthenticationToken(final WindowsPrincipal identity) {
        this(identity, WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY,
                WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITIES_MAPPER,
                WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY);
    }

    /**
     * Instantiates a new windows authentication token.
     *
     * @param identity
     *            The {@link WindowsPrincipal} for which this token exists.
     * @param grantedAuthorityFactory
     *            used to construct {@link GrantedAuthority}s for each of the groups to which the
     *            {@link WindowsPrincipal} belongs
     * @param defaultGrantedAuthority
     *            if not null, this {@link GrantedAuthority} will always be added to the granted authorities list
     */
    public WindowsAuthenticationToken(final WindowsPrincipal identity,
            final GrantedAuthorityFactory grantedAuthorityFactory,
            final GrantedAuthoritiesMapper grantedAuthoritiesMapper,
            final GrantedAuthority defaultGrantedAuthority) {

        this.principal = identity;
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (defaultGrantedAuthority != null) {
            authorities.add(defaultGrantedAuthority);
        }
        for (final WindowsAccount group : this.principal.getGroups().values()) {
            authorities.add(grantedAuthorityFactory.createGrantedAuthority(group));
        }
        if (grantedAuthoritiesMapper == null) {
            this.authorities = authorities;
        }
        else {
            Collection<? extends GrantedAuthority> mappedAuthorities = grantedAuthoritiesMapper.mapAuthorities(authorities);
            this.authorities = new ArrayList<>(mappedAuthorities);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getAuthorities()
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getDetails()
     */
    @Override
    public Object getDetails() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.core.Authentication#isAuthenticated()
     */
    @Override
    public boolean isAuthenticated() {
        return this.principal != null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.core.Authentication#setAuthenticated(boolean)
     */
    @Override
    public void setAuthenticated(final boolean authenticated) {
        throw new IllegalArgumentException();
    }

    /*
     * (non-Javadoc)
     * @see java.security.Principal#getName()
     */
    @Override
    public String getName() {
        return this.principal.getName();
    }
}
