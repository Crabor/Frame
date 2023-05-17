[
	{
		"type": "Window",
		"id": "main",
		"title": "Platform",
		"size": [800, 600]
	},
	{
		"type": "Label",
		"id": "welcomeLabel",
		"text": "Welcome",
		"font": ["Arial", "PLAIN", 40],
		"listeners": [
			{
				"type": "MouseClick",
				"actions": [
					{
						"type": "LayoutChange",
						"layout_type": "Window",
						"layout_id": "main",
						"component_type": "Tree",
						"component_id": "NavigateTree",
						"position": [0, 0, 1, 1],
						"align": "North"
					},
					{
						"type": "LayoutChange",
						"layout_type": "Window",
						"layout_id": "main",
						"component_type": "Panel",
						"component_id": "ContentPanel",
						"position": [2, 0, 8, 1]
					}
				]
			}
		]
	},
	{
		"type": "Tree",
		"id": "NavigateTree",
		"dirs": ["App", "Resource", "Service"],
		"listeners": [
			{
				"type": "Timer",
				"freq": 1,
				"actions": [
					{
						"type": "DatabaseGet",
						"component_type": "${type}",
						"component_id": "${id}",
						"component_attribute": "content",
						"sql": "SELECT dir,file FROM ${id}"
					}
				]
			},
			{
				"type": "ItemSelect",
				"actions": [
					{
						"type": "AttributeChange",
						"component_type": "Panel",
						"component_id": "ContentPanel",
						"component_attribute": "user_vals",
						"value": ["(`${selected_item}` == 'App' || `${selected_item}` == 'Resource' || `${selected_item}` == 'Service') ? `${selected_item}` : (${selected_path.0})"]
					}
				]
			}
		]
	},
	{
		"type": "Panel",
		"id": "ContentPanel",
		"scroll": "vertical"
	},
	{
		"type": "Label",
		"id": "AppLabel",
		"text": "APP",
		"font": ["Arial", "BOLD", 30]
	},
	{
		"type": "TextField",
		"id": "AppDescription",
		"text": "  This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.",
		"column_width": 60,
		"editable": false
	},
	{
		"type": "Table",
		"id": "AppTable",
		"column_names": ["id", "AppName", "Description", "Service", "Resource"]
	},
	{
		"type": "Label",
		"id": "AppDetailLabel",
		"font": ["Arial", "BOLD", 30],
		"listeners": [
			{
				"type": "Timer",
				"freq": 1,
				"actions": [
					{
						"type": "AttributeChange",
						"component_type": "${type}",
						"component_id": "${id}",
						"component_attribute": "text",
						"value": "${Panel.AppDetailPanel.user_vals.0}}"
					}
				]
			}
		]
	}
]