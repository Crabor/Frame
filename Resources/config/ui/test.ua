[
	{
		"type": "Window",
		"id": "main",
		"title": "Platform",
		"size": [400, 300]
	},
	{
		"type": "Button",
		"id": "button1",
		"text": "hello world",
		"listeners": [
			{
				"type": "MouseClick",
				"action": {
					"type": "LayoutChange",
					"layout_type": "Window",
					"layout_id": "main",
					"component_type": "${type}",
					"component_id": "${id}",
					"position": ["(${position.0} + 1) % 5", 0, 1, 1]
				}
			}
		]
	}
]