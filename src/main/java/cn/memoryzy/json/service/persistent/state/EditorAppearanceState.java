package cn.memoryzy.json.service.persistent.state;

import cn.memoryzy.json.enums.ColorScheme;
import cn.memoryzy.json.model.jackson.ColorDeserializer;
import cn.memoryzy.json.model.jackson.ColorSerializer;
import cn.memoryzy.json.util.PlatformUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.awt.*;

/**
 * 编辑器外观
 *
 * @author Memory
 * @since 2024/11/18
 */
public class EditorAppearanceState {

    /**
     * 展示编辑器行号
     */
    public boolean displayLineNumbers = false;

    /**
     * 显示折叠轮廓
     */
    public boolean foldingOutline = PlatformUtil.isNewUi();


    // ----------------------------------------------------

    /**
     * 编辑器背景配色名称（指定颜色或自定义）
     */
    public ColorScheme colorScheme = ColorScheme.Default;

    /**
     * 当选中自定义项时，用户指定的颜色（亮色）
     */
    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    public Color customLightColor;

    /**
     * 当选中自定义项时，用户指定的颜色（暗色）
     */
    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    public Color customDarkcolor;

}
