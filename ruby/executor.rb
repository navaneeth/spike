

$steps_map = Hash.new

def step(text,&block)
	$steps_map[text] = block;
end

def load_steps steps_location
 	steps_location.each {|x| require_relative x}
end

def is_valid_step(step)
	$steps_map.has_key? step
end

def execute_step(step, args)
 	block = $steps_map[step]
 	if args.size == 1 
 		block.call(args[0])
 	else
		block.call(args)
	end
end
