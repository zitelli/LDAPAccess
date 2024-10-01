package br.com.alura.LDAPAccess.config;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;
import com.novell.ldap.util.Base64;

public class StringUtil {

	private static final char[] INVALID_CHARS = new char[] {'&', '|', '(', ')', '=','*','\\','/','!'};


	/**
	 * Verifica se o parametro possui algum caracter nao permitido.
	 * @param param
	 * @throws ServiceException
	 */
	public static boolean validaLdapInjection(String param) {

		if (isNotEmpty(param)) 
		{
			String valor = (String) param;
			for (Character c : valor.toCharArray()) 
			{
				for (char invalid : INVALID_CHARS) 
				{
					if (c == invalid) 
					{
						return false;
					}
				}
			}

			return true;
		}
		return false;
	}

	/**
	 * Verifica se uma String contem algum valor.
	 * @param value
	 * @return boolean
	 */
	public static boolean isNotEmpty(String value) {
		if (value == null) {
			return false;
		}
		for (Character c : value.toCharArray()) {
			if (!Character.isWhitespace(c)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Verifica se um objecto  nulo.
	 * @param object
	 * @return boolean
	 */
	public static boolean isNull(Object object) {
		if (object == null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Verifica se um objecto no  nulo.
	 * @param object
	 * @return boolean
	 */
	public static boolean isNotNull(Object object) {
		return !isNull(object);
	}	

	public static URL parseUrl(String url) {
		try {
			return new URL(URLDecoder.decode(url, "UTF-8"));
		} catch (Exception e) {

		} 
		return null;
	}
	public static URL parseUrlFromQuery(String url,String param) {
		try {
			URL u = new URL(URLDecoder.decode(url, "UTF-8"));
			String query = u.getQuery();
			if(query!=null && !query.isEmpty()) {
				String[] params = query.split("&");
				for(String p : params) {
					if(p.toLowerCase().contains(param.toLowerCase())) {
						String subUrl = p.split("=")[1];
						if(subUrl!=null && !subUrl.isEmpty()) {
							return new URL(URLDecoder.decode(subUrl, "UTF-8"));
						}
					}
				}
			}
			
			return u;
		} catch (Exception e) {
	
		} 
	return null;
	}
	public static String getParamFromUrl(String url,String param) {
		try {
			URL u = new URL(URLDecoder.decode(url, "UTF-8"));
			String query = u.getQuery();
			if(query!=null && !query.isEmpty()) {
				String[] params = query.split("&");
				for(String p : params) {
					if(p.toLowerCase().contains(param.toLowerCase())) {
						return p.split("=",2)[1];
					}
				}
			}
		} catch (Exception e) {
		} 
	    return null;
	}
	public static String getNamAppFromUrl(String url) {
		return getNamAppFromUrl(url, "redirect_uri");
	}
	public static String getNamAppFromUrl(String url,String param) {
		URL u = parseUrlFromQuery(url,param);
		if(u !=null) {
			return u.getHost();
		}
		return null;
	}

	public static String clearMsg(String msg) {
		if(isNotEmpty(msg) && msg.contains("#")) {
			return msg.substring(0,msg.indexOf("#"));
		}
		return msg;
	}
	
	public static String onlyNumber(String value) {
		return value.replaceAll("[^\\d]", "");
	}
	
	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		//System.out.println(validaLdapInjection("00162007132"));
		Properties propertiesMethod = new Properties();
		propertiesMethod.setProperty("TESTE", "0");
		propertiesMethod.setProperty("TESTE", "1");
		propertiesMethod.setProperty("TESTE", "3");
		propertiesMethod.setProperty("NOME", "silvio");
		propertiesMethod.size();
		for(Object key : propertiesMethod.keySet()){
			//System.out.println(s+" - "+propertiesMethod.getProperty(""+s));
			propertiesMethod.setProperty(""+key, propertiesMethod.getProperty(""+key));
		}

		String valor = "-1";
		try {
			System.out.println( Integer.parseInt(valor));
		} catch (Exception e) {
			System.out.println("Valor do cookie de intruder é inválido. Valor ["+valor+"]");
		}
		String target = "https://dloginmoi.oi.net.br/nidp/app/login?id=MINHAOI_Fibra_Validacao&sid=3&option=credential&sid=3&target=https%3A%2F%2Fdloginmoi.oi.net.br%2Fnidp%2Foauth%2Fnam%2Fauthz%3Facr_values%3D%2Fsecure%2Fname%2Fpassword%2Fmoi%2Fvalidacao%2Furi%26client_id%3Dbcdfaf08-cae2-41b1-8e1a-ba56422e6c89%26response_type%3Dcode%26scope%3Dapiportaldss%26cpf%3D00162007132%26redirect_uri%3Dhttps%3A%2F%2Fdminha.oi.net.br%2Fportal%2Fcallback%3Ft%3D333342432";
		System.out.println(getParamFromUrl(target,"redirect_uri"));
		System.out.println(new String(Base64.decode("TVNHX0VSUk9fQ0FQVENIQT1BIGF1dGVudGljYcOnw6NvIGZhbGhvdS4gUG9yIGZhdm9yIHZlcmlmaXF1ZSBzZSB0b2RvcyBvcyBjYW1wb3MgZm9yYW0gaW5mb3JtYWRvcyBjb3JyZXRhbWVudGUu")));
	
		System.out.println(clearMsg("Falha no login. Tente novamente.#NIDPMAIN.125"));
	}

	
	
}
