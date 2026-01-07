library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_unsigned.all;

entity subtracter is
	port(A, B: in STD_LOGIC_VECTOR (19 downto 0);
		 BIN: in STD_LOGIC;
		 D: out STD_LOGIC_VECTOR (19 downto 0);
		 BOUT: out STD_LOGIC;
		 ES: in STD_LOGIC);
end subtracter;

architecture behavioral of subtracter is
	component full_subtracter_one_bit 
		port (A, B, BIN: in STD_LOGIC;
			  D, BOUT: out STD_LOGIC;
			  ES: in STD_LOGIC);
	end component;	
signal BORROW_TEMP: STD_LOGIC_VECTOR (18 downto 0);	

begin

	D0: full_subtracter_one_bit port map(A(0), B(0), BIN, D(0), BORROW_TEMP(0), ES);	
	D1: full_subtracter_one_bit port map(A(1), B(1), BORROW_TEMP(0), D(1), BORROW_TEMP(1), ES);
	D2: full_subtracter_one_bit port map(A(2), B(2), BORROW_TEMP(1), D(2), BORROW_TEMP(2), ES);
	D3: full_subtracter_one_bit port map(A(3), B(3), BORROW_TEMP(2), D(3), BORROW_TEMP(3), ES);
	D4: full_subtracter_one_bit port map(A(4), B(4), BORROW_TEMP(3), D(4), BORROW_TEMP(4), ES);
	D5: full_subtracter_one_bit port map(A(5), B(5), BORROW_TEMP(4), D(5), BORROW_TEMP(5), ES);
	D6: full_subtracter_one_bit port map(A(6), B(6), BORROW_TEMP(5), D(6), BORROW_TEMP(6), ES);
	D7: full_subtracter_one_bit port map(A(7), B(7), BORROW_TEMP(6), D(7), BORROW_TEMP(7), ES);
	D8: full_subtracter_one_bit port map(A(8), B(8), BORROW_TEMP(7), D(8), BORROW_TEMP(8), ES);
	D9: full_subtracter_one_bit port map(A(9), B(9), BORROW_TEMP(8), D(9), BORROW_TEMP(9), ES);
	D10:full_subtracter_one_bit  port map(A(10), B(10), BORROW_TEMP(9), D(10), BORROW_TEMP(10), ES);
	D11:full_subtracter_one_bit  port map(A(11), B(11), BORROW_TEMP(10), D(11), BORROW_TEMP(11), ES);
	D12:full_subtracter_one_bit  port map(A(12), B(12), BORROW_TEMP(11), D(12), BORROW_TEMP(12), ES);
	D13:full_subtracter_one_bit  port map(A(13), B(13), BORROW_TEMP(12), D(13), BORROW_TEMP(13), ES);
	D14:full_subtracter_one_bit  port map(A(14), B(14), BORROW_TEMP(13), D(14), BORROW_TEMP(14), ES);
	D15:full_subtracter_one_bit  port map(A(15), B(15), BORROW_TEMP(14), D(15), BORROW_TEMP(15), ES);
	D16:full_subtracter_one_bit  port map(A(16), B(16), BORROW_TEMP(15), D(16), BORROW_TEMP(16), ES);
	D17:full_subtracter_one_bit  port map(A(17), B(17), BORROW_TEMP(16), D(17), BORROW_TEMP(17), ES);
	D18:full_subtracter_one_bit  port map(A(18), B(18), BORROW_TEMP(17), D(18), BORROW_TEMP(18), ES);
	D19:full_subtracter_one_bit  port map(A(19), B(19), BORROW_TEMP(18), D(19), BOUT, ES);

	
end behavioral;