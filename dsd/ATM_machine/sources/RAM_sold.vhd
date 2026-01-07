library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity RAM_sold is
  port(clk: in std_logic;
       rst: in std_logic;
       poz: in integer range 0 to 3;
       balance: out STD_LOGIC_VECTOR(19 downto 0);
       deposit_final_balance, withdraw_final_balance: in STD_LOGIC_VECTOR(19 downto 0);
       ES, EA: in STD_LOGIC);
end RAM_sold;

architecture Behavioral of RAM_sold is

type RAM_TYPE is array (0 to 3) of STD_LOGIC_VECTOR(19 downto 0);
signal RAM: RAM_TYPE := (0 => "00000000011111010000",  --2000
                         1 => "00000000110110101100",   --3500
                         2 => "00000000000110010000",   --400
                         3 => "00000000010010110000");  --1200


begin
process (clk,rst)
	begin 
	    if rst = '1' then
        RAM <= (0 => "00000000011111010000",
                1 => "00000000110110101100",
                2 => "00000000000110010000",
                3 => "00000000010010110000");
        end if;
		if rising_edge(clk) then
		if EA = '1' then
			RAM(poz) <= deposit_final_balance;
		elsif ES = '1' then 
			RAM(poz) <= withdraw_final_balance; 
		end if;
		end if;
end process;
	
balance <= RAM(poz);

end Behavioral;