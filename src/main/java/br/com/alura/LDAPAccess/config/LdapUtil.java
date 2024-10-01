package br.com.alura.LDAPAccess.config;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;


public class LdapUtil {
		private static LdapContext ldapContext;
		public int idEvento = 0;
		private static String CHAVE;
		private static String SENHA;
		private static String URL;
		private static String USUARIOCPF;

	private static final LogUtil log = new LogUtil(LdapUtil.class);

	public static LdapContext getContext(Properties parametros) throws NamingException {

		String urlLdap = parametros.getProperty("LDAP_OI_URL");
//			if()
//			String provUrl = (String)ldapContext.getEnvironment().get(Context.PROVIDER_URL);
//			log.logger().log(Level.INFO, "Ldap URL is equal? "+urlLdap.equalsIgnoreCase(provUrl)+" - URL["+urlLdap+"] URL_MEM["+provUrl+"]");
//			if (ldapContext == null || !urlLdap.equalsIgnoreCase(provUrl)) 
//			{
		
			log.logger().log(Level.INFO, "Recriando a conexao ldap.");
			
			String timeout = parametros.getProperty("LDAP_OI_TIMEOUT");
			String contaServico = parametros.getProperty("LDAP_OI_CONTA_SERVICO");
			String senhaContaServico = EncryptionUtils.decrypt(parametros.getProperty("LDAP_OI_SENHA"), CHAVE);
			
			log.logger().log(Level.INFO, "Parametros de conexao. LDAP_OI_URL["+urlLdap+"]");
			Hashtable<String, String> environment = new Hashtable<String, String>();
			environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			environment.put("com.sun.jndi.ldap.read.timeout",timeout);
			environment.put(Context.PROVIDER_URL, urlLdap);
			if (urlLdap.startsWith("ldaps://")) {
				environment.put(Context.SECURITY_PROTOCOL, "ssl");
				environment.put(Context.SECURITY_AUTHENTICATION, "simple");
				environment.put("java.naming.ldap.factory.socket", "br.com.alura.LDAPAccess.config.DummySSLSocketFactory");
			}
			environment.put(Context.SECURITY_PRINCIPAL, contaServico);
			environment.put(Context.SECURITY_CREDENTIALS, senhaContaServico);
			environment.put(Context.REFERRAL, "follow");
			ldapContext = new InitialLdapContext(environment, (Control[]) null);
//			}else{
//				log.logger().log(Level.INFO,"ldap context reaproveitado");
//			}

		return ldapContext;
	}

	public static int deinit() {
		try {
			if(ldapContext != null)
			{
				ldapContext.close();
				ldapContext = null;
			}
		} catch (Exception e) {
			log.logger().log(Level.WARNING, "deinit - Erro = " + e.getMessage());
		}
		return 0;
	} // END of deinit()

