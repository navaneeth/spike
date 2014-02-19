
interface StringToPrimitiveConverter {
    Object convert(String source);
}

class StringToIntegerConverter implements StringToPrimitiveConverter {
    @Override
    public Object convert(String source) {
        return Integer.parseInt(source);
    }
}

class StringToBooleanConverter implements StringToPrimitiveConverter {
    @Override
    public Object convert(String source) {
        return Boolean.parseBoolean(source);
    }
}

class StringToDoubleConverter implements StringToPrimitiveConverter {
    @Override
    public Object convert(String source) {
        return Double.parseDouble(source);
    }
}

class StringToLongConverter implements StringToPrimitiveConverter {
    @Override
    public Object convert(String source) {
        return Long.parseLong(source);
    }
}

class StringToFloatConverter implements StringToPrimitiveConverter {
    @Override
    public Object convert(String source) {
        return Float.parseFloat(source);
    }
}
