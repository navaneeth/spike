
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

    @Step("enter age {arg0}")
    public void enterAge(int age) {
        System.out.println(age);
    }

    @Step("{arg0} life")
    public void life(boolean arg) {
        System.out.println(arg + " life, yeah!!");
    }

    @Step("{arg0} double value")
    public void doubleArg(float arg) {
        System.out.println(arg);
    }

    @Step("enter {arg0} and {arg1} and {arg2}")
    public void twoArgs(int age, String name, double weight) {
        System.out.println("in 3 args");
    }
}

