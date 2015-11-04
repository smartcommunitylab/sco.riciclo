package it.smartcommunitylab.riciclo.security;

import it.smartcommunitylab.riciclo.storage.DataSetInfo;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppDetails implements UserDetails {
	private static final long serialVersionUID = 1970015369860723085L;

	private DataSetInfo app;
	
	public AppDetails() {
		super();
	}

	public AppDetails(DataSetInfo app) {
		super();
		this.app = app;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(app.getOwnerId()));
	}

	@Override
	public String getPassword() {
		return app.getPassword();
	}

	@Override
	public String getUsername() {
		return app.getOwnerId();
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

	public DataSetInfo getApp() {
		return app;
	}

	public void setApp(DataSetInfo app) {
		this.app = app;
	}
}
