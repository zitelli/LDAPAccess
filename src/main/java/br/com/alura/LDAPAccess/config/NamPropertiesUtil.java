package br.com.alura.LDAPAccess.config;

import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.novell.nidp.authentication.AuthenticationContract;
import com.novell.nidp.authentication.AuthenticationMethod;
import com.novell.nidp.authentication.card.AuthenticationCard;
import com.novell.nidp.authentication.card.LocalAuthenticationCard;
import com.novell.nidp.ui.ContentHandler;

public class NamPropertiesUtil {

	public enum Recaptcha {
		ENTERPRISE, V2, V3;
	}

	public enum Opcoes {
		RECAPTCHA("0"), SIMPLECAPTCHA("1"), RELOAD("reload");

		String label;

		Opcoes(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

	}

	public enum PropertiesKeys {
		// boolean
		CAPTCHA_ENABLED,
		// int
		INTRUDER_SIZE,
		// string
		MSG_ERRO_CAPTCHA, TIPO_CAPTCHA, RECAPTCHA_PUB_KEY, RECAPTCHA_PRIV_KEY, NOME_APLICACAO, URL_PORTAL,
		URL_AUTENTICACAO, URL_APLICACAO, NO_TARGET_DEFAULT_URL, URL_RETORNO_CALLBACK, CAPTCHA_ALTERNADO,
		LOG_ELASTIC_ENABLED, URL_ELASTIC, LOG_CSV_ENABLED, APIM_TOKEN_URL, APIM_CLIENT_ID, APIM_CLIENT_SECRET,
		APIM_SCOPE, APIM_CADASTRO_URL, MOI_PUB_KEY, RECAPTCHA_PROJECT_ID, RECAPTCHA_VERSION, MSG_ERRO_LOGIN,
		LOG_CSV_FILE_PATH,DISABLE_CAD_SIMPLIFICADO,CNPJ_ENABLED
	}

	private static Properties propertiesClass;
	private static final LogUtil log = new LogUtil(NamPropertiesUtil.class);
	private static String DEF_MSG_ERRO_CAPTCHA = "Error na validação do Recaptcha.";
	private static String DEF_MSG_ERRO_LOGIN = "Falha no login. Tente novamente.";
	private static String DEF_CSV_FILE_PATH = "/opt/novell/nam/idp/logs/nam-access-user";

	public static String ATTR_SESSION_INTRUDER = "intruder";
	public static String ATTR_IS_CAPTCHA_LIBERADO = "isCaptchaLiberado";
	public static String ATTR_COOKIE_INTRUDER = "controle_itr";
	public static String ATTR_TEXTO_CAPTCHA = "TextoCaptcha";
	public static String ATTR_CAPTCHA_SELECIONADO = "nam_cap_select";
	public static String ATTR_PARAM_OP = "op";
	public static String ATTR_SESSION_INTRUDER_GERAL = "sessionIntruderGeral";
	public static String ATTR_IS_INTRUDER_ATTEMPT_RESETADO = "isIntruderAttemptResetado";
	public static String ATTR_INTRUDER_ATTEMPT_RESET_TIME = "intruderAttemptResetTime";
	public static String ATTR_RECAPTCHA_PRIV_KEY = "recaptchaKey";
	public static String ATTR_USERNAME = "attrUsername";

	public static String ATTR_APIM_TOKEN_URL = "ATTR_APIM_TOKEN_URL";
	public static String ATTR_APIM_CLIENT_ID = "ATTR_APIM_CLIENT_ID";
	public static String ATTR_APIM_CLIENT_SECRET = "ATTR_APIM_CLIENT_SECRET";
	public static String ATTR_APIM_SCOPE = "ATTR_APIM_SCOPE";
	public static String ATTR_APIM_CADASTRO_URL = "ATTR_APIM_CADASTRO_URL";
	public static String ATTR_MOI_PUB_KEY = "ATTR_MOI_PUB_KEY";
	public static String ATTR_CAD_SIMPLIFICADO = "ATTR_CAD_SIMPLIFICADO";
	public static String ATTR_RECAPTCHA_PUB_KEY = "ATTR_RECAPTCHA_PUB_KEY";
	public static String ATTR_RECAPTCHA_PRJ_ID = "ATTR_RECAPTCHA_PRJ_ID";
	public static String ATTR_APP_NAME = "ATTR_APP_NAME";
	public static String KEY_CRYPTO_RESET = "0x2bc370";

	public NamPropertiesUtil(Properties properties) {
		loadProperties(properties);
	}

	public NamPropertiesUtil(ContentHandler handler) {
		loadProperties(handler);
	}

