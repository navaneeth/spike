require "selenium-webdriver"


$browser = Selenium::WebDriver.for :firefox

step "Login as {arg0} and password {arg1}" do |username, pass|
	$browser.navigate.to("http://sabu:8080")
	$browser.find_element(:id, "user_login").send_keys(username)
	$browser.find_element(:id, "user_password").send_keys(pass)
	$browser.find_element(:name, "commit").click
end

step "Create new project with name {arg0} and description {arg1}" do |project_name, description|
	$browser.find_element(:link, "Projects").click
	$browser.find_element(:link, "NEW PROJECT").click
	$browser.find_element(:id, "project_name").send_keys(project_name)
	$browser.find_element(:id, "project_description").send_keys(description)
	$browser.find_element(:link, "CREATE PROJECT").click
end

step "Verify project {arg0} exists" do |project_name|
	$browser.find_element(:link, "All projects").click
	if ($browser.find_element(:link, project_name).displayed? == false)
		raise 'Project #{project_name} not displayed'
	end
end

step "Delete project" do
	$browser.find_element(:link, "Delete this").click
	$browser.find_element(:link, "CONTINUE TO DELETE").click
end

step "log out the user" do
	$browser.find_element(:link, "Sign out").click
	$browser.close
end


step "step that {arg0}" do |x| 
	if (x == "fails")
  		raise Exception, "*** Step has failed"
  	else
  		puts "***** step passed"
  	end
end

step "my step with {arg0} and {arg1}" do |x,y|
	puts "****** executing step wih paramerter #{x} and #{y}"
end




