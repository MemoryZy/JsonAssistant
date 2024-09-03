package cn.memoryzy.json.model.formats;

import cn.hutool.core.util.StrUtil;
import cn.memoryzy.json.util.JsonUtil;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.TextRange;

import javax.swing.*;

/**
 * @author Memory
 * @since 2024/8/3
 */
public class JsonFormatHandleModel extends BaseFormatModel {

    private static final Logger LOG = Logger.getInstance(JsonFormatHandleModel.class);

    /**
     * 是否为 JSON 格式文本
     */
    private final boolean isJsonStr;

    /**
     * 选中的文本转变后发出的提示
     */
    private String selectHint;

    /**
     * 全部的文本转变后发出的提示
     */
    private String defaultHint;

    public JsonFormatHandleModel(Boolean isSelected, int startOffset, int endOffset, Caret primaryCaret, String content, boolean isJsonStr, String selectHint, String defaultHint) {
        super(isSelected, startOffset, endOffset, primaryCaret, content);
        this.isJsonStr = isJsonStr;
        this.selectHint = selectHint;
        this.defaultHint = defaultHint;
    }

    public static JsonFormatHandleModel of(Editor editor) {
        return of(editor, null, null);
    }

    @SuppressWarnings("DuplicatedCode")
    public static JsonFormatHandleModel of(Editor editor, String selectHint, String defaultHint) {
        if (editor == null) {
            return null;
        }

        Caret primaryCaret = null;
        int startOffset = 0;
        int endOffset = 0;
        String jsonContent = null;
        boolean isSelectedText = false;
        try {
            Document document = editor.getDocument();
            primaryCaret = editor.getCaretModel().getPrimaryCaret();
            startOffset = primaryCaret.getSelectionStart();
            endOffset = primaryCaret.getSelectionEnd();
            String selectText = document.getText(new TextRange(startOffset, endOffset));
            jsonContent = (JsonUtil.isJsonStr(selectText)) ? selectText : JsonUtil.extractJsonStr(selectText);

            isSelectedText = true;
            if (StrUtil.isBlank(jsonContent)) {
                isSelectedText = false;
                String documentText = document.getText();
                jsonContent = (JsonUtil.isJsonStr(documentText)) ? documentText : JsonUtil.extractJsonStr(documentText);
            }
        } catch (Exception ignored) {
        }

        return new JsonFormatHandleModel(isSelectedText, startOffset, endOffset,
                primaryCaret, jsonContent, StrUtil.isNotBlank(jsonContent), selectHint, defaultHint);
    }


    public boolean isJsonStr() {
        return isJsonStr;
    }

    public JsonFormatHandleModel setSelectHint(String selectHint) {
        this.selectHint = selectHint;
        return this;
    }

    public JsonFormatHandleModel setDefaultHint(String defaultHint) {
        this.defaultHint = defaultHint;
        return this;
    }

// ---------------- 不需要实现...

    @Override
    public boolean isConformFormat(String text) {
        return false;
    }

    @Override
    public String convertToJson() {
        return null;
    }

    @Override
    public String getActionName() {
        return null;
    }

    @Override
    public String getActionDescription() {
        return null;
    }

    @Override
    public Icon getActionIcon() {
        return null;
    }

    @Override
    public String getSelectHint() {
        return selectHint;
    }

    @Override
    public String getDefaultHint() {
        return defaultHint;
    }

    @Override
    public FileType getFileType() {
        return JsonFileType.INSTANCE;
    }
}
