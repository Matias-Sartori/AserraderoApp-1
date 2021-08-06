package aserradero.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UFecha
{
	//Formatos de fecha y hora
	public static final String dd_MM_yyyy = "dd/MM/yyyy";
	public static final String dd_MM_yyyy_GUION = "dd_MM_yyyy";
	public static final String dd_MM_yyyy_ESPACIOS = "dd MM yyyy";
	public static final String EEEE_dd_MMMM_yyyy = "EEEE/dd/MMMM/yyyy";
	public static final String EEEE_dd = "EEEE/dd";
	public static final String EEEE_dd_MMMM_yyyy_ESPACIOS = "EEEE dd MMMM yyyy";
	public static final String dd_MM_yyyy_HH_mm_ss = "dd/MM/yyyy HH:mm:ss a";
	public static final String HH_mm_a = "HH:mm: a";
	
	public static String formatearFecha(LocalDate f, String formato)
	{
		DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern(formato);
		
		return formatoFecha.format(f);
	}
	
	public static String formatearFechaHora(LocalDateTime f, String formato)
	{
		DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern(formato);
		return formatoFecha.format(f);
	}
	
	public static String formatearHora(LocalTime h, String formato)
	{
		DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern(formato);
		return formatoFecha.format(h);
	}
}
