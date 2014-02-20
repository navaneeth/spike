

$steps_map = Hash.new

def step(text,&block)
	$steps_map[text] = block;
end

def load_steps
 	require_relative 'steps_definition'
end

def is_valid_step(step)
	$steps_map.has_key? step
end

def execute_step(step, args)
 	block = $steps_map[step]
	block.call(args)
end
