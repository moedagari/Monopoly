import java.util.ArrayList;

// HalalTingz
public class HalalTingz implements Bot 
{

	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects

	BoardAPI board;
	PlayerAPI player;
	DiceAPI dice;
	int noOfTurns = 0;
	boolean hasRolled = false;

	HalalTingz(BoardAPI board, PlayerAPI player, DiceAPI dice) // This enables us to use the functions within these API classes
	{
		this.board = board;
		this.player = player;
		this.dice = dice;
		return;
	}

	public String getName() 
	{
		return "HalalTingz";
	}

	public String getCommand() 
	{
		try {
			Thread.sleep(1); // Controls the speed of the game
		} 
		catch (InterruptedException exception) {
			exception.printStackTrace();
		}
		
		// if in jail
		if(player.isInJail())
		{
			if (player.hasGetOutOfJailCard()) // Calls for the bot to use the card if available first and foremost
			{
				hasRolled = true;
				return "card";
			}
			else
			{
				hasRolled = true;
				return "pay"; // If the bot does not have the card then just pay the fine
			}
		}
		// roll
		if (hasRolled == false)
		{
			hasRolled = true;
			return "roll"; // If the bot has not rolled then make sure they do roll
		}
		// buy
		if (board.isProperty(player.getPosition()) && board.getProperty(player.getPosition()).isOwned() == false && player.getBalance() > 450) 
		{
			return "buy"; // If the position is a property and is vancant (not owned) and the bots balance is greater than 450 then proceed to buy
		}
		// build
		if (player.getBalance() > 450)
		{
			ArrayList<Property> properties = player.getProperties(); // This array list stores the properties of the player
			for (int i = 0; i < properties.size(); i++)
			{
				if (properties.get(i) instanceof Site)
				{
					Site property = (Site) properties.get(i);
					if (player.getBalance() > 450 && property.canBuild(1) && player.isGroupOwner(property) && properties.get(i).isMortgaged() == false)
					{
						return "build " + properties.get(i).getShortName() + " " + 1; // If the bot has sufficient balance (greater than 450) and its a property that can be built on and if they have own the color group while the property is not mortgages then return build followed by the short name of the property
					}
				}
			}
		}
		// bankrupt, demolish & redeem
		while (player.getBalance() <= 0) // These will continue while the players balance is equivalent to or less than 0
		{
			// demolish
			if (player.getNumHousesOwned() > 0 || player.getNumHotelsOwned() > 0) 
			{
				ArrayList<Property> properties = player.getProperties();
				for (int i = 0; i < properties.size(); i++)
				{
					if (properties.get(i) instanceof Site)
					{
						Site property = (Site) properties.get(i);
						if (property.getNumHouses() > 0 || property.getNumHotels() > 0 && player.getBalance() <= 0) // If the bot has houses or hotels and requires money to increase their balance then they will return demolish followed by the shortname of the property on which they have buildings on 
						{
							return "demolish " + properties.get(i).getShortName() + " " + 1; 
						}
					}
				}
			}
			// mortgage
			if (player.getNumProperties() > 0 && player.getNumHousesOwned() == 0 && player.getNumHotelsOwned() == 0) 
			{
				ArrayList<Property> properties = player.getProperties();
				for (int i = 0; i < properties.size(); i++)
				{
					if (player.getBalance() < 0 && properties.get(i).isMortgaged() == false)
					{
						return "mortgage " + properties.get(i).getShortName(); // If the bot owns a property and they have no houses or hotels while they do not have enough money they they will return mortgage along with the shortname of the property
					}
				}
			}
			// bankrupt
			return "bankrupt"; // In the case where the bot can simply not satisfy any of the above statements then they must decalre bankruptcy as it is the only other option
		}
		// return balance
		if (noOfTurns == 8) // This just means that instead of constantly returning the bots balance, it returns it every 8 turns
		{
			noOfTurns = 0;
			return "balance";
		}
		// if none of them, do this.
		hasRolled = false;
		noOfTurns++;
		return "done";
	}


	public String getDecision() 
	{
		return "pay";
	}

}