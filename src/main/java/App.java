public class App {
    public static void main(String[] args){
        String plainText = "0000 0001 0010 0011 0100 0101 0110 0111 1000 1001 1010 1011 1100 1101 1110 1111";
        String secretKey = "00010011 00110100 01010111 01111001 10011011 10111100 11011111 11110001";
        DES des = new DES();
        System.out.println("Here are the 16 sub keys that will be used for DES...");
        des.generateKeys(secretKey).forEach(System.out::println);
        System.out.println("\nHere is the encrypted string in binary...");
        System.out.println(des.encodePlainText(plainText));
    }
}
