package towntalk.util;

public class LocationUtil {
	private static final double ONE_KM_LATITUDE = 0.009044;
	private static final double ONE_KM_LONGITUDE = 0.008983; 

	public static final int MIN_RANGE_HALF_KM = 2;
	public static final int MAX_RANGE_HALF_KM = 20;
	public static final int RANGE_USER_COUNT = 10000;

	public static double getSearchLatitude(int km){
		return ONE_KM_LATITUDE * km;
	}
	
	public static double getSearchLongitude(int km){
		return ONE_KM_LONGITUDE * km;
	}
}
