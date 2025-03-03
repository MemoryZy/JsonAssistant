package cn.memoryzy.json.action.toolwindow;

import cn.hutool.core.util.StrUtil;
import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.ui.panel.JsonAssistantToolWindowPanel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import icons.JsonAssistantIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/8/21
 */
public class ClearEditorAction extends DumbAwareAction implements UpdateInBackground {

    private final EditorEx editor;
    private final SimpleToolWindowPanel simpleToolWindowPanel;

    public ClearEditorAction(EditorEx editor, SimpleToolWindowPanel simpleToolWindowPanel) {
        super();
        this.editor = editor;
        this.simpleToolWindowPanel = simpleToolWindowPanel;
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(JsonAssistantBundle.messageOnSystem("action.clear.editor.text"));
        presentation.setDescription(JsonAssistantBundle.messageOnSystem("action.clear.editor.description"));
        presentation.setIcon(JsonAssistantIcons.ToolWindow.DELETE);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        WriteCommandAction.runWriteCommandAction(event.getProject(), () -> editor.getDocument().setText(""));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(
                getEventProject(event) != null
                        && StrUtil.isNotBlank(editor.getDocument().getText())
                        && JsonAssistantToolWindowPanel.isEditorCardDisplayed(simpleToolWindowPanel)
                        && !editor.isViewer());
    }
}
