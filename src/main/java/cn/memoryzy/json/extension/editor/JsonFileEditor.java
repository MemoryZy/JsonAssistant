package cn.memoryzy.json.extension.editor;

import cn.memoryzy.json.ui.JsonEditorComponentProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * @author Memory
 * @since 2024/12/16
 */
public class JsonFileEditor extends UserDataHolderBase implements FileEditor {

    private final VirtualFile file;
    private final JsonEditorComponentProvider provider;

    public JsonFileEditor(Project project, VirtualFile file) {
        this.file = file;
        this.provider = new JsonEditorComponentProvider(project);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return provider.getComponent();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return provider.getPreferredFocusedComponent();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "Structure";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public VirtualFile getFile() {
        return file;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {

    }
}
