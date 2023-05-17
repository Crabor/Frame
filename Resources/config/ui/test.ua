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
		"text": "${systime}",
		"listeners": [
			{
				"type": "MouseClick",
				"actions": [
					{
						"type": "LayoutChange",
						"layout_type": "Window",
						"layout_id": "main",
						"component_type": "${type}",
						"component_id": "${id}",
						"position": ["(${position.0} + 1) % 5", 0, 1, 1]
					}
				]
			},
			{
				"type": "Timer",
				"freq": 1,
				"actions": [
					{
						"type": "AttributeChange",
						"component_type": "${type}",
						"component_id": "${id}",
						"component_attribute": "text",
						"value": "${systime}"
					}
				]
			}
		]
	}
]