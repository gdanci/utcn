library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity RAM_pin is
Port (clk: in std_logic;
      rst: in std_logic;
      verif_pin: in std_logic_vector (3 downto 0);
      ok: out std_logic;
      WE: in std_logic;
      poz_in: in integer range 0 to 3;
      poz_out: out integer range 0 to 3);
end RAM_pin;

architecture Behavioral of RAM_pin is
signal temp: std_logic;
type Mem_Rom is array (0 to 3) of std_logic_vector(3 downto 0);
signal functie: Mem_Rom := (0 =>"1011",
                            1 =>"0110",
                            2 =>"1111",
                            3 =>"0000");

begin
process(clk,rst)
variable pin_rep: integer := 0;
begin
if rst = '1' then
    functie <= (0 =>"1011",
                1 =>"0110",
                2 =>"1111",
                3 =>"0000");
end if;
if rising_edge(clk) then
if WE = '0' then
    if verif_pin=functie(0) then
        temp<='1';
        poz_out<=0;
    elsif verif_pin=functie(1) then
        temp<='1';
        poz_out<=1;
    elsif verif_pin=functie(2) then
        temp<='1';
        poz_out<=2;
    elsif verif_pin=functie(3) then
        temp<='1';
        poz_out<=3;
    end if;
else
    pin_rep:=0;
    if verif_pin = functie(0) then
            pin_rep:=pin_rep+1;
    elsif verif_pin = functie(1) then
            pin_rep:=pin_rep+1;
    elsif verif_pin = functie(2) then
            pin_rep:=pin_rep+1;
    elsif verif_pin = functie(3) then
            pin_rep:=pin_rep+1;
    end if;
    if pin_rep=0 then
        functie(poz_in)<=verif_pin;
        temp <= '1';
    end if;
end if;
end if;
end process;
ok <= temp;

end Behavioral;