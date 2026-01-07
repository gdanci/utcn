----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 05/15/2025 02:00:17 PM
-- Design Name: 
-- Module Name: main - Behavioral
-- Project Name: 
-- Target Devices: 
-- Tool Versions: 
-- Description: 
-- 
-- Dependencies: 
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
-- 
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;
-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity main is
  Port ( clk: in std_logic;
         rst: in std_logic;
         receipt: out std_logic;
         led_ok: out std_logic;
         led_error: out std_logic;
         pin_in: in std_logic_vector(3 downto 0);
         sum_in: in std_logic_vector(6 downto 0);
         btn_check_balance: in std_logic;
         btn_withdraw: in std_logic;
         btn_deposit: in std_logic;
         btn_pin: in std_logic;
         btn_ok: in std_logic;
         seven_segment: out std_logic_vector(6 downto 0);
         anod_out: out std_logic_vector(7 downto 0));
end main;

architecture Behavioral of main is

component RAM_pin is
Port (clk: in std_logic;
      rst: in std_logic;
      verif_pin: in std_logic_vector (3 downto 0);
      ok: out std_logic;
      WE: in std_logic;
      poz_in: in integer range 0 to 3;
      poz_out: out integer range 0 to 3);
end component;

component debouncer is
    Port (btn : in STD_LOGIC;
          clk : in STD_LOGIC;
          en : out STD_LOGIC );
end component;

component RAM_sold is
        port(clk: in std_logic;
             rst: in std_logic;
             poz: in integer range 0 to 3;
             balance: out STD_LOGIC_VECTOR(19 downto 0);
             deposit_final_balance: in STD_LOGIC_VECTOR(19 downto 0);
             withdraw_final_balance: in STD_LOGIC_VECTOR(19 downto 0);
             ES: in STD_LOGIC;
             EA: in STD_LOGIC);
end component;

component bcd7segment is
Port ( BCDin : in STD_LOGIC_VECTOR (3 downto 0);
       Seven_Segment : out STD_LOGIC_VECTOR (6 downto 0);
       anod_in : in std_logic_vector(7 downto 0);
       anod_out : out std_logic_vector (7 downto 0));
end component;

component adder is
	port(A, B: in STD_LOGIC_VECTOR (19 downto 0);
		CIN: in STD_LOGIC;
		S: out STD_LOGIC_VECTOR (19 downto 0);
		COUT : out STD_LOGIC;
		EA: in STD_LOGIC);
end component;

component subtracter is
	port(A, B: in STD_LOGIC_VECTOR (19 downto 0);
		 BIN: in STD_LOGIC;
		 D: out STD_LOGIC_VECTOR (19 downto 0);
		 BOUT: out STD_LOGIC;
		 ES: in STD_LOGIC);
end component;

type t_State is (enter_pin,wait_operation,check_balance,deposit_sum,withdraw_sum,change_pin,deposit_money,withdraw_money,update_balance,check_change_pin,wait_check_pin,check_ATM);
signal state: t_State:=enter_pin;
signal EnPIN: std_logic:='0';
signal poz_in: integer range 0 to 3;
signal poz_out: integer range 0 to 3;
signal pin_ok: std_logic:='0';
signal btn_check_balance_d, btn_withdraw_d, btn_deposit_d, btn_pin_d,btn_ok_d: std_logic:='0';
signal balance: std_logic_vector(19 downto 0);
signal balance_final_deposit,balance_final_withdraw: std_logic_vector(19 downto 0):=(others =>'0');
signal EA,ES: std_logic:='0';
signal sum: std_logic_vector(19 downto 0):=(others =>'0');
signal bcdin: std_logic_vector(3 downto 0):=(others =>'0');
signal anod_in: std_logic_vector(7 downto 0):=(others =>'1');
signal hun_thousands,ten_thousands,thousands,hundreds,tens,unit :std_logic_vector(3 downto 0):=(others =>'0');
signal display: std_logic_vector(2 downto 0):=(others =>'0');
signal EN_sum,EN_scaz: std_logic:='0';
signal cnt: std_logic_vector(16 downto 0):=(others => '0');
signal ATM_balance: std_logic_vector(19 downto 0) := "00000000111110100000";

begin

