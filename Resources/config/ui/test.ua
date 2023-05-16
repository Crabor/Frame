[
	{
		"type": "Window",
		"id": "main",
		"title": "Platform",
		"size": [800, 600]
	},
	{
		"type": "Button",
		"id": "button1",
		"text": "${systime}",
		"listeners": [
//			{
//				"type": "MouseClick",
//				"action": {
//					"type": "LayoutChange",
//					"layout_type": "Window",
//					"layout_id": "main",
//					"component_type": "${type}",
//					"component_id": "${id}",
//					"position": ["(${position.0} + 1) % 5", 0, 1, 1]
//				}
//			},
			{
				"type": "MouseClick",
				"action": {
					"type": "DatabaseSet",
					"sql": "INSERT INTO time (time) VALUES ('${systime}')"
				}
			},
			{
				"type": "Timer",
				"freq": 1,
				"action": {
					"type": "AttributeChange",
					"component_type": "${type}",
					"component_id": "${id}",
					"attribute": "text",
					"value": "${systime}"
				}
			}
		]
	},
//	{
//		"type": "Tree",
//		"id": "tree1",
//		"dirs": ["App", "Resource", "Service"],
//		"listeners": [
//			{
//				"type": "Timer",
//				"freq": 1,
//				"action": {
//					"type": "DatabaseGet",
//					"component_type": "${type}",
//					"component_id": "${id}",
//					"component_attribute": "content",
//					"sql": "SELECT * FROM ${id}"
//				}
//			}
//		]
//	},
	{
		"type": "Table",
		"id": "table1",
		"column_names": ["time"],
		"listeners": [
			{
				"type": "Timer",
				"freq": 1,
				"action": {
					"type": "DatabaseGet",
					"component_type": "${type}",
					"component_id": "${id}",
					"component_attribute": "content",
					"sql": "SELECT * FROM time"
				}
			}
		]
	}
]