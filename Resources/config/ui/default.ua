[
	{
		"type": "Window",
		"id": "main",
		"title": "Platform",
		"width": 800,
		"height": 600
	},
	{
		"type": "Button",
		"id": "welcomeButton",
		"text": "Welcome",
		"font": ["Arial", "PLAIN", 40],
		"listeners": {
			"type": "MouseClick",
			"action": {
				"type": ""
			},
		}
	},
	{
		"type": "Tree",
		"id": "NavigateTree",
		"dirs": ["App", "Resource", "Service"]
	},
	{
		"type": "Panel",
		"id": "ContentPanel"
	},
	{
		"type": "Panel",
		"id": "AppPanel",
		"scroll": "vertical"
	},
	{
		"type": "Label",
		"id": "App",
		"text": "APP",
		"font": ["Arial", "BOLD", 30]
	},
	{
		"type": "TextField",
		"id": "Description",
		"text": "  This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.",
		"columns": 60,
		"editable": false
	},
	{
		"type": "Table",
		"id": "AppTable",
		"columns": ["id", "AppName", "Description", "Service", "Resource"]
	}
]