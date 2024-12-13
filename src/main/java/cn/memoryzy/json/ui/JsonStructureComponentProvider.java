package cn.memoryzy.json.ui;

import cn.hutool.core.util.StrUtil;
import cn.memoryzy.json.action.structure.*;
import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.enums.JsonTreeNodeType;
import cn.memoryzy.json.model.wrapper.ArrayWrapper;
import cn.memoryzy.json.model.wrapper.JsonWrapper;
import cn.memoryzy.json.model.wrapper.ObjectWrapper;
import cn.memoryzy.json.ui.component.node.JsonTreeNode;
import cn.memoryzy.json.ui.listener.TreeRightClickPopupMenuMouseAdapter;
import cn.memoryzy.json.util.UIManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.*;
import com.intellij.ui.treeStructure.Tree;
import icons.JsonAssistantIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/12/12
 */
public class JsonStructureComponentProvider {

    private Tree tree;
    private JPanel treeComponent;

    /**
     * 构造器
     *
     * @param wrapper   JSON 结构
     * @param component 注册快捷键的组件
     */
    public JsonStructureComponentProvider(JsonWrapper wrapper, JComponent component) {
        init(wrapper, component);
    }

    /**
     * 初始化组件
     *
     * @param wrapper   JSON 结构
     * @param component 注册快捷键的组件
     */
    private void init(JsonWrapper wrapper, JComponent component) {
        JsonTreeNode rootNode = new JsonTreeNode("root");
        // 允许在后面再进行树的构建
        if (wrapper != null) {
            convertToTreeNode(wrapper, rootNode);
        }

        // 构建树
        tree = new Tree(new DefaultTreeModel(rootNode));
        tree.setDragEnabled(true);
        tree.setExpandableItemsEnabled(true);
        tree.setFont(UIManager.jetBrainsMonoFont(12));
        tree.setCellRenderer(new StyleTreeCellRenderer());
        tree.addMouseListener(new TreeRightClickPopupMenuMouseAdapter(tree, buildRightMousePopupMenu()));

        // 触发快速检索
        new TreeSpeedSearch(tree);

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(tree)
                .addExtraAction(new ExpandAllAction(tree, component, true))
                .addExtraAction(new CollapseAllAction(tree, component, true));

        this.treeComponent = new JPanel(new BorderLayout());
        this.treeComponent.add(decorator.createPanel(), BorderLayout.CENTER);
    }

    public void rebuildTree(JsonWrapper wrapper) {
        JsonTreeNode rootNode = new JsonTreeNode("root");
        if (wrapper != null) {
            convertToTreeNode(wrapper, rootNode);
        }

        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(rootNode);
        // UIManager.repaintComponent(tree);
    }

