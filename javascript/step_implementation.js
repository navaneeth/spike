
var twist = require("./twist")

twist.step("test step", function() {
	console.log("inside step implementation");
});

twist.step("test step with {arg0} and {arg1}", function(arg0, arg1) {
	console.log("inside step implementation with args");
	console.log(arg0);
	console.log(arg1);
});

