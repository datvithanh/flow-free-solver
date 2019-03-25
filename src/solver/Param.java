package solver;

public class Param {
	public static final int maxNode = 2000000;
	
	// Co cho phep flow quay lai cham cac nut da qua ko
	public static boolean selfTouchable = true;
	
	// Co tim nuoc di bat buoc ko
	public static boolean forcedMove = true;
	
	// Chi di theo 1 flow hay sinh tat ca flow
	public static boolean activeColor = true;
	
	// Sap xep thu tu flow duoc di
	// 0: ko sort
	// 1: sort theo so nuoc di
	// 2: sort theo tuong
	public static int sortColor = 0;
	
	// Co su dung tia nhanh ko
	public static boolean diagnose = true;
	
	// Search tu ngoai vao
	public static boolean out_in = true;
	
	// Co su dung ham g ko
	public static boolean g = false;
	
	// Chon ham h
	public static int h = 0;
	
	// Trong so g so voi h
	public static double bias = 0.5;
}
