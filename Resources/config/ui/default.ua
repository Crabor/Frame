[
	{
		"type": "Window",
		"id": "main",
		"title": "Platform",
		"size": [800, 600]
	},
	{
		"type": "Label",
		"id": "WelcomeLabel",
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
						"type": "LayoutChange",
						"layout_type": "Panel",
						"layout_id": "ContentPanel",
						"component_type": "Panel",
						"component_id": "item = '${selected_item}'; path0 = '${selected_path.0}'; (item == path0) ? (path0 + 'Panel') : (path0 + 'DetailPanel')",
						"position": [0, 0, 1, 1]
					},
					{
						"type": "AttributeChange",
						"component_type": "Panel",
						"component_id": "ContentPanel",
						"component_attribute": "user_vals",
						"value": ["${selected_path.0}"]
					},
					{
						"type": "AttributeChange",
						"component_type": "Panel",
						"component_id": "'${Panel.ContentPanel.user_vals.0}' + 'DetailPanel'",
						"component_attribute": "user_vals",
						"value": ["${selected_path.1}"]
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
		"column_width": 50,
		"editable": false
	},
	{
		"type": "Table",
		"id": "AppTable",
		"column_names": ["AppName", "Description", "Service", "Resource", "Status"],
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
						"sql": "SELECT * FROM ${id}"
					}
				]
			}
		]
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
		"column_width": 50,
		"editable": false
	},
	{
		"type": "Table",
		"id": "ResourceTable",
		"column_names": ["ResourceName", "Type", "App", "Status"],
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
						"sql": "SELECT * FROM ${id}"
					}
				]
			}
		]
	},
	{
		"type": "Label",
		"id": "ResourceDetailTitle",
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
		"id": "ResourceDetailType",
		"text": "Type: "
	},
	{
		"type": "Label",
		"id": "ResourceDetailTypeValue",
		"text": "",
		"listeners": [
			{
				"type": "Timer",
				"freq": 1,
				"actions": [
					{
						"type": "DatabaseGet",
						"component_type": "${type}",
						"component_id": "${id}",
						"component_attribute": "text",
						"sql": "SELECT Type FROM ResourceTable WHERE ResourceName = '${Panel.ResourceDetailPanel.user_vals.0}'"
					}
				]
			}
		]
	},
	{
		"type": "Label",
		"id": "ResourceDetailStatus",
		"text": "Status: "
	},
	{
		"type": "Label",
		"id": "ResourceDetailStatusValue",
		"text": "",
		"listeners": [
			{
				"type": "Timer",
				"freq": 1,
				"actions": [
					{
						"type": "DatabaseGet",
						"component_type": "${type}",
						"component_id": "${id}",
						"component_attribute": "text",
						"sql": "SELECT Status FROM ResourceTable WHERE ResourceName = '${Panel.ResourceDetailPanel.user_vals.0}'"
					}
				]
			}
		]
	},
	{
		"type": "Label",
		"id": "ResourceDetailRegisteredBy",
		"text": "Registered By: "
	},
	{
		"type": "Label",
		"id": "ResourceDetailRegisteredByValue",
		"text": "",
		"listeners": [
			{
				"type": "Timer",
				"freq": 1,
				"actions": [
					{
						"type": "DatabaseGet",
						"component_type": "${type}",
						"component_id": "${id}",
						"component_attribute": "text",
						"sql": "SELECT App FROM ResourceTable WHERE ResourceName = '${Panel.ResourceDetailPanel.user_vals.0}'"
					}
				]
			}
		]
	},
	{
		"type": "Label",
		"id": "ResourceDetailValue",
		"text": "Value: "
	},
	{
		"type": "Label",
		"id": "ResourceDetailValueValue",
		"text": "",
		"listeners": [
			{
				"type": "Timer",
				"freq": 1,
				"actions": [
					{
						"type": "DatabaseGet",
						"component_type": "${type}",
						"component_id": "${id}",
						"component_attribute": "text",
						"sql": "SELECT Val FROM ResourceTable WHERE ResourceName = '${Panel.ResourceDetailPanel.user_vals.0}'"
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
		"column_width": 50,
		"editable": false
	},
	{
		"type": "Table",
		"id": "ServiceTable",
		"column_names": ["ServiceName", "App"],
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
						"sql": "SELECT * FROM ${id}"
					}
				]
			}
		]
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