	private void loadProperties(ContentHandler handler) {
		log.logger().info("Load properties from handler");
		propertiesClass = new Properties();
		AuthenticationCard card = handler.getCurrentCard();
		log.logger().info("Card: " + card);

		AuthenticationContract contrato = ((LocalAuthenticationCard) card).getContract();
		log.logger().info("Carregando as propriedades do contrato [" + contrato.getName() + "]");
		log.logger().info("Total de metodos do contrato [" + contrato.getName() + "]: " + contrato.getMethodCount());
		for (AuthenticationMethod metodo : contrato.getMethods()) {
			log.logger().info("Metodo [" + metodo.getName() + "]");
			Properties propMethods = metodo.getProperties();
			for (Object key : propMethods.keySet()) {
				log.logger().info("Key [" + key + "] Value [" + propMethods.getProperty("" + key) + "]");
				propertiesClass.setProperty("" + key, propMethods.getProperty("" + key));
			}
		}
		log.logger().info("Carregamento das propriedades realizado com sucesso!");
	}

	private void loadProperties(Properties properties) {
		log.logger().info("Load properties from properties obj");
		propertiesClass = properties;
		log.logger().info("Carregamento das propriedades realizado com sucesso!");
	}

	public Object getProperty(PropertiesKeys key) {
		return getProperty(key, null);
	}

	public Object getProperty(PropertiesKeys key, Object padrao) {
		return getProperty(key.name(), padrao);
	}

	public Object getProperty(String key, Object padrao) {
		if (propertiesClass != null && propertiesClass.containsKey(key)) {
			return propertiesClass.getProperty(key);
		} else {
			return padrao;
		}
	}

	public Boolean isCaptchaAlternado() {
		if (getTipoCaptcha() != null && getTipoCaptcha().equalsIgnoreCase("ALL")) {
			return true;
		}
		return false;
	}

	public Boolean isCaptchaEnabled() {
		String valor = (String) getProperty(PropertiesKeys.CAPTCHA_ENABLED);
		if (valor == null || !Boolean.parseBoolean(valor)) {
			return false;
		}
		return true;
	}

	public int getIntruderSize() {
		String valor = (String) getProperty(PropertiesKeys.INTRUDER_SIZE);
		if (valor != null) {
			return Integer.parseInt(valor);
		}
		return 3;
	}

//	MSG_ERRO_CAPTCHA,		
	public String getMsgErroCaptcha() {
		String cap = (String) getProperty(PropertiesKeys.MSG_ERRO_CAPTCHA);
		if (cap == null || cap.isEmpty()) {
			return DEF_MSG_ERRO_CAPTCHA;
		}
		return cap;
	}

//	TIPO_CAPTCHA,
	public String getTipoCaptcha() {
		return (String) getProperty(PropertiesKeys.TIPO_CAPTCHA);
	}

	public boolean isShowRecaptcha() {
		if (getTipoCaptcha() == null || getTipoCaptcha().equalsIgnoreCase("SIMPLE_CAPTCHA")) {
			return false;
		}
		return true;
	}

//	RECAPTCHA_PUB_KEY,
	public String getRecaptchaPubKey() {
		return (String) getProperty(PropertiesKeys.RECAPTCHA_PUB_KEY);
	}

//	RECAPTCHA_PRIV_KEY,
	public String getRecaptchaPrivKey() {
		return (String) getProperty(PropertiesKeys.RECAPTCHA_PRIV_KEY);
	}

//	NOME_APLICACAO,
	public String getNomeAplicacao() {
		return (String) getProperty(PropertiesKeys.NOME_APLICACAO);
	}

//	URL_PORTAL,
	public String getUrlPortal() {
		return (String) getProperty(PropertiesKeys.URL_PORTAL);
	}

//	URL_AUTENTICACAO,
	public String getUrlAutenticacao() {
		return (String) getProperty(PropertiesKeys.URL_AUTENTICACAO);
	}

//	URL_APLICACAO,
	public String getUrlAplicacao() {
		return (String) getProperty(PropertiesKeys.URL_APLICACAO);
	}

//	NO_TORGET_DEFAULT_URL,
	public String getNoTargetDefaultUrl() {
		return (String) getProperty(PropertiesKeys.NO_TARGET_DEFAULT_URL);
	}

//URL_RETORNO_CALLBACK

	public String getUrlRetornoCallback() {
		return (String) getProperty(PropertiesKeys.URL_RETORNO_CALLBACK);
	}

