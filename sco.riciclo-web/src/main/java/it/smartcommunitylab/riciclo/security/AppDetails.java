package it.smartcommunitylab.riciclo.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppDetails implements UserDetails {
	private static final long serialVersionUID = 1970015369860723085L;

	private AppCredentials app;
	
	public AppDetails() {
		super();
	}

	public AppDetails(AppCredentials app) {
		super();
		this.app = app;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(app.getId()));
	}

	@Override
	public String getPassword() {
		return app.getPassword();
	}

	@Override
	public String getUsername() {
		return app.getId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public AppCredentials getApp() {
		return app;
	}

	public void setApp(AppCredentials app) {
		this.app = app;
	}
}
