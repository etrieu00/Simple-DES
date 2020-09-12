import Tables.*;

import java.util.ArrayList;

public class DES {
    /**
     * Tables for DES
     */
    private final int[] pci = Tables.IP;
    private final int[] pc1 = Tables.PC1;
    private final int[] pc2 = Tables.PC2;
    private final int[] pcf1 = Tables.PCF1;
    private final int[] iterations = Tables.ITERATIONS;
    private final int[] expansion = Tables.EXPANSION;
    private final int[][] sbox = Tables.SBOX;
    private final int[] ptable = Tables.PTABLE;
    private ArrayList<String> subKeys;

    public ArrayList<String> generateKeys(String key){
        key = key.replaceAll("\\s+","");
        String k0 = "";
        //K0 will be the key after the going through PC1
        for(int index : pc1){
            k0 += key.charAt(index - 1);
        }
        ArrayList<String> keys = new ArrayList<String>();
        String CLeft = k0.substring(0,28);
        String DRight = k0.substring(28);
        //Shift the keys and combine the keys
        for(int i : iterations){
            CLeft = CLeft.substring(i, 28) + CLeft.substring(0, i);
            DRight = DRight.substring(i, 28) + DRight.substring(0, i);
            keys.add(CLeft + DRight);
        }
        //Process the keys through PC2
        ArrayList<String> subKeys = new ArrayList<>();
        keys.stream().forEach(sub ->{
            String k = "";
            for(int index : pc2){
               k += sub.charAt(index - 1);
            }
            subKeys.add(k);
        });
        this.subKeys = subKeys;
        return subKeys;
    }

    public String encodePlainText(String plain){
        plain = plain.replaceAll("\\s+","");
        String IP = "";
        //Process the plain text through initial permuation table
        for(int index : pci){
            IP += plain.charAt(index-1);
        }
        //Split the process text into two halves
        String left = IP.substring(0,32);
        String right = IP.substring(32);
        int c = 1;
        for(String k : subKeys){
            String er0 = "";
            //Process through expansion box
            for(int index : expansion){
                er0 += right.charAt(index - 1);
            }
            StringBuilder a = new StringBuilder("");
            // Calculate A = E[R0] XOR K1
            for(int x = 0; x < er0.length(); x ++){
                a.append(charOf(bitOf(er0.charAt(x)) ^ bitOf(k.charAt(x))));
            }
            String[] groups = a.toString().split("(?<=\\G.{6})");
            String row, column = "";
            int rv, cv;
            int counter = 0;
            String b = "";
            String temp;
            // Group the 48-bit result of (d) into sets of 6 bit and evaluate
            for(String item : groups){
                row = item.substring(0, 1) + item.substring(5);
                column = item.substring(1, 5);
                rv = Integer.parseInt(row,2);
                cv = Integer.parseInt(column,2);
                temp = String.format("%4s", Integer.toBinaryString(sbox[counter++][(cv + (16 * rv))])).replace(' ', '0');
                b += temp;
            }
            String pb = "";
            // Apply the permutation to get P(B)
            for(int index: ptable){
                pb += b.charAt(index - 1);
            }
            StringBuilder r1 = new StringBuilder("");
            for(int x = 0; x < pb.length(); x ++) {
                r1.append(charOf(bitOf(pb.charAt(x)) ^ bitOf(left.charAt(x))));
            }
            left = right;
            right = r1.toString();
        }
        String rightLeft = right + left;
        String cipher = "";
        for(int index : pcf1){
            cipher += rightLeft.charAt(index - 1);
        }
        return cipher;
    }

    private boolean bitOf(char bit) {
        return (bit == '1');
    }

    private char charOf(boolean bit) {
        return bit ? '1' : '0';
    }
}
