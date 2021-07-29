package me.smartygeek.pixelpaint.tools;

public enum Tools{
	
	Circle("Circle Tool",1,"Circle.png"),
	Square("Square Tool", 2, "Square.png"),
	Fill("Bucket Fill Tool",3,"FillBucket.png"),
	Eraser("Eraser Tool",4,"Eraser.png"),
	EyeDropper("EyeDropper Tool",5,"EyeDropper.png");
	
	public String name, icon;
	public int id;
	
	public static final int CIRCLETOOL = 1;
	public static final int SQUARETOOl = 2;
	public static final int FILLTOOL = 3;
	public static final int ERASERTOOL = 4;
	public static final int EYEDROPPERTOOL = 5;
	
	Tools(String name, int id,String icon) {
		this.name = name;
		this.id = id;
		this.icon = "/"+icon;
	}
	
	public static Tools getFromId(int id) {
		for (Tools t: Tools.values()) {
			if (t.id == id) {
				return t;
			}
		}
		return null;
	}

}
