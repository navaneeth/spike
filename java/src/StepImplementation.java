
public class StepImplementation {

    @Step("test something")
    public void testSomething() {
        System.out.println("Inside implementation of test something step");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("test something with {arg0}")
    public void testSomething(String value) {
        System.out.println("test something implementation with " + value);
    }

}
