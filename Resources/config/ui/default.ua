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
						"component_type": "Panel",
						"component_id": "SidePanel",
						"position": [0, 0, 1, 1]
					},
					{
						"type": "LayoutChange",
						"layout_type": "Window",
						"layout_id": "main",
						"component_type": "Panel",
						"component_id": "ContentPanel",
						"position": [1, 0, 9, 1]
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
						"value": ["('${selected_item}' == 'App' || '${selected_item}' == 'Resource' || '${selected_item}' == 'Service') ? ('${selected_item}' + 'Panel') : ('${selected_path.0}' == 'App' ? 'AppDetailPanel' : ('${selected_path.0}' == 'Resource' ? 'ResourceDetailPanel' : 'ServiceDetailPanel'))"]
					},
					{
						"type": "AttributeChange",
						"component_type": "Panel",
						"component_id": "AppDetailPanel",
						"component_attribute": "user_vals",
						"value": ["('${selected_path.0}' == 'App' && '${selected_path.1}' != '') ? '${selected_path.1}' : ${Panel.AppDetailPanel.user_vals.0}"]
					},
					{
						"type": "AttributeChange",
						"component_type": "Panel",
						"component_id": "ResourceDetailPanel",
						"component_attribute": "user_vals",
						"value": ["('${selected_path.0}' == 'Resource' && '${selected_path.1}' != '') ? '${selected_path.1}' : ${Panel.ResourceDetailPanel.user_vals.0}"]
					},
					{
						"type": "AttributeChange",
						"component_type": "Panel",
						"component_id": "ServiceDetailPanel",
						"component_attribute": "user_vals",
						"value": ["('${selected_path.0}' == 'Service' && '${selected_path.1}' != '') ? '${selected_path.1}' : ${Panel.ServiceDetailPanel.user_vals.0}"]
					}
				]
			}
		]
	},
	{
		"type": "Panel",
		"id": "ContentPanel",
		"scroll": "vertical",
		"user_vals": ["AppPanel"],
		"listeners": [
			{
				"type": "Timer",
				"freq": 1,
				"actions": [
					{
						"type": "LayoutChange",
						"layout_type": "Panel",
						"layout_id": "${id}",
						"component_type": "Panel",
						"component_id": "${user_vals.0}",
						"position": [0, 0, 1, 1]
					}
				]
			}
		]
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
						"value": "${Panel.AppDetailPanel.user_vals.0}"
					}
				]
			}
		]
	},
	{
		"type": "Label",
		"id": "ResourceLabel",
		"text": "RESOURCE",
		"font": ["Arial", "BOLD", 30]
	},
	{
		"type": "TextField",
		"id": "ResourceDescription",
		"text": "  This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.",
		"column_width": 60,
		"editable": false
	},
	{
		"type": "Table",
		"id": "ResourceTable",
		"column_names": ["id", "ResourceName", "Description", "App"]
	},
	{
		"type": "Label",
		"id": "ResourceDetailLabel",
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
						"value": "${Panel.ResourceDetailPanel.user_vals.0}"
					}
				]
			}
		]
	},
	{
		"type": "Label",
		"id": "ServiceLabel",
		"text": "SERVICE",
		"font": ["Arial", "BOLD", 30]
	},
	{
		"type": "TextField",
		"id": "ServiceDescription",
		"text": "  This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.This is description.",
		"column_width": 60,
		"editable": false
	},
	{
		"type": "Table",
		"id": "ServiceTable",
		"column_names": ["id", "ServiceName", "Description", "App"]
	},
	{
		"type": "Label",
		"id": "ServiceDetailLabel",
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
						"value": "${Panel.ServiceDetailPanel.user_vals.0}"
					}
				]
			}
		]
	}
]