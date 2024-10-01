package br.com.alura.LDAPAccess;

import javax.naming.NamingException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.alura.LDAPAccess.config.LdapUtil;

@SpringBootTest
class LDAPAccessApplicationTests {

	@Test
	void contextLoads() throws NamingException {
		LdapUtil.includeUser();
		LdapUtil.totalUser();	
	}

}
