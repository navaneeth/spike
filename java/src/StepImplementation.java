import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static junit.framework.Assert.assertTrue;

public class StepImplementation {

    WebDriver browser = new FirefoxDriver();

    @Step("Login as {arg0} and password {arg1}")
    public void Login(String username, String password) {
        browser.get("http://sabu:8080");
        browser.findElement(By.id("user_login")).sendKeys(username);
        browser.findElement(By.id("user_password")).sendKeys(password);
        browser.findElement(By.name("commit")).click();
    }

    @Step("Create new project with name {arg0} and description {arg1}")
    public void CreateProject(String projectName, String description) {
        browser.findElement(By.linkText("Projects")).click();
        browser.findElement(By.linkText("NEW PROJECT")).click();
        browser.findElement(By.id("project_name")).sendKeys(projectName);
        browser.findElement(By.id("project_description")).sendKeys(description);
        browser.findElement(By.linkText("CREATE PROJECT")).click();
    }

    @Step("Verify project {arg0} exists")
    public void verifyProjectExists(String projectName) {
        browser.findElement(By.linkText("All projects")).click();
        assertTrue(browser.findElement(By.linkText(projectName)).isDisplayed());
    }

    @Step("Delete project")
    public void deleteProject() {
        browser.findElement(By.linkText("Delete this")).click();
        browser.findElement(By.linkText("CONTINUE TO DELETE")).click();
    }

    @Step("log out the user")
    public void logoutUser() {
        browser.findElement(By.linkText("Sign out")).click();
        browser.quit();
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

