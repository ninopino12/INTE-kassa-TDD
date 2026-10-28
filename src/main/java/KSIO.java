public interface KSIO {
    // null betyder att inmatningen är slut.
    String read();

    void print(String str);
}
