package br.com.alura.LDAPAccess.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;


public class DataUtil {

	private static final LogUtil log = new LogUtil(DataUtil.class);
	
	/**
	 * Converte a data UTC retornada pelo NDS em um date com Timezone Local
	 * 
	 */
	public static Date stringUTCtoLocaleDate(String dateUTC){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.YEAR        , Integer.parseInt(dateUTC.substring(0, 4)));
		cal.set(Calendar.MONTH       , (Integer.parseInt(dateUTC.substring(4, 6)) - 1));
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateUTC.substring(6, 8)));
		cal.set(Calendar.HOUR_OF_DAY , Integer.parseInt(dateUTC.substring(8,10)));
		cal.set(Calendar.MINUTE      , Integer.parseInt(dateUTC.substring(10,12)));
		cal.set(Calendar.SECOND      , Integer.parseInt(dateUTC.substring(12,14)));
		return cal.getTime();
	}

	public static Date string2Date(String dt) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return simpleDateFormat.parse(dt);
	} 

	public static boolean cadastradoA24hs(String dataNDS) throws ParseException {
		String[] dt = dataNDS.replace("Z", "").split("");
		int ano = Integer.parseInt(dt[0] + "" + dt[1] + "" + dt[2] + "" + dt[3]);
		int mes = Integer.parseInt(dt[4] + "" + dt[5]);
		int dia = Integer.parseInt(dt[6] + "" + dt[7]);
		int hora = Integer.parseInt(dt[8] + "" + dt[9]);
		int minuto = Integer.parseInt(dt[10] + "" + dt[11]);
		int segundo = Integer.parseInt(dt[12] + "" + dt[13]);
		String strDate0 = mes + "/" + dia + "/" + ano + " " + hora + ":" + minuto + ":" + segundo;
		log.logger().log(Level.INFO, "strDate0: " + strDate0);
		Date data0 = string2Date(strDate0);
		log.logger().log(Level.INFO, "data0: " + data0);
		Calendar c = Calendar.getInstance();
		c.setTime(data0);
		c.add(5, 1);
		Date data24 = c.getTime();
		log.logger().log(Level.INFO, "data24: " + data24);
		Date dataCorrente = new Date();
		log.logger().log(Level.INFO, "dataCorrente: " + dataCorrente);
		if (dataCorrente.before(data24)) {
			log.logger().log(Level.INFO, "cadastradoA24hs: true");
			return true;
		}
		log.logger().log(Level.INFO, "cadastradoA24hs: false");
		return false;
	}
}