    private void convertToTreeNode(JsonWrapper jsonWrapper, JsonTreeNode node) {
        if (jsonWrapper instanceof ObjectWrapper) {
            ObjectWrapper jsonObject = (ObjectWrapper) jsonWrapper;
            // 为了确定图标
            if (Objects.isNull(node.getNodeType())) {
                node.setNodeType(JsonTreeNodeType.JSONObject);
            }

            if (Objects.isNull(node.getValue())) {
                node.setValue(jsonObject);
            }

            node.setSize(jsonObject.size());

            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                JsonTreeNode childNode = new JsonTreeNode(key);

                if (value instanceof ObjectWrapper) {
                    ObjectWrapper nestedJsonObject = (ObjectWrapper) value;
                    childNode.setValue(value).setNodeType(JsonTreeNodeType.JSONObject).setSize(nestedJsonObject.size());
                    convertToTreeNode(nestedJsonObject, childNode);

                } else if (value instanceof ArrayWrapper) {
                    ArrayWrapper jsonArray = (ArrayWrapper) value;
                    childNode.setValue(value).setNodeType(JsonTreeNodeType.JSONArray).setSize(jsonArray.size());
                    handleJsonArray(childNode, jsonArray);

                } else {
                    // 若不是对象或数组，则不添加子集，直接同层级
                    childNode.setValue(value)
                            .setNodeType(JsonTreeNodeType.JSONObjectProperty)
                            .setUserObject(key);
                }

                node.add(childNode);
            }
        } else if (jsonWrapper instanceof ArrayWrapper) {
            ArrayWrapper jsonArray = (ArrayWrapper) jsonWrapper;
            // 为了确定图标
            node.setNodeType(JsonTreeNodeType.JSONArray).setSize(jsonArray.size());
            if (Objects.isNull(node.getValue())) {
                node.setValue(jsonArray);
            }

            handleJsonArray(node, jsonArray);
        }
    }

    private void handleJsonArray(JsonTreeNode childNode, ArrayWrapper jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            Object el = jsonArray.get(i);
            if (el instanceof ObjectWrapper) {
                ObjectWrapper jsonObjectElement = (ObjectWrapper) el;
                JsonTreeNode childNodeElement = new JsonTreeNode(
                        "item" + i, el, JsonTreeNodeType.JSONObjectElement, jsonObjectElement.size());

                convertToTreeNode(jsonObjectElement, childNodeElement);
                childNode.add(childNodeElement);
            } else if (el instanceof ArrayWrapper) {
                ArrayWrapper jsonArrayElement = (ArrayWrapper) el;
                JsonTreeNode childNodeElement = new JsonTreeNode(
                        "item" + i, el, JsonTreeNodeType.JSONArrayElement, jsonArrayElement.size());

                convertToTreeNode(jsonArrayElement, childNodeElement);
                childNode.add(childNodeElement);
            } else {
                Object obj = el;
                if (el instanceof String) {
                    String str = (String) el;
                    obj = "\"" + str + "\"";
                }

                childNode.add(new JsonTreeNode(obj).setValue(el).setNodeType(JsonTreeNodeType.JSONArrayElement));
            }
        }
    }

    private JPopupMenu buildRightMousePopupMenu() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addSeparator();
        group.add(new CopyKeyAction(tree));
        group.addSeparator();
        group.add(new CopyValueAction(tree));
        group.addSeparator();
        group.add(new CopyKeyValueAction(tree));
        group.addSeparator();
        group.add(new ExpandMultiAction(tree));
        group.addSeparator();
        group.add(new CollapseMultiAction(tree));
        group.addSeparator();
        group.add(new RemoveTreeNodeAction(tree));
        ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu(ActionPlaces.POPUP, group);
        return actionPopupMenu.getComponent();
    }

    public Tree getTree() {
        return tree;
    }

    public JPanel getTreeComponent() {
        return treeComponent;
    }

    private static class StyleTreeCellRenderer extends ColoredTreeCellRenderer {
        @Override
        public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JsonTreeNode jsonTreeNode = (JsonTreeNode) value;
            JsonTreeNodeType nodeType = jsonTreeNode.getNodeType();

            String text = jsonTreeNode.getUserObject().toString();
            SimpleTextAttributes simpleTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;

            SimpleTextAttributes lightAttributes = SimpleTextAttributes.merge(simpleTextAttributes, SimpleTextAttributes.GRAYED_ATTRIBUTES);
            SimpleTextAttributes blueAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(63, 120, 230), new Color(137, 174, 246)));
            SimpleTextAttributes purpleAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(248, 108, 101), new Color(244, 184, 181)));

            SimpleTextAttributes stringColorAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(6, 125, 23), new Color(104, 169, 114)));
            SimpleTextAttributes booleanWithNullColorAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(0, 51, 179), new Color(206, 141, 108)));
            SimpleTextAttributes numberColorAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(25, 80, 234), new Color(41, 171, 183)));

            Icon icon = JsonAssistantIcons.Structure.JSON_KEY;

            Integer size = jsonTreeNode.getSize();
            Object nodeValue = jsonTreeNode.getValue();

            String squareBracketsStart = "";
            String nodeTypeStr = "";
            String squareBracketsEnd = "";

            String sizeStrPre = "";
            String sizeStr = "";
            String sizeStrPost = "";

            // json的value
            String jsonValue = "";
            String jsonValueType = "";

            if (Objects.nonNull(nodeType)) {
                switch (nodeType) {
                    case JSONObject: {
                        squareBracketsStart = " [";
                        nodeTypeStr = "object";
                        squareBracketsEnd = "]";
                        sizeStrPre = " (";
                        sizeStr = size + " " + JsonAssistantBundle.messageOnSystem(size == 1 ? "dialog.structure.size.obj.singular.text" : "dialog.structure.size.obj.plural.text");
                        sizeStrPost = ")";

                        icon = JsonAssistantIcons.Structure.JSON_OBJECT;
                        break;
                    }

                    case JSONArray: {
                        squareBracketsStart = " [";
                        nodeTypeStr = "array";
                        squareBracketsEnd = "]";
                        sizeStrPre = " (";
                        sizeStr = size + " " + JsonAssistantBundle.messageOnSystem(size == 1 ? "dialog.structure.size.array.singular.text" : "dialog.structure.size.array.plural.text");
                        sizeStrPost = ")";

                        icon = JsonAssistantIcons.Structure.JSON_ARRAY;
                        break;
                    }

                    case JSONObjectElement: {
                        squareBracketsStart = " [";
                        nodeTypeStr = "array_object";
                        squareBracketsEnd = "]";
                        sizeStrPre = " (";
                        sizeStr = size + " " + JsonAssistantBundle.messageOnSystem(size == 1 ? "dialog.structure.size.obj.singular.text" : "dialog.structure.size.obj.plural.text");
                        sizeStrPost = ")";

                        icon = JsonAssistantIcons.Structure.JSON_OBJECT_ITEM;
                        break;
                    }

                    case JSONArrayElement: {
                        icon = JsonAssistantIcons.Structure.JSON_ITEM;
                        String valueStr;
                        if (Objects.isNull(nodeValue)) {
                            valueStr = "null";
                            jsonValueType = "null";
                        } else {
                            if (nodeValue instanceof String) {
                                String str = (String) nodeValue;

                                if (str.isEmpty()) {
                                    valueStr = "\"\"";
                                } else {
                                    valueStr = "\"" + str + "\"";
                                }
                                jsonValueType = String.class.getName();

                            } else if (nodeValue instanceof Boolean) {
                                jsonValueType = Boolean.class.getName();
                                valueStr = nodeValue + "";

                            } else if (nodeValue instanceof Number) {
                                jsonValueType = Number.class.getName();
                                valueStr = nodeValue + "";
                            } else {
                                valueStr = nodeValue + "";
                            }
                        }

                        jsonValue = valueStr;
                        break;
                    }

                    case JSONObjectProperty: {
                        String valueStr;
                        if (Objects.isNull(nodeValue)) {
                            valueStr = "null";
                            jsonValueType = "null";
                        } else {
                            if (nodeValue instanceof String) {
                                String str = (String) nodeValue;

                                if (str.isEmpty()) {
                                    valueStr = "\"\"";
                                } else {
                                    valueStr = "\"" + str + "\"";
                                }
                                jsonValueType = String.class.getName();

                            } else if (nodeValue instanceof Boolean) {
                                jsonValueType = Boolean.class.getName();
                                valueStr = nodeValue + "";

                            } else if (nodeValue instanceof Number) {
                                jsonValueType = Number.class.getName();
                                valueStr = nodeValue + "";
                            } else {
                                valueStr = nodeValue + "";
                            }
                        }

                        jsonValue = valueStr;
                        break;
                    }
                }
            }

            if (!Objects.equals(JsonTreeNodeType.JSONArrayElement, nodeType)) {
                append(Objects.equals(JsonTreeNodeType.JSONObjectProperty, nodeType) ? text + ": " : text, simpleTextAttributes);
            }

            if (StrUtil.isNotBlank(squareBracketsStart)) append(squareBracketsStart, lightAttributes, false);
            if (StrUtil.isNotBlank(nodeTypeStr)) append(nodeTypeStr, blueAttributes, false);
            if (StrUtil.isNotBlank(squareBracketsEnd)) append(squareBracketsEnd, lightAttributes, false);
            if (StrUtil.isNotBlank(sizeStrPre)) append(sizeStrPre, lightAttributes, false);
            if (StrUtil.isNotBlank(sizeStr)) append(sizeStr, purpleAttributes, false);
            if (StrUtil.isNotBlank(sizeStrPost)) append(sizeStrPost, lightAttributes, false);

            // 普通节点
            if (StrUtil.isNotBlank(jsonValue)) {
                SimpleTextAttributes attributes;
                if ("null".equals(jsonValueType) || Boolean.class.getName().equals(jsonValueType)) {
                    attributes = booleanWithNullColorAttributes;
                } else if (String.class.getName().equals(jsonValueType)) {
                    attributes = stringColorAttributes;
                } else if (Number.class.getName().equals(jsonValueType)) {
                    attributes = numberColorAttributes;
                } else {
                    attributes = stringColorAttributes;
                }

                append(jsonValue, attributes, Objects.equals(JsonTreeNodeType.JSONArrayElement, nodeType));
            }

            setIcon(icon);
        }
    }

}
