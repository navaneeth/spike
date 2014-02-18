
public class StepImplementation {

    @Step("test something")
    public void testSomething() {
        System.out.println("Inside implementation of test something step");
    }

    @Step("test something with {arg0}")
    public void testSomething(String value) {
        System.out.println("test something implementation with " + value);
        if (value.equalsIgnoreCase("fail")) {
            String str = null;
            System.out.println(str.toString());
        }
    }
}