C1: RAM_pin port map(clk,rst,pin_in,pin_ok,EnPIN,poz_in,poz_out);
C2: debouncer port map(btn_check_balance,clk,btn_check_balance_d);
C3: debouncer port map(btn_withdraw,clk,btn_withdraw_d);
C4: debouncer port map(btn_deposit,clk,btn_deposit_d);
C5: debouncer port map(btn_pin,clk,btn_pin_d);
C6: debouncer port map(btn_ok,clk,btn_ok_d);
C7: RAM_sold port map(clk,rst,poz_out,balance,balance_final_withdraw,balance_final_deposit,ES,EA);
C8: bcd7segment port map(bcdin,seven_segment,anod_in,anod_out);
C9: adder port map(balance,sum,'0',balance_final_deposit,open,EN_sum);
C10: subtracter port map(balance,sum,'0',balance_final_withdraw,open,EN_scaz);

process(clk,rst)
variable balance_conv: integer := 0;
variable sum_temp: std_logic_vector(19 downto 0);
variable aux: std_logic_vector(19 downto 0);
constant val: std_logic_vector(16 downto 0):="11000011010100000";
constant unu: std_logic_vector(16 downto 0):="00000000000000001";
begin
    if rst = '1' then
        ATM_balance <= "00000000111110100000";
        state <= enter_pin;
        led_ok <= '0';
        led_error <= '0';
        receipt <= '0';
    end if;
    if rising_edge(clk) then
        cnt <= std_logic_vector(unsigned(cnt) + unsigned(unu));
        case state is
            when enter_pin =>
                EA <= '0';
                ES <= '0';
                EN_sum <= '0';
                EN_scaz <= '0';
                led_ok <= '0';
                anod_in <= (others => '1');
                sum<=(others=>'0');
                if pin_ok = '1' and btn_ok_d = '1' then
                    led_ok <= '1';
                    led_error <= '0';
                    receipt <= '0';
                    state <= wait_operation;
                end if;
                if pin_ok = '0' and btn_ok_d = '1' then
                    led_error <= '1';
                    receipt <= '0';
                end if;
            when wait_operation =>
                if btn_check_balance_d = '1' then
                    led_ok <= '0';
                    state <= check_balance;
                elsif btn_withdraw_d = '1' then
                    led_ok <= '0';
                    state <= withdraw_sum;
                elsif btn_deposit_d = '1' then
                    led_ok <= '0';
                    state <= deposit_sum;
                elsif btn_pin_d = '1' then
                    led_ok <= '0';
                    state <= change_pin;
                end if;
            when withdraw_sum =>
                sum_temp := (others => '0');
                if sum_in(6) = '1' then
                    aux := "00000000001111101000";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(5) = '1' then
                    aux := "00000000000111110100";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(4) = '1' then
                    aux := "00000000000011001000";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(3) = '1' then
                    aux := "00000000000001100100";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(2) = '1' then
                    aux := "00000000000000110010";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(1) = '1' then
                    aux := "00000000000000010100";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(0) = '1' then
                    aux := "00000000000000001010";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                
                if btn_ok_d = '1' then
                    state <= withdraw_money;
                end if;
                
            when deposit_sum =>
                sum_temp := (others => '0');
                if sum_in(6) = '1' then
                    aux := "00000000001111101000";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(5) = '1' then
                    aux := "00000000000111110100";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(4) = '1' then
                    aux := "00000000000011001000";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(3) = '1' then
                    aux := "00000000000001100100";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(2) = '1' then
                    aux := "00000000000000110010";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(1) = '1' then
                    aux := "00000000000000010100";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;
                if sum_in(0) = '1' then
                    aux := "00000000000000001010";
                    sum_temp := std_logic_vector(unsigned(sum_temp) + unsigned(aux));
                end if;

                if btn_ok_d = '1' then
                    state <= deposit_money;
                end if;
            when check_balance =>
                balance_conv := to_integer(unsigned(balance));
                unit <= std_logic_vector(to_unsigned(balance_conv mod 10, 4));
                tens <= std_logic_vector(to_unsigned((balance_conv / 10) mod 10, 4));
                hundreds <= std_logic_vector(to_unsigned((balance_conv / 100) mod 10, 4));
                thousands <= std_logic_vector(to_unsigned((balance_conv / 1000) mod 10, 4));
                ten_thousands <= std_logic_vector(to_unsigned((balance_conv / 10000) mod 10, 4));
                hun_thousands <= std_logic_vector(to_unsigned((balance_conv / 100000) mod 10, 4));
                if cnt = val then
                case display is
                when "000" =>
                    if hun_thousands = "0000" then
                        anod_in <= "11111111";
                    else
                        anod_in <= "01111111";
                        bcdin <= hun_thousands;
                    end if;
                    display <= "001";
                when "001" =>
                    if ten_thousands = "0000" and balance_conv < 100000 then
                        anod_in <= "11111111";
                    else
                        anod_in <= "10111111";
                        bcdin <= ten_thousands;
                    end if;
                    display <= "010";
                when "010" =>
                    if thousands = "0000" and balance_conv < 10000 then
                        anod_in <= "11111111";
                    else
                        anod_in <= "11011111";
                        bcdin <= thousands;
                    end if;
                    display <= "011";
                when "011" =>
                    if hundreds = "0000" and balance_conv < 1000 then
                        anod_in <= "11111111";
                    else
                        anod_in <= "11101111";
                        bcdin <= hundreds;
                    end if;
                    display <= "100";
                when "100" =>
                    if tens = "0000" and balance_conv < 100 then
                        anod_in <= "11111111";
                    else
                        anod_in <= "11110111";
                        bcdin <= tens;
                    end if;
                    display <= "101";
                when others =>
                    anod_in <= "11111011";
                    bcdin <= unit;
                    display <= "000";
                end case;
                end if;
                if btn_ok_d = '1' then
                    receipt <= '1';
                    state <= enter_pin;
                end if;
            when check_ATM =>
                balance_conv := to_integer(unsigned(ATM_balance));
                unit <= std_logic_vector(to_unsigned(balance_conv mod 10, 4));
                tens <= std_logic_vector(to_unsigned((balance_conv / 10) mod 10, 4));
                hundreds <= std_logic_vector(to_unsigned((balance_conv / 100) mod 10, 4));
                thousands <= std_logic_vector(to_unsigned((balance_conv / 1000) mod 10, 4));
                ten_thousands <= std_logic_vector(to_unsigned((balance_conv / 10000) mod 10, 4));
                hun_thousands <= std_logic_vector(to_unsigned((balance_conv / 100000) mod 10, 4));
                if cnt = val then
                case display is
                when "000" =>
                    if hun_thousands = "0000" then
                        anod_in <= "11111111";
                    else
                        anod_in <= "01111111";
                        bcdin <= hun_thousands;
                    end if;
                    display <= "001";
                when "001" =>
                    if ten_thousands = "0000" and balance_conv < 100000 then
                        anod_in <= "11111111";
                    else
                        anod_in <= "10111111";
                        bcdin <= ten_thousands;
                    end if;
                    display <= "010";
                when "010" =>
                    if thousands = "0000" and balance_conv < 10000 then
                        anod_in <= "11111111";
                    else
                        anod_in <= "11011111";
                        bcdin <= thousands;
                    end if;
                    display <= "011";
                when "011" =>
                    if hundreds = "0000" and balance_conv < 1000 then
                        anod_in <= "11111111";
                    else
                        anod_in <= "11101111";
                        bcdin <= hundreds;
                    end if;
                    display <= "100";
                when "100" =>
                    if tens = "0000" and balance_conv < 100 then
                        anod_in <= "11111111";
                    else
                        anod_in <= "11110111";
                        bcdin <= tens;
                    end if;
                    display <= "101";
                when others =>
                    anod_in <= "11111011";
                    bcdin <= unit;
                    display <= "000";
                end case;
                end if;
                if btn_ok_d = '1' then
                    receipt <= '1';
                    state <= enter_pin;
                end if;
            when deposit_money =>
                EN_sum <= '1';
                state <= update_balance;
                
            when withdraw_money =>
                if unsigned(balance) >= unsigned(sum) and unsigned(ATM_balance) >= unsigned(sum) then
                    EN_scaz <= '1';
                else
                    led_error <= '1';
                end if;
                state <= update_balance;
                
            when update_balance =>
                if EN_scaz = '1' then
                    ES <= '1';
                    ATM_balance <= std_logic_vector(unsigned(ATM_balance) - unsigned(sum));
                elsif EN_sum = '1' then
                    EA <= '1';
                    ATM_balance <= std_logic_vector(unsigned(ATM_balance) + unsigned(sum));
                end if;
                receipt <= '1';
                    state <= enter_pin;
                    
            when change_pin =>
                if btn_ok_d = '1' then
                    EnPIN <= '1';
                    state <= wait_check_pin;
                end if;
                
            when wait_check_pin =>
                EnPIN <= '0';
                state <= check_change_pin;
            
            when check_change_pin =>
                if pin_ok = '0' then
                    led_error <= '1';
                else
                    anod_in <= "11111110";
                    bcdin <= std_logic_vector(to_unsigned(poz_out+1, 4));
                end if;
                receipt <= '1';
                if btn_ok_d = '1' then
                    state <= enter_pin;
                end if;
            
        end case;
        sum <= sum_temp;
        poz_in<=poz_out;
    end if;
end process;
end Behavioral;

