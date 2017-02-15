package waffle.spring.mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

public class ExcludedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    Set<String> excludedAuthorities;

    public ExcludedAuthoritiesMapper(String... excludedAuthorities) {
        this.excludedAuthorities = new HashSet<>(Arrays.asList(excludedAuthorities));
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> mapped = new ArrayList<>(authorities.size());
        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();
            if (!excludedAuthorities.contains(authority)) {
                mapped.add(grantedAuthority);
            }
        }
        return mapped;
    }

}