	public boolean showCaptcha(HttpServletRequest request, HttpSession session) {

		Cookie cookies[] = request.getCookies();
		Integer ic = null;
		Integer is = (Integer) session.getAttribute(NamPropertiesUtil.ATTR_SESSION_INTRUDER);

		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equalsIgnoreCase(NamPropertiesUtil.ATTR_COOKIE_INTRUDER)) {
					ic = Integer.parseInt(c.getValue());
					break;
				}
			}
		}

		return (is != null && is >= getIntruderSize()) || (ic != null && ic >= getIntruderSize());
	}

	public boolean showCaptchaMinhaOi(HttpServletRequest request, HttpSession session) {

		Integer is = (Integer) session.getAttribute(NamPropertiesUtil.ATTR_SESSION_INTRUDER);
		Integer isg = (Integer) session.getAttribute(NamPropertiesUtil.ATTR_SESSION_INTRUDER_GERAL);
		Boolean iiar = (Boolean) session.getAttribute(NamPropertiesUtil.ATTR_IS_INTRUDER_ATTEMPT_RESETADO);

		// Para exibir o captcha o atributo loginIntruderAttempts nao pode ser nulo,
		// deve ser maior ou igual ao limite de intrusoes parametrizado,
		// e o atributo loginIntruderAttemptResetTime deve ser maior q a data atual,
		// caso contrario foi resetado. OU a quantidade geral de intrus�es
		// da sessao deve ser maior ou igual a 4.
		return ((is != null && is >= getIntruderSize() && !(iiar)) || (isg != null && isg >= getIntruderSize()));
	}

	public boolean isSimpleCaptcha(String op) {
		if (StringUtil.isNotEmpty(op) && Opcoes.SIMPLECAPTCHA.getLabel().equalsIgnoreCase(op.trim())) {
			return true;
		}
		return false;
	}

	public boolean isRecaptcha(String op) {
		if (StringUtil.isNotEmpty(op) && Opcoes.RECAPTCHA.getLabel().equalsIgnoreCase(op.trim())) {
			return true;
		}
		return false;
	}

	public boolean isReload(String op) {
		if (StringUtil.isNotEmpty(op) && Opcoes.RELOAD.getLabel().equalsIgnoreCase(op.trim())) {
			return true;
		}
		return false;
	}

	public Boolean isLogElasticEnabled() {
		String valor = (String) getProperty(PropertiesKeys.LOG_ELASTIC_ENABLED);
		if (valor == null || !Boolean.parseBoolean(valor)) {
			return false;
		}
		return true;
	}

	public String getUrlElastic() {
		return (String) getProperty(PropertiesKeys.URL_ELASTIC);
	}

	public Boolean isLogCsvEnabled() {
		String valor = (String) getProperty(PropertiesKeys.LOG_CSV_ENABLED);
		if (valor == null || !Boolean.parseBoolean(valor)) {
			log.logger().info("LOG_CSV_ENABLED: false");
			return false;
		}
		log.logger().info("LOG_CSV_ENABLED: true");
		return true;
	}

	public String getApimTokenUrl() {
		return (String) getProperty(PropertiesKeys.APIM_TOKEN_URL);
	}

	public String getApimClientId() {
		return (String) getProperty(PropertiesKeys.APIM_CLIENT_ID);
	}

	public String getApimClientSecret() {
		return (String) getProperty(PropertiesKeys.APIM_CLIENT_SECRET);
	}

	public String getApimScope() {
		return (String) getProperty(PropertiesKeys.APIM_SCOPE);
	}

	public String getApimCadastroUrl() {
		return (String) getProperty(PropertiesKeys.APIM_CADASTRO_URL);
	}

	public String getMoiPubKey() {
		return (String) getProperty(PropertiesKeys.MOI_PUB_KEY);
	}

	public Boolean isRecaptchaEnterprise() {
		String op = (String) getProperty(PropertiesKeys.RECAPTCHA_VERSION);
		if (StringUtil.isNull(op) || Recaptcha.ENTERPRISE.name().equalsIgnoreCase(op.trim())) {
			return true;
		}
		return false;
	}

	public String getRecaptchaProjectId() {
		return (String) getProperty(PropertiesKeys.RECAPTCHA_PROJECT_ID);
	}

	public String getMsgErroLogin() {
		String cap = (String) getProperty(PropertiesKeys.MSG_ERRO_LOGIN);
		if (cap == null || cap.isEmpty()) {
			return DEF_MSG_ERRO_LOGIN;
		}
		return cap;
	}
	public String getCsvFilePath() {
		String csv = (String) getProperty(PropertiesKeys.LOG_CSV_FILE_PATH);
		
		if (csv == null || csv.isEmpty()) {
			log.logger().info("CSV_FILE_PATH: " + DEF_CSV_FILE_PATH);
			return DEF_CSV_FILE_PATH;
		}
		log.logger().info("CSV_FILE_PATH: " + csv);
		return csv;
	}
	
	public Boolean isDisableCadSimplificado() {
		String valor = (String) getProperty(PropertiesKeys.DISABLE_CAD_SIMPLIFICADO);
		if (valor != null && valor.trim().equalsIgnoreCase("true")) {
			return true;
		}
		return false;
	}
	public Boolean isCnpjEnable() {
		String valor = (String) getProperty(PropertiesKeys.CNPJ_ENABLED);
		if (valor != null && valor.trim().equalsIgnoreCase("true")) {
			return true;
		}
		return false;
	}
	
	
	
}
