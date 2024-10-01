package br.com.alura.LDAPAccess;

import java.text.ParseException;
import java.util.logging.Level;

//import java.util.logging.Level;
//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.com.alura.LDAPAccess.config.LdapUtil;
import br.com.alura.LDAPAccess.config.LogUtil;


@SpringBootApplication
public class LDAPAccessApplication {

	public static void main(String[] args) throws ParseException {
		
		LogUtil log = new LogUtil(LdapUtil.class);
		
//		SpringApplication.run(LDAPAccessApplication.class, args);
		log.logger().log(Level.INFO,"****** Testes Interno  ******");
		LdapUtil.testeInterno();
		log.logger().log(Level.INFO,"*****************************");
	
	}

}
