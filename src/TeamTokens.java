import java.util.ArrayList;

//TeamTokens
public class TeamTokens implements Bot {
	
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects

	boolean roll=true;
	int turns=0;
	BoardAPI board;
	PlayerAPI player;
	DiceAPI dice;

	TeamTokens (BoardAPI board, PlayerAPI player, DiceAPI dice) {
		this.board=board;
		this.player=player;
		this.dice=dice;
		return;
	}
	
	public String getName () {
		return "TeamTokens";
	}
	
	public String getCommand () {
		try{
			Thread.sleep(1);
		}catch (InterruptedException e){
			e.printStackTrace();
		}
		if(player.getBalance()< 10000){
			System.out.println("Money");
		}
		
		if(player.isInJail())
		{
			if(player.hasGetOutOfJailCard())
			{
				roll=false;
				return "card";
			}
			else if(player.isInJail()&&player.getBalance()>100)
			{
				roll=false;
				return "pay";
			}
		}
		if(roll)
		{
			roll = false;
			return "roll";
		}
		
		if(board.isProperty(player.getPosition())&&!board.getProperty(player.getPosition()).isOwned()&& player.getBalance()>500){
	
				 return "buy";
		}
		
		if(player.getBalance()>500)
		{
			ArrayList<Property> test=player.getProperties();
			
			for(int i=0;i<test.size();i++)
			{
				if (test.get(i) instanceof Site)
				{
				
					Site prop=(Site) test.get(i);
					
					if(player.getBalance()>500&&prop.canBuild(1)&&player.isGroupOwner(prop)&&!test.get(i).isMortgaged())
					{
						return "build "+test.get(i).getShortName()+" "+1;
					}
				}
			}
		}
		
		while(player.getBalance() <= 0){
			
		if(player.getNumHousesOwned()>0||player.getNumHotelsOwned()>0)
		{
			
				ArrayList<Property> test=player.getProperties();
				
				for(int i=0;i<test.size();i++)
				{
					if (test.get(i) instanceof Site)
					{
					
					Site prop=(Site) test.get(i);
					
					if(prop.getNumHouses()>0||prop.getNumHotels()>0&&player.getBalance()<=0)
					{
						System.out.println("Here build");
						return "demolish "+test.get(i).getShortName()+" 1";
					}
					}
				}
		}
		if(player.getNumProperties()>0&&player.getNumHousesOwned()==0&&player.getNumHotelsOwned()==0)
		{
			
			ArrayList<Property> test=player.getProperties();
			
			for(int i=0;i<test.size();i++)
			{
			
				if(player.getBalance()<0&&!test.get(i).isMortgaged())
				{
					System.out.println("Here mort");
					return "mortgage "+test.get(i).getShortName();
				}
				
			}	
		}
		System.out.println("here");
			return "bankrupt";
				
		}
		
		
		
		if(player.getBalance()>2000)
		{
			ArrayList<Property> test=player.getProperties();
			
			for(int i=0;i<test.size();i++)
			{
				if (test.get(i) instanceof Site)
				{
					Site prop=(Site) test.get(i);
					
					if(prop.isMortgaged()&&player.getBalance()>2500)
					{
						return "redeem "+test.get(i).getShortName();
					}
				}
			}
		}
		
		
		
		
		
		
		
		if(turns==5)
		{
			System.out.println(player.getTokenName()+" "+player.getNumHousesOwned()+" "+player.getNumHotelsOwned()+" "+player.getBalance()+" "+ player.getNumProperties());
			turns=0;
			return "balance";
		}
		else{
			roll=true;
			turns++;
			
			return "done";
		}
		
	}
	
	public String getDecision () {
		
		roll=false;
		return "pay";
		
		
	}	
}
