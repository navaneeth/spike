
step "test something" do
	puts "**** executing a step"
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




