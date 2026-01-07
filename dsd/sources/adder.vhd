library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_unsigned.all;

entity adder is
	port(A, B: in STD_LOGIC_VECTOR (19 downto 0);
		CIN: in STD_LOGIC;
		S: out STD_LOGIC_VECTOR (19 downto 0);
		COUT : out STD_LOGIC;
		EA: in STD_LOGIC);
end adder;

architecture behavioral of adder is	
component full_adder_one_bit
	port (A, B, CIN: in STD_LOGIC;
		  S, COUT: out STD_LOGIC;
		  EA: in STD_LOGIC); 
	end component;
signal COUT_TEMP: STD_LOGIC_VECTOR (19 downto 0);
begin
    S0:  full_adder_one_bit port map (A(0),  B(0),  CIN,            S(0),  COUT_TEMP(0),  EA);
    S1:  full_adder_one_bit port map (A(1),  B(1),  COUT_TEMP(0),   S(1),  COUT_TEMP(1),  EA);
    S2:  full_adder_one_bit port map (A(2),  B(2),  COUT_TEMP(1),   S(2),  COUT_TEMP(2),  EA);
    S3:  full_adder_one_bit port map (A(3),  B(3),  COUT_TEMP(2),   S(3),  COUT_TEMP(3),  EA);
    S4:  full_adder_one_bit port map (A(4),  B(4),  COUT_TEMP(3),   S(4),  COUT_TEMP(4),  EA);
    S5:  full_adder_one_bit port map (A(5),  B(5),  COUT_TEMP(4),   S(5),  COUT_TEMP(5),  EA);
    S6:  full_adder_one_bit port map (A(6),  B(6),  COUT_TEMP(5),   S(6),  COUT_TEMP(6),  EA);
    S7:  full_adder_one_bit port map (A(7),  B(7),  COUT_TEMP(6),   S(7),  COUT_TEMP(7),  EA);
    S8:  full_adder_one_bit port map (A(8),  B(8),  COUT_TEMP(7),   S(8),  COUT_TEMP(8),  EA);
    S9:  full_adder_one_bit port map (A(9),  B(9),  COUT_TEMP(8),   S(9),  COUT_TEMP(9),  EA);
    S10: full_adder_one_bit port map (A(10), B(10), COUT_TEMP(9),   S(10), COUT_TEMP(10), EA);
    S11: full_adder_one_bit port map (A(11), B(11), COUT_TEMP(10),  S(11), COUT_TEMP(11), EA);
    S12: full_adder_one_bit port map (A(12), B(12), COUT_TEMP(11),  S(12), COUT_TEMP(12), EA);
    S13: full_adder_one_bit port map (A(13), B(13), COUT_TEMP(12),  S(13), COUT_TEMP(13), EA);
    S14: full_adder_one_bit port map (A(14), B(14), COUT_TEMP(13),  S(14), COUT_TEMP(14), EA);
    S15: full_adder_one_bit port map (A(15), B(15), COUT_TEMP(14),  S(15), COUT_TEMP(15), EA);
    S16: full_adder_one_bit port map (A(16), B(16), COUT_TEMP(15),  S(16), COUT_TEMP(16), EA);
    S17: full_adder_one_bit port map (A(17), B(17), COUT_TEMP(16),  S(17), COUT_TEMP(17), EA);
    S18: full_adder_one_bit port map (A(18), B(18), COUT_TEMP(17),  S(18), COUT_TEMP(18), EA);
    S19: full_adder_one_bit port map (A(19), B(19), COUT_TEMP(18),  S(19), COUT, EA);
	

	
end behavioral;