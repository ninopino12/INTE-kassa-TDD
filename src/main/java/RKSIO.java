import java.util.Scanner;

public final class RKSIO implements KSIO {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String read() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    @Override
    public void print(String str) {
        System.out.println(str);
    }
}
