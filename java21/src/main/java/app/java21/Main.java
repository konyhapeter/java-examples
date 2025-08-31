void main() {
    Object obj = "apacuka";

    if (obj instanceof String s) { // no need to cast separately
        System.out.println("Length: " + s.length());
    }
}