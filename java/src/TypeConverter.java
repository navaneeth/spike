
interface StringToPremitiveConverter {
    Object convert(String source);
}

class StringToIntegerConverter implements StringToPremitiveConverter {

    @Override
    public Object convert(String source) {
        return Integer.parseInt(source);
    }
}