	private static void inicializa() {
		
        Properties prop = new Properties();
        try (InputStream input = LdapUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load the properties file
            prop.load(input);

            // Get the property values and print them out
            CHAVE = prop.getProperty("chave");
            SENHA = prop.getProperty("senha");
            URL = prop.getProperty("url");
            USUARIOCPF = prop.getProperty("usuarioCpf");
            
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	public static Map<String, Object> searchUser(int scope, String baseDn, String id,String filter, String[] atributos, Double thread, Properties parametros) {

		Map<String, Object> retorno = new HashMap<String, Object>();

		if (id != null && !id.trim().equalsIgnoreCase("") && StringUtil.validaLdapInjection(id)) {
			int contador = 1;

			while (contador <= 3) {
				try {

					// setup search controls
					SearchControls searchControls = new SearchControls();
					searchControls.setTimeLimit(5000);
					searchControls.setReturningObjFlag(true);
					searchControls.setSearchScope(scope);
					searchControls.setReturningAttributes(atributos);

					NamingEnumeration<SearchResult> result = getContext(parametros).search(baseDn, filter, searchControls);

					log.logger().log(Level.INFO, "THREAD["+id+"_"+thread+"]"+" --- Consultando.... ");

					if (result.hasMoreElements()) 
					{
						SearchResult usuario = result.next();
						Attributes attrs = usuario.getAttributes();
						NamingEnumeration<String> attrsKeys = attrs.getIDs();
						retorno.put("dn", usuario.getNameInNamespace());
						while(attrsKeys.hasMoreElements()){
							String atributo = attrsKeys.next();
							Object valor = null;
							if("objectClass".equals(atributo)){
								Object v = attrs.get(atributo);
								String s = v.toString();
								valor =  s.replace("objectClass: ","");								
							}else{
								valor = attrs.get(atributo).get();
							}
							retorno.put(atributo, valor);
							log.logger().log(Level.INFO,"Usuario: ["+id+"] Attr ["+atributo+"] - Valor: ["+valor+"]");
						}

						return retorno;
					}
					else
					{
						log.logger().log(Level.INFO,"Usuario: ["+id+"] nao encontrado.");
					}

					break;
				} 
				catch (NamingException e) {
					log.logger().log(Level.WARNING,"Erro ao buscar o usuario: [" + id+ "] [" + e.getMessage() + "]\n");
					log.logger().log(Level.WARNING,"Retry search [" + contador + "]...");
					log.logger().log(Level.WARNING,"ERROR: " + e.toString());
					contador++;
					deinit();
					e.printStackTrace();
					continue;
				} catch (Exception e) {
					log.logger().log(Level.SEVERE,"Erro ao buscar o usuario: [" + id+ "] [" + e.getMessage() + "]\n");
					log.logger().log(Level.SEVERE, "ERROR: " + e.toString());
					deinit();
					e.printStackTrace();
					break;
				}
			}
		}else{
			log.logger().log(Level.WARNING,"O id informado e invalido ["+id+"].");
		}
		return retorno;
	}


	public static Map<String, Object> searchCliente(String id,Double thread,Properties parametros) throws ParseException{

		Map<String, Object> retorno = new HashMap<String, Object>();

		
		StringBuffer sbLdapFilter = new StringBuffer(128);
		sbLdapFilter.append("(&(!(cn=*_*))(objectClass=gidOiFibra)(|(gidCPFCNPJ=");
		sbLdapFilter.append(id);
		sbLdapFilter.append(")(gidLogin=");
		sbLdapFilter.append(id);
		sbLdapFilter.append(")(cn=");
		sbLdapFilter.append(id);
		sbLdapFilter.append(")))");
		String strLdapFilter = new String(sbLdapFilter);

		retorno = LdapUtil.searchUser(SearchControls.SUBTREE_SCOPE, 
				"ou=pessoa,o=gid", id, strLdapFilter, 
				new String[] { "cn","gidLogin","userPassword" },
				thread,parametros);

		if(retorno!= null && retorno.size()>0){

			int quantidade = retorno.size();
			log.logger().log(Level.INFO,"Usuario: ["+id+"] Quantidade: ["+quantidade+"]");
			
			if(retorno.containsKey("lockedByIntruder")){
				retorno.put("LBI", Boolean.valueOf(((String)retorno.get("lockedByIntruder")).toLowerCase()));
			}else{
				retorno.put("LBI", Boolean.valueOf(false));
			}

			if(retorno.containsKey("loginIntruderAttempts")){
				retorno.put("ITR", Integer.parseInt((String)retorno.get("loginIntruderAttempts")));
			}else{
				retorno.put("ITR", Integer.valueOf(0));
			}
			
			if(retorno.containsKey("loginIntruderResetTime") && retorno.get("loginIntruderResetTime") != null){
				String strLoginIntruderResetTime = (String) retorno.get("loginIntruderResetTime");
				retorno.put("LIRT", DataUtil.stringUTCtoLocaleDate(strLoginIntruderResetTime));
			}
			return retorno;
		}
		return null;
	}

	public static void testeInterno() throws ParseException{
		int cont = 1;
		Properties p = new Properties();

		inicializa();
		p.setProperty("LDAP_OI_URL", URL);
		p.setProperty("LDAP_OI_TIMEOUT", "50000");
		p.setProperty("LDAP_OI_CONTA_SERVICO","cn=admin,ou=contasservico,ou=usuarios,o=gid");
		String senhaContaServico = EncryptionUtils.encrypt(SENHA, CHAVE);
		p.setProperty("LDAP_OI_SENHA",senhaContaServico);

        // Now you can make your LDAPS connection
		for(int i = 1;i<=cont;i++){
			Map<String, Object> retorno = LdapUtil.searchCliente(USUARIOCPF,3312d,p);
			log.logger().log(Level.INFO,"Intruder: "+retorno.get("ITR"));
		}
        // Your LDAP connection logic here...

	}

	public static void includeUser() {
		  try {
			  Hashtable<String, String> ldapEnv = new Hashtable<>();
			  ldapEnv.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			  ldapEnv.put(Context.PROVIDER_URL, "ldap://localhost:10389");
			  ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			  ldapEnv.put(Context.SECURITY_PRINCIPAL , "uid=admin,ou=system");
			  ldapEnv.put(Context.SECURITY_CREDENTIALS, "secret");
			  DirContext context = new InitialDirContext(ldapEnv);
			  
			  Attributes attributes =new BasicAttributes();
			  Attribute attribute =new BasicAttribute("objectClass");
			  attribute.add("inetOrgPerson");
			  attributes.put(attribute);
			  Attribute sn =new BasicAttribute("sn");
			  sn.add("Zitelli");
			  Attribute cn =new BasicAttribute("cn");
			  cn.add("Francisco");
			  
			  attributes.put(sn);
			  attributes.put(cn);
			 attributes.put("telephoneNumber", "12332");
			 context.createSubcontext("employeeNumber=2 ,ou=users, ou=system",attributes);
			  
			  System.out.println(" success");
		 }
		  catch (Exception e) {
			  System.out.println(" error - " + e.getMessage());
		}		
	}
	
	public static void totalUser() throws NamingException {
		
		Properties initialProperties = new Properties();
		initialProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		initialProperties.put(Context.PROVIDER_URL,"ldap://127.0.0.1:10389");
		initialProperties.put(Context.SECURITY_PRINCIPAL,"uid=admin,ou=system");
		initialProperties.put(Context.SECURITY_CREDENTIALS,"secret");
		DirContext context = new InitialDirContext(initialProperties);
		
		String searchFilter = "(objectClass=inetOrgPerson)";	
		String[] requireAttributes = {"sn","cn","employeeNumber","telephoneNumber"};
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(requireAttributes);
		NamingEnumeration users = context.search("ou=users, ou=system", searchFilter, controls);
		SearchResult searchResult = null;
		String commonName = null;
		String surName = null;
		String employeeNum = null;
		String telephoneNum = null;
		while (users.hasMore()) {
			searchResult = (SearchResult) users.next();
			Attributes attr = searchResult.getAttributes();
			commonName = attr.get("cn").get(0).toString();
			surName = attr.get("sn").get(0).toString();
			employeeNum = attr.get("employeeNumber").get(0).toString();
			telephoneNum = attr.get("telephoneNumber").get(0).toString();
			System.out.println("Name = " + commonName);
			System.out.println("Surname = " + surName);
			System.out.println("Employee Number = " + employeeNum);
			System.out.println("Telephone Number = " + telephoneNum);
			System.out.println("-------------------------------------------");
		}
	}
	
}


