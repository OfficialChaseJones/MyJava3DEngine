public class Weapon
{
	public static final int TYPE_DEFUALT = 5;
	
	public int type;
	
	public Weapon()
	{
		type = TYPE_DEFUALT;
	}
	
	
	public int getCorrespondingTextureIndex()
	{
		switch(type)
		{
			case TYPE_DEFUALT:		
				return Constants.DEFAULT_WEAPON_TEXTURE;
		}

		return Constants.DEFAULT_WEAPON_TEXTURE;
	}
}